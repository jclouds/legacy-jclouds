/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.deltacloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.deltacloud.DeltacloudClient;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.PasswordAuthentication;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.domain.TransitionOnAction;
import org.jclouds.deltacloud.domain.Instance.State;
import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.deltacloud.predicates.InstanceFinished;
import org.jclouds.deltacloud.predicates.InstanceRunning;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * defines the connection between the {@link DeltacloudClient} implementation
 * and the jclouds {@link ComputeService}
 * 
 */
@Singleton
public class DeltacloudComputeServiceAdapter implements
      ComputeServiceAdapter<Instance, HardwareProfile, org.jclouds.deltacloud.domain.Image, Realm> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final org.jclouds.deltacloud.DeltacloudClient client;
   private final ImmutableMap<State, Predicate<Instance>> stateChanges;

   @Inject
   public DeltacloudComputeServiceAdapter(DeltacloudClient client) {
      this.client = checkNotNull(client, "client");
      // TODO: parameterize
      stateChanges = ImmutableMap.<Instance.State, Predicate<Instance>> of(//
            Instance.State.RUNNING, new RetryablePredicate<Instance>(new InstanceRunning(client), 600, 1,
                  TimeUnit.SECONDS),//
            Instance.State.FINISH, new RetryablePredicate<Instance>(new InstanceFinished(client), 30, 1,
                  TimeUnit.SECONDS)//
            );
   }

   @Override
   public NodeAndInitialCredentials<Instance> createNodeWithGroupEncodedIntoName(String tag, String name,
         Template template) {
      Instance instance = client.createInstance(template.getImage().getProviderId(), CreateInstanceOptions.Builder
            .named(name).hardwareProfile(template.getHardware().getId()).realm(template.getLocation().getId()));
      LoginCredentials creds = null;
      if (instance.getAuthentication() != null && instance.getAuthentication() instanceof PasswordAuthentication) {
         creds = PasswordAuthentication.class.cast(instance.getAuthentication()).getLoginCredentials();
      }
      return new NodeAndInitialCredentials<Instance>(instance, instance.getId(), creds);
   }

   @Override
   public Iterable<HardwareProfile> listHardwareProfiles() {
      return client.listHardwareProfiles();
   }

   @Override
   public Iterable<org.jclouds.deltacloud.domain.Image> listImages() {
      return client.listImages();
   }

   @Override
   public Iterable<Instance> listNodes() {
      return client.listInstances();
   }

   @Override
   public Iterable<Realm> listLocations() {
      return client.listRealms();
   }

   @Override
   public org.jclouds.deltacloud.domain.Instance getNode(String id) {
      return client.getInstance(URI.create(checkNotNull(id, "id")));
   }

   @Override
   public org.jclouds.deltacloud.domain.Image getImage(String id) {
      return client.getImage(URI.create(checkNotNull(id, "id")));
   }
   
   @Override
   public void destroyNode(String id) {
      Instance instance = getNode(id);
      for (Transition transition : findChainTo(Instance.State.FINISH, instance.getState(), client.getInstanceStates())) {
         instance = getNode(id);
         if (instance == null)
            break;
         if (transition instanceof TransitionOnAction) {
            client.performAction(instance.getActions().get(TransitionOnAction.class.cast(transition).getAction()));
         }
         Predicate<Instance> stateTester = stateChanges.get(transition.getTo());
         if (stateTester != null)
            stateTester.apply(instance);
         else
            logger.debug(String.format("no state tester for: %s", transition));
      }
   }

   Iterable<Transition> findChainTo(Instance.State desired, Instance.State currentState,
         Multimap<Instance.State, ? extends Transition> states) {
      for (Transition transition : states.get(currentState)) {
         if (currentState.ordinal() >= transition.getTo().ordinal())
            continue;
         if (transition.getTo() == desired)
            return ImmutableSet.<Transition> of(transition);
         Iterable<Transition> transitions = findChainTo(desired, transition.getTo(), states);
         if (Iterables.size(transitions) > 0)
            return Iterables.concat(ImmutableSet.of(transition), transitions);
      }
      return ImmutableSet.<Transition> of();
   }

   @Override
   public void rebootNode(String id) {
      HttpRequest rebootUri = getNode(id).getActions().get(Instance.Action.REBOOT);
      if (rebootUri != null) {
         client.performAction(rebootUri);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException();
   }
}
