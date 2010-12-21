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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudsigma.domain.BlockDevice;
import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.IDEDevice;
import org.jclouds.cloudsigma.domain.MediaType;
import org.jclouds.cloudsigma.domain.SCSIDevice;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class MapToDevices implements Function<Map<String, String>, Map<String, ? extends Device>> {
   @Singleton
   public static class DeviceToId implements Function<Device, String> {
      @Override
      public String apply(Device input) {
         return input.getId();
      }
   }

   private final Function<Device, String> deviceToId;

   @Inject
   public MapToDevices(Function<Device, String> deviceToId) {
      this.deviceToId = deviceToId;
   }

   public Map<String, ? extends Device> apply(Map<String, String> from) {
      Builder<Device> devices = ImmutableSet.builder();
      addIDEDevices(from, devices);
      addSCSIDevices(from, devices);
      addBlockDevices(from, devices);

      return Maps.uniqueIndex(devices.build(), deviceToId);
   }

   protected void addBlockDevices(Map<String, String> from, Builder<Device> devices) {
      BLOCK: for (int index : new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) {
         String key = String.format("block:0:%d", index);
         if (!from.containsKey(key))
            break BLOCK;
         devices.add(populateBuilder(new BlockDevice.Builder(index), key, from).build());
      }
   }

   protected void addSCSIDevices(Map<String, String> from, Builder<Device> devices) {
      SCSI: for (int unit : new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }) {
         String key = String.format("scsi:0:%d", unit);
         if (!from.containsKey(key))
            break SCSI;
         devices.add(populateBuilder(new SCSIDevice.Builder(unit), key, from).build());
      }
   }

   protected void addIDEDevices(Map<String, String> from, Builder<Device> devices) {
      IDE: for (int bus : new int[] { 0, 1 })
         for (int unit : new int[] { 0, 1 }) {
            String key = String.format("ide:%d:%d", bus, unit);
            if (!from.containsKey(key))
               break IDE;
            devices.add(populateBuilder(new IDEDevice.Builder(bus, unit), key, from).build());
         }
   }

   protected Device.Builder populateBuilder(Device.Builder deviceBuilder, String key, Map<String, String> from) {
      deviceBuilder.uuid(from.get(key));
      if (from.containsKey(key + ":media"))
         deviceBuilder.mediaType(MediaType.fromValue(from.get(key + ":media")));
      return deviceBuilder;
   }
}