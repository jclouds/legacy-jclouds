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
 * Wraps individual TemplateFamily elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="TemplateFamilies">
 * @author Jason King
 */
public class TemplateFamilies {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateFamilies(this);
   }

   public static class Builder {

       private Set<TemplateFamily> families = Sets.newLinkedHashSet();

       /**
        * @see TemplateFamilies#getTemplateFamilies()
        */
       public Builder families(Set<TemplateFamily> families) {
          this.families = Sets.newLinkedHashSet(checkNotNull(families, "families"));
          return this;
       }

       public Builder addFamily(TemplateFamily family) {
          families.add(checkNotNull(family,"family"));
          return this;
       }

       public TemplateFamilies build() {
           return new TemplateFamilies(families);
       }

       public Builder fromTemplateFamilies(TemplateFamilies in) {
         return families(in.getTemplateFamilies());
       }
   }

   private TemplateFamilies() {
      //For JAXB and builder use
   }

   private TemplateFamilies(Set<TemplateFamily> families) {
      this.families = Sets.newLinkedHashSet(families);
   }

   @XmlElement(name = "Family")
   private Set<TemplateFamily> families = Sets.newLinkedHashSet();

   public Set<TemplateFamily> getTemplateFamilies() {
      return Collections.unmodifiableSet(families);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateFamilies that = (TemplateFamilies) o;

      if (families != null ? !families.equals(that.families) : that.families != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return families != null ? families.hashCode() : 0;
   }

   public String toString() {
      return "["+ families.toString()+"]";
   }
}
