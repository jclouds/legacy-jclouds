/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.checkNotEmpty;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.config.EC2ComputeServiceContextModule.GetRegionFromLocation;
import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.domain.RegionNameAndIngressRules;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.LoadBalancerStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class EC2ComputeService extends BaseComputeService {
   private final EC2Client ec2Client;
   private final GetRegionFromLocation getRegionFromLocation;
   private final Map<RegionAndName, KeyPair> credentialsMap;
   private final Map<RegionAndName, String> securityGroupMap;

   @Inject
   protected EC2ComputeService(ComputeServiceContext context,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes,
            Provider<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy,
            RunNodesAndAddToSetStrategy runNodesAndAddToSetStrategy,
            RebootNodeStrategy rebootNodeStrategy, DestroyNodeStrategy destroyNodeStrategy,
            LoadBalancerStrategy loadBalancerStrategy,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<TemplateOptions> templateOptionsProvider, ComputeUtils utils,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, EC2Client ec2Client,
            GetRegionFromLocation getRegionFromLocation,
            Map<RegionAndName, KeyPair> credentialsMap, Map<RegionAndName, String> securityGroupMap) {
      super(context, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
               loadBalancerStrategy, templateBuilderProvider, templateOptionsProvider, utils, executor);
      this.ec2Client = ec2Client;
      this.getRegionFromLocation = getRegionFromLocation;
      this.credentialsMap = credentialsMap;
      this.securityGroupMap = securityGroupMap;
   }

   private void deleteSecurityGroup(String region, String tag) {
      checkNotEmpty(tag, "tag");
      String group = "jclouds#" + tag;
      if (ec2Client.getSecurityGroupServices().describeSecurityGroupsInRegion(region, group).size() > 0) {
         logger.debug(">> deleting securityGroup(%s)", group);
         ec2Client.getSecurityGroupServices().deleteSecurityGroupInRegion(region, group);
         // TODO: test this clear happens
         securityGroupMap.remove(new RegionNameAndIngressRules(region, tag, null, false));
         logger.debug("<< deleted securityGroup(%s)", group);
      }
   }

   private void deleteKeyPair(String region, String tag) {
      for (KeyPair keyPair : ec2Client.getKeyPairServices().describeKeyPairsInRegion(region)) {
         if (keyPair.getKeyName().matches("jclouds#" + tag + "-[0-9]+")) {
            logger.debug(">> deleting keyPair(%s)", keyPair.getKeyName());
            ec2Client.getKeyPairServices().deleteKeyPairInRegion(region, keyPair.getKeyName());
            // TODO: test this clear happens
            credentialsMap.remove(new RegionAndName(region, keyPair.getKeyName()));
            logger.debug("<< deleted keyPair(%s)", keyPair.getKeyName());
         }
      }
   }

   /**
    * like {@link BaseComputeService#destroyNodesMatching} except that this will clean implicit
    * keypairs and security groups.
    */
   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      Set<? extends NodeMetadata> deadOnes = super.destroyNodesMatching(filter);
      Map<String, String> regionTags = Maps.newHashMap();
      for (NodeMetadata nodeMetadata : deadOnes) {
         if (nodeMetadata.getTag() != null)
            regionTags.put(getRegionFromLocation.apply(nodeMetadata.getLocation()), nodeMetadata
                     .getTag());
      }
      for (Entry<String, String> regionTag : regionTags.entrySet()) {
         deleteKeyPair(regionTag.getKey(), regionTag.getValue());
         deleteSecurityGroup(regionTag.getKey(), regionTag.getValue());
      }
      return deadOnes;

   }
   
    @Override
    public String loadBalanceNodesMatching(String loadBalancerName,
            String protocol, Integer loadBalancerPort, Integer instancePort,
            Predicate<NodeMetadata> filter)
    {
        checkNotNull(loadBalancerName, "loadBalancerName");
        checkNotNull(protocol, "protocol");
        checkArgument(protocol.toUpperCase().equals("HTTP")
                || protocol.toUpperCase().equals("TCP"),
                "Acceptable values for protocol are HTTP or TCP");
        checkNotNull(loadBalancerPort, "loadBalancerPort");
        checkNotNull(instancePort, "instancePort");

        Location location = null;
        Set<String> ids = new HashSet<String>();
        for (final NodeMetadata node : Iterables.filter(super
                .listNodesDetailsMatching(NodePredicates.all()), Predicates
                .and(filter, Predicates.not(NodePredicates.TERMINATED))))
        {
            ids.add(node.getId());
            location = node.getLocation();
        }
        logger.debug(">> creating load balancer (%s)", loadBalancerName);
        String dnsName = loadBalancerStrategy
                .execute(location, loadBalancerName, protocol,
                        loadBalancerPort, instancePort, ids);
        logger.debug("<< created load balancer (%s) DNS (%s)",
                loadBalancerName, dnsName);
        return dnsName;
    }
   
   

    @Override
    public void deleteLoadBalancer(String loadBalancerName,
            Predicate<NodeMetadata> filter)
    {

        Location location = Iterables.filter(
                super.listNodesDetailsMatching(NodePredicates.all()),
                Predicates.and(filter, Predicates
                        .not(NodePredicates.TERMINATED))).iterator().next()
                .getLocation();
        ec2Client.getElasticLoadBalancerServices().deleteLoadBalancer(
                getRegionFromLocation.apply(location), loadBalancerName);
    }

  /**
    * returns template options, except of type {@link EC2TemplateOptions}.
    */
   @Override
   public EC2TemplateOptions templateOptions() {
      return EC2TemplateOptions.class.cast(super.templateOptions());
   }
}