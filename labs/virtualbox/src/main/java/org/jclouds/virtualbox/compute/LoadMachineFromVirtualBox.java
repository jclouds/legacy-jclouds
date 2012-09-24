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

package org.jclouds.virtualbox.compute;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_MACHINE_CREDENTIAL;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_MACHINE_GROUP;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_MACHINE_LOCATION;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_MACHINE_USERNAME;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.jclouds.byon.Node;
import org.jclouds.compute.domain.OsFamily;
import org.virtualbox_4_2.IGuestOSType;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;

/**
 * Loads a node from a VirtualBox IMachine
 * 
 * @author Mattias Holmqvist
 */
public class LoadMachineFromVirtualBox extends CacheLoader<String, Node> {

   private VirtualBoxManager manager;
   private Function<IMachine, String> iMachineToIpAddress;

   @Inject
   public LoadMachineFromVirtualBox(VirtualBoxManager manager, Function<IMachine, String> iMachineToIpAddress) {
      this.manager = manager;
      this.iMachineToIpAddress = iMachineToIpAddress;
   }

   @Override
   public Node load(final String id) throws Exception {

      if (id.equals("host")) {
         final Node hostNode = Node.builder().id("host").name("host installing virtualbox").hostname("localhost")
               .osFamily(OsFamily.LINUX.toString()).osDescription(System.getProperty("os.name"))
               .osVersion(System.getProperty("os.version")).group("ssh").username(System.getProperty("user.name"))
               .credentialUrl(privateKeyFile()).build();
         return hostNode;
      }

      final IMachine machine = manager.getVBox().findMachine(id);
      final String ipAddress = iMachineToIpAddress.apply(machine);
      final String osTypeId = machine.getOSTypeId();
      final IGuestOSType guestOSType = manager.getVBox().getGuestOSType(osTypeId);

      final Node node = Node.builder().id(machine.getId()).name(machine.getName())
            .description(machine.getDescription()).loginPort(22).group(System.getProperty(VIRTUALBOX_MACHINE_GROUP))
            .username(System.getProperty(VIRTUALBOX_MACHINE_USERNAME))
            .credential(System.getProperty(VIRTUALBOX_MACHINE_CREDENTIAL))
            .sudoPassword(System.getProperty(VIRTUALBOX_MACHINE_CREDENTIAL))
            .locationId(System.getProperty(VIRTUALBOX_MACHINE_LOCATION)).os64Bit(guestOSType.getIs64Bit())
            .osArch(guestOSType.getDescription()).osFamily(guestOSType.getFamilyDescription())
            .osVersion(guestOSType.getId()).osDescription(guestOSType.getDescription()).hostname(ipAddress).build();

      return node;

   }

   private static URI privateKeyFile() {
      try {
         return new URI("file://" + System.getProperty("user.home") + "/.ssh/id_rsa");
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
      return null;
   }

}
