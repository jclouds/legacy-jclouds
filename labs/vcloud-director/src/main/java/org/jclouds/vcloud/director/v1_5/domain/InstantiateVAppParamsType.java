/*
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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp instantiation parameters.
 *
 * <pre>
 * &lt;complexType name="InstantiateVAppParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "InstantiateVAppParams")
public class InstantiateVAppParamsType<T extends InstantiateVAppParamsType<T>> extends VAppCreationParamsType<T> {

   public static <T extends InstantiateVAppParamsType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromInstantiateVAppParamsType(this);
   }

   public static class Builder<T extends InstantiateVAppParamsType<T>> extends VAppCreationParamsType.Builder<T> {

      protected ReferenceType<?> source;
      protected Boolean sourceDelete;
      protected Boolean linkedClone;

      /**
       * @see InstantiateVAppParamsType#getSource()
       */
      public Builder<T> source(ReferenceType<?> source) {
         this.source = source;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public Builder<T> isSourceDelete(Boolean sourceDelete) {
         this.sourceDelete = sourceDelete;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public Builder<T> sourceDelete() {
         this.sourceDelete = Boolean.TRUE;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public Builder<T> notSourceDelete() {
         this.sourceDelete = Boolean.FALSE;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public Builder<T> isLinkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public Builder<T> linkedClone() {
         this.linkedClone = Boolean.TRUE;
         return this;
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public Builder<T> notLinkedClone() {
         this.linkedClone = Boolean.FALSE;
         return this;
      }

      @Override
      public InstantiateVAppParamsType<T> build() {
         return new InstantiateVAppParamsType<T>(description, name, vAppParent, instantiationParams, deploy, powerOn, source, sourceDelete, linkedClone);
      }

      /**
       * @see ParamsType#getDescription()
       */
      @Override
      public Builder<T> description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      @Override
      public Builder<T> name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see VAppCreationParamsType#getVAppParent()
       */
      @Override
      public Builder<T> vAppParent(Reference vAppParent) {
         this.vAppParent = vAppParent;
         return this;
      }

      /**
       * @see VAppCreationParamsType#getInstantiationParams()
       */
      @Override
      public Builder<T> instantiationParams(InstantiationParams instantiationParams) {
         this.instantiationParams = instantiationParams;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder<T> deploy(Boolean deploy) {
         this.deploy = deploy;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder<T> deploy() {
         this.deploy = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isDeploy()
       */
      @Override
      public Builder<T> notDeploy() {
         this.deploy = Boolean.FALSE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder<T> powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder<T> powerOn() {
         this.powerOn = Boolean.TRUE;
         return this;
      }

      /**
       * @see VAppCreationParamsType#isPowerOn()
       */
      @Override
      public Builder<T> notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return this;
      }

      @Override
      @SuppressWarnings("unchecked")
      public Builder<T> fromVAppCreationParamsType(VAppCreationParamsType<T> in) {
         return Builder.class.cast(super.fromVAppCreationParamsType(in));
      }

      public Builder<T> fromInstantiateVAppParamsType(InstantiateVAppParamsType<T> in) {
         return fromVAppCreationParamsType(in)
               .source(in.getSource())
               .isSourceDelete(in.isSourceDelete())
               .isLinkedClone(in.isLinkedClone());
      }
   }
   
   protected InstantiateVAppParamsType() {
      // For JAXB and builder use
   }

   public InstantiateVAppParamsType(String description, String name, Reference vAppParent, InstantiationParams instantiationParams,
                                    Boolean deploy, Boolean powerOn, ReferenceType<?> source, Boolean sourceDelete, Boolean linkedClone) {
      super(description, name, vAppParent, instantiationParams, deploy, powerOn);
      this.source = source;
      this.sourceDelete = sourceDelete;
      this.linkedClone = linkedClone;
   }

   @XmlElement(name = "Source", required = true)
   protected ReferenceType<?> source;
   @XmlElement(name = "IsSourceDelete")
   protected Boolean sourceDelete;
   @XmlAttribute
   protected Boolean linkedClone;

   /**
    * Gets the value of the source property.
    */
   public ReferenceType<?> getSource() {
      return source;
   }

   /**
    * Gets the value of the isSourceDelete property.
    */
   public Boolean isSourceDelete() {
      return sourceDelete;
   }

   /**
    * Gets the value of the linkedClone property.
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
      return super.equals(that) &&
            equal(this.source, that.source) &&
            equal(this.sourceDelete, that.sourceDelete) &&
            equal(this.linkedClone, that.linkedClone);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), source, sourceDelete, linkedClone);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("source", source)
            .add("isSourceDelete", sourceDelete)
            .add("linkedClone", linkedClone);
   }
}
