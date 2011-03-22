/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <name@cloudconscious.com>
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

package org.jclouds.savvis.vpdc.domain;

import org.jclouds.cim.OSType;
import org.jclouds.compute.domain.OsFamily;

/**
 * Savvis Operating System Specification
 * 
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/addSingleVM.html" />
 * @author Adrian Cole
 */
public class SavvisOperatingSystemSpecification {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private int savvisId;
      private int typeId;
      private String name;
      private OsFamily family;
      private String description;
      private boolean supportedBySavvis;

      /**
       * @see SavvisOperatingSystemSpecification#getSavvisId
       */
      public Builder savvisId(int savvisId) {
         this.savvisId = savvisId;
         return this;
      }

      /**
       * @see SavvisOperatingSystemSpecification#getTypeId
       */
      public Builder typeId(int typeId) {
         this.typeId = typeId;
         return this;
      }

      /**
       * @see SavvisOperatingSystemSpecification#getOsFamily
       */
      public Builder family(OsFamily family) {
         this.family = family;
         return this;
      }

      /**
       * @see SavvisOperatingSystemSpecification#getInfo
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see SavvisOperatingSystemSpecification#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see SavvisOperatingSystemSpecification#isSupportedBySavvis
       */
      public Builder supportedBySavvis(boolean supportedBySavvis) {
         this.supportedBySavvis = supportedBySavvis;
         return this;
      }

      public SavvisOperatingSystemSpecification build() {
         return new SavvisOperatingSystemSpecification(savvisId, typeId, family, name, description, supportedBySavvis);
      }

      public Builder fromSavvisOperatingSystemSpecification(SavvisOperatingSystemSpecification in) {
         return savvisId(in.getSavvisId()).typeId(in.getTypeId()).family(in.getFamily()).name(in.getName())
                  .description(in.getDescription()).supportedBySavvis(in.isSupportedBySavvis());
      }
   }

   private final int savvisId;
   private final int typeId;
   private final OsFamily family;
   private final String name;
   private final String description;
   private final boolean supportedBySavvis;

   public SavvisOperatingSystemSpecification(int savvisId, int typeId, OsFamily family, String name,
            String description, boolean supportedBySavvis) {
      this.savvisId = savvisId;
      this.typeId = typeId;
      this.family = family;
      this.name = name;
      this.description = description;
      this.supportedBySavvis = supportedBySavvis;
   }

   /**
    * @return Internal name used by Savvis
    */
   public int getSavvisId() {
      return savvisId;
   }

   /**
    * 
    * @return CIM OS Type Id
    * @see OSType#getCode()
    */
   public int getTypeId() {
      return typeId;
   }

   /**
    * 
    * @return the name of the OS
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * @return the operating system family
    */
   public OsFamily getFamily() {
      return family;
   }

   /**
    * 
    * @return description
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * @return whether or not this operating system is supported by Savvis
    */
   public boolean isSupportedBySavvis() {
      return supportedBySavvis;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + typeId;
      result = prime * result + savvisId;
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
      SavvisOperatingSystemSpecification other = (SavvisOperatingSystemSpecification) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (typeId != other.typeId)
         return false;
      if (savvisId != other.savvisId)
         return false;
      return true;
   }

   public Builder toBuilder() {
      return builder().fromSavvisOperatingSystemSpecification(this);
   }

   @Override
   public String toString() {
      return String.format("[description=%s, family=%s, name=%s, typeId=%s, savvisId=%s, supportedBySavvis=%s]",
               description, family, name, typeId, savvisId, supportedBySavvis);
   }

}