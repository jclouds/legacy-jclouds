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

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

/**
 * @author Ivan Meredith
 */
@Singleton
public class RimuHostingComputeService implements ComputeService {
   RimuHostingClient rhClient;

   @Inject
   public RimuHostingComputeService(RimuHostingClient rhClient) {
      this.rhClient = rhClient;
   }

   @Override
   public CreateServerResponse createServer(String name, String profile, String image) {
      NewServerResponse serverResponse = rhClient.createInstance(name, image, profile);
      return new RimuHostingCreateServerResponse(serverResponse);
   }

   public SortedSet<org.jclouds.compute.Server> listServers() {
      SortedSet<org.jclouds.compute.Server> servers = new TreeSet<org.jclouds.compute.Server>();
      SortedSet<Server> rhServers = rhClient.getInstanceList();
      for (Server rhServer : rhServers) {
         servers.add(new RimuHostingServer(rhServer, rhClient));
      }
      return servers;
   }

   public org.jclouds.compute.Server getServerById(String id) {
      return new RimuHostingServer(rhClient.getInstance(Long.valueOf(id)), rhClient);
   }

   @Override
   public SortedSet<org.jclouds.compute.Server> getServerByName(String id) {
      SortedSet<org.jclouds.compute.Server> serverSet = new TreeSet<org.jclouds.compute.Server>();
      for(Server rhServer : rhClient.getInstanceList()){
         serverSet.add(new RimuHostingServer(rhServer, rhClient));         
      }
      return serverSet;
   }
}