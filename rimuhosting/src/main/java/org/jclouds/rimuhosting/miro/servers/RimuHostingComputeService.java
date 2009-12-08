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

import org.jclouds.compute.ComputeService;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService implements ComputeService {
   RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeService(RimuHostingClient rhClient){
      this.rhClient = rhClient;
   }

   public org.jclouds.compute.Server createServerAndWait(String name, String profile, String image) {
      NewServerResponse serverResp = rhClient.createInstance(name, image, profile);
      return new RimuHostingServer(serverResp.getInstance(), rhClient);
   }

   public Future<org.jclouds.compute.Server> createServer(String name, String profile, String image) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   public SortedSet<org.jclouds.compute.Server> listServers() {
      SortedSet<org.jclouds.compute.Server> servers = new TreeSet<org.jclouds.compute.Server>();
      SortedSet<Server> rhServers = rhClient.getInstanceList();
      for(Server rhServer : rhServers) {
         servers.add(new RimuHostingServer(rhServer,rhClient));
      }
      return servers;
   }

   public org.jclouds.compute.Server getServer(String id) {
      return new RimuHostingServer(rhClient.getInstance(Long.valueOf(id)), rhClient);
   }
}