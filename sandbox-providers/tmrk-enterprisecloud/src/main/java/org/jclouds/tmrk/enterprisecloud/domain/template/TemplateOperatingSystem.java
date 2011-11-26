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
package org.jclouds.tmrk.enterprisecloud.domain.template;

import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;

import javax.xml.bind.annotation.XmlElement;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="TemplateOperatingSystem">
 * @author Jason King
 */
public class TemplateOperatingSystem {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateOperatingSystem(this);
   }

   public static class Builder {

      private String name;
      private Set<NamedResource> templates = Sets.newLinkedHashSet();

      /**
       * @see TemplateOperatingSystem#getTemplates
       */
      public Builder templates(Set<NamedResource> templates) {
        this.templates = Sets.newLinkedHashSet(checkNotNull(templates, "templates"));
        return this;
      }

      /**
       * @see TemplateOperatingSystem#getName
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public TemplateOperatingSystem build() {
         return new TemplateOperatingSystem(name,templates);
      }

      public Builder fromTemplateOperatingSystem(TemplateOperatingSystem in) {
         return name(in.getName()).templates(in.getTemplates());
      }
   }

   @XmlElement(name = "Name", required = false)
   private String name;

   @XmlElement(name = "Templates", required = false)
   private TemplateReferences templates = TemplateReferences.builder().build();

   private TemplateOperatingSystem(@Nullable String name, Set<NamedResource> templates) {
      this.name = name;
      this.templates = TemplateReferences.builder().templateReferences(checkNotNull(templates,"templates")).build();
   }

   private TemplateOperatingSystem() {
       //For JAXB
   }

   @Nullable
   public String getName() {
       return name;
   }

   public Set<NamedResource> getTemplates() {
       return templates.getTemplateReferences();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateOperatingSystem that = (TemplateOperatingSystem) o;

      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;
      if (templates != null ? !templates.equals(that.templates) : that.templates != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (templates != null ? templates.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[name="+name+", +templates="+templates+"]";
   }
}