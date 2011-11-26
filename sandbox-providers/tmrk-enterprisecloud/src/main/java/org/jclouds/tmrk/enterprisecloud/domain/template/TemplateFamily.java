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

import javax.xml.bind.annotation.XmlElement;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="TemplateFamily">
 * @author Jason King
 */
public class TemplateFamily {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateFamily(this);
   }

   public static class Builder {

      private String name;
      private Set<TemplateCategory> categories = Sets.newLinkedHashSet();

      /**
       * @see TemplateFamily#getTemplateCategories
       */
      public Builder templateCategories(Set<TemplateCategory> categories) {
        this.categories = Sets.newLinkedHashSet(checkNotNull(categories, "categories"));
        return this;
      }

      /**
       * @see TemplateFamily#getName
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public TemplateFamily build() {
         return new TemplateFamily(name,categories);
      }

      public Builder fromTemplateFamily(TemplateFamily in) {
         return name(in.getName()).templateCategories(in.getTemplateCategories());
      }
   }

   @XmlElement(name = "Name", required = false)
   private String name;

   @XmlElement(name = "Categories", required = false)
   private TemplateCategories categories = TemplateCategories.builder().build();

   private TemplateFamily(@Nullable String name, Set<TemplateCategory> categories) {
      this.name = name;
      this.categories = TemplateCategories.builder().categories(categories).build();
   }

   protected TemplateFamily() {
       //For JAXB
   }

   @Nullable
   public String getName() {
       return name;
   }

   public Set<TemplateCategory> getTemplateCategories() {
       return categories.getTemplateCategories();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateFamily that = (TemplateFamily) o;

      if (categories != null ? !categories.equals(that.categories) : that.categories != null)
         return false;
      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (categories != null ? categories.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[name="+name+", +categories="+categories+"]";
   }
}