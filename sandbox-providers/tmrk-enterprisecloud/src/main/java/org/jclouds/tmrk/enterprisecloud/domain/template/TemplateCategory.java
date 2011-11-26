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
 * <xs:complexType name="TemplateCategory">
 * @author Jason King
 */
public class TemplateCategory {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateCategory(this);
   }

   public static class Builder {

      private String name;
      private Set<TemplateOperatingSystem> operatingSystems = Sets.newLinkedHashSet();

      /**
       * @see TemplateCategory#getTemplateOperatingSystems
       */
      public Builder templateOperatingSystems(Set<TemplateOperatingSystem> operatingSystems) {
        this.operatingSystems = Sets.newLinkedHashSet(checkNotNull(operatingSystems, "operatingSystems"));
        return this;
      }

      /**
       * @see TemplateCategory#getName
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public TemplateCategory build() {
         return new TemplateCategory(name,operatingSystems);
      }

      public Builder fromTemplateCategory(TemplateCategory in) {
         return name(in.getName()).templateOperatingSystems(in.getTemplateOperatingSystems());
      }
   }

   @XmlElement(name = "Name", required = false)
   private String name;

   @XmlElement(name = "OperatingSystems", required = false)
   private TemplateOperatingSystems operatingSystems = TemplateOperatingSystems.builder().build();

   private TemplateCategory(@Nullable String name, Set<TemplateOperatingSystem> operatingSystems) {
      this.name = name;
      this.operatingSystems = TemplateOperatingSystems.builder().operatingSystems(operatingSystems).build();
   }

   protected TemplateCategory() {
       //For JAXB
   }

   @Nullable
   public String getName() {
       return name;
   }

   public Set<TemplateOperatingSystem> getTemplateOperatingSystems() {
       return operatingSystems.getTemplateOperatingSystems();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateCategory that = (TemplateCategory) o;

      if (name != null ? !name.equals(that.name) : that.name != null)
         return false;
      if (operatingSystems != null ? !operatingSystems.equals(that.operatingSystems) : that.operatingSystems != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (operatingSystems != null ? operatingSystems.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[name="+name+", +operatingSystems="+operatingSystems+"]";
   }
}