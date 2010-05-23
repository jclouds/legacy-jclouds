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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.aws.ec2.services.ElasticLoadBalancerClient;
import org.jclouds.compute.BaseLoadBalancerServiceLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * @author Lili Nadar
 */
@Test(groups = "live", sequential = true, testName = "ec2.EC2LoadBalancerServiceLiveTest")
public class EC2LoadBalancerServiceLiveTest extends BaseLoadBalancerServiceLiveTest {

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "ec2";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @Override
   protected void validateNodesInLoadBalancer() {
      // TODO create a LoadBalancer object and an appropriate list method so that this
      // does not have to be EC2 specific code
      ElasticLoadBalancerClient elbClient = EC2Client.class.cast(
               context.getProviderSpecificContext().getApi()).getElasticLoadBalancerServices();

      Set<String> instanceIds = new HashSet<String>();
      for (NodeMetadata node : nodes) {
         instanceIds.add(node.getProviderId());
      }
      Set<ElasticLoadBalancer> elbs = elbClient
               .describeLoadBalancersInRegion(Region.US_EAST_1, tag);
      assertNotNull(elbs);
      ElasticLoadBalancer elb = elbs.iterator().next();
      assertEquals(elb.getInstanceIds(), instanceIds);
   }

}
