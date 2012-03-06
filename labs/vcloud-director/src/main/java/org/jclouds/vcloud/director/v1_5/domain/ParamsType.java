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
 * A basic type used to specify parameters for operations.
 * <p/>
 * <p/>
 * <p>Java class for Params complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Params">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Params", propOrder = {
      "description"
})
@XmlSeeAlso({
//    CaptureVAppParams.class, // FIXME!
    CloneVAppTemplateParams.class,
    CloneMediaParams.class,
    UploadVAppTemplateParams.class
//,    ImportVmAsVAppTemplateParams.class,
//    ImportMediaParams.class,
//    UpdateResourcePoolSetParams.class,
//    VAppCreationParams.class
})
public class ParamsType<T extends ParamsType<T>> {
   public static <T extends ParamsType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromParamsType(this);
   }

   public static class Builder<T extends ParamsType<T>> {

      protected String description;
      protected String name;

      /**
       * @see ParamsType#getDescription()
       */
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see ParamsType#getName()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }


      public ParamsType<T> build() {
         return new ParamsType<T>(description, name);
      }

      public Builder<T> fromParamsType(ParamsType<T> in) {
         return description(in.getDescription())
               .name(in.getName());
      }
   }

   protected ParamsType(String description, String name) {
      this.description = description;
      this.name = name;
   }

   protected ParamsType() {
      // for JAXB
   }


   @XmlElement(name = "Description")
   protected String description;
   @XmlAttribute
   protected String name;

   /**
    * Gets the value of the description property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getDescription() {
      return description;
   }

   /**
    * Gets the value of the name property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ParamsType<?> that = ParamsType.class.cast(o);
      return equal(description, that.description) &&
            equal(name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(description,
            name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("description", description)
            .add("name", name).toString();
   }

}
