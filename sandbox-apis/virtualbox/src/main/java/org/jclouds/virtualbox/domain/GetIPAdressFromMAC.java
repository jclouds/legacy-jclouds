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

package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class GetIPAdressFromMAC implements Statement {

   public static final Map<OsFamily, String> OS_TO_ARP = ImmutableMap.of(
         OsFamily.UNIX, "arp -an | grep {macAddress}\n", OsFamily.WINDOWS, "TODO");

   private String macAddress;

   public GetIPAdressFromMAC(String macAddress) {
      this.macAddress = checkNotNull(macAddress, "macAddress");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(macAddress);
      checkArgument(macAddress.length() == 12);

      macAddress = Joiner.on(":")
            .join(Splitter.fixedLength(2).split(macAddress)).toLowerCase();

      StringBuilder arp = new StringBuilder();
      arp.append(Utils.replaceTokens(OS_TO_ARP.get(family),
            ImmutableMap.of("macAddress", macAddress)));
      return arp.toString();
   }

}
