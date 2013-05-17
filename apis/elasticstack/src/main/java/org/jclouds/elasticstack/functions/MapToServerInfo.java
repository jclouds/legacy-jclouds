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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.domain.ServerMetrics;
import org.jclouds.elasticstack.domain.ServerStatus;
import org.jclouds.elasticstack.domain.VNC;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class MapToServerInfo implements Function<Map<String, String>, ServerInfo> {
   private final Function<Map<String, String>, Map<String, ? extends Device>> mapToDevices;
   private final Function<Map<String, String>, ServerMetrics> mapToMetrics;
   private final Function<Map<String, String>, List<NIC>> mapToNICs;

   @Inject
   public MapToServerInfo(Function<Map<String, String>, Map<String, ? extends Device>> mapToDevices,
                          Function<Map<String, String>, ServerMetrics> mapToMetrics, Function<Map<String, String>, List<NIC>> mapToNICs) {
      this.mapToDevices = mapToDevices;
      this.mapToMetrics = mapToMetrics;
      this.mapToNICs = mapToNICs;
   }

   @Override
   public ServerInfo apply(Map<String, String> from) {
      if (from.size() == 0)
         return null;
      ServerInfo.Builder builder = new ServerInfo.Builder();
      builder.name(from.get("name"));
      builder.persistent(Boolean.parseBoolean(from.get("persistent")));
      if (from.containsKey("tags"))
         builder.tags(Splitter.on(' ').split(from.get("tags")));
      if (from.containsKey("status"))
         builder.status(ServerStatus.fromValue(from.get("status")));


      if (from.containsKey("smp:cores")) {
            builder.smp(Integer.valueOf(from.get("smp:cores")));
      } else if (from.containsKey("smp") && !"auto".equals(from.get("smp"))) {
            builder.smp(Integer.valueOf(from.get("smp")));
      }

      builder.cpu(Integer.parseInt(from.get("cpu")));
      builder.mem(Integer.parseInt(from.get("mem")));
      builder.user(from.get("user"));
      if (from.containsKey("started"))
         builder.started(new Date(Long.valueOf(from.get("started"))));
      builder.uuid(from.get("server"));
      if (from.containsKey("boot"))
         builder.bootDeviceIds(Splitter.on(' ').split(from.get("boot")));


      builder.vnc(new VNC(from.get("vnc:ip") == null ? "auto" : from.get("vnc:ip"), from.get("vnc:password"), from.containsKey("vnc:tls")
              && Boolean.valueOf(from.get("vnc:tls"))));


      Map<String, String> metadata = Maps.newLinkedHashMap();
      for (Entry<String, String> entry : from.entrySet()) {
         if (entry.getKey().startsWith("user:"))
            metadata.put(entry.getKey().substring(entry.getKey().indexOf(':') + 1), entry.getValue());
      }
      builder.userMetadata(metadata);
      builder.nics(mapToNICs.apply(from));
      builder.devices(mapToDevices.apply(from));
      builder.metrics(mapToMetrics.apply(from));
      return builder.build();
   }
}
