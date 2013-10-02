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

import org.jclouds.cim.OSType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.Envelope;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Operating system based on DMTF CIM model.
 * 
 * @author Adrian Cole
 * @see <a href="http://dmtf.org/standards/cim/cim_schema_v2260">DMTF CIM model</a>
 */
@Beta
public class CIMOperatingSystem extends OperatingSystem {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends OperatingSystem.Builder {
      private OSType osType;

      /**
       * @see CIMOperatingSystem#getOsType
       */
      public Builder osType(@Nullable OSType osType) {
         this.osType = osType;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public CIMOperatingSystem build() {
         return new CIMOperatingSystem(family, name, version, arch, description, is64Bit, osType);
      }

      public Builder fromCIMOperatingSystem(CIMOperatingSystem in) {
         return fromOperatingSystem(in).osType(in.getOsType());
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public Builder arch(String arch) {
         return Builder.class.cast(super.arch(arch));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder description(String description) {
         return Builder.class.cast(super.description(description));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder family(OsFamily family) {
         return Builder.class.cast(super.family(family));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromOperatingSystem(OperatingSystem in) {
         return Builder.class.cast(super.fromOperatingSystem(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder is64Bit(boolean is64Bit) {
         return Builder.class.cast(super.is64Bit(is64Bit));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder version(String version) {
         return Builder.class.cast(super.version(version));
      }
   }

   public static CIMOperatingSystem toComputeOs(org.jclouds.ovf.OperatingSystemSection os) {
      return new CIMOperatingSystem(OSType.fromValue(os.getId()), "", null, os.getDescription());
   }

   public static CIMOperatingSystem toComputeOs(Envelope ovf) {
      return toComputeOs(ovf.getVirtualSystem().getOperatingSystemSection());
   }

   private OSType osType;

   protected CIMOperatingSystem() {
      super();
   }

   public CIMOperatingSystem(OSType osType, String version, String arch, String description) {
      this(osType.getFamily(), osType.getValue(), version, arch, description, osType.is64Bit(), osType);
   }

   public CIMOperatingSystem(@Nullable OsFamily family, @Nullable String name, @Nullable String version,
            @Nullable String arch, String description, boolean is64Bit, OSType osType) {
      super(family, name, version, arch, description, is64Bit);
      this.osType = osType;

   }

   /**
    * CIM OSType of the image
    */
   public OSType getOsType() {
      return osType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((osType == null) ? 0 : osType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CIMOperatingSystem other = (CIMOperatingSystem) obj;
      if (osType == null) {
         if (other.osType != null)
            return false;
      } else if (!osType.equals(other.osType))
         return false;
      return true;
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("osType", osType);
   }
}
