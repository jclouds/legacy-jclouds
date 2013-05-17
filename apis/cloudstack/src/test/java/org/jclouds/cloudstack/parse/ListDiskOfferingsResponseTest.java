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

import org.jclouds.cloudstack.domain.DiskOffering;
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
public class ListDiskOfferingsResponseTest extends BaseSetParserTest<DiskOffering> {

   @Override
   public String resource() {
      return "/listdiskofferingsresponse.json";
   }

   @Override
   @SelectJson("diskoffering")
   public Set<DiskOffering> expected() {
      return ImmutableSet.<DiskOffering> of(
            DiskOffering.builder().id("3").domainId("1").domain("ROOT").name("Small").displayText("Small Disk, 5 GB")
                  .diskSize(5)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .customized(false).build(),
            DiskOffering.builder().id("4").domainId("1").domain("ROOT").name("Medium").displayText("Medium Disk, 20 GB")
                  .diskSize(20)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .customized(false).build(),
            DiskOffering.builder().id("5").domainId("1").domain("ROOT").name("Large").displayText("Large Disk, 100 GB")
                  .diskSize(100)
                  .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-02-11T15:22:32-0800"))
                  .customized(false).build());
   }
}
