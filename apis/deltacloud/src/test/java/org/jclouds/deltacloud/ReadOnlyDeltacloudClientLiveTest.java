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
package org.jclouds.deltacloud;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.Instance.State;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.predicates.InstanceFinished;
import org.jclouds.deltacloud.predicates.InstanceRunning;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.net.HostAndPort;
import com.google.inject.Module;

/**
 * Tests behavior of {@code DeltacloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "ReadOnlyDeltacloudClientLiveTest")
public class ReadOnlyDeltacloudClientLiveTest extends BaseComputeServiceContextLiveTest {

   public ReadOnlyDeltacloudClientLiveTest() {
      provider = "deltacloud";
   }

   protected DeltacloudClient client;

   protected Predicate<HostAndPort> socketTester;
   protected ImmutableMap<State, Predicate<Instance>> stateChanges;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(DeltacloudApiMetadata.CONTEXT_TOKEN).getApi();
      socketTester = new RetryablePredicate<HostAndPort>(new InetSocketAddressConnect(), 180, 1, TimeUnit.SECONDS);
      stateChanges = ImmutableMap.<Instance.State, Predicate<Instance>> of(//
               Instance.State.RUNNING, new RetryablePredicate<Instance>(new InstanceRunning(client), 600, 1,
                        TimeUnit.SECONDS),//
               Instance.State.FINISH, new RetryablePredicate<Instance>(new InstanceFinished(client), 30, 1,
                        TimeUnit.SECONDS)//
               );
   }

   @Test
   public void testGetLinksContainsAll() throws Exception {
      Set<? extends DeltacloudCollection> links = client.getCollections();
      assertNotNull(links);
   }

   @Test
   public void testGetInstanceStatesCanGoFromStartToFinish() throws Exception {
      Multimap<Instance.State, ? extends Transition> states = client.getInstanceStates();
      assertNotNull(states);
      Iterable<Transition> toFinishFromStart = findChainTo(Instance.State.FINISH, Instance.State.START, states);
      assert Iterables.size(toFinishFromStart) > 0 : toFinishFromStart;
      Iterable<Transition> toRunningFromStart = findChainTo(Instance.State.RUNNING, Instance.State.START, states);
      assert Iterables.size(toRunningFromStart) > 0 : toRunningFromStart;
      Iterable<Transition> toFinishFromRunning = findChainTo(Instance.State.FINISH, Instance.State.RUNNING, states);
      assert Iterables.size(toFinishFromRunning) > 0 : toFinishFromRunning;
      assertEquals(ImmutableList.copyOf(Iterables.concat(toRunningFromStart, toFinishFromRunning)), ImmutableList
               .copyOf(toFinishFromStart));
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

   public void testListAndGetRealms() throws Exception {
      Set<? extends Realm> response = client.listRealms();
      assert null != response;
      long realmCount = response.size();
      assertTrue(realmCount >= 0);
      for (Realm realm : response) {
         Realm newDetails = client.getRealm(realm.getHref());
         assertEquals(realm, newDetails);
      }
   }

   public void testListAndGetImages() throws Exception {
      Set<? extends Image> response = client.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = client.getImage(image.getHref());
         assertEquals(image, newDetails);
      }
   }

   public void testListAndGetHardwareProfiles() throws Exception {
      Set<? extends HardwareProfile> response = client.listHardwareProfiles();
      assert null != response;
      long profileCount = response.size();
      assertTrue(profileCount >= 0);
      for (HardwareProfile profile : response) {
         HardwareProfile newDetails = client.getHardwareProfile(profile.getHref());
         assertEquals(profile, newDetails);
      }
   }

   public void testListAndGetInstances() throws Exception {
      Set<? extends Instance> response = client.listInstances();
      assert null != response;
      long instanceCount = response.size();
      assertTrue(instanceCount >= 0);
      for (Instance instance : response) {
         Instance newDetails = client.getInstance(instance.getHref());
         assertEquals(instance, newDetails);
      }
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
