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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.internal.CreateServerResponseImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @author Ivan Meredith
 */
public class RimuHostingCreateServerResponse extends CreateServerResponseImpl {

   public RimuHostingCreateServerResponse(NewServerResponse rhServerResponse) {
      super(rhServerResponse.getServer().getId().toString(),
               rhServerResponse.getServer().getName(), getPublicAddresses(rhServerResponse
                        .getServer()), ImmutableList.<InetAddress> of(), 22, LoginType.SSH,
               new Credentials("root", rhServerResponse.getNewInstanceRequest().getCreateOptions()
                        .getPassword()));
   }

   @VisibleForTesting
   static Iterable<InetAddress> getPublicAddresses(Server rhServer) {
      Iterable<String> addresses = Iterables.concat(ImmutableList.of(rhServer.getIpAddresses()
               .getPrimaryIp()), rhServer.getIpAddresses().getSecondaryIps());
      return Iterables.transform(addresses, new Function<String, InetAddress>() {

         @Override
         public InetAddress apply(String from) {
            try {
               return InetAddress.getByName(from);
            } catch (UnknownHostException e) {
               // TODO: log the failure.
               return null;
            }
         }
      });
   }
}
