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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.ovf.DeploymentOptionSection;
import org.jclouds.ovf.DiskSection;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.ProductSection;
import org.jclouds.ovf.Section;
import org.jclouds.ovf.VirtualHardwareSection;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents the parameters for capturing a vApp to a vApp template.
 *             
 * 
 * <p>Java class for CaptureVAppParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CaptureVAppParams">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ParamsType">
 *       &lt;sequence>
 *         &lt;element name="Source" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType"/>
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
@XmlType(name = "CaptureVAppParams", propOrder = {
    "source",
    "section"
})
public class CaptureVAppParams
    extends ParamsType<CaptureVAppParams>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCaptureVAppParams(this);
   }

   public static class Builder extends ParamsType.Builder<CaptureVAppParams> {
      
      private Reference source;
      private List<JAXBElement<? extends Section<?>>> sections;

      /**
       * @see CaptureVAppParams#getSource()
       */
      public Builder source(Reference source) {
         this.source = source;
         return this;
      }

      /**
       * @see CaptureVAppParams#getSections()
       */
      public Builder sections(List<JAXBElement<? extends Section<?>>> sections) {
         this.sections = sections;
         return this;
      }


      public CaptureVAppParams build() {
         CaptureVAppParams captureVAppParams = new CaptureVAppParams(sections);
         captureVAppParams.setSource(source);
         return captureVAppParams;
      }


      @Override
      public Builder fromParamsType(ParamsType<CaptureVAppParams> in) {
          return Builder.class.cast(super.fromParamsType(in));
      }
      public Builder fromCaptureVAppParams(CaptureVAppParams in) {
         return fromParamsType(in)
            .source(in.getSource())
            .sections(in.getSections());
      }
   }

   private CaptureVAppParams() {
      // For JAXB and builder use
   }

   private CaptureVAppParams(List<JAXBElement<? extends Section<?>>> sections) {
      this.sections = sections;
   }


    @XmlElement(name = "Source", required = true)
    protected Reference source;
    @XmlElementRef(name = "Section", namespace = "http://schemas.dmtf.org/ovf/envelope/1", type = JAXBElement.class)
    protected List<JAXBElement<? extends Section<?>>> sections;

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reference }
     *     
     */
    public void setSource(Reference value) {
        this.source = value;
    }

    /**
     * 
     *                                 An ovf:Section to configure the captured vAppTemplate.
     *                             Gets the value of the section property.
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
     * {@link JAXBElement }{@code <}{@link Section> }{@code >}
     * {@link JAXBElement }{@code <}{@link VirtualHardwareSection > }{@code >}
     * {@link JAXBElement }{@code <}{@link LeaseSettingsSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link EulaSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link RuntimeInfoSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link AnnotationSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link DeploymentOptionSection > }{@code >}
     * {@link JAXBElement }{@code <}{@link StartupSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link ResourceAllocationSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConnectionSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link CustomizationSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link ProductSection > }{@code >}
     * {@link JAXBElement }{@code <}{@link GuestCustomizationSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link OperatingSystemSection > }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkConfigSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link NetworkSection> }{@code >}
     * {@link JAXBElement }{@code <}{@link DiskSection > }{@code >}
     * {@link JAXBElement }{@code <}{@link InstallSection> }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends Section<?>>> getSections() {
        if (sections == null) {
            sections = new ArrayList<JAXBElement<? extends Section<?>>>();
        }
        return this.sections;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      CaptureVAppParams that = CaptureVAppParams.class.cast(o);
      return equal(source, that.source) && 
           equal(sections, that.sections);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(source, 
            sections);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("source", source)
            .add("sections", sections).toString();
   }

}
