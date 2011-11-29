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



import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.config.GleSYSParserModule;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.functions.MergeArgumentsAndReturnServerDetails;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.ResponseParser;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseServerDetailsWithoutIPsTest")
public class ParseServerDetailsWithoutIPsTest extends BaseItemParserTest<ServerDetails> {

   @Override
   public String resource() {
      return "/server_noip.json";
   }

   @Override
   @ResponseParser(MergeArgumentsAndReturnServerDetails.class)
   @Consumes(MediaType.APPLICATION_JSON)
   public ServerDetails expected() {
      return ServerDetails.builder().id("vz1541880").hostname("mammamia").datacenter("Falkenberg").platform("OpenVZ")
            .description("description").cpuCores(1).memory(128).disk(5).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }

}
