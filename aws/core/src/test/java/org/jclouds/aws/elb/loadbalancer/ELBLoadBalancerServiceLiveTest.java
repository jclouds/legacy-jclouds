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

package org.jclouds.aws.elb.loadbalancer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.elb.ELBAsyncClient;
import org.jclouds.aws.elb.ELBClient;
import org.jclouds.aws.elb.domain.LoadBalancer;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.loadbalancer.BaseLoadBalancerServiceLiveTest;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Lili Nadar
 */
@Test(groups = "live", sequential = true)
public class ELBLoadBalancerServiceLiveTest extends BaseLoadBalancerServiceLiveTest {

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      provider = "elb";
      computeProvider = "ec2";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Override
   protected void validateNodesInLoadBalancer() {
      RestContext<ELBClient, ELBAsyncClient> elbContext = context.getProviderSpecificContext();
      // TODO create a LoadBalancer object and an appropriate list method so that this
      // does not have to be EC2 specific code
      ELBClient elbClient = elbContext.getApi();

      Set<String> instanceIds = new HashSet<String>();
      for (NodeMetadata node : nodes) {
         instanceIds.add(node.getProviderId());
      }
      Set<LoadBalancer> elbs = elbClient.describeLoadBalancersInRegion(Region.US_EAST_1);
      assertNotNull(elbs);
      for (LoadBalancer elb : elbs) {
         if (elb.getName().equals(tag))
            assertEquals(elb.getInstanceIds(), instanceIds);
      }
   }
}
