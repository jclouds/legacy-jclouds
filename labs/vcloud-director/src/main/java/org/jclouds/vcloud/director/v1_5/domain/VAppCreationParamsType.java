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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents vApp creation parameters.
 * <p/>
 * <p/>
 * <p>Java class for VAppCreationParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="VAppCreationParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;sequence>
 *         &lt;element name="VAppParent" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" minOccurs="0"/>
 *         &lt;element name="InstantiationParams" type="{http://www.vmware.com/vcloud/v1.5}InstantiationParamsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="deploy" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="powerOn" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "VAppCreationParams", propOrder = {
      "vAppParent",
      "instantiationParams"
})
@XmlSeeAlso({
//    InstantiateOvfParamsType.class,
//    ComposeVAppParamsType.class,
//    InstantiateVAppParamsType.class,
//    ImportVmAsVAppParamsType.class
})
public class VAppCreationParamsType<T extends VAppCreationParamsType<T>>
      extends ParamsType<T>

{
   public static <T extends VAppCreationParamsType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromVAppCreationParamsType(this);
   }

   public static class Builder<T extends VAppCreationParamsType<T>> extends ParamsType.Builder<T> {

      protected Reference vAppParent;
      protected InstantiationParams instantiationParams;
      protected Boolean deploy;
      protected Boolean powerOn;

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      public Builder<T> vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public Builder<T> instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder<T> deploy(Boolean deploy) {
         this.deploy = deploy;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder<T> powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }


      public VAppCreationParamsType<T> build() {
         return new VAppCreationParamsType<T>(description, name, vAppParent, instantiationParams,deploy, powerOn);
      }

      /**
       * @see ParamsType#getDescription()
       */
      public Builder<T> description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      public Builder<T> name(String name) {
         super.name(name);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromParamsType(ParamsType<T> in) {
         return Builder.class.cast(super.fromParamsType(in));
      }

      public Builder<T> fromVAppCreationParamsType(VAppCreationParamsType<T> in) {
         return fromParamsType(in)
               .vAppParent(in.getVAppParent())
               .instantiationParams(in.getInstantiationParams())
               .deploy(in.isDeploy())
               .powerOn(in.isPowerOn());
      }
   }

   @XmlElement(name = "VAppParent")
   protected Reference vAppParent;
   @XmlElement(name = "InstantiationParams")
   protected InstantiationParams instantiationParams;
   @XmlAttribute
   protected Boolean deploy;
   @XmlAttribute
   protected Boolean powerOn;

   public VAppCreationParamsType(String description, String name, Reference vAppParent,
                                 InstantiationParams instantiationParams, Boolean deploy, Boolean powerOn) {
      super(description, name);
      this.vAppParent = vAppParent;
      this.instantiationParams = instantiationParams;
      this.deploy = deploy;
      this.powerOn = powerOn;
   }

   protected VAppCreationParamsType() {
      // For JAXB and builder use
   }

   /**
    * Gets the value of the vAppParent property.
    *
    * @return possible object is
    *         {@link Reference }
    */
   public Reference getVAppParent() {
      return vAppParent;
   }

   /**
    * Gets the value of the instantiationParams property.
    *
    * @return possible object is
    *         {@link InstantiationParams }
    */
   public InstantiationParams getInstantiationParams() {
      return instantiationParams;
   }

   /**
    * Gets the value of the deploy property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isDeploy() {
      return deploy;
   }

   /**
    * Gets the value of the powerOn property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isPowerOn() {
      return powerOn;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VAppCreationParamsType<?> that = VAppCreationParamsType.class.cast(o);
      return equal(vAppParent, that.vAppParent) &&
            equal(instantiationParams, that.instantiationParams) &&
            equal(deploy, that.deploy) &&
            equal(powerOn, that.powerOn);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(vAppParent,
            instantiationParams,
            deploy,
            powerOn);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("vAppParent", vAppParent)
            .add("instantiationParams", instantiationParams)
            .add("deploy", deploy)
            .add("powerOn", powerOn).toString();
   }

}
