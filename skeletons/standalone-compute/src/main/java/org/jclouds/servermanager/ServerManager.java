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
package org.jclouds.servermanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * This would be replaced with the real connection to the service that can
 * create/list/reboot/get/destroy things
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServerManager {

   private static final Map<Integer, Server> servers = Maps.newHashMap();
   private static final Map<Integer, Image> images = ImmutableMap.of(1, new Image(1, "ubuntu"));
   private static final Map<Integer, Hardware> hardware = ImmutableMap.of(1, new Hardware(1, "small", 1, 512, 10));

   private static final AtomicInteger nodeIds = new AtomicInteger(0);

   /**
    * simulate creating a server, as this is really going to happen with the api underneath
    * 
    * @param name
    * @param name
    * @param imageId
    * @param hardwareId
    * @return new server
    */
   public Server createServerInDC(String datacenter, String name, int imageId, int hardwareId) {
      Server server = new Server();
      server.id = nodeIds.getAndIncrement();
      server.name = name;
      server.datacenter = datacenter;
      server.imageId = imageId;
      server.hardwareId = hardwareId;
      server.publicAddress = "7.1.1." + server.id;
      server.privateAddress = "10.1.1." + server.id;
      server.loginUser = "root";
      server.password = "password";
      servers.put(server.id, server);
      return server;
   }

   public Server getServer(int serverId) {
      return servers.get(serverId);
   }

   public Iterable<Server> listServers() {
      return servers.values();
   }

   public Image getImage(int imageId) {
      return images.get(imageId);
   }

   public Iterable<Image> listImages() {
      return images.values();
   }

   public Hardware getHardware(int hardwareId) {
      return hardware.get(hardwareId);
   }

   public Iterable<org.jclouds.servermanager.Hardware> listHardware() {
      return hardware.values();
   }

   public void destroyServer(int serverId) {
      servers.remove(serverId);
   }

   public void rebootServer(int serverId) {
   }

   public void stopServer(int serverId) {
   }
   
   public void startServer(int serverId) {
   }
}
