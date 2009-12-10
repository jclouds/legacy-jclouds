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

import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ivan Meredith
 */
public class RimuHostingCreateServerResponse implements CreateServerResponse {
   private Server rhServer;
   private NewServerResponse rhServerResponse;

   public RimuHostingCreateServerResponse(NewServerResponse rhServerResponse){
      this.rhServer = rhServerResponse.getServer();
      this.rhServerResponse = rhServerResponse;   
   }
   public String getId() {
      return rhServer.getId().toString();
   }

   public String getName() {
      return rhServer.getName();
   }

   public SortedSet<InetAddress> getPublicAddresses() {
      SortedSet<InetAddress> ipAddresses = new TreeSet<InetAddress>();
      try {
         InetAddress address = InetAddress.getByName(rhServer.getIpAddresses().getPrimaryIp());
         ipAddresses.add(address);
      } catch (UnknownHostException e) {
         //TODO: log the failure.
      }

      for(String ip : rhServer.getIpAddresses().getSecondaryIps()){
         try {
            InetAddress address = InetAddress.getByName(rhServer.getIpAddresses().getPrimaryIp());
            ipAddresses.add(address);
         } catch (UnknownHostException e) {
            //TODO: log the failure.
         }
      }
      return null;
   }

   /**
    * Rimuhosting does not support private addressess at this time.
    * @return  null
    */
   public SortedSet<InetAddress> getPrivateAddresses() {
      return null;
   }

   /**
    * Default port is always 22.
    * @return 22
    */
   public int getLoginPort() {
      return 22;
   }

   public LoginType getLoginType() {
      return LoginType.SSH;
   }

   public Credentials getCredentials() {
      return new Credentials("root",rhServerResponse.getNewInstanceRequest().getCreateOptions().getPassword());
   }
}
