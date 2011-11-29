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

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual TemplateCategory elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="TemplateCategories">
 * @author Jason King
 */
public class TemplateCategories {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateCategories(this);
   }

   public static class Builder {

       private Set<TemplateCategory> categories = Sets.newLinkedHashSet();

       /**
        * @see TemplateCategories#getTemplateCategories()
        */
       public Builder categories(Set<TemplateCategory> categories) {
          this.categories = Sets.newLinkedHashSet(checkNotNull(categories, "categories"));
          return this;
       }

       public Builder addCategory(TemplateCategory category) {
          categories.add(checkNotNull(category,"category"));
          return this;
       }

       public TemplateCategories build() {
           return new TemplateCategories(categories);
       }

       public Builder fromTemplateCategories(TemplateCategories in) {
         return categories(in.getTemplateCategories());
       }
   }

   private TemplateCategories() {
      //For JAXB and builder use
   }

   private TemplateCategories(Set<TemplateCategory> categories) {
      this.categories = Sets.newLinkedHashSet(categories);
   }

   @XmlElement(name = "Category")
   private Set<TemplateCategory> categories = Sets.newLinkedHashSet();

   public Set<TemplateCategory> getTemplateCategories() {
      return Collections.unmodifiableSet(categories);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateCategories that = (TemplateCategories) o;

      if (categories != null ? !categories.equals(that.categories) : that.categories != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return categories != null ? categories.hashCode() : 0;
   }

   public String toString() {
      return "["+ categories.toString()+"]";
   }
}
