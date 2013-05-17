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
package org.jclouds.ultradns.ws.internal;

import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.ultradns.ws.UltraDNSWSApi;
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.domain.Zone;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseUltraDNSWSApiLiveTest extends BaseApiLiveTest<UltraDNSWSApi> {
   protected String zoneName = String.format("%s-%s.ultradnstest.jclouds.org.", System.getProperty("user.name")
         .replace('.', '-'), UPPER_CAMEL.to(LOWER_HYPHEN, getClass().getSimpleName()));
   protected String zoneId;
   protected IdAndName account;

   public BaseUltraDNSWSApiLiveTest() {
      provider = "ultradns-ws";
   }

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setup() {
      super.setup();
      account = api.getCurrentAccount();
   }

   protected void createZone() {
      api.getZoneApi().delete(zoneName);
      api.getZoneApi().createInAccount(zoneName, account.getId());
      zoneId = getZoneByName(zoneName).get().getId();
   }

   protected Optional<Zone> getZoneByName(final String zoneName) {
      return api.getZoneApi().listByAccount(account.getId()).firstMatch(new Predicate<Zone>() {
         public boolean apply(Zone in) {
            return in.getName().equals(zoneName);
         }
      });
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDown() {
      if (zoneId != null)
         api.getZoneApi().delete(zoneName);
      super.tearDown();
   }
}
