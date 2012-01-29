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
package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkState;

import javax.inject.Singleton;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

@Singleton
public class IMachineToSshClient implements Function<IMachine, SshClient> {
   private final SshClient.Factory sshClientFactory;

   @Inject
   public IMachineToSshClient(SshClient.Factory sshClientFactory) {
      this.sshClientFactory = sshClientFactory;
   }

   @Override
   public SshClient apply(final IMachine vm) {
      INetworkAdapter networkAdapter = vm.getNetworkAdapter(0l);
      SshClient client = null;
      checkState(networkAdapter != null);
      for (String nameProtocolnumberAddressInboudportGuestTargetport : networkAdapter.getNatDriver().getRedirects()) {
         Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
         String protocolNumber = Iterables.get(stuff, 1);
         String hostAddress = Iterables.get(stuff, 2);
         String inboundPort = Iterables.get(stuff, 3);
         String targetPort = Iterables.get(stuff, 5);
         // TODO: we need a way to align the default login credentials from the iso with the
         // vmspec
         if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
            client = sshClientFactory.create(new IPSocket(hostAddress, Integer.parseInt(inboundPort)),
                     LoginCredentials.builder().user("toor").password("password").authenticateSudo(true).build());
         }
      }
      return client;
   }
}