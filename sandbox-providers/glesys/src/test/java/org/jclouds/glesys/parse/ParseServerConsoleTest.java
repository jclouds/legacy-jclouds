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
import org.jclouds.glesys.domain.ServerConsole;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerCreatedTest")
public class ParseServerConsoleTest extends BaseItemParserTest<ServerConsole> {

   @Override
   public String resource() {
      return "/server_console.json";
   }

   @Override
   @SelectJson("remote")
   @Consumes(MediaType.APPLICATION_JSON)
   public ServerConsole expected() {
      return ServerConsole.builder().host("79.99.2.147").port(59478).password("1476897311").build();
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }
}
