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
package org.jclouds.cloudstack.domain;

/**
 * @author Richard Downer
 */
public class TemplateMetadata {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String name;
      private long osTypeId;
      private String displayText;

      /**
       * @param name the name of the template
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param osTypeId the ID of the OS Type that best represents the OS of this template.
       */
      public Builder osTypeId(long osTypeId) {
         this.osTypeId = osTypeId;
         return this;
      }

      /**
       * @param displayText the display text of the template. This is usually used for display purposes.
       */
      public Builder displayText(String displayText) {
         this.displayText = displayText;
         return this;
      }

      public TemplateMetadata build() {
         return new TemplateMetadata(name, osTypeId, displayText);
      }
   }

   private String name;
   private long osTypeId;
   private String displayText;

   public TemplateMetadata(String name, long osTypeId, String displayText) {
      this.name = name;
      this.osTypeId = osTypeId;
      this.displayText = displayText;
   }

   /**
    * present only for serializer
    */
   TemplateMetadata() {
   }

   /**
    * @return the name of the template
    */
   public String getName() {
      return name;
   }

   /**
    * @return the ID of the OS Type that best represents the OS of this template.
    */
   public long getOsTypeId() {
      return osTypeId;
   }

   /**
    * @return the display text of the template. This is usually used for display purposes.
    */
   public String getDisplayText() {
      return displayText;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateMetadata that = (TemplateMetadata) o;

      if (osTypeId != that.osTypeId) return false;
      if (displayText != null ? !displayText.equals(that.displayText) : that.displayText != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (int) (osTypeId ^ (osTypeId >>> 32));
      result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
         "name='" + name + '\'' +
         ", osTypeId=" + osTypeId +
         ", displayText='" + displayText + '\'' +
         ']';
   }

}
