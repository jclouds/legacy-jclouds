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

package org.jclouds.vcloud.director.v1_5.domain.params;

import static com.google.common.base.Objects.equal;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * Represents parameters for copying a vApp template and optionally
 * deleting the source.
 *
 * <pre>
 * &lt;complexType name="CloneVAppTemplateParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
 *         &lt;element name="IsSourceDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "CloneVAppTemplateParams", propOrder = {
      "source",
      "isSourceDelete"
})
@XmlRootElement(name = "CloneVAppTemplateParams")
public class CloneVAppTemplateParams extends ParamsType {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromCloneVAppTemplateParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ParamsType.Builder<B> {

      private Reference source;
      private Boolean isSourceDelete;

      @SuppressWarnings("unchecked")
      @Override
      protected B self() {
         return (B) this;
      }

      /**
       * @see CloneVAppTemplateParams#getSource()
       */
      public B source(Reference source) {
         this.source = source;
         return self();
      }

      /**
       * Sets source to a new Reference that uses this URI as the href.
       * 
       * @see CloneVAppTemplateParams#getSource()
       */
      public B source(URI source) {
         this.source = Reference.builder().href(source).build();
         return self();
      }

      /**
       * @see CloneVAppTemplateParams#isSourceDelete()
       */
      public B isSourceDelete(Boolean isSourceDelete) {
         this.isSourceDelete = isSourceDelete;
         return self();
      }

      @Override
      public CloneVAppTemplateParams build() {
         return new CloneVAppTemplateParams(this);
      }

      public B fromCloneVAppTemplateParams(CloneVAppTemplateParams in) {
         return fromParamsType(in)
               .source(in.getSource())
               .isSourceDelete(in.isSourceDelete());
      }
   }

   protected CloneVAppTemplateParams(Builder<?> builder) {
      super(builder);
      this.source = builder.source;
      isSourceDelete = builder.isSourceDelete;
   }

   protected CloneVAppTemplateParams() {
      // for JAXB
   }


   @XmlElement(name = "Source", required = true)
   protected Reference source;
   @XmlElement(name = "IsSourceDelete")
   protected Boolean isSourceDelete;

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
      return isSourceDelete;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CloneVAppTemplateParams that = CloneVAppTemplateParams.class.cast(o);
      return super.equals(that) &&
            equal(this.source, that.source) &&
            equal(this.isSourceDelete, that.isSourceDelete);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), source, isSourceDelete);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("source", source)
            .add("isSourceDelete", isSourceDelete);
   }

}
