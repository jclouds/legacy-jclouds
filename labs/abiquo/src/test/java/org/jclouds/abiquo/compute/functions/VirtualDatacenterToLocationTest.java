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

package org.jclouds.abiquo.compute.functions;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * Unit tests for the {@link VirtualDatacenterToLocation} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "VirtualDatacenterToLocationTest")
public class VirtualDatacenterToLocationTest {
   public void testVirtualDatacenterToLocation() {
      Function<Datacenter, Location> dcToLocation = mockDatacenterToLocation();
      Supplier<Map<Integer, Datacenter>> regionMap = mockRegionMap();
      VirtualDatacenterToLocation function = new VirtualDatacenterToLocation(dcToLocation, regionMap);

      VirtualDatacenter vdc = mockVirtualDatacenter();

      Location location = function.apply(vdc);

      verify(regionMap);
      verify(dcToLocation);

      assertEquals(location.getId(), "5");
      assertEquals(location.getScope(), LocationScope.ZONE);
   }

   @SuppressWarnings("unchecked")
   private static VirtualDatacenter mockVirtualDatacenter() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      Datacenter datacenter = EasyMock.createMock(Datacenter.class);
      Enterprise enterprise = EasyMock.createMock(Enterprise.class);
      PrivateNetwork network = EasyMock.createMock(PrivateNetwork.class);

      VirtualDatacenter vdc = VirtualDatacenter.builder(context, datacenter, enterprise) //
            .network(network) //
            .name("mock").build();
      vdc.unwrap().setId(5);

      return vdc;
   }

   @SuppressWarnings("unchecked")
   private static Function<Datacenter, Location> mockDatacenterToLocation() {
      Function<Datacenter, Location> mock = EasyMock.createMock(Function.class);
      expect(mock.apply(anyObject(Datacenter.class))).andReturn(null);
      replay(mock);
      return mock;
   }

   @SuppressWarnings("unchecked")
   private static Supplier<Map<Integer, Datacenter>> mockRegionMap() {
      Supplier<Map<Integer, Datacenter>> mock = EasyMock.createMock(Supplier.class);
      expect(mock.get()).andReturn(Collections.EMPTY_MAP);
      replay(mock);
      return mock;
   }
}
