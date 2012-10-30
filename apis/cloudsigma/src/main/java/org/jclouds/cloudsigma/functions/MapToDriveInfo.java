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
package org.jclouds.cloudsigma.functions;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.ClaimType;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.DriveMetrics;
import org.jclouds.cloudsigma.domain.DriveStatus;
import org.jclouds.cloudsigma.domain.DriveType;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToDriveInfo implements Function<Map<String, String>, DriveInfo> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public DriveInfo apply(Map<String, String> from) {
      if (from.size() == 0)
         return null;
      DriveInfo.Builder builder = new DriveInfo.Builder();
      builder.name(from.get("name"));
      if (from.containsKey("use"))
         builder.use(Splitter.on(' ').split(from.get("use")));
      if (from.containsKey("status"))
         builder.status(DriveStatus.fromValue(from.get("status")));
      builder.metrics(buildMetrics(from));
      builder.user(from.get("user"));
      builder.encryptionCipher(from.get("encryption:cipher"));
      builder.uuid(from.get("drive"));
      if (from.containsKey("claim:type"))
         builder.claimType(ClaimType.fromValue(from.get("claim:type")));
      if (from.containsKey("claimed"))
         builder.claimed(Splitter.on(' ').split(from.get("claimed")));
      if (from.containsKey("tags"))
          builder.tags(Splitter.on(' ').split(from.get("tags")));
      if (from.containsKey("readers"))
         builder.readers(Splitter.on(' ').split(from.get("readers")));
      if (from.containsKey("size"))
         builder.size(Long.valueOf(from.get("size")));
      Map<String, String> metadata = Maps.newLinkedHashMap();
      for (Entry<String, String> entry : from.entrySet()) {
         if (entry.getKey().startsWith("user:"))
            metadata.put(entry.getKey().substring(entry.getKey().indexOf(':') + 1), entry.getValue());
      }
      if (from.containsKey("use"))
         builder.use(Splitter.on(' ').split(from.get("use")));
      if (from.containsKey("bits"))
         builder.bits(Integer.valueOf(from.get("bits")));
      if (from.containsKey("url"))
         builder.url(URI.create(from.get("url")));
      builder.encryptionKey(from.get("encryption:key"));
      builder.description(from.get("description"));
      builder.installNotes(from.get("install_notes"));
      builder.os(from.get("os"));
      if (from.containsKey("drive_type"))
         builder.driveType(Splitter.on(',').split(from.get("drive_type")));
      if (from.containsKey("autoexpanding"))
         builder.autoexpanding(Boolean.valueOf(from.get("autoexpanding")));
      if (from.containsKey("free"))
         builder.free(Boolean.valueOf(from.get("free")));
      if (from.containsKey("type"))
         builder.type(DriveType.fromValue(from.get("type")));
      try {
         return builder.build();
      } catch (NullPointerException e) {
         logger.trace("entry missing data: %s; %s", e.getMessage(), from);
         return null;
      }
   }

   protected DriveMetrics buildMetrics(Map<String, String> from) {
      DriveMetrics.Builder metricsBuilder = new DriveMetrics.Builder();
      if (from.containsKey("read:bytes"))
         metricsBuilder.readBytes(Long.valueOf(from.get("read:bytes")));
      if (from.containsKey("read:requests"))
         metricsBuilder.readRequests(Long.valueOf(from.get("read:requests")));
      if (from.containsKey("write:bytes"))
         metricsBuilder.writeBytes(Long.valueOf(from.get("write:bytes")));
      if (from.containsKey("write:requests"))
         metricsBuilder.writeRequests(Long.valueOf(from.get("write:requests")));
      return metricsBuilder.build();
   }
}
