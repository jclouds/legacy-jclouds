/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.aws.ec2.util.EC2Utils.GetRegionFromLocation;
import org.jclouds.aws.elb.ELBClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.LoadBalanceNodesStrategy;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class EC2LoadBalanceNodesStrategy implements LoadBalanceNodesStrategy {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final ELBClient elbClient;
   protected final GetRegionFromLocation getRegionFromLocation;

   @Inject
   protected EC2LoadBalanceNodesStrategy(ELBClient elbClient,
            GetRegionFromLocation getRegionFromLocation) {
      this.elbClient = elbClient;
      this.getRegionFromLocation = getRegionFromLocation;
   }

   @Override
   public String execute(Location location, String name, String protocol, int loadBalancerPort,
            int instancePort, Set<String> instanceIds) {
      String region = getRegionFromLocation.apply(location);
      String dnsName = new String();
      
      dnsName = elbClient.createLoadBalancerInRegion(region, name, protocol, loadBalancerPort,
               instancePort, EC2Utils.getAvailabilityZonesForRegion(region));

      List<String> instanceIdlist = new ArrayList<String>(instanceIds);
      String[] instanceIdArray = new String[instanceIdlist.size()];
      for (int i = 0; i < instanceIdlist.size(); i++) {
         instanceIdArray[i] = instanceIdlist.get(i);
      }

      Set<String> registeredInstanceIds = elbClient.registerInstancesWithLoadBalancerInRegion(
               region, name, instanceIdArray);

      // deregister instances
      boolean changed = registeredInstanceIds.removeAll(instanceIds);
      if (changed) {
         List<String> list = new ArrayList<String>(registeredInstanceIds);
         instanceIdArray = new String[list.size()];
         for (int i = 0; i < list.size(); i++) {
            instanceIdArray[i] = list.get(i);
         }
         if (instanceIdArray.length > 0)
            elbClient.deregisterInstancesWithLoadBalancerInRegion(region, name, instanceIdArray);
      }

      return dnsName;
   }
}