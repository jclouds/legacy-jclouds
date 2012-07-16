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
package org.jclouds.elb.features;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.elb.domain.InstanceHealth;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.internal.BaseELBApiLiveTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "InstanceApiLiveTest")
public class InstanceApiLiveTest extends BaseELBApiLiveTest {

   private void checkInstanceState(InstanceHealth instanceState) {
      checkNotNull(instanceState.getDescription(), "Description cannot be null for InstanceState");
      checkNotNull(instanceState.getInstanceId(), "InstanceId cannot be null for InstanceState");
      checkNotNull(instanceState.getReasonCode(),
               "While ReasonCode can be null for InstanceState, its Optional wrapper cannot");
      checkNotNull(instanceState.getState(), "State cannot be null for InstanceState");
   }

   @Test
   protected void testListInstanceStates() {
      for (LoadBalancer loadBalancer : Iterables.concat(context.getApi().getLoadBalancerApi().list())) {
         Set<InstanceHealth> response = api().getHealthOfInstancesOfLoadBalancer(loadBalancer.getName());

         for (InstanceHealth instanceState : response) {
            checkInstanceState(instanceState);
         }

         if (response.size() > 0) {
            InstanceHealth instanceState = response.iterator().next();
            Assert.assertEquals(
                     ImmutableSet.of(api().getHealthOfInstancesOfLoadBalancer(ImmutableSet.of(instanceState.getInstanceId()),
                              loadBalancer.getName())), instanceState);
         }
      }

   }

   protected InstanceApi api() {
      return context.getApi().getInstanceApi();
   }
}
