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
import com.google.common.base.Objects.ToStringHelper;

/**
 * Provides an administrative view of a vDC. Includes all members
 * of the Vdc element, and adds several elements that can be
 * viewed and modified only by administrators.
 *
 * <pre>
 * &lt;complexType name="AdminVdc">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VdcType">
 *       &lt;sequence>
 *         &lt;element name="ResourceGuaranteedMemory" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="ResourceGuaranteedCpu" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="VCpuInMhz" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="IsThinProvision" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="NetworkPoolReference" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" minOccurs="0"/>
 *         &lt;element name="ProviderVdcReference" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" minOccurs="0"/>
 *         &lt;element name="UsesFastProvisioning" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "AdminVdc")
@XmlType(name = "AdminVdcType", propOrder = {
    "resourceGuaranteedMemory",
    "resourceGuaranteedCpu",
    "vCpuInMhz",
    "isThinProvision",
    "networkPoolReference",
    "providerVdcReference",
    "usesFastProvisioning"
})
public class AdminVdc extends Vdc {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public abstract static class Builder<T extends Builder<T>> extends Vdc.Builder<T> {
      private Double resourceGuaranteedMemory;
      private Double resourceGuaranteedCpu;
      private Long vCpuInMhz;
      private Boolean isThinProvision;
      private Reference networkPoolReference;
      private Reference providerVdcReference;
      private Boolean usesFastProvisioning;

      /**
       * @see AdminVdc#getResourceGuaranteedMemory()
       */
      public T resourceGuaranteedMemory(Double resourceGuaranteedMemory) {
         this.resourceGuaranteedMemory = resourceGuaranteedMemory;
         return self();
      }

      /**
       * @see AdminVdc#getResourceGuaranteedCpu()
       */
      public T resourceGuaranteedCpu(Double resourceGuaranteedCpu) {
         this.resourceGuaranteedCpu = resourceGuaranteedCpu;
         return self();
      }

      /**
       * @see AdminVdc#getVCpuInMhz()
       */
      public T vCpuInMhz(Long vCpuInMhz) {
         this.vCpuInMhz = vCpuInMhz;
         return self();
      }

      /**
       * @see AdminVdc#getIsThinProvision()
       */
      public T isThinProvision(Boolean isThinProvision) {
         this.isThinProvision = isThinProvision;
         return self();
      }

      /**
       * @see AdminVdc#getNetworkPoolReference()
       */
      public T networkPoolReference(Reference networkPoolReference) {
         this.networkPoolReference = networkPoolReference;
         return self();
      }

      /**
       * @see AdminVdc#getProviderVdcReference()
       */
      public T providerVdcReference(Reference providerVdcReference) {
         this.providerVdcReference = providerVdcReference;
         return self();
      }

      /**
       * @see AdminVdc#getUsesFastProvisioning()
       */
      public T usesFastProvisioning(Boolean usesFastProvisioning) {
         this.usesFastProvisioning = usesFastProvisioning;
         return self();
      }

      @Override
      public AdminVdc build() {
         return new AdminVdc(this);
      }

      public T fromAdminVdc(AdminVdc in) {
         return fromVdc(in)
            .resourceGuaranteedMemory(in.getResourceGuaranteedMemory())
            .resourceGuaranteedCpu(in.getResourceGuaranteedCpu())
            .vCpuInMhz(in.getVCpuInMhz())
            .isThinProvision(in.isThinProvision())
            .networkPoolReference(in.getNetworkPoolReference())
            .providerVdcReference(in.getProviderVdcReference())
            .usesFastProvisioning(in.usesFastProvisioning());
      }
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override protected ConcreteBuilder self() {
         return this;
      }
   }
   
   @SuppressWarnings("unused")
   private AdminVdc() {
      // For JAXB
   }
   
    public AdminVdc(Builder<?> b) {
      super(b);
      resourceGuaranteedMemory = b.resourceGuaranteedMemory;
      resourceGuaranteedCpu = b.resourceGuaranteedCpu;
      vCpuInMhz = b.vCpuInMhz;
      isThinProvision = b.isThinProvision;
      networkPoolReference = b.networkPoolReference;
      providerVdcReference = b.providerVdcReference;
      usesFastProvisioning = b.usesFastProvisioning;
   }

   @XmlElement(name = "ResourceGuaranteedMemory")
    protected Double resourceGuaranteedMemory;
    @XmlElement(name = "ResourceGuaranteedCpu")
    protected Double resourceGuaranteedCpu;
    @XmlElement(name = "VCpuInMhz")
    protected Long vCpuInMhz;
    @XmlElement(name = "IsThinProvision")
    protected Boolean isThinProvision;
    @XmlElement(name = "NetworkPoolReference")
    protected Reference networkPoolReference;
    @XmlElement(name = "ProviderVdcReference")
    protected Reference providerVdcReference;
    @XmlElement(name = "UsesFastProvisioning")
    protected Boolean usesFastProvisioning;

    /**
     * Gets the value of the resourceGuaranteedMemory property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getResourceGuaranteedMemory() {
        return resourceGuaranteedMemory;
    }

    /**
     * Gets the value of the resourceGuaranteedCpu property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getResourceGuaranteedCpu() {
        return resourceGuaranteedCpu;
    }

    /**
     * Gets the value of the vCpuInMhz property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getVCpuInMhz() {
        return vCpuInMhz;
    }

    /**
     * Gets the value of the isThinProvision property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isThinProvision() {
        return isThinProvision;
    }

    /**
     * Gets the value of the networkPoolReference property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getNetworkPoolReference() {
        return networkPoolReference;
    }

    /**
     * Gets the value of the providerVdcReference property.
     * 
     * @return
     *     possible object is
     *     {@link Reference }
     *     
     */
    public Reference getProviderVdcReference() {
        return providerVdcReference;
    }

    /**
     * Gets the value of the usesFastProvisioning property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean usesFastProvisioning() {
        return usesFastProvisioning;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AdminVdc that = AdminVdc.class.cast(o);
      return super.equals(that) &&
           equal(resourceGuaranteedMemory, that.resourceGuaranteedMemory) && 
           equal(resourceGuaranteedCpu, that.resourceGuaranteedCpu) && 
           equal(vCpuInMhz, that.vCpuInMhz) && 
           equal(isThinProvision, that.isThinProvision) && 
           equal(networkPoolReference, that.networkPoolReference) && 
           equal(providerVdcReference, that.providerVdcReference) && 
           equal(usesFastProvisioning, that.usesFastProvisioning);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           resourceGuaranteedMemory, 
           resourceGuaranteedCpu, 
           vCpuInMhz, 
           isThinProvision, 
           networkPoolReference, 
           providerVdcReference, 
           usesFastProvisioning);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("resourceGuaranteedMemory", resourceGuaranteedMemory)
            .add("resourceGuaranteedCpu", resourceGuaranteedCpu)
            .add("vCpuInMhz", vCpuInMhz)
            .add("isThinProvision", isThinProvision)
            .add("networkPoolReference", networkPoolReference)
            .add("providerVdcReference", providerVdcReference)
            .add("usesFastProvisioning", usesFastProvisioning);
   }

}
