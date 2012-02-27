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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.SectionType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a list of ovf:Section to configure for instantiating a VApp.
 *             
 * 
 * <p>Java class for InstantiationParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InstantiationParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element ref="{http://schemas.dmtf.org/ovf/envelope/1}Section" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "InstantiationParams", propOrder = {
    "section"
})
public class InstantiationParams {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromInstantiationParams(this);
   }

   public static class Builder {
      
      private List<JAXBElement<? extends SectionType<?>>> sections;

      /**
       * @see InstantiationParams#getSections()
       */
      public Builder sections(List<JAXBElement<? extends SectionType<?>>> sections) {
         this.sections = sections;
         return this;
      }


      public InstantiationParams build() {
         InstantiationParams instantiationParams = new InstantiationParams(sections);
         return instantiationParams;
      }


      public Builder fromInstantiationParams(InstantiationParams in) {
         return sections(in.getSections());
      }
   }

   private InstantiationParams() {
      // For JAXB and builder use
   }

   private InstantiationParams(List<JAXBElement<? extends SectionType<?>>> sections) {
      this.sections = sections;
   }


    @XmlElementRef(name = "Section", namespace = "http://schemas.dmtf.org/ovf/envelope/1", type = JAXBElement.class)
    protected List<JAXBElement<? extends SectionType<?>>> sections;

    /**
     * 
     * An ovf:Section to configure for instantiation.
     * 
     * Gets the value of the section property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the section property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link SectionType }{@code >}
     * {@link JAXBElement }{@code <}{@link VirtualHardwareSection }{@code >}
     * {@link JAXBElement }{@code <}{@link LeaseSettingsSection }{@code >}
     * {@link JAXBElement }{@code <}{@link EulaSection }{@code >}
     * {@link JAXBElement }{@code <}{@link RuntimeInfoSection }{@code >}
     * {@link JAXBElement }{@code <}{@link AnnotationSection }{@code >}
     * {@link JAXBElement }{@code <}{@link DeploymentOptionSection }{@code >}
     * {@link JAXBElement }{@code <}{@link StartupSection }{@code >}
     * {@link JAXBElement }{@code <}{@link ResourceAllocationSection }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConnectionSection }{@code >}
     * {@link JAXBElement }{@code <}{@link CustomizationSection }{@code >}
     * {@link JAXBElement }{@code <}{@link ProductSection }{@code >}
     * {@link JAXBElement }{@code <}{@link GuestCustomizationSection }{@code >}
     * {@link JAXBElement }{@code <}{@link org.jclouds.ovf.OperatingSystemSection }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConfigSection }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkSection }{@code >}
     * {@link JAXBElement }{@code <}{@link DiskSection }{@code >}
     * {@link JAXBElement }{@code <}{@link InstallSection }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends SectionType<?>>> getSections() {
        if (sections == null) {
           sections = new ArrayList<JAXBElement<? extends SectionType<?>>>();
        }
        return this.sections;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      InstantiationParams that = InstantiationParams.class.cast(o);
      return equal(sections, that.sections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(sections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("sections", sections).toString();
   }

}
