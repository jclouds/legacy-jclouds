/*
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

package org.jclouds.googlecompute.compute.strategy;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.jclouds.Constants;
import org.jclouds.compute.config.CustomizationResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.strategy.CreateNodeWithGroupEncodedIntoName;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.googlecompute.GoogleComputeApi;
import org.jclouds.googlecompute.compute.options.GoogleComputeTemplateOptions;
import org.jclouds.googlecompute.config.UserProject;
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.FirewallOptions;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.of;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecompute.GoogleComputeConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.util.Predicates2.retry;

/**
 * @author David Alves
 */
public class ApplyTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet extends
        CreateNodesWithGroupEncodedIntoNameThenAddToSet {

   public static final String EXTERIOR_RANGE = "0.0.0.0/0";
   public static final String DEFAULT_INTERNAL_NETWORK_RANGE = "10.0.0.0/8";

   private final GoogleComputeApi api;
   private final Supplier<String> userProject;
   private final Predicate<AtomicReference<Operation>> operationDonePredicate;
   private final long operationCompleteCheckInterval;
   private final long operationCompleteCheckTimeout;

   @Inject
   protected ApplyTemplateOptionsCreateNodesWithGroupEncodedIntoNameThenAddToSet(
           CreateNodeWithGroupEncodedIntoName addNodeWithGroupStrategy,
           ListNodesStrategy listNodesStrategy,
           GroupNamingConvention.Factory namingConvention,
           @Named(Constants.PROPERTY_USER_THREADS)
           ListeningExecutorService userExecutor,
           CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory
                   customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory,
           GoogleComputeApi api,
           @UserProject Supplier<String> userProject,
           Predicate<AtomicReference<Operation>> operationDonePredicate,
           @Named(OPERATION_COMPLETE_INTERVAL) Long operationCompleteCheckInterval,
           @Named(OPERATION_COMPLETE_TIMEOUT) Long operationCompleteCheckTimeout) {
      super(addNodeWithGroupStrategy, listNodesStrategy, namingConvention, userExecutor,
              customizeNodeAndAddToGoodMapOrPutExceptionIntoBadMapFactory);

      this.api = checkNotNull(api, "google compute api");
      this.userProject = checkNotNull(userProject, "user project name");
      this.operationCompleteCheckInterval = checkNotNull(operationCompleteCheckInterval,
              "operation completed check interval");
      this.operationCompleteCheckTimeout = checkNotNull(operationCompleteCheckTimeout,
              "operation completed check timeout");
      this.operationDonePredicate = operationDonePredicate;
   }

   @Override
   public Map<?, ListenableFuture<Void>> execute(String group, int count, Template template,
                                                 Set<NodeMetadata> goodNodes, Map<NodeMetadata, Exception> badNodes,
                                                 Multimap<NodeMetadata, CustomizationResponse> customizationResponses) {

      String sharedResourceName = namingConvention.create().sharedNameForGroup(group);
      Template mutableTemplate = template.clone();
      GoogleComputeTemplateOptions templateOptions = GoogleComputeTemplateOptions.class.cast(mutableTemplate
              .getOptions());
      assert template.getOptions().equals(templateOptions) : "options didn't clone properly";

      // get or create the network and create a firewall with the users configuration
      Network network = getOrCreateNetwork(templateOptions, sharedResourceName);
      createFirewall(templateOptions, network, sharedResourceName);
      templateOptions.network(network.getSelfLink());

      return super.execute(group, count, mutableTemplate, goodNodes, badNodes, customizationResponses);
   }

   /**
    * Try and find a network either previously created by jclouds or user defined.
    */
   private Network getOrCreateNetwork(GoogleComputeTemplateOptions templateOptions, String sharedResourceName) {

      String networkName = templateOptions.getNetworkName().isPresent() ? templateOptions.getNetworkName().get() :
              sharedResourceName;

      // check if the network was previously created (cache???)
      Network network = api.getNetworkApiForProject(userProject.get()).get(networkName);

      if (network != null) {
         return network;
      }

      if (network == null && templateOptions.getNetwork().isPresent()) {
         throw new IllegalStateException("user defined network does not exist: " + templateOptions.getNetwork().get());
      }

      AtomicReference<Operation> operation = new AtomicReference<Operation>(api.getNetworkApiForProject(userProject
              .get()).createInIPv4Range(sharedResourceName, DEFAULT_INTERNAL_NETWORK_RANGE));
      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);
      if (operation.get().getHttpError().isPresent()) {
         throw new IllegalStateException("Could not create network, operation failed" + operation);
      }

      return checkNotNull(api.getNetworkApiForProject(userProject.get()).get(sharedResourceName),
              "no network with name %s was found", sharedResourceName);

   }

   /**
    * Tries to find if a firewall already exists for this group, if not it creates one.
    *
    * @see org.jclouds.googlecompute.features.FirewallAsyncApi#patch(String, org.jclouds.googlecompute.options.FirewallOptions)
    */
   private void createFirewall(GoogleComputeTemplateOptions templateOptions, Network network,
                               String sharedResourceName) {

      Firewall firewall = api.getFirewallApiForProject(userProject.get()).get(sharedResourceName);

      if (firewall != null) {
         return;
      }

      ImmutableSet.Builder<Firewall.Rule> rules = ImmutableSet.builder();

      Firewall.Rule.Builder tcpRule = Firewall.Rule.builder();
      tcpRule.IPProtocol(Firewall.Rule.IPProtocol.TCP);
      Firewall.Rule.Builder udpRule = Firewall.Rule.builder();
      udpRule.IPProtocol(Firewall.Rule.IPProtocol.UDP);
      for (Integer port : templateOptions.getInboundPorts()) {
         tcpRule.addPort(port);
         udpRule.addPort(port);
      }
      rules.add(tcpRule.build());
      rules.add(udpRule.build());


      FirewallOptions options = new FirewallOptions()
              .name(sharedResourceName)
              .network(network.getSelfLink())
              .sourceTags(templateOptions.getTags())
              .allowedRules(rules.build())
              .sourceRanges(of(DEFAULT_INTERNAL_NETWORK_RANGE, EXTERIOR_RANGE));

      AtomicReference<Operation> operation = new AtomicReference<Operation>(api.getFirewallApiForProject(userProject
              .get()).createInNetwork(
              sharedResourceName,
              network.getSelfLink(),
              options));

      retry(operationDonePredicate, operationCompleteCheckTimeout, operationCompleteCheckInterval,
              MILLISECONDS).apply(operation);

      if (operation.get().getHttpError().isPresent()) {
         throw new IllegalStateException("Could not create firewall, operation failed" + operation.get());
      }
   }


}
