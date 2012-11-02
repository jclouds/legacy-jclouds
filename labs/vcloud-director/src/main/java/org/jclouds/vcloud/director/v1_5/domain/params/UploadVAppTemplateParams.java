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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

/**
 * Represents vApp Template upload parameters.
 *
 * <pre>
 * &lt;complexType name="UploadVAppTemplateParamsType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 * @since 0.9
 */
@XmlRootElement(name = "UploadVAppTemplateParams")
@XmlType(name = "UploadVAppTemplateParamsType")
public class UploadVAppTemplateParams extends ParamsType {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromUploadVAppTemplateParams(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends ParamsType.Builder<B> {

      private String transferFormat;
      private Boolean manifestRequired;

      /**
       * @see UploadVAppTemplateParams#getTransferFormat()
       */
      public B transferFormat(String transferFormat) {
         this.transferFormat = transferFormat;
         return self();
      }

      /**
       * @see UploadVAppTemplateParams#isManifestRequired()
       */
      public B manifestRequired(Boolean manifestRequired) {
         this.manifestRequired = manifestRequired;
         return self();
      }


      @Override
      public UploadVAppTemplateParams build() {
         return new UploadVAppTemplateParams(this);
      }

      public B fromUploadVAppTemplateParams(UploadVAppTemplateParams in) {
         return fromParamsType(in)
               .transferFormat(in.getTransferFormat())
               .manifestRequired(in.isManifestRequired());
      }
   }

   public UploadVAppTemplateParams(Builder<?> builder) {
      super(builder);
      this.transferFormat = builder.transferFormat;
      this.manifestRequired = builder.manifestRequired;
   }

   protected UploadVAppTemplateParams() {
      // For JAXB
   }


   @XmlAttribute
   protected String transferFormat;
   @XmlAttribute
   protected Boolean manifestRequired;

   /**
    * Gets the value of the transferFormat property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getTransferFormat() {
      return transferFormat;
   }

   /**
    * Gets the value of the manifestRequired property.
    *
    * @return possible object is
    *         {@link Boolean }
    */
   public Boolean isManifestRequired() {
      return manifestRequired;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      UploadVAppTemplateParams that = UploadVAppTemplateParams.class.cast(o);
      return equal(transferFormat, that.transferFormat) &&
            equal(manifestRequired, that.manifestRequired);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(transferFormat,
            manifestRequired);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("transferFormat", transferFormat)
            .add("manifestRequired", manifestRequired).toString();
   }

}
