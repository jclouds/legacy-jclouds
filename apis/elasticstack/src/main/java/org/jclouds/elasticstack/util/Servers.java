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
package org.jclouds.elasticstack.util;

import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.VNC;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class Servers {
   /**
    * Helper to create a small persistent server
    * 
    * @param name
    *           what to name the server
    * @param driveUuuid
    *           id of the boot drive
    * @param vncPassword
    *           password for vnc
    * @return a builder for a persistent 1Ghz 512m server with DHCP enabled network.
    */
   public static Server.Builder small(String name, String driveUuuid, String vncPassword) {
      return new Server.Builder().name(name).cpu(1000).mem(512).persistent(true)
            .devices(ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid(driveUuuid).build()))
            .bootDeviceIds(ImmutableSet.of("ide:0:0"))
            .nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).dhcp("auto").build()))
            .vnc(new VNC(null, vncPassword, false));
   }
}
