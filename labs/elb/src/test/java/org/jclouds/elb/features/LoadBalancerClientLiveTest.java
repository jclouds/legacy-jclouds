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
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.elb.domain.ListenerWithPolicies;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.internal.BaseELBClientLiveTest;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "LoadBalancerClientLiveTest")
public class LoadBalancerClientLiveTest extends BaseELBClientLiveTest {

   private void checkLoadBalancer(LoadBalancer loadBalancer) {
      checkNotNull(loadBalancer.getName(), "While Name can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);
      checkNotNull(loadBalancer.getCreatedTime(), "CreatedTime cannot be null for a LoadBalancer: %s", loadBalancer);
      checkNotNull(loadBalancer.getDnsName(), "DnsName cannot be null for a LoadBalancer: %s", loadBalancer);
      checkNotNull(loadBalancer.getHealthCheck(), "HealthCheck cannot be null for a LoadBalancer: %s", loadBalancer);
      checkState(loadBalancer.getAvailabilityZones().size() > 0, "AvailabilityZones must have at least one zone: %s", loadBalancer);
      checkNotNull(loadBalancer.getInstanceIds(), "While InstanceIds can be empty, it cannot be null: %s", loadBalancer);
      checkNotNull(loadBalancer.getSourceSecurityGroup(),
              "While SourceSecurityGroup can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);

      // VPC
      checkNotNull(loadBalancer.getVPCId(), "While VPCId can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);
      checkNotNull(loadBalancer.getScheme(),
              "While Scheme can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);
      checkNotNull(loadBalancer.getSecurityGroups(), "While SecurityGroups can be empty, it cannot be null: %s", loadBalancer);
      checkNotNull(loadBalancer.getSubnets(), "While Subnets can be empty, it cannot be null: %s", loadBalancer);

      // Route 53
      checkNotNull(loadBalancer.getHostedZoneId(), "While HostedZoneId can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);
      checkNotNull(loadBalancer.getHostedZoneName(), "While HostedZoneName can be null for a LoadBalancer, its Optional wrapper cannot: %s", loadBalancer);

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
      PaginatedIterable<LoadBalancer> response = client().list();

      for (LoadBalancer loadBalancer : response) {
         checkLoadBalancer(loadBalancer);
         for (ListenerWithPolicies listener : loadBalancer.getListeners()) {
            checkListener(listener);
         }
      }

      if (Iterables.size(response) > 0) {
         LoadBalancer loadBalancer = response.iterator().next();
         Assert.assertEquals(client().get(loadBalancer.getName()), loadBalancer);
      }

      // Test with a Marker, even if it's null
      response = client().list(ListLoadBalancersOptions.Builder.afterMarker(response.getNextMarker()));
      for (LoadBalancer loadBalancer : response) {
         checkLoadBalancer(loadBalancer);
      }
   }

   protected LoadBalancerClient client() {
      return context.getApi().getLoadBalancerClient();
   }
}
