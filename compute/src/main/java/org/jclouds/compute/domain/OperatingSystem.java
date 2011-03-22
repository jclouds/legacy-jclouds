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

import com.google.common.annotations.Beta;

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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((arch == null) ? 0 : arch.hashCode());
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((family == null) ? 0 : family.hashCode());
      result = prime * result + (is64Bit ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OperatingSystem other = (OperatingSystem) obj;
      if (arch == null) {
         if (other.arch != null)
            return false;
      } else if (!arch.equals(other.arch))
         return false;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (family == null) {
         if (other.family != null)
            return false;
      } else if (!family.equals(other.family))
         return false;
      if (is64Bit != other.is64Bit)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

   public Builder toBuilder() {
      return builder().fromOperatingSystem(this);
   }

   @Override
   public String toString() {
      return "[name=" + name + ", family=" + family + ", version=" + version + ", arch=" + arch + ", is64Bit="
               + is64Bit + ", description=" + description + "]";
   }

}