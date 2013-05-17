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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToMap implements Function<Server, Map<String, String>> {
   @Resource
   protected Logger logger = Logger.NULL;

   @ApiVersion
   private final String apiVersion;

   @Inject
   public ServerToMap(@ApiVersion String apiVersion) {
      this.apiVersion = apiVersion;
   }

   @Override
   public Map<String, String> apply(Server from) {
      checkNotNull(from, "server");
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      builder.put("name", from.getName());
      builder.put("cpu", from.getCpu() + "");
      if (from.getSmp() != null)
         builder.put("smp", from.getSmp() + "");
      else
         builder.put("smp", "auto");
      builder.put("mem", from.getMem() + "");
      builder.put("persistent", from.isPersistent() + "");
      if (from.getBootDeviceIds().size() != 0)
         builder.put("boot", Joiner.on(' ').join(from.getBootDeviceIds()));
      for (Entry<String, ? extends Device> entry : from.getDevices().entrySet()) {
         builder.put(entry.getKey(), entry.getValue().getDriveUuid());
         builder.put(entry.getKey() + ":media", entry.getValue().getMediaType().toString());
      }
      int nicId = 0;
      for (NIC nic : from.getNics()) {
         builder.put("nic:" + nicId + ":model", nic.getModel().toString());
         if (nic.getDhcp() != null)
            builder.put("nic:" + nicId + ":dhcp", nic.getDhcp());
         if (nic.getVlan() != null)
            builder.put("nic:" + nicId + ":vlan", nic.getVlan());
         if (nic.getMac() != null)
            logger.trace("setting mac on network interfaces not supported: %s", nic);
         nicId++;
      }

      String vncIp = from.getVnc().getIp();
      if (apiVersion.equals("2.0")) {
         builder.put("vnc", "auto");
      } else {
         builder.put("vnc:ip", vncIp == null ? "auto" : vncIp);
      }

      if (from.getVnc().getPassword() != null)
         builder.put("vnc:password", from.getVnc().getPassword());
      if (from.getVnc().isTls())
         builder.put("vnc:tls", "on");
      if (from.getTags().size() != 0)
         builder.put("tags", Joiner.on(' ').join(from.getTags()));
      for (Entry<String, String> entry : from.getUserMetadata().entrySet())
         builder.put("user:" + entry.getKey(), entry.getValue());
      return builder.build();
   }
}
