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

package org.jclouds.virtualbox.statements;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.testng.annotations.Test;
import org.virtualbox_4_2.NetworkAttachmentType;

public class SetIpAddressTest {

   @Test
   public void testSetIpeth0() {
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
               .builder()
               .slot(0L)
               .addNetworkAdapter(
                        NetworkAdapter.builder().staticIp("127.0.0.1").networkAttachmentType(NetworkAttachmentType.NAT)
                                 .build()).build();
      SetIpAddress setIpAddressStmtm = new SetIpAddress(networkInterfaceCard);
      assertEquals("ifconfig eth0 127.0.0.1;", setIpAddressStmtm.render(OsFamily.UNIX));
   }
   
   @Test
   public void testSetIpeth3() {
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
               .builder()
               .slot(3L)
               .addNetworkAdapter(
                        NetworkAdapter.builder().staticIp("localhost").networkAttachmentType(NetworkAttachmentType.NAT)
                                 .build()).build();
      SetIpAddress setIpAddressStmtm = new SetIpAddress(networkInterfaceCard);
      assertEquals("ifconfig eth3 localhost;", setIpAddressStmtm.render(OsFamily.UNIX));
   }
   
   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testThrowsIllegalArgumentExceptionOnWrongSlot() {
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard
               .builder()
               .slot(4L)
               .addNetworkAdapter(
                        NetworkAdapter.builder().staticIp("localhost").networkAttachmentType(NetworkAttachmentType.NAT)
                                 .build()).build();
      new SetIpAddress(networkInterfaceCard);
   }

}
