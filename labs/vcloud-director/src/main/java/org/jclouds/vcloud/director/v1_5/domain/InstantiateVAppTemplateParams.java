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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents vApp template instantiation parameters.
 * <p/>
 * <p/>
 * <p>Java class for InstantiateVAppTemplateParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="InstantiateVAppTemplateParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}InstantiateVAppParamsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.vmware.com/vcloud/v1.5}AllEULAsAccepted" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "InstantiateVAppTemplateParams", propOrder = {
      "allEULAsAccepted"
})
public class InstantiateVAppTemplateParams
      extends InstantiateVAppParamsType<InstantiateVAppTemplateParams> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromInstantiateVAppTemplateParams(this);
   }

   public static class Builder extends InstantiateVAppParamsType.Builder<InstantiateVAppTemplateParams> {

      private Boolean allEULAsAccepted;

      /**
       * @see InstantiateVAppTemplateParams#isAllEULAsAccepted()
       */
      public Builder allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return this;
      }

      public InstantiateVAppTemplateParams build() {
         return new InstantiateVAppTemplateParams(description, name, vAppParent, instantiationParams, deploy, powerOn, source, isSourceDelete, linkedClone, allEULAsAccepted);
      }

      /**
       * @see ParamsType#getDescription()
       */
      public Builder description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      public Builder name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      public Builder vAppParent(Reference vAppParent) {
         super.vAppParent(vAppParent);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public Builder instantiationParams(InstantiationParams instantiationParams) {
         super.instantiationParams(instantiationParams);
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder deploy(Boolean deploy) {
         super.deploy(deploy);
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder powerOn(Boolean powerOn) {
         super.powerOn(powerOn);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromInstantiateVAppParamsType(InstantiateVAppParamsType<InstantiateVAppTemplateParams> in) {
         return Builder.class.cast(super.fromInstantiateVAppParamsType(in));
      }

      public Builder fromInstantiateVAppTemplateParams(InstantiateVAppTemplateParams in) {
         return fromInstantiateVAppParamsType(in)
               .allEULAsAccepted(in.isAllEULAsAccepted());
      }
   }

   public InstantiateVAppTemplateParams(String description, String name, Reference vAppParent, InstantiationParams instantiationParams,
                                        Boolean deploy, Boolean powerOn, Reference source, Boolean sourceDelete, Boolean linkedClone, Boolean allEULAsAccepted) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn, source, sourceDelete, linkedClone);
      this.allEULAsAccepted = allEULAsAccepted;
   }

   private InstantiateVAppTemplateParams() {
      // For JAXB and builder use
   }


   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;

   /**
    * Used to confirm acceptance of all EULAs in a
    * vApp template. Instantiation fails if this
    * element is missing, empty, or set to false
    * and one or more EulaSection elements are
    * present.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isAllEULAsAccepted() {
      return allEULAsAccepted;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateVAppTemplateParams that = InstantiateVAppTemplateParams.class.cast(o);
      return equal(allEULAsAccepted, that.allEULAsAccepted);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(allEULAsAccepted);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("allEULAsAccepted", allEULAsAccepted).toString();
   }

}
