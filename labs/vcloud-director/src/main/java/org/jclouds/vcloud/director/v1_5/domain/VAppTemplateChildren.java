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

package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents vApp template children.
 * <p/>
 * <p/>
 * <p>Java class for VAppTemplateChildren complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="VAppTemplateChildren">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Vm" type="{http://www.vmware.com/vcloud/v1.5}VAppTemplateType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "Children")
@XmlType(propOrder = {
      "vms"
})
public class VAppTemplateChildren {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromVAppTemplateChildren(this);
   }

   public static class Builder {
      private Set<VAppTemplate> vms = Sets.newLinkedHashSet();

      /**
       * @see VAppTemplateChildren#getVms()
       */
      public Builder vms(Set<VAppTemplate> vms) {
         this.vms = checkNotNull(vms, "vms");
         return this;
      }


      public VAppTemplateChildren build() {
         return new VAppTemplateChildren(vms);
      }


      public Builder fromVAppTemplateChildren(VAppTemplateChildren in) {
         return vms(in.getVms());
      }
   }

   private VAppTemplateChildren(Set<VAppTemplate> vm) {
      this.vms = ImmutableSet.copyOf(vm);
   }

   private VAppTemplateChildren() {
      // For JAXB
   }

   @XmlElement(name = "Vm")
   protected Set<VAppTemplate> vms = Sets.newLinkedHashSet();

   /**
    * Gets the value of the vm property.
    */
   public Set<VAppTemplate> getVms() {
      return Collections.unmodifiableSet(this.vms);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppTemplateChildren that = VAppTemplateChildren.class.cast(o);
      return equal(vms, that.vms);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vms);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("vms", vms).toString();
   }

}
