/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.compute;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.internal.CreateNodeResponseImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * @author Ivan Meredith
 */
public class RimuHostingCreateNodeResponse extends CreateNodeResponseImpl {

   public RimuHostingCreateNodeResponse(NewServerResponse rhNodeResponse) {
      super(rhNodeResponse.getServer().getId().toString(),
               rhNodeResponse.getServer().getName(),
               NodeState.RUNNING,// TODO need a real state!
               getPublicAddresses(rhNodeResponse.getServer()), ImmutableList.<InetAddress> of(),
               22, LoginType.SSH, new Credentials("root", rhNodeResponse.getNewInstanceRequest()
                        .getCreateOptions().getPassword()), ImmutableMap.<String, String> of());
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
