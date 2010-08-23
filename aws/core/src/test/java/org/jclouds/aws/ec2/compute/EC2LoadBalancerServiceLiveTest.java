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

package org.jclouds.aws.ec2.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.elb.ELBAsyncClient;
import org.jclouds.aws.elb.ELBClient;
import org.jclouds.aws.elb.domain.LoadBalancer;
import org.jclouds.compute.BaseLoadBalancerServiceLiveTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Lili Nadar
 */
@Test(groups = "live", sequential = true, testName = "ec2.EC2LoadBalancerServiceLiveTest")
public class EC2LoadBalancerServiceLiveTest extends BaseLoadBalancerServiceLiveTest {

   private RestContext<ELBClient, ELBAsyncClient> elbContext;

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      provider = "ec2";
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   @BeforeGroups(groups = { "live" })
   public void setupELBClient()  {
      elbContext = new RestContextFactory().createContext("elb", identity, credential,
               ImmutableSet.of(new Log4JLoggingModule()));
   }

   @AfterGroups(groups = { "live" })
   public void tearDownELBClient() {
      if (elbContext != null)
         elbContext.close();
   }

   @Override
   protected void validateNodesInLoadBalancer() {
      // TODO create a LoadBalancer object and an appropriate list method so that this
      // does not have to be EC2 specific code
      ELBClient elbClient = elbContext.getApi();

      Set<String> instanceIds = new HashSet<String>();
      for (NodeMetadata node : nodes) {
         instanceIds.add(node.getProviderId());
      }
      Set<LoadBalancer> elbs = elbClient.describeLoadBalancersInRegion(Region.US_EAST_1);
      assertNotNull(elbs);
      for(LoadBalancer elb:elbs)
      {
          if(elb.getName().equals(tag))
              assertEquals(elb.getInstanceIds(), instanceIds);
      }
   }
}
