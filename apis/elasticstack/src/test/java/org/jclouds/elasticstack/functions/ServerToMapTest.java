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
package org.jclouds.elasticstack.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.VNC;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 *
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class ServerToMapTest {

   private static final ServerToMap SERVER_TO_MAP = new ServerToMap("1.0");
   private static final ServerToMap SERVER_TO_MAP_V2 = new ServerToMap("2.0");

    public void testBasics() {
      assertEquals(
            SERVER_TO_MAP.apply(new Server.Builder()
                  .name("TestServer")
                  .cpu(2000)
                  .mem(1024)
                  .devices(
                        ImmutableMap.of("ide:0:0",
                              new IDEDevice.Builder(0, 0).uuid("08c92dd5-70a0-4f51-83d2-835919d254df").build()))
                  .bootDeviceIds(ImmutableSet.of("ide:0:0")).nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).

                  build())).vnc(new VNC(null, "XXXXXXXX", false)).build()),
            ImmutableMap
                  .builder()
                  .putAll(ImmutableMap.of("name", "TestServer", "cpu", "2000", "smp", "auto", "mem", "1024"))
                  .putAll(
                        ImmutableMap.of("persistent", "false", "boot", "ide:0:0", "ide:0:0",
                              "08c92dd5-70a0-4f51-83d2-835919d254df"))
                  .putAll(
                        ImmutableMap.of("ide:0:0:media", "disk", "nic:0:model", "e1000", "vnc:ip", "auto",
                              "vnc:password", "XXXXXXXX")).build());
   }

   public void testWeDontSetMac() {
      assertEquals(
            SERVER_TO_MAP.apply(new Server.Builder()
                  .name("TestServer")
                  .cpu(2000)
                  .mem(1024)
                  .devices(
                        ImmutableMap.of("ide:0:0",
                              new IDEDevice.Builder(0, 0).uuid("08c92dd5-70a0-4f51-83d2-835919d254df").build()))
                  .bootDeviceIds(ImmutableSet.of("ide:0:0"))
                  .nics(ImmutableSet.of(new NIC.Builder().mac("foo").model(Model.E1000).

                  build())).vnc(new VNC(null, "XXXXXXXX", false)).build()),
            ImmutableMap
                  .builder()
                  .putAll(ImmutableMap.of("name", "TestServer", "cpu", "2000", "smp", "auto", "mem", "1024"))
                  .putAll(
                        ImmutableMap.of("persistent", "false", "boot", "ide:0:0", "ide:0:0",
                              "08c92dd5-70a0-4f51-83d2-835919d254df"))
                  .putAll(
                        ImmutableMap.of("ide:0:0:media", "disk", "nic:0:model", "e1000", "vnc:ip", "auto",
                              "vnc:password", "XXXXXXXX")).build());
   }

    public void testBasicsV2() {
      assertEquals(
            SERVER_TO_MAP_V2.apply(new Server.Builder()
                  .name("TestServer")
                  .cpu(2000)
                  .mem(1024)
                  .devices(
                        ImmutableMap.of("ide:0:0",
                              new IDEDevice.Builder(0, 0).uuid("08c92dd5-70a0-4f51-83d2-835919d254df").build()))
                  .bootDeviceIds(ImmutableSet.of("ide:0:0")).nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).

                  build())).vnc(new VNC(null, "XXXXXXXX", false)).build()),
            ImmutableMap
                  .builder()
                  .putAll(ImmutableMap.of("name", "TestServer", "cpu", "2000", "smp", "auto", "mem", "1024"))
                  .putAll(
                        ImmutableMap.of("persistent", "false", "boot", "ide:0:0", "ide:0:0",
                              "08c92dd5-70a0-4f51-83d2-835919d254df"))
                  .putAll(
                        ImmutableMap.of("ide:0:0:media", "disk", "nic:0:model", "e1000", "vnc", "auto",
                              "vnc:password", "XXXXXXXX")).build());
   }

}
