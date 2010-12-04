/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma.functions;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.DriveType;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToDriveInfo implements Function<Map<String, String>, DriveInfo> {
   private final org.jclouds.elasticstack.functions.MapToDriveInfo mapToDriveInfo;

   @Inject
   public MapToDriveInfo(org.jclouds.elasticstack.functions.MapToDriveInfo mapToDriveInfo) {
      this.mapToDriveInfo = mapToDriveInfo;
   }

   @Override
   public DriveInfo apply(Map<String, String> from) {
      if (from.size() == 0)
         return null;
      DriveInfo.Builder builder = DriveInfo.Builder.fromDriveInfo(mapToDriveInfo.apply(from));
      if (from.containsKey("use"))
         builder.tags(Splitter.on(',').split(from.get("use")));
      if (from.containsKey("bits"))
         builder.bits(new Integer(from.get("bits")));
      if (from.containsKey("url"))
         builder.url(URI.create(from.get("url")));
      builder.encryptionKey(from.get("encryption:key"));
      builder.description(from.get("description"));
      builder.installNotes(from.get("install_notes"));
      builder.os(from.get("os"));
      if (from.containsKey("drive_type"))
         builder.driveType(Splitter.on(',').split(from.get("drive_type")));
      if (from.containsKey("autoexpanding"))
         builder.autoexpanding(new Boolean(from.get("autoexpanding")));
      if (from.containsKey("free"))
         builder.free(new Boolean(from.get("free")));
      if (from.containsKey("type"))
         builder.type(DriveType.fromValue(from.get("type")));
      return builder.build();
   }
}