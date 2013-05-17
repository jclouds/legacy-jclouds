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
package org.jclouds.glesys.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.IpDetails;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ParseFullIpDetailsTest extends BaseItemParserTest<IpDetails> {

   @Override
   protected String resource() {
      return "/ip_get_details_xen.json";
   }

   @Override
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   public IpDetails expected() {
      return IpDetails.builder().datacenter("Falkenberg").version4()
            .address("109.74.10.13")
            .platform("Xen").ptr("109-74-10-13-static.serverhotell.net.").netmask("255.255.254.0").broadcast("109.74.11.255")
            .gateway("109.74.10.1").nameServers("79.99.4.100", "79.99.4.101")
            .cost(Cost.builder().amount(2.0).currency("EUR").timePeriod("month").build()).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
   
}
