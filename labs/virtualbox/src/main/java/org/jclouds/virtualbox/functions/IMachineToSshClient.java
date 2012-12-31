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

import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_PASSWORD;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.GUEST_OS_USER;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.util.NetworkUtils;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.NetworkAttachmentType;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.inject.Inject;

@Singleton
public class IMachineToSshClient implements Function<IMachine, SshClient> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final SshClient.Factory sshClientFactory;
   private final NetworkUtils networkUtils;

   @Inject
   public IMachineToSshClient(SshClient.Factory sshClientFactory, NetworkUtils networkUtils) {
      this.sshClientFactory = sshClientFactory;
      this.networkUtils = networkUtils;
   }

   @Override
   public SshClient apply(final IMachine vm) {
      String sshPort = "22";
      String guestIdentity = vm.getExtraData(GUEST_OS_USER);
      String guestCredential = vm.getExtraData(GUEST_OS_PASSWORD);
      LoginCredentials loginCredentials = LoginCredentials.builder().user(guestIdentity).password(guestCredential)
            .authenticateSudo(true).build();

      String clientIpAddress = null;

      long nicSlot = 0;
      while (nicSlot < 4 && Strings.isNullOrEmpty(clientIpAddress)) {
         INetworkAdapter networkAdapter = vm.getNetworkAdapter(nicSlot);

         if (networkAdapter.getAttachmentType().equals(NetworkAttachmentType.NAT)) {
            for (String nameProtocolnumberAddressInboudportGuestTargetport : networkAdapter.getNATEngine()
                  .getRedirects()) {
               Iterable<String> stuff = Splitter.on(',').split(nameProtocolnumberAddressInboudportGuestTargetport);
               String protocolNumber = Iterables.get(stuff, 1);
               String hostAddress = Iterables.get(stuff, 2);
               String inboundPort = Iterables.get(stuff, 3);
               String targetPort = Iterables.get(stuff, 5);
               if ("1".equals(protocolNumber) && "22".equals(targetPort)) {
                  clientIpAddress = hostAddress;
                  sshPort = inboundPort;
               }
            }
         } else if (networkAdapter.getAttachmentType().equals(NetworkAttachmentType.Bridged)) {
            clientIpAddress = networkUtils.getIpAddressFromNicSlot(vm.getName(), networkAdapter.getSlot());
         } else if (networkAdapter.getAttachmentType().equals(NetworkAttachmentType.HostOnly)) {
            clientIpAddress = networkUtils.getValidHostOnlyIpFromVm(vm.getName());
         }
         nicSlot++;
      }
      return sshClientFactory.create(HostAndPort.fromParts(clientIpAddress, Integer.parseInt(sshPort)),
            loginCredentials);
   }

}