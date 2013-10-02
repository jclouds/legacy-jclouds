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
package org.jclouds.compute.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Running Operating system
 * 
 * @author Adrian Cole
 */
@Beta
public class OperatingSystem {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected OsFamily family;
      protected String name;
      protected String arch;
      protected String version;
      protected String description;
      protected boolean is64Bit;

      public Builder family(@Nullable OsFamily family) {
         this.family = family;
         return this;
      }

      public Builder name(@Nullable String name) {
         this.name = name;
         return this;
      }

      public Builder arch(@Nullable String arch) {
         this.arch = arch;
         return this;
      }

      public Builder version(@Nullable String version) {
         this.version = version;
         return this;
      }

      public Builder description(String description) {
         this.description = checkNotNull(description, "description");
         return this;
      }

      public Builder is64Bit(boolean is64Bit) {
         this.is64Bit = is64Bit;
         return this;
      }

      public OperatingSystem build() {
         return new OperatingSystem(family, name, version, arch, description, is64Bit);
      }

      public Builder fromOperatingSystem(OperatingSystem in) {
         return family(in.getFamily()).name(in.getName()).version(in.getVersion()).arch(in.getArch()).description(
                  in.getDescription()).is64Bit(in.is64Bit());
      }
   }

   @Nullable
   protected OsFamily family;
   @Nullable
   protected String name;
   @Nullable
   protected String arch;
   @Nullable
   protected String version;
   protected String description;
   protected boolean is64Bit;

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OperatingSystem that = OperatingSystem.class.cast(o);
      return equal(this.family, that.family) && equal(this.name, that.name) && equal(this.arch, that.arch)
               && equal(this.version, that.version) && equal(this.description, that.description)
               && equal(this.is64Bit, that.is64Bit);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(family, name, arch, version, description, is64Bit);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("family", family).add("name", name).add("arch", arch)
               .add("version", version).add("description", description).add("is64Bit", is64Bit);
   }

   // for serialization/deserialization
   protected OperatingSystem() {

   }

   public OperatingSystem(@Nullable OsFamily family, @Nullable String name, @Nullable String version,
            @Nullable String arch, String description, boolean is64Bit) {
      this.family = family;
      this.name = name;
      this.arch = arch;
      this.version = version;
      this.description = checkNotNull(description, "description");
      this.is64Bit = is64Bit;
   }

   /**
    * Type of the operating system
    * <p/>
    * generally, this is used to compare the means by which you use an operating system. For
    * example, to determine compatibility of a particular bootstrapping or package installation
    * approach.
    */
   @Nullable
   public OsFamily getFamily() {
      return family;
   }

   /**
    * name of the operating system; ex. {@code Red Hat Enterprise Linux}
    * 
    * <h2>note</h2> While this looks similar to, and may in some cases be the same as the java
    * system property {@code os.name} it isn't guaranteed to match a particular value. For example,
    * this value could be derived from data parsed for a cloud api or the OVF CIM OSType enum value;
    * 
    * @return operating system name or null if it couldn't be determined.
    */
   @Nullable
   public String getName() {
      return name;
   }

   /**
    * architecture of the operating system; ex. {@code x86_64}
    * <p/>
    * generally, this is used to decide whether an operating system will run certain binaries, for
    * example, a 64bit JDK.
    * 
    * <h2>note</h2>
    * While this looks similar to, and may in some cases be the same as the java system property
    * {@code os.arch} it isn't guaranteed to match a particular value. For example, this value could
    * be derived from data parsed for a cloud api or the OVF CIM OSType enum value;
    * 
    * @return operating system architecture or null if it couldn't be determined.
    */
   @Nullable
   public String getArch() {
      return arch;
   }

   /**
    * version of the operating system; ex. {@code 10.0.4}
    * <p/>
    * generally, this is used to compare versions of the same operating system name. It should be
    * meaningful when sorted against, although this isn't necessary.
    * <h2>note</h2>
    * While this looks similar to, and may in some cases be the same as the java system property
    * {@code os.version} it isn't guaranteed to match a particular value. For example, this value
    * could be derived from data parsed for a cloud api or the OVF CIM OSType enum value;
    * 
    * @return operating system version or null if it couldn't be determined.
    */
   @Nullable
   public String getVersion() {
      return version;
   }

   /**
    * description of the operating system; ex. {@code CentOS 32-bit},{@code Other Linux (32-bit)}
    * <p/>
    * This is the only required field in the operating system object. In some implementations, it is
    * this data that is used to parse the value of the {@link #name}, {@link #version}, and
    * {@link #arch} fields.
    * 
    * @return operating system description
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return whether this operating system supports 64 bit computation.
    */
   public boolean is64Bit() {
      return is64Bit;
   }

   public Builder toBuilder() {
      return builder().fromOperatingSystem(this);
   }

}
