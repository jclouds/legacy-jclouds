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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents vApp instantiation from OVF parameters
 *
 * <pre>
 * &lt;complexType name="InstantiateOvfParams" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 */
@XmlType(name = "InstantiateOvfParams")
public class InstantiateOvfParams extends VAppCreationParams {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromInstantiateOvfParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends VAppCreationParams.Builder<B> {

      private Boolean allEULAsAccepted;
      private String transferFormat;

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public B isAllEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return self();
      }

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public B allEULAsAccepted() {
         this.allEULAsAccepted = Boolean.TRUE;
         return self();
      }

      /**
       * @see InstantiateOvfParams#isAllEULAsAccepted()
       */
      public B allEULAsNotAccepted() {
         this.allEULAsAccepted = Boolean.FALSE;
         return self();
      }

      /**
       * @see InstantiateOvfParams#getTransferFormat()
       */
      public B transferFormat(String transferFormat) {
         this.transferFormat = transferFormat;
         return self();
      }

      @Override
      public InstantiateOvfParams build() {
         InstantiateOvfParams instantiateOvfParams = new InstantiateOvfParams(this);
         return instantiateOvfParams;
      }

      public B fromInstantiateOvfParams(InstantiateOvfParams in) {
         return fromVAppCreationParamsType(in).isAllEULAsAccepted(in.isAllEULAsAccepted()).transferFormat(in.getTransferFormat());
      }
   }

   protected InstantiateOvfParams() {
      // For JAXB and B use
   }

   public InstantiateOvfParams(Builder<?> builder) {
      super(builder);
      this.allEULAsAccepted = builder.allEULAsAccepted;
      this.transferFormat = builder.transferFormat;
   }

   @XmlElement(name = "AllEULAsAccepted")
   protected Boolean allEULAsAccepted;
   @XmlAttribute
   protected String transferFormat;

   /**
    * Gets the value of the allEULAsAccepted property.
    */
   public Boolean isAllEULAsAccepted() {
      return allEULAsAccepted;
   }

   /**
    * Gets the value of the transferFormat property.
    */
   public String getTransferFormat() {
      return transferFormat;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateOvfParams that = InstantiateOvfParams.class.cast(o);
      return super.equals(that) && equal(this.allEULAsAccepted, that.allEULAsAccepted) && equal(this.transferFormat, that.transferFormat);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), allEULAsAccepted, transferFormat);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("allEULAsAccepted", allEULAsAccepted).add("transferFormat", transferFormat);
   }
}
