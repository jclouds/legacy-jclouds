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
package org.jclouds.dmtf.ovf;

import static org.jclouds.dmtf.DMTFConstants.OVF_NS;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlType(name = "Configuration", namespace = OVF_NS, propOrder = {
      "label", "description"
})
public class Configuration {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String id;
      private boolean isDefault;
      private String label;
      private String description;

      /**
       * @see Configuration#getId
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Configuration#getLabel
       */
      public Builder label(String label) {
         this.label = label;
         return this;
      }

      /**
       * @see Configuration#getDescription
       */
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see Configuration#isDefault
       */
      public Builder isDefault(boolean isDefault) {
         this.isDefault = isDefault;
         return this;
      }
      
      public Configuration build() {
         return new Configuration(id, isDefault, label, description);
      }

      public Builder fromConfiguration(Configuration in) {
         return id(in.getId()).description(in.getDescription()).label(in.getLabel());
      }
   }

   @XmlAttribute
   private String id;
   @XmlAttribute(name = "default")
   private boolean isDefault;
   @XmlElement(name = "Label")
   private String label;
   @XmlElement(name = "Description")
   private String description;
   
   public Configuration(String id, boolean isDefault, String label, String description) {
      this.id = id;
      this.label = label;
      this.description = description;
      this.isDefault = isDefault;
   }

   public Configuration() {
      // for JAXB
   }
   
   @Override
   public int hashCode() {
      return Objects.hashCode(id, label, description);
  }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Configuration other = (Configuration) obj;
      return Objects.equal(id, other.id)
            && Objects.equal(label, other.label)
            && Objects.equal(description, other.description);
   }


   @Override
   public String toString() {
      return string().toString();
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper("").add("id", id).add("default", isDefault).add("label", label).add("description", description);
   }

   public String getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public String getLabel() {
      return label;
   }

   public boolean isDefault() {
      return isDefault;
   }
}
