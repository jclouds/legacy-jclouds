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
import org.jclouds.glesys.domain.Bandwidth;
import org.jclouds.glesys.domain.Cost;
import org.jclouds.glesys.domain.Cpu;
import org.jclouds.glesys.domain.Disk;
import org.jclouds.glesys.domain.Memory;
import org.jclouds.glesys.domain.ServerDetails;
import org.jclouds.glesys.domain.ServerState;
import org.jclouds.glesys.domain.ServerStatus;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseServerStatusTest")
public class ParseServerStatusTest extends BaseItemParserTest<ServerStatus> {

   @Override
   public String resource() {
      return "/server_status.json";
   }

   @Override
   @SelectJson("server")
   @Consumes(MediaType.APPLICATION_JSON)
   public ServerStatus expected() {
      Bandwidth bandwidth = Bandwidth.builder().today(0).last30Days(0).max(50).build();
      Cpu cpu = Cpu.builder().unit("%").idle(100.0).system(0.0).user(0.0).nice(0.0).build();
      Disk disk = Disk.builder().unit("MB").size(0).used(0).build();
      Memory memory = Memory.builder().unit("MB").usage(3).size(128).build();
      return ServerStatus.builder().state(ServerState.RUNNING).uptime(38 * 60 + 6).bandwidth(bandwidth).
            cpu(cpu).disk(disk).memory(memory).build();
   }

   protected Injector injector() {
      return Guice.createInjector(new GleSYSParserModule(), new GsonModule());
   }

}
