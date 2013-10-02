/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.elasticstack.functions;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class MapToNICs implements Function<Map<String, String>, List<NIC>> {

   private String apiVersion;

   @Inject
   public MapToNICs(@ApiVersion String apiVersion) {
      this.apiVersion = apiVersion;
   }

   @Override
   public List<NIC> apply(Map<String, String> from) {
      ImmutableList.Builder<NIC> nics = ImmutableList.builder();
      NIC:
      for (int id : new int[]{0, 1}) {
         String key = String.format("nic:%d", id);
         if (!from.containsKey(key + ":model"))
            break NIC;

         NIC.Builder nicBuilder = new NIC.Builder();
         final String ip = getDhcpIp(from, key);
         nicBuilder.dhcp(ip);
         nicBuilder.model(Model.fromValue(from.get(key + ":model")));
         nicBuilder.vlan(from.get(key + ":vlan"));
         nicBuilder.mac(from.get(key + ":mac"));
         if (from.containsKey(key + ":block"))
            nicBuilder.block(Splitter.on(' ').split(from.get(key + ":block")));
         nics.add(nicBuilder.build());
      }

      return nics.build();
   }

   private String getDhcpIp(Map<String, String> from, String key) {
      if (apiVersion.equals("2.0")) {
         final String ip = from.get(key + ":dhcp:ip");
         return ip == null ? "auto" : ip;
      } else {
         final String ip = from.get(key + ":dhcp");
         return ip == null ? "auto" : ip;
      }
   }
}
