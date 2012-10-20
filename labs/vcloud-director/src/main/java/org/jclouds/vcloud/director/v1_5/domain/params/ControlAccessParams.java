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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.AccessSetting;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Used to control access to resources.
 *
 * <pre>
 * &lt;complexType name="ControlAccessParams" /&gt;
 * </pre>
 *
 * @since 0.9
 */
@XmlRootElement(name = "ControlAccessParams")
@XmlType(name = "ControlAccessParamsType")
public class ControlAccessParams {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromControlAccessParams(this);
   }

   public static class Builder {

      private Boolean sharedToEveryone = Boolean.FALSE;
      private String everyoneAccessLevel;
      private Set<AccessSetting> accessSettings;

      /**
       * @see ControlAccessParams#getIsSharedToEveryone()
       */
      public Builder isSharedToEveryone(Boolean sharedToEveryone) {
         this.sharedToEveryone = sharedToEveryone;
         return this;
      }

      /**
       * @see ControlAccessParams#getIsSharedToEveryone()
       */
      public Builder sharedToEveryone() {
         this.sharedToEveryone = Boolean.TRUE;
         return this;
      }

      /**
       * @see ControlAccessParams#getIsSharedToEveryone()
       */
      public Builder notSharedToEveryone() {
         this.sharedToEveryone = Boolean.FALSE;
         return this;
      }

      /**
       * @see ControlAccessParams#getEveryoneAccessLevel()
       */
      public Builder everyoneAccessLevel(String everyoneAccessLevel) {
         this.everyoneAccessLevel = everyoneAccessLevel;
         return this;
      }

      /**
       * @see ControlAccessParams#getAccessSettings()
       */
      public Builder accessSettings(Iterable<AccessSetting> accessSettings) {
         this.accessSettings = Sets.newLinkedHashSet(checkNotNull(accessSettings));
         return this;
      }

      /**
       * @see ControlAccessParams#getAccessSettings()
       */
      public Builder accessSetting(AccessSetting accessSetting) {
         if (accessSettings == null) {
            accessSettings = Sets.newLinkedHashSet();
         }
         accessSettings.add(checkNotNull(accessSetting, "accessSetting"));
         return this;
      }

      public ControlAccessParams build() {
         ControlAccessParams controlAccessParams = new ControlAccessParams(sharedToEveryone, everyoneAccessLevel, accessSettings);
         return controlAccessParams;
      }

      public Builder fromControlAccessParams(ControlAccessParams in) {
         return isSharedToEveryone(in.isSharedToEveryone()).everyoneAccessLevel(in.getEveryoneAccessLevel()).accessSettings(in.getAccessSettings());
      }
   }

   protected ControlAccessParams() {
      // For JAXB and builder use
   }

   public ControlAccessParams(Boolean sharedToEveryone, String everyoneAccessLevel, Iterable<AccessSetting> accessSettings) {
      this.sharedToEveryone = sharedToEveryone;
      if (sharedToEveryone) {
	      this.everyoneAccessLevel = checkNotNull(everyoneAccessLevel, "everyoneAccessLevel");
	      this.accessSettings = null;
      } else {
	      this.everyoneAccessLevel = null;
            this.accessSettings = Iterables.isEmpty(checkNotNull(accessSettings, "accessSettings")) ? null : ImmutableList.copyOf(accessSettings);
      }
   }

   @XmlElement(name = "IsSharedToEveryone", required = true)
   protected Boolean sharedToEveryone;
   @XmlElement(name = "EveryoneAccessLevel")
   protected String everyoneAccessLevel;
   @XmlElementWrapper(name = "AccessSettings")
   @XmlElement(name = "AccessSetting")
   protected List<AccessSetting> accessSettings;

   /**
    * If true, this means that the resource is shared with everyone in the organization.
    *
    * Defaults to false. Sharing settings must be manipulated through the organization.
    */
   public Boolean isSharedToEveryone() {
      return sharedToEveryone;
   }

   /**
    * If {@link #isSharedToEveryone()} is true, this element must be present and determines the access level.
    */
   public String getEveryoneAccessLevel() {
      return everyoneAccessLevel;
   }

   /**
    * The access settings to be applied if {@link #isSharedToEveryone()} is false.
    *
    * Required on create and edit if {@link #isSharedToEveryone()} is false.
    */
   public List<AccessSetting> getAccessSettings() {
      return accessSettings == null ? ImmutableList.<AccessSetting>of() : accessSettings;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ControlAccessParams that = ControlAccessParams.class.cast(o);
      return equal(this.sharedToEveryone, that.sharedToEveryone) && equal(this.everyoneAccessLevel, that.everyoneAccessLevel) && equal(this.accessSettings, that.accessSettings);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sharedToEveryone, everyoneAccessLevel, accessSettings);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("sharedToEveryone", sharedToEveryone).add("everyoneAccessLevel", everyoneAccessLevel).add("accessSettings", accessSettings).toString();
   }
}
