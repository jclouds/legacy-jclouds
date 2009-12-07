/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.servers;

import org.jclouds.compute.Server;
import org.jclouds.compute.Platform;
import org.jclouds.compute.Instance;
import org.jclouds.rimuhosting.miro.RimuHostingClient;

import java.util.SortedSet;

public class RimuHostingServer implements Server {
   org.jclouds.rimuhosting.miro.domain.Server rhServer;

   RimuHostingClient rhClient;

   public RimuHostingServer(org.jclouds.rimuhosting.miro.domain.Server rhServer, RimuHostingClient rhClient){
      this.rhServer = rhServer;
      this.rhClient = rhClient;
   }

   public String getId() {
      return rhServer.toString();
   }

   public Platform createPlatform(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public Platform getPlatform(String id) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<Platform> listPlatforms() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<Instance> listInstances() {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public Boolean destroyServer() {
      rhClient.destroyInstance(rhServer.getId());
      return Boolean.TRUE;
   }
}
