/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.softlayer.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.softlayer.domain.Address;
import org.jclouds.softlayer.domain.Datacenter;
import org.jclouds.softlayer.domain.Region;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Tests behavior of {@code DatacenterClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class DatacenterClientLiveTest extends BaseSoftLayerClientLiveTest {

   @Test
   public void testListDatacenters() {
      Set<Datacenter> response = api().listDatacenters();
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Datacenter vg : response) {
         Datacenter newDetails = api().getDatacenter(vg.getId());
         assertEquals(vg.getId(), newDetails.getId());
         checkDatacenter(newDetails);
      }
   }

   @Test
   public void testListDatacentersContent() {
      Builder<Datacenter> builder = ImmutableSet.builder();
      builder.add(Datacenter.builder().id(265592).name("ams01").longName("Amsterdam 1").build());
      builder.add(Datacenter.builder().id(3).name("dal01").longName("Dallas").build());
      builder.add(Datacenter.builder().id(18171).name("sea01").longName("Seattle").build());
      builder.add(Datacenter.builder().id(168642).name("sjc01").longName("San Jose 1").build());
      builder.add(Datacenter.builder().id(2).name("dal00").longName("Corporate HQ").build());
      builder.add(Datacenter.builder().id(37473).name("wdc01").longName("Washington, DC").build());
      builder.add(Datacenter.builder().id(154770).name("dal02").longName("Dallas 2").build());
      builder.add(Datacenter.builder().id(138124).name("dal05").longName("Dallas 5").build());
      builder.add(Datacenter.builder().id(167093).name("hou01").longName("Houston 1").build());
      builder.add(Datacenter.builder().id(167094).name("lon01").longName("London 1").build());
      builder.add(Datacenter.builder().id(167092).name("dal04").longName("Dallas 4").build());
      builder.add(Datacenter.builder().id(224092).name("sng01").longName("Singapore 1").build());
      builder.add(Datacenter.builder().id(142775).name("hou02").longName("Houston 2").build());
      builder.add(Datacenter.builder().id(142776).name("dal07").longName("Dallas 7").build());
      builder.add(Datacenter.builder().id(154820).name("dal06").longName("Dallas 6").build());
      Set<Datacenter> response = api().listDatacenters();
      Set<Datacenter> expected = builder.build();

      assertEquals(response.size(),expected.size());
      assertTrue(response.containsAll(expected));

      for(Datacenter datacenter: response) {
         Address address = datacenter.getLocationAddress();
         if(address!=null) checkAddress(address);
      }
   }

   private DatacenterClient api() {
      return api.getDatacenterClient();
   }

   private void checkDatacenter(Datacenter dc) {
      assert dc.getId() > 0 : dc;
      assert dc.getName() != null : dc;
      assert dc.getLongName() != null : dc;
      for( Region region: dc.getRegions()) checkRegion(region);
   }

   private void checkRegion(Region region) {
      assert !region.getDescription().isEmpty() : region;
      assert !region.getKeyname().isEmpty() : region;
   }

   private void checkAddress(Address address) {
      assert address.getId() >0 : address;
      assert address.getCountry() != null : address;
   }
}
