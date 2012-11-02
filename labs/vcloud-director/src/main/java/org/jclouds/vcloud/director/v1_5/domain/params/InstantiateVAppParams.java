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
package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp instantiation parameters.
 *
 * @author grkvlt@apache.org
 * @see <a href="http://www.vmware.com/support/vcd/doc/rest-api-doc-1.5-html/types/InstantiateVAppParamsType.html">
 *    vCloud REST API - InstantiateVAppParamsType</a>
 * @since 0.9
 */
@XmlRootElement(name = "InstantiateVAppParams")
@XmlType(name = "InstantiateVAppParamsType")
public class InstantiateVAppParams extends VAppCreationParams {

   public static final String MEDIA_TYPe = VCloudDirectorMediaType.INSTANTIATE_VAPP_TEMPLATE_PARAMS;

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromInstantiateVAppParamsType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends VAppCreationParams.Builder<B> {

      private Reference source;
      private Boolean sourceDelete;
      private Boolean linkedClone;

      @SuppressWarnings("unchecked")
      @Override
      protected B self() {
         return (B) this;
      }

      /**
       * @see InstantiateVAppParamsType#getSource()
       */
      public B source(Reference source) {
         this.source = source;
         return self();
      }

      /**
       * Sets source to a new Reference that uses this URI as the href.
       * 
       * @see InstantiateVAppParamsType#getSource()
       */
      public B source(URI source) {
         this.source = Reference.builder().href(source).build();
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public B isSourceDelete(Boolean sourceDelete) {
         this.sourceDelete = sourceDelete;
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public B sourceDelete() {
         this.sourceDelete = Boolean.TRUE;
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isSourceDelete()
       */
      public B notSourceDelete() {
         this.sourceDelete = Boolean.FALSE;
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public B isLinkedClone(Boolean linkedClone) {
         this.linkedClone = linkedClone;
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public B linkedClone() {
         this.linkedClone = Boolean.TRUE;
         return self();
      }

      /**
       * @see InstantiateVAppParamsType#isLinkedClone()
       */
      public B notLinkedClone() {
         this.linkedClone = Boolean.FALSE;
         return self();
      }

      @Override
      public InstantiateVAppParams build() {
         return new InstantiateVAppParams(this);
      }

      public B fromInstantiateVAppParamsType(InstantiateVAppParams in) {
         return fromVAppCreationParamsType(in)
               .source(in.getSource())
               .isSourceDelete(in.isSourceDelete())
               .isLinkedClone(in.isLinkedClone());
      }
   }
   
   protected InstantiateVAppParams() {
      // For JAXB and builder use
   }

   public InstantiateVAppParams(Builder<?> builder) {
      super(builder);
      this.source = builder.source;
      this.sourceDelete = builder.sourceDelete;
      this.linkedClone = builder.linkedClone;
   }

   @XmlElement(name = "Source", required = true)
   private Reference source;
   @XmlElement(name = "IsSourceDelete")
   private Boolean sourceDelete;
   @XmlAttribute
   private Boolean linkedClone;

   /**
    * Gets the value of the source property.
    */
   public Reference getSource() {
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
      InstantiateVAppParams that = InstantiateVAppParams.class.cast(o);
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
