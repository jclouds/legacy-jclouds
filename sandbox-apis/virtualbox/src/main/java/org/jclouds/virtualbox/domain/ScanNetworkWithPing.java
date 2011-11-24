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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.util.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @author Andrea Turli
 */
public class ScanNetworkWithPing implements Statement {

   public static final Map<OsFamily, String> OS_TO_PING = ImmutableMap
         .of(OsFamily.UNIX,
               "for i in {1..254} ; do ping -c 1 -t 1 {network}.$i & done",
               OsFamily.WINDOWS, "TODO");

   private String network;

   public ScanNetworkWithPing(String network) {
      this.network = checkNotNull(network, "network");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      network = network.substring(0, network.lastIndexOf("."));
      StringBuilder arp = new StringBuilder();
      arp.append(Utils.replaceTokens(OS_TO_PING.get(family), ImmutableMap.of("network", network)));
      return arp.toString();
   }

}
