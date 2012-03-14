/*
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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A list of access settings for a resource.
 *
 * <pre>
 * &lt;complexType name="AccessSettings" /&gt;
 * </pre>
 *
 * @since 0.9
 */
@XmlRootElement(name = "AccessSettings")
@XmlType(name = "AccessSettingsType")
public class AccessSettings {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAccessSettings(this);
   }

   public static class Builder {

      private List<AccessSetting> accessSettings = Lists.newArrayList();

      /**
       * @see AccessSettings#getAccessSettings()
       */
      public Builder accessSettings(List<AccessSetting> accessSettings) {
         this.accessSettings = checkNotNull(accessSettings, "accessSettings");
         return this;
      }

      /**
       * @see AccessSettings#getAccessSettings()
       */
      public Builder accessSetting(AccessSetting accessSetting) {
         this.accessSettings.add(checkNotNull(accessSetting, "accessSetting"));
         return this;
      }

      public AccessSettings build() {
         return new AccessSettings(accessSettings);
      }

      public Builder fromAccessSettings(AccessSettings in) {
         return accessSettings(in.getAccessSettings());
      }
   }

   protected AccessSettings() {
      // For JAXB and builder use
   }

   public AccessSettings(List<AccessSetting> accessSettings) {
      this.accessSettings = accessSettings;
   }

   @XmlElement(name = "AccessSetting", required = true)
   protected List<AccessSetting> accessSettings = Lists.newArrayList();

   /**
    * Gets the value of the accessSetting property.
    */
   public List<AccessSetting> getAccessSettings() {
      return accessSettings;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AccessSettings that = AccessSettings.class.cast(o);
      return equal(this.accessSettings, that.accessSettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(accessSettings);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("accessSettings", accessSettings).toString();
   }
}
