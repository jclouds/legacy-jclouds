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
 * Represents vApp instantiation parameters.
 * <p/>
 * <p/>
 * <p>Java class for InstantiateVAppParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="InstantiateVAppParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VAppCreationParamsType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *         &lt;element name="IsSourceDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="linkedClone" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "InstantiateVAppParams", propOrder = {
      "source",
      "isSourceDelete"
})
@XmlSeeAlso({
//    InstantiateVAppTemplateParamsType.class,
//    CloneVAppParamsType.class
})
public class InstantiateVAppParamsType<T extends InstantiateVAppParamsType<T>>
      extends VAppCreationParamsType<T>

{
   public static <T extends InstantiateVAppParamsType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromInstantiateVAppParamsType(this);
   }

   public static class Builder<T extends InstantiateVAppParamsType<T>> extends VAppCreationParamsType.Builder<T> {

      protected Reference source;
      protected Boolean isSourceDelete;
      protected Boolean linkedClone;

      /**
       * @see InstantiateVAppParamsType#getSource()
       */
      public Builder<T> source(Reference source) {
         this.source = source;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public Builder<T> isSourceDelete(Boolean isSourceDelete) {
         this.isSourceDelete = isSourceDelete;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public Builder<T> linkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return this;
      }

      public InstantiateVAppParamsType<T> build() {
         return new InstantiateVAppParamsType<T>(description, name, vAppParent, instantiationParams, deploy, powerOn, source, isSourceDelete, linkedClone);
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
       * @see VAppCreationParamsType#getVAppParent()
       */
      public Builder<T> vAppParent(Reference vAppParent) {
         super.vAppParent(vAppParent);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      public Builder<T> instantiationParams(InstantiationParams instantiationParams) {
         super.instantiationParams(instantiationParams);
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      public Builder<T> deploy(Boolean deploy) {
         super.deploy(deploy);
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      public Builder<T> powerOn(Boolean powerOn) {
         super.powerOn(powerOn);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromVAppCreationParamsType(VAppCreationParamsType<T> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder<T> fromInstantiateVAppParamsType(InstantiateVAppParamsType<T> in) {
         return fromVAppCreationParamsType(in)
               .source(in.getSource())
               .isSourceDelete(in.isSourceDelete())
               .linkedClone(in.isLinkedClone());
      }
   }

   public InstantiateVAppParamsType(String description, String name, Reference vAppParent, InstantiationParams instantiationParams,
                                    Boolean deploy, Boolean powerOn, Reference source, Boolean sourceDelete, Boolean linkedClone) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
      this.source = source;
      isSourceDelete = sourceDelete;
      this.linkedClone = linkedClone;
   }

   protected InstantiateVAppParamsType() {
      // For JAXB and builder use
   }


   @XmlElement(name = "Source", required = true)
   protected Reference source;
   @XmlElement(name = "IsSourceDelete")
   protected Boolean isSourceDelete;
   @XmlAttribute
   protected Boolean linkedClone;

   /**
    * Gets the value of the source property.
    *
    * @return possible object is
    *         {@link Reference }
    */
   public Reference getSource() {
      return source;
   }

   /**
    * Gets the value of the isSourceDelete property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isSourceDelete() {
      return isSourceDelete;
   }

   /**
    * Gets the value of the linkedClone property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isLinkedClone() {
      return linkedClone;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateVAppParamsType<?> that = InstantiateVAppParamsType.class.cast(o);
      return equal(source, that.source) &&
            equal(isSourceDelete, that.isSourceDelete) &&
            equal(linkedClone, that.linkedClone);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source,
            isSourceDelete,
            linkedClone);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("isSourceDelete", isSourceDelete)
            .add("linkedClone", linkedClone).toString();
   }

}
