/*
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

import com.google.common.base.Function;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.INetworkAdapter;

import javax.annotation.Nullable;

import static org.virtualbox_4_1.NATProtocol.TCP;
import static org.virtualbox_4_1.NetworkAttachmentType.NAT;

/**
 * @author Mattias Holmqvist
 */
public class AttachNATRedirectRuleToMachine implements Function<IMachine, Void> {

   private long adapterIndex;

   public AttachNATRedirectRuleToMachine(long adapterSlot) {
      this.adapterIndex = adapterSlot;
   }

   @Override
   public Void apply(@Nullable IMachine machine) {
      INetworkAdapter networkAdapter = machine.getNetworkAdapter(adapterIndex);
      networkAdapter.setAttachmentType(NAT);
      networkAdapter.getNatDriver().addRedirect("guestssh", TCP, "127.0.0.1", 2222, "", 22);
      networkAdapter.setEnabled(true);
      machine.saveSettings();
      return null;
   }
}
