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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp template instantiation parameters.
 *
 * <pre>
 * &lt;complexType name="InstantiateVAppTemplateParams" /&gt;
 * </pre>
 */
@XmlRootElement(name = "InstantiateVAppTemplateParams")
public class InstantiateVAppTemplateParams extends InstantiateVAppParams {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromInstantiateVAppTemplateParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends InstantiateVAppParams.Builder<B> {

      private Boolean allEULAsAccepted;

      /**
       * @see InstantiateVAppTemplateParams#isAllEULAsAccepted()
       */
      public B allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return self();
      }

      @Override
      public InstantiateVAppTemplateParams build() {
         return new InstantiateVAppTemplateParams(this);
      }

      public B fromInstantiateVAppTemplateParams(InstantiateVAppTemplateParams in) {
         return fromInstantiateVAppParamsType(in).allEULAsAccepted(in.isAllEULAsAccepted());
      }
   }

   protected InstantiateVAppTemplateParams(Builder<?> builder) {
      super(builder);
      this.allEULAsAccepted = builder.allEULAsAccepted;
   }

   protected InstantiateVAppTemplateParams() {
      // for JAXB
   }


   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;

   /**
    * Used to confirm acceptance of all EULAs in a
    * vApp template. Instantiation fails if this
    * element is missing, empty, or set to false
    * and one or more EulaSection elements are
    * present.
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
      return super.equals(that) && equal(this.allEULAsAccepted, that.allEULAsAccepted);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), allEULAsAccepted);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("allEULAsAccepted", allEULAsAccepted);
   }
}
