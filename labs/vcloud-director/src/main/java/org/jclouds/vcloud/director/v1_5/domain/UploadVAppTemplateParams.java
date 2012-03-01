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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents vApp Template upload parameters.
 * <p/>
 * <p/>
 * <p>Java class for UploadVAppTemplateParams complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="UploadVAppTemplateParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;attribute name="transferFormat" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="manifestRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "UploadVAppTemplateParams")
public class UploadVAppTemplateParams
      extends ParamsType<UploadVAppTemplateParams>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromUploadVAppTemplateParams(this);
   }

   public static class Builder extends ParamsType.Builder<UploadVAppTemplateParams> {

      private String transferFormat;
      private Boolean manifestRequired;

      /**
       * @see UploadVAppTemplateParams#getTransferFormat()
       */
      public Builder transferFormat(String transferFormat) {
         this.transferFormat = transferFormat;
         return this;
      }

      /**
       * @see UploadVAppTemplateParams#isManifestRequired()
       */
      public Builder manifestRequired(Boolean manifestRequired) {
         this.manifestRequired = manifestRequired;
         return this;
      }


      public UploadVAppTemplateParams build() {
         return new UploadVAppTemplateParams(description, name, transferFormat, manifestRequired);
      }


      @Override
      public Builder fromParamsType(ParamsType<UploadVAppTemplateParams> in) {
         return Builder.class.cast(super.fromParamsType(in));
      }

      public Builder fromUploadVAppTemplateParams(UploadVAppTemplateParams in) {
         return fromParamsType(in)
               .transferFormat(in.getTransferFormat())
               .manifestRequired(in.isManifestRequired());
      }
   }

   public UploadVAppTemplateParams(String description, String name, String transferFormat, Boolean manifestRequired) {
      super(description, name);
      this.transferFormat = transferFormat;
      this.manifestRequired = manifestRequired;
   }

   private UploadVAppTemplateParams() {
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
