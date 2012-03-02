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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Collection of supported hardware capabilities.
 * <p/>
 * <p/>
 * <p>Java class for Capabilities complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="Capabilities">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="SupportedHardwareVersions" type="{http://www.vmware.com/vcloud/v1.5}SupportedHardwareVersionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "Capabilities", propOrder = {
      "supportedHardwareVersions"
})
public class Capabilities {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCapabilities(this);
   }

   public static class Builder {

      private SupportedHardwareVersions supportedHardwareVersions;

      /**
       * @see Capabilities#getSupportedHardwareVersions()
       */
      public Builder supportedHardwareVersions(SupportedHardwareVersions supportedHardwareVersions) {
         this.supportedHardwareVersions = supportedHardwareVersions;
         return this;
      }


      public Capabilities build() {
         return new Capabilities(supportedHardwareVersions);
      }


      public Builder fromCapabilities(Capabilities in) {
         return supportedHardwareVersions(in.getSupportedHardwareVersions());
      }
   }

   private Capabilities(SupportedHardwareVersions supportedHardwareVersions) {
      this.supportedHardwareVersions = supportedHardwareVersions;
   }

   private Capabilities() {
      // For JAXB 
   }


   @XmlElement(name = "SupportedHardwareVersions")
   protected SupportedHardwareVersions supportedHardwareVersions;

   /**
    * Gets the value of the supportedHardwareVersions property.
    *
    * @return possible object is
    *         {@link SupportedHardwareVersions }
    */
   public SupportedHardwareVersions getSupportedHardwareVersions() {
      return supportedHardwareVersions;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Capabilities that = Capabilities.class.cast(o);
      return equal(supportedHardwareVersions, that.supportedHardwareVersions);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(supportedHardwareVersions);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("supportedHardwareVersions", supportedHardwareVersions).toString();
   }

}
