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
package org.jclouds.cloudstack.parse;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.cloudstack.config.CloudStackParserModule;
import org.jclouds.cloudstack.domain.Account;
import org.jclouds.cloudstack.domain.Account.State;
import org.jclouds.cloudstack.domain.Account.Type;
import org.jclouds.cloudstack.domain.Capacity;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;
import sun.util.resources.CalendarData;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * 
 * @author Richard Downer
 */
@Test(groups = "unit")
public class ListCapacityResponseTest extends BaseSetParserTest<Capacity> {

   @Override
   public String resource() {
      return "/listcapacityresponse.json";
   }

   @Override
   @SelectJson("capacity")
   public Set<Capacity> expected() {
      Capacity a = Capacity.builder().type(Capacity.Type.PRIMARY_STORAGE_ALLOCATED_BYTES)
         .zoneId(1).zoneName("Dev Zone 1").podId(-1).podName("All")
         .capacityUsed(34057748480L).capacityTotal(1796712955904L).percentUsed(1.9).build();
      Capacity b = Capacity.builder().type(Capacity.Type.PRIMARY_STORAGE_ALLOCATED_BYTES)
         .zoneId(1).zoneName("Dev Zone 1").podId(1).podName("Dev Pod 1")
         .capacityUsed(34057748480L).capacityTotal(1796712955904L).percentUsed(1.9).build();
      return ImmutableSet.of(a, b);
   }

}
