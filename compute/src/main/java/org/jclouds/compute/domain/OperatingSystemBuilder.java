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

package org.jclouds.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class OperatingSystemBuilder {
   @Nullable
   private OsFamily family;
   @Nullable
   private String name;
   @Nullable
   private String arch;
   @Nullable
   private String version;
   private String description;
   private boolean is64Bit;

   public OperatingSystemBuilder family(@Nullable OsFamily family) {
      this.family = family;
      return this;
   }

   public OperatingSystemBuilder name(@Nullable String name) {
      this.name = name;
      return this;
   }

   public OperatingSystemBuilder arch(@Nullable String arch) {
      this.arch = arch;
      return this;
   }

   public OperatingSystemBuilder version(@Nullable String version) {
      this.version = version;
      return this;
   }

   public OperatingSystemBuilder description(String description) {
      this.description = checkNotNull(description, "description");
      return this;
   }

   public OperatingSystemBuilder is64Bit(boolean is64Bit) {
      this.is64Bit = is64Bit;
      return this;
   }

   public OperatingSystem build() {
      return new OperatingSystem(family, name, version, arch, description, is64Bit);
   }

   public static OperatingSystem fromOperatingSystem(OperatingSystem in) {
      return new OperatingSystem(in.getFamily(), in.getName(), in.getVersion(), in.getArch(), in.getDescription(),
            in.is64Bit());
   }
}