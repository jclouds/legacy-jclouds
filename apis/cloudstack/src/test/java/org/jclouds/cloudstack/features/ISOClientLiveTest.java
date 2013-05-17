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
package org.jclouds.cloudstack.features;

import static org.jclouds.cloudstack.options.ListZonesOptions.Builder.available;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.ISOPermissions;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link ISOClient} and {@link ISOAsyncClient}
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = "live", singleThreaded = true, testName = "ISOClientLiveTest")
public class ISOClientLiveTest extends BaseCloudStackClientLiveTest {
    
   private static final String isoName = "jcloudsTestISO";
   private static final String url = System.getProperty("test.cloudstack.iso-url", "http://archive.ubuntu.com/ubuntu/dists/maverick/main/installer-i386/current/images/netboot/mini.iso");

   public void testListPublicISOs() throws Exception {
      Set<ISO> response = client.getISOClient().listISOs(ListISOsOptions.Builder.isPublic());
      assertNotNull(response);
      assertFalse(response.isEmpty());
      long isoCount = response.size();
      assertTrue(isoCount >= 0);

      for (ISO iso : response) {
         ISO query = client.getISOClient().getISO(iso.getId());
         assertEquals(query.getId(), iso.getId());
      }
   }

   public void testListISOPermissions() throws Exception {
      Set<ISO> response = client.getISOClient().listISOs(ListISOsOptions.Builder.isPublic());
      assertNotNull(response);
      assertFalse(response.isEmpty());
      long isoCount = response.size();
      assertTrue(isoCount >= 0);

      for (ISO iso : response) {
         ISOPermissions perms = client.getISOClient().listISOPermissions(iso.getId());
         assertNotNull(perms);
      }
   }
   
   public void testRegisterISO() throws Exception {
      Optional<OSType> guestOSTypeOptional = Iterables.tryFind(client.getGuestOSClient().listOSTypes(), Predicates.notNull());
      Optional<Zone> zoneOptional = Iterables.tryFind(client.getZoneClient().listZones(available(true)), Predicates.notNull());
      if(guestOSTypeOptional.isPresent() && zoneOptional.isPresent()) {
         String osTypeId = guestOSTypeOptional.get().getId();
         String zoneId = zoneOptional.get().getId();
         ISO iso = client.getISOClient().registerISO(isoName, "", url, zoneId, RegisterISOOptions.Builder.isPublic(true).osTypeId(osTypeId));
             assertNotNull(iso);
             assertNotNull(iso.getId());
             assertEquals(iso.getName(), isoName);
      } else {
         String skipMessage = String.format("Cannot register the iso with url: %s", url);
         if(zoneOptional.isPresent())
             skipMessage += " without a valid zone";
         else
             skipMessage += " without a valid guest OS type";
         throw new SkipException(skipMessage);
      }
   }

   @AfterClass
   @Override
   protected void tearDownContext() {
       Set<ISO> isos = client.getISOClient().listISOs(ListISOsOptions.Builder.name(isoName));
       for (ISO iso : isos) {
           client.getISOClient().deleteISO(iso.getId(), DeleteISOOptions.NONE);
       }
       super.tearDownContext();
   }
  
}
