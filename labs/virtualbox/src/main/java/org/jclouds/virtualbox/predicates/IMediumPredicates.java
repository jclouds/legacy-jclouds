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
package org.jclouds.virtualbox.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.any;

import org.virtualbox_4_1.DeviceType;
import org.virtualbox_4_1.IMedium;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class IMediumPredicates {
   public static class DeviceTypeEquals implements Predicate<IMedium> {
      private final DeviceType deviceType;

      public DeviceTypeEquals(DeviceType deviceType) {
         this.deviceType = checkNotNull(deviceType, "deviceType");
      }

      @Override
      public boolean apply(IMedium arg0) {
         return deviceType.equals(arg0.getDeviceType());
      }

      @Override
      public String toString() {
         return "deviceTypeEquals(" + deviceType + ")";
      }
   }

   public static Predicate<IMedium> deviceTypeEquals(DeviceType deviceType) {
      return new DeviceTypeEquals(deviceType);
   }

   public static enum HasParent implements Predicate<IMedium> {
      INSTANCE;

      @Override
      public boolean apply(IMedium arg0) {
         return arg0.getParent() != null;
      }

      @Override
      public String toString() {
         return "hasParent()";
      }
   }

   public static Predicate<IMedium> hasParent() {
      return HasParent.INSTANCE;
   }

   public static class MachineIdsContain implements Predicate<IMedium> {
      private final String id;

      public MachineIdsContain(String id) {
         this.id = checkNotNull(id, "id");
      }

      @Override
      public boolean apply(IMedium arg0) {
         return any(arg0.getMachineIds(), equalTo(id));
      }

      @Override
      public String toString() {
         return "machineIdsContain(" + id + ")";
      }
   }

   public static Predicate<IMedium> machineIdsContain(String id) {
      return new MachineIdsContain(id);
   }

}
