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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.util.Utils;
import org.jclouds.virtualbox.functions.MacAddressToBSD;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Andrea Turli
 */
public class GetIPAddressFromMAC implements Statement {

   public static final Map<OsFamily, String> OS_TO_ARP = ImmutableMap
         .of(OsFamily.UNIX,
               "MAC={macAddress} && [[ `uname -s` = \"Darwin\" ]] && MAC={macAddressBsd}\n arp -an | grep $MAC\n",
               OsFamily.WINDOWS, "set MAC={macAddress} arp -a | Findstr %MAC%");

   private String macAddress;
   private String macAddressBsd; 

   public GetIPAddressFromMAC(String macAddress) {
   	this(Joiner.on(":").join(Splitter.fixedLength(2).split(macAddress)).toLowerCase(),
   	      MacAddressToBSD.INSTANCE.apply(Joiner.on(":").join(Splitter.fixedLength(2).split(macAddress)).toLowerCase()));
   }
   
   public GetIPAddressFromMAC(String macAddress, String macAddressBsd) {
      checkNotNull(macAddress, "macAddress");
      checkArgument(macAddress.length() == 17);
      this.macAddress = macAddress;
      checkNotNull(macAddressBsd, "macAddressBsd");
      this.macAddressBsd = macAddressBsd;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      StringBuilder arp = new StringBuilder();
      arp.append(Utils.replaceTokens(OS_TO_ARP.get(family), ImmutableMap.of(
            "macAddress", macAddress, "macAddressBsd", macAddressBsd)));
      return arp.toString();
   }
   
}
