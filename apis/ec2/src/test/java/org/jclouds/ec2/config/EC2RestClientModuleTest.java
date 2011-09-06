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
package org.jclouds.ec2.config;

import static org.easymock.classextension.EasyMock.*;

import org.easymock.classextension.IMocksControl;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionClient;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A test for {@link EC2RestClientModule}.
 *
 * @author Eric Pabst (pabstec@familysearch.org)
 */
public class EC2RestClientModuleTest {
   @Test
   public void testDescribeAvailabilityZonesInRegion_BestEffort() {
      IMocksControl control = createControl();
      EC2Client client = control.createMock(EC2Client.class);
      AvailabilityZoneAndRegionClient regionClient = control.createMock(AvailabilityZoneAndRegionClient.class);
      AvailabilityZoneInfo info1 = control.createMock(AvailabilityZoneInfo.class);
      AvailabilityZoneInfo info2 = control.createMock(AvailabilityZoneInfo.class);
      HttpCommand command = control.createMock(HttpCommand.class);
      HttpResponseException exception = new HttpResponseException("Error: Unable to tunnel through proxy: ...", command, null);

      expect(client.getAvailabilityZoneAndRegionServices()).andStubReturn(regionClient);
      expect(regionClient.describeAvailabilityZonesInRegion("accessibleRegion1")).andReturn(Collections.singleton(info1));
      expect(regionClient.describeAvailabilityZonesInRegion("inaccessibleRegion")).andThrow(exception);
      expect(regionClient.describeAvailabilityZonesInRegion("accessibleRegion2")).andReturn(Collections.singleton(info2));
      expect(info1.getZone()).andStubReturn("zone1");
      expect(info2.getZone()).andStubReturn("zone2");

      Map<String, URI> regions = new LinkedHashMap<String, URI>();
      regions.put("accessibleRegion1", null);
      regions.put("inaccessibleRegion", null);
      regions.put("accessibleRegion2", null);
      control.replay();

      Map<String,String> expectedResult = new HashMap<String,String>();
      expectedResult.put("zone1", "accessibleRegion1");
      expectedResult.put("zone2", "accessibleRegion2");

      EC2RestClientModule.RegionIdToZoneId regionIdToZoneId = new EC2RestClientModule.RegionIdToZoneId(client, regions);
      assertEquals(regionIdToZoneId.get(), expectedResult);
      control.verify();
   }

   @Test
   public void testDescribeAvailabilityZonesInRegion_RethrowIfNoneFound() {
      IMocksControl control = createControl();
      EC2Client client = control.createMock(EC2Client.class);
      AvailabilityZoneAndRegionClient regionClient = control.createMock(AvailabilityZoneAndRegionClient.class);
      HttpCommand command = control.createMock(HttpCommand.class);
      HttpResponseException exception = new HttpResponseException("Error: Unable to tunnel through proxy: ...", command, null);

      expect(client.getAvailabilityZoneAndRegionServices()).andStubReturn(regionClient);
      expect(regionClient.describeAvailabilityZonesInRegion("inaccessibleRegion")).andThrow(exception);

      Map<String, URI> regions = new LinkedHashMap<String, URI>();
      regions.put("inaccessibleRegion", null);
      control.replay();

      EC2RestClientModule.RegionIdToZoneId regionIdToZoneId = new EC2RestClientModule.RegionIdToZoneId(client, regions);
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
      expect(regionClient.describeAvailabilityZonesInRegion("emptyRegion")).andReturn(Collections.<AvailabilityZoneInfo>emptySet());

      Map<String, URI> regions = new LinkedHashMap<String, URI>();
      regions.put("emptyRegion", null);
      control.replay();

      EC2RestClientModule.RegionIdToZoneId regionIdToZoneId = new EC2RestClientModule.RegionIdToZoneId(client, regions);
      assertEquals(regionIdToZoneId.get(), Collections.<String, String>emptyMap());
      control.verify();
   }
}
