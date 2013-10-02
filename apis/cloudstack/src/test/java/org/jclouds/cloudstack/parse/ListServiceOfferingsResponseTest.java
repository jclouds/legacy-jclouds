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
package org.jclouds.cloudstack.parse;

import java.util.Set;

import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.cloudstack.domain.StorageType;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ListServiceOfferingsResponseTest extends BaseSetParserTest<ServiceOffering> {

   @Override
   public String resource() {
      return "/listserviceofferingsresponse.json";
   }

   @Override
   @SelectJson("serviceoffering")
   public Set<ServiceOffering> expected() {
      return ImmutableSet.<ServiceOffering> of(
            ServiceOffering.builder().id("1").name("Small Instance")
                  .displayText("Small Instance - 500 MhZ CPU, 512 MB RAM - $0.05 per hour").cpuNumber(1).cpuSpeed(500)
                  .memory(512)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .storageType(StorageType.SHARED).supportsHA(false).build(),
            ServiceOffering.builder().id("2").name("Medium Instance")
                  .displayText("Medium Instance, 1 GhZ CPU,  1 GB RAM - $0.10 per hour").cpuNumber(1).cpuSpeed(1000)
                  .memory(1024)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .storageType(StorageType.SHARED).supportsHA(false).build());
   }

}
