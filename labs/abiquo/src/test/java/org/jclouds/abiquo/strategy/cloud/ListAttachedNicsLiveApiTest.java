/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.strategy.cloud;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.network.ExternalIp;
import org.jclouds.abiquo.domain.network.Ip;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.domain.network.UnmanagedNetwork;
import org.jclouds.abiquo.predicates.network.IpPredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.jclouds.abiquo.strategy.cloud.ListAttachedNics;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * Live tests for the {@link ListAttachedNics} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListAttachedNicsLiveApiTest")
public class ListAttachedNicsLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListAttachedNics strategy;

   private PrivateIp privateIp;

   private ExternalIp externalIp;

   private PublicIp publicIp;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListAttachedNics.class);

      privateIp = env.privateNetwork.listUnusedIps().get(0);
      assertNotNull(privateIp);

      externalIp = env.externalNetwork.listUnusedIps().get(0);
      assertNotNull(externalIp);

      publicIp = env.virtualDatacenter.listAvailablePublicIps().get(0);
      env.virtualDatacenter.purchasePublicIp(publicIp);
      publicIp = env.virtualDatacenter.findPurchasedPublicIp(IpPredicates.<PublicIp> address(publicIp.getIp()));
      assertNotNull(publicIp);

      env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(privateIp, externalIp, publicIp),
            Lists.<UnmanagedNetwork> newArrayList(env.unmanagedNetwork));
   }

   @AfterClass(groups = "api")
   protected void tearDownStrategy() {
      env.virtualMachine.setNics(Lists.<Ip<?, ?>> newArrayList(privateIp));
      String address = publicIp.getIp();
      env.virtualDatacenter.releasePublicIp(publicIp);
      assertNull(env.virtualDatacenter.findPurchasedPublicIp(IpPredicates.<PublicIp> address(address)));
   }

   public void testExecute() {
      Iterable<Ip<?, ?>> vapps = strategy.execute(env.virtualMachine);
      assertNotNull(vapps);
      assertEquals(4, size(vapps));
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<Ip<?, ?>> vapps = strategy.execute(env.virtualMachine, IpPredicates.address("UNEXISTING"));
      assertNotNull(vapps);
      assertEquals(size(vapps), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<Ip<?, ?>> vapps = strategy.execute(env.virtualMachine, IpPredicates.address(publicIp.getIp()));
      assertNotNull(vapps);
      assertEquals(size(vapps), 1);
   }
}
