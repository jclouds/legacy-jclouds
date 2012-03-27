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

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Specifies who can access the resource.
 *
 * <pre>
 * &lt;complexType name="AccessSetting" /&gt;
 * </pre>
 *
 * @since 0.9
 */
@XmlRootElement(name = "AccessSetting")
@XmlType(name = "AccessSettingType")
public class AccessSetting {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAccessSetting(this);
   }

   public static class Builder {

      private Reference subject;
      private String accessLevel;

      /**
       * @see AccessSetting#getSubject()
       */
      public Builder subject(Reference subject) {
         this.subject = subject;
         return this;
      }

      /**
       * @see AccessSetting#getAccessLevel()
       */
      public Builder accessLevel(String accessLevel) {
         this.accessLevel = accessLevel;
         return this;
      }

      public AccessSetting build() {
         AccessSetting accessSetting = new AccessSetting(subject, accessLevel);
         return accessSetting;
      }

      public Builder fromAccessSetting(AccessSetting in) {
         return subject(in.getSubject()).accessLevel(in.getAccessLevel());
      }
   }

   protected AccessSetting() {
      // For JAXB and builder use
   }

   public AccessSetting(Reference subject, String accessLevel) {
      this.subject = subject;
      this.accessLevel = accessLevel;
   }

   @XmlElement(name = "Subject", required = true)
   protected Reference subject;
   @XmlElement(name = "AccessLevel", required = true)
   protected String accessLevel;

   /**
    * Gets the value of the subject property.
    */
   public Reference getSubject() {
      return subject;
   }

   /**
    * Gets the value of the accessLevel property.
    */
   public String getAccessLevel() {
      return accessLevel;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AccessSetting that = AccessSetting.class.cast(o);
      return equal(this.subject, that.subject) && equal(this.accessLevel, that.accessLevel);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(subject, accessLevel);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("subject", subject).add("accessLevel", accessLevel).toString();
   }
}
