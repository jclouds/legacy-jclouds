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
package org.jclouds.glesys.parse;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.ServerCreated;
import org.jclouds.glesys.domain.ServerCreatedIp;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerDetailsTest")
public class ParseServerDetailsTest extends BaseItemParserTest<ServerDetails> {

   @Override
   public String resource() {
      return "/server_details.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public ServerDetails expected() {
      Cost cost = Cost.builder().amount(6.38).currency("EUR").timePeriod("month").build();
      return ServerDetails.builder().id("vz1908384").hostname("jclouds-unit").cpuCores(1).
            memory(128).disk(5).
            description("unit test server").datacenter("Falkenberg").platform("OpenVZ").cost(cost).build();
   }
   
   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
}
