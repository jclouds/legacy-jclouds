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

import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

/**
 * Unit tests for the {@link DatacenterToLocation} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "DatacenterToLocationTest")
public class DatacenterToLocationTest {
   @SuppressWarnings("unchecked")
   public void testDatacenterToLocation() {
      RestContext<AbiquoApi, AbiquoAsyncApi> context = EasyMock.createMock(RestContext.class);
      DatacenterToLocation function = new DatacenterToLocation();

      Datacenter datacenter = Datacenter.builder(context).name("dc").location("New York").build();
      datacenter.unwrap().setId(5);
      Location location = function.apply(datacenter);

      assertEquals(location.getId(), "5");
      assertEquals(location.getScope(), LocationScope.REGION);
   }
}
