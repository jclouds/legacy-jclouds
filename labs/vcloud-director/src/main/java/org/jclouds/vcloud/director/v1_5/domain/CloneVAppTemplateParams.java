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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents parameters for copying a vApp template and optionally
 * deleting the source.
 * <p/>
 * <p/>
 * <p>Java class for CloneVAppTemplateParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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

   public Builder<?> toBuilder() {
      return builder().fromCloneVAppTemplateParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ParamsType.Builder<B> {

      private Reference source;
      private Boolean isSourceDelete;

      /**
       * @see CloneVAppTemplateParams#getSource()
       */
      public B source(Reference source) {
         this.source = source;
         return self();
      }

      /**
       * @see CloneVAppTemplateParams#isSourceDelete()
       */
      public B isSourceDelete(Boolean isSourceDelete) {
         this.isSourceDelete = isSourceDelete;
         return self();
      }

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

   private CloneVAppTemplateParams() {
      // for JAXB
   }


   @XmlElement(name = "Source", required = true)
   protected Reference source;
   @XmlElement(name = "IsSourceDelete")
   protected Boolean isSourceDelete;

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

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CloneVAppTemplateParams that = CloneVAppTemplateParams.class.cast(o);
      return equal(source, that.source) &&
            equal(isSourceDelete, that.isSourceDelete);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source,
            isSourceDelete);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("isSourceDelete", isSourceDelete).toString();
   }

}
