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
 * Wraps individual TemplateOperatingSystem elements.
 * Needed because parsing is done with JAXB and it does not handle Generic collections
 * <xs:complexType name="TemplateOperatingSystems">
 * @author Jason King
 */
public class TemplateOperatingSystems {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromTemplateOperatingSystems(this);
   }

   public static class Builder {

       private Set<TemplateOperatingSystem> operatingSystems = Sets.newLinkedHashSet();

       /**
        * @see TemplateOperatingSystems#getTemplateOperatingSystems()
        */
       public Builder operatingSystems(Set<TemplateOperatingSystem> operatingSystems) {
          this.operatingSystems = Sets.newLinkedHashSet(checkNotNull(operatingSystems, "operatingSystems"));
          return this;
       }

       public Builder addOperatingSystem(TemplateOperatingSystem operatingSystem) {
          operatingSystems.add(checkNotNull(operatingSystem,"operatingSystem"));
          return this;
       }

       public TemplateOperatingSystems build() {
           return new TemplateOperatingSystems(operatingSystems);
       }

       public Builder fromTemplateOperatingSystems(TemplateOperatingSystems in) {
         return operatingSystems(in.getTemplateOperatingSystems());
       }
   }

   private TemplateOperatingSystems() {
      //For JAXB and builder use
   }

   private TemplateOperatingSystems(Set<TemplateOperatingSystem> operatingSystems) {
      this.operatingSystems = Sets.newLinkedHashSet(operatingSystems);
   }

   @XmlElement(name = "OperatingSystem")
   private Set<TemplateOperatingSystem> operatingSystems = Sets.newLinkedHashSet();

   public Set<TemplateOperatingSystem> getTemplateOperatingSystems() {
      return Collections.unmodifiableSet(operatingSystems);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TemplateOperatingSystems that = (TemplateOperatingSystems) o;

      if (operatingSystems != null ? !operatingSystems.equals(that.operatingSystems) : that.operatingSystems != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return operatingSystems != null ? operatingSystems.hashCode() : 0;
   }

   public String toString() {
      return "["+ operatingSystems.toString()+"]";
   }
}
