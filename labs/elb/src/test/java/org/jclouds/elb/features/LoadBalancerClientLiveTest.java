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

import org.jclouds.collect.PaginatedSet;
import org.jclouds.elb.domain.ListenerWithPolicies;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.internal.BaseELBClientLiveTest;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "LoadBalancerClientLiveTest")
public class LoadBalancerClientLiveTest extends BaseELBClientLiveTest {

   private void checkLoadBalancer(LoadBalancer loadBalancer) {
      checkNotNull(loadBalancer.getName(), "While Name can be null for a LoadBalancer, its Optional wrapper cannot.");
      checkNotNull(loadBalancer.getCreatedTime(), "CreatedTime cannot be null for a LoadBalancer.");
      checkNotNull(loadBalancer.getDnsName(), "DnsName cannot be null for a LoadBalancer.");
      checkNotNull(loadBalancer.getScheme(),
               "While Scheme can be null for a LoadBalancer, its Optional wrapper cannot.");
      checkNotNull(loadBalancer.getVPCId(), "While VPCId can be null for a LoadBalancer, its Optional wrapper cannot.");
   }

   private void checkListener(ListenerWithPolicies listener) {
      checkNotNull(listener.getPolicyNames(), "While PolicyNames can be empty, it cannot be null.");
      assert listener.getInstancePort() > 0 : "InstancePort must be positive";
      checkNotNull(listener.getInstanceProtocol(), "InstanceProtocol cannot be null");
      assert listener.getPort() > 0 : "Port must be positive";
      checkNotNull(listener.getProtocol(), "Protocol cannot be null");
      checkNotNull(listener.getSSLCertificateId(),
               "While SSLCertificateId can be null for a ListenerWithPolicies, its Optional wrapper cannot.");
   }

   @Test
   protected void testDescribeLoadBalancers() {
      PaginatedSet<LoadBalancer> response = client().list();

      for (LoadBalancer loadBalancer : response) {
         checkLoadBalancer(loadBalancer);
         for (ListenerWithPolicies listener : loadBalancer.getListeners()) {
            checkListener(listener);
         }
      }

      if (response.size() > 0) {
         LoadBalancer loadBalancer = response.iterator().next();
         Assert.assertEquals(client().get(loadBalancer.getName()), loadBalancer);
      }

      // Test with a Marker, even if it's null
      response = client().list(ListLoadBalancersOptions.Builder.marker(response.getNextMarker()));
      for (LoadBalancer loadBalancer : response) {
         checkLoadBalancer(loadBalancer);
      }
   }

   protected LoadBalancerClient client() {
      return context.getApi().getLoadBalancerClientForRegion(null);
   }
}
