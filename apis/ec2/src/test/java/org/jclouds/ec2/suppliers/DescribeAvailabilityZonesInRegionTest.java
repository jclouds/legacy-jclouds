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
package org.jclouds.ec2.suppliers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createControl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.easymock.classextension.IMocksControl;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * A test for {@link DescribeAvailabilityZonesInRegion}.
 * 
 * @author Eric Pabst (pabstec@familysearch.org)
 */
public class DescribeAvailabilityZonesInRegionTest {
   @Test
   public void testDescribeAvailabilityZonesInRegion_BestEffort() {
      IMocksControl control = createControl();
      EC2Client client = control.createMock(EC2Client.class);
      AvailabilityZoneAndRegionClient regionClient = control.createMock(AvailabilityZoneAndRegionClient.class);
      AvailabilityZoneInfo info1 = control.createMock(AvailabilityZoneInfo.class);
      AvailabilityZoneInfo info2 = control.createMock(AvailabilityZoneInfo.class);
      HttpCommand command = control.createMock(HttpCommand.class);
      HttpResponseException exception = new HttpResponseException("Error: Unable to tunnel through proxy: ...",
               command, null);

      expect(client.getAvailabilityZoneAndRegionServices()).andStubReturn(regionClient);
      expect(regionClient.describeAvailabilityZonesInRegion("accessibleRegion1")).andReturn(
               Collections.singleton(info1));
      expect(regionClient.describeAvailabilityZonesInRegion("inaccessibleRegion")).andThrow(exception);
      expect(regionClient.describeAvailabilityZonesInRegion("accessibleRegion2")).andReturn(
               Collections.singleton(info2));
      expect(info1.getZone()).andStubReturn("zone1");
      expect(info2.getZone()).andStubReturn("zone2");

      Set<String> regions = ImmutableSet.of("accessibleRegion1", "inaccessibleRegion", "accessibleRegion2");
      control.replay();

      Map<String, Set<String>> expectedResult = ImmutableMap.<String, Set<String>> builder().put("accessibleRegion1",
               ImmutableSet.of("zone1")).put("accessibleRegion2", ImmutableSet.of("zone2")).build();

      DescribeAvailabilityZonesInRegion regionIdToZoneId = new DescribeAvailabilityZonesInRegion(client, Suppliers
               .ofInstance(regions));
      assertEquals(Maps.transformValues(regionIdToZoneId.get(), Suppliers.<Set<String>> supplierFunction()),
               expectedResult);
      control.verify();
   }

   @Test
   public void testDescribeAvailabilityZonesInRegion_RethrowIfNoneFound() {
      IMocksControl control = createControl();
      EC2Client client = control.createMock(EC2Client.class);
      AvailabilityZoneAndRegionClient regionClient = control.createMock(AvailabilityZoneAndRegionClient.class);
      HttpCommand command = control.createMock(HttpCommand.class);
      HttpResponseException exception = new HttpResponseException("Error: Unable to tunnel through proxy: ...",
               command, null);

      expect(client.getAvailabilityZoneAndRegionServices()).andStubReturn(regionClient);
      expect(regionClient.describeAvailabilityZonesInRegion("inaccessibleRegion")).andThrow(exception);

      Set<String> regions = ImmutableSet.of("inaccessibleRegion");
      control.replay();

      DescribeAvailabilityZonesInRegion regionIdToZoneId = new DescribeAvailabilityZonesInRegion(client, Suppliers
               .ofInstance(regions));
      try {
         regionIdToZoneId.get();
         fail("expected exception");
      } catch (HttpResponseException e) {
         assertEquals(e, exception);
      }
      control.verify();
   }

   @Test
   public void testDescribeAvailabilityZonesInRegion_NoZones() {
      IMocksControl control = createControl();
      EC2Client client = control.createMock(EC2Client.class);
      AvailabilityZoneAndRegionClient regionClient = control.createMock(AvailabilityZoneAndRegionClient.class);

      expect(client.getAvailabilityZoneAndRegionServices()).andStubReturn(regionClient);
      expect(regionClient.describeAvailabilityZonesInRegion("emptyRegion")).andReturn(
               Collections.<AvailabilityZoneInfo> emptySet());

      Set<String> regions = ImmutableSet.of("emptyRegion");
      control.replay();

      DescribeAvailabilityZonesInRegion regionIdToZoneId = new DescribeAvailabilityZonesInRegion(client, Suppliers
               .ofInstance(regions));
      assertEquals(regionIdToZoneId.get(), Collections.<String, String> emptyMap());
      control.verify();
   }
}
