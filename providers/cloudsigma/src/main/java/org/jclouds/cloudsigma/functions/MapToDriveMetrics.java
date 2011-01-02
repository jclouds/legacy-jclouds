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

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.DriveMetrics;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToDriveMetrics implements Function<Map<String, String>, Map<String, ? extends DriveMetrics>> {

   public Map<String, ? extends DriveMetrics> apply(Map<String, String> from) {
      Builder<String, DriveMetrics> builder = ImmutableMap.<String, DriveMetrics> builder();
      addIDEDevices(from, builder);
      addSCSIDevices(from, builder);
      addBlockDevices(from, builder);
      return builder.build();
   }

   protected void addBlockDevices(Map<String, String> from, Builder<String, DriveMetrics> devices) {
      BLOCK: for (int index : new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) {
         String key = String.format("block:0:%d", index);
         if (!from.containsKey(key))
            break BLOCK;
         devices.put(key, buildMetrics(key, from));
      }
   }

   protected void addSCSIDevices(Map<String, String> from, Builder<String, DriveMetrics> devices) {
      SCSI: for (int unit : new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) {
         String key = String.format("scsi:0:%d", unit);
         if (!from.containsKey(key))
            break SCSI;
         devices.put(key, buildMetrics(key, from));
      }
   }

   protected void addIDEDevices(Map<String, String> from, Builder<String, DriveMetrics> devices) {
      IDE: for (int bus : new int[] { 0, 1 })
         for (int unit : new int[] { 0, 1 }) {
            String key = String.format("ide:%d:%d", bus, unit);
            if (!from.containsKey(key))
               break IDE;
            devices.put(key, buildMetrics(key, from));
         }
   }

   protected DriveMetrics buildMetrics(String key, Map<String, String> from) {
      DriveMetrics.Builder builder = new DriveMetrics.Builder();
      if (from.containsKey(key + ":read:bytes"))
         builder.readBytes(new Long(from.get(key + ":read:bytes")));
      if (from.containsKey(key + ":read:requests"))
         builder.readRequests(new Long(from.get(key + ":read:requests")));
      if (from.containsKey(key + ":write:bytes"))
         builder.writeBytes(new Long(from.get(key + ":write:bytes")));
      if (from.containsKey(key + ":write:requests"))
         builder.writeRequests(new Long(from.get(key + ":write:requests")));
      return builder.build();
   }
}