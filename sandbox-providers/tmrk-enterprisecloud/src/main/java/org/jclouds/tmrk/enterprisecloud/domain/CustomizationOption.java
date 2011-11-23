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
package org.jclouds.tmrk.enterprisecloud.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="CustomizationOption">
 * @author Jason King
 */
public class CustomizationOption {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromConfigurationOptionRange(this);
   }

   public static class Builder {

      private CustomizationType type;
      private boolean canPowerOn;
      private boolean passwordRequired;
      private boolean sshKeyRequired;

      /**
       * @see CustomizationOption#getType
       */
      public Builder type(CustomizationType type) {
         this.type = type;
         return this;
      }

      /**
       * @see CustomizationOption#canPowerOn
       */
      public Builder canPowerOn(boolean canPowerOn) {
         this.canPowerOn = canPowerOn;
         return this;
      }

      /**
       * @see CustomizationOption#isPasswordRequired()
       */
      public Builder passwordRequired(boolean passwordRequired) {
         this.passwordRequired = passwordRequired;
         return this;
      }

      /**
       * @see CustomizationOption#isSshKeyRequired()
       */
      public Builder sshKeyRequired(boolean sshKeyRequired) {
         this.sshKeyRequired = sshKeyRequired;
         return this;
      }

      public CustomizationOption build() {
         return new CustomizationOption(type, canPowerOn, passwordRequired,sshKeyRequired);
      }

      public Builder fromConfigurationOptionRange(CustomizationOption in) {
        return type(in.getType()).canPowerOn(in.canPowerOn()).passwordRequired(in.isPasswordRequired()).sshKeyRequired(in.isSshKeyRequired());
      }
   }

   @XmlElement(name = "Type")
   private CustomizationType type;

   @XmlElement(name = "CanPowerOn")
   private boolean canPowerOn;

   @XmlElement(name = "PasswordRequired")
   private boolean passwordRequired;

   @XmlElement(name = "SshKeyRequired")
   private boolean sshKeyRequired;

   private CustomizationOption(CustomizationType type, boolean canPowerOn, boolean passwordRequired, boolean sshKeyRequired) {
      this.type = checkNotNull(type,"type");
      this.canPowerOn = canPowerOn;
      this.passwordRequired = passwordRequired;
      this.sshKeyRequired = sshKeyRequired;
   }

   private CustomizationOption() {
      //For JAXB
   }

   public CustomizationType getType() {
      return type;
   }

   public boolean canPowerOn() {
      return canPowerOn;
   }

   public boolean isPasswordRequired() {
      return passwordRequired;
   }

   public boolean isSshKeyRequired() {
      return sshKeyRequired;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CustomizationOption that = (CustomizationOption) o;

      if (canPowerOn != that.canPowerOn) return false;
      if (passwordRequired != that.passwordRequired) return false;
      if (sshKeyRequired != that.sshKeyRequired) return false;
      if (type != that.type) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = type.hashCode();
      result = 31 * result + (canPowerOn ? 1 : 0);
      result = 31 * result + (passwordRequired ? 1 : 0);
      result = 31 * result + (sshKeyRequired ? 1 : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[type="+type+", canPowerOn="+ canPowerOn +", passwordRequired="+ passwordRequired +", sshKeyRequired="+sshKeyRequired+"]";
   }

   @XmlEnum
   public enum CustomizationType {
      @XmlEnumValue("Linux")
      LINUX,

      @XmlEnumValue("Windows")
      WINDOWS;
   }

}
