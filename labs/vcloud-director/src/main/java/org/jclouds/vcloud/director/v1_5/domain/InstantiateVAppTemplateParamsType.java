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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents vApp template instantiation parameters.
 *             
 * 
 * <p>Java class for InstantiateVAppTemplateParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstantiateVAppTemplateParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}InstantiateVAppParamsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.vmware.com/vcloud/v1.5}AllEULAsAccepted" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstantiateVAppTemplateParams", propOrder = {
    "allEULAsAccepted"
})
public class InstantiateVAppTemplateParamsType<T extends InstantiateVAppTemplateParamsType<T>>
    extends InstantiateVAppParamsType<T>

{
   public static <T extends InstantiateVAppTemplateParamsType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromInstantiateVAppTemplateParams(this);
   }

   public static class Builder<T extends InstantiateVAppTemplateParamsType<T>> extends InstantiateVAppParamsType.Builder<T> {
      
      private Boolean allEULAsAccepted;

      /**
       * @see InstantiateVAppTemplateParamsType#getAllEULAsAccepted()
       */
      public Builder<T> allEULAsAccepted(Boolean allEULAsAccepted) {
         this.allEULAsAccepted = allEULAsAccepted;
         return this;
      }


      public InstantiateVAppTemplateParamsType<T> build() {
         InstantiateVAppTemplateParamsType<T> instantiateVAppTemplateParams = new InstantiateVAppTemplateParamsType<T>();
         instantiateVAppTemplateParams.setAllEULAsAccepted(allEULAsAccepted);
         return instantiateVAppTemplateParams;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromInstantiateVAppParamsType(InstantiateVAppParamsType<T> in) {
          return Builder.class.cast(super.fromInstantiateVAppParamsType(in));
      }
      public Builder<T> fromInstantiateVAppTemplateParams(InstantiateVAppTemplateParamsType<T> in) {
         return fromInstantiateVAppParamsType(in)
            .allEULAsAccepted(in.isAllEULAsAccepted());
      }
   }

   private InstantiateVAppTemplateParamsType() {
      // For JAXB and builder use
   }



    @XmlElement(name = "AllEULAsAccepted")
    protected Boolean allEULAsAccepted;

    /**
     * 
     *                                 Used to confirm acceptance of all EULAs in a
     *                                 vApp template. Instantiation fails if this
     *                                 element is missing, empty, or set to false
     *                                 and one or more EulaSection elements are
     *                                 present.
     *                             
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllEULAsAccepted() {
        return allEULAsAccepted;
    }

    /**
     * Sets the value of the allEULAsAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllEULAsAccepted(Boolean value) {
        this.allEULAsAccepted = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiateVAppTemplateParamsType<?> that = InstantiateVAppTemplateParamsType.class.cast(o);
      return equal(allEULAsAccepted, that.allEULAsAccepted);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(allEULAsAccepted);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("allEULAsAccepted", allEULAsAccepted).toString();
   }

}
