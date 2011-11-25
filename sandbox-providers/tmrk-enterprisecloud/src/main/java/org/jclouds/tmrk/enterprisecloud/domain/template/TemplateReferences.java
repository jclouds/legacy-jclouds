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
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps individual TemplateReference elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="TemplateReferences">
 * @author Jason King
 */
public class TemplateReferences {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateReferences(this);
   }

   public static class Builder {

       private Set<NamedResource> templateReferences = Sets.newLinkedHashSet();

       /**
        * @see org.jclouds.tmrk.enterprisecloud.domain.template.TemplateReferences#getTemplateReferences()
        */
       public Builder templateReferences(Set<NamedResource> templateReferences) {
          this.templateReferences = Sets.newLinkedHashSet(checkNotNull(templateReferences, "templateReferences"));
          return this;
       }

       public Builder addTemplateReference(NamedResource templateReference) {
          templateReferences.add(checkNotNull(templateReference,"templateReference"));
          return this;
       }

       public TemplateReferences build() {
           return new TemplateReferences(templateReferences);
       }

       public Builder fromTemplateReferences(TemplateReferences in) {
         return templateReferences(in.getTemplateReferences());
       }
   }

   private TemplateReferences() {
      //For JAXB and builder use
   }

   private TemplateReferences(Set<NamedResource> templateReferences) {
      this.templateReferences = Sets.newLinkedHashSet(templateReferences);
   }

   @XmlElement(name = "Template")
   private Set<NamedResource> templateReferences = Sets.newLinkedHashSet();

   public Set<NamedResource> getTemplateReferences() {
      return Collections.unmodifiableSet(templateReferences);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateReferences that = (TemplateReferences) o;

      if (templateReferences != null ? !templateReferences.equals(that.templateReferences) : that.templateReferences != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return templateReferences != null ? templateReferences.hashCode() : 0;
   }

   public String toString() {
      return "["+ templateReferences.toString()+"]";
   }
}
