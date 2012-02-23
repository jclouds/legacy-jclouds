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
 *                 Represents a compute capacity with units.
 *             
 * 
 * <p>Java class for ComputeCapacity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ComputeCapacity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Cpu" type="{http://www.vmware.com/vcloud/v1.5}CapacityWithUsageType"/>
 *         &lt;element name="Memory" type="{http://www.vmware.com/vcloud/v1.5}CapacityWithUsageType"/>
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
@XmlType(name = "ComputeCapacity", propOrder = {
    "cpu",
    "memory"
})
public class ComputeCapacity {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromComputeCapacity(this);
   }

   public static class Builder {
      
      private CapacityWithUsage cpu;
      private CapacityWithUsage memory;

      /**
       * @see ComputeCapacity#getCpu()
       */
      public Builder cpu(CapacityWithUsage cpu) {
         this.cpu = cpu;
         return this;
      }

      /**
       * @see ComputeCapacity#getMemory()
       */
      public Builder memory(CapacityWithUsage memory) {
         this.memory = memory;
         return this;
      }


      public ComputeCapacity build() {
         ComputeCapacity computeCapacity = new ComputeCapacity();
         computeCapacity.setCpu(cpu);
         computeCapacity.setMemory(memory);
         return computeCapacity;
      }


      public Builder fromComputeCapacity(ComputeCapacity in) {
         return cpu(in.getCpu())
            .memory(in.getMemory());
      }
   }

   private ComputeCapacity() {
      // For JAXB and builder use
   }



    @XmlElement(name = "Cpu", required = true)
    protected CapacityWithUsage cpu;
    @XmlElement(name = "Memory", required = true)
    protected CapacityWithUsage memory;

    /**
     * Gets the value of the cpu property.
     * 
     * @return
     *     possible object is
     *     {@link CapacityWithUsage }
     *     
     */
    public CapacityWithUsage getCpu() {
        return cpu;
    }

    /**
     * Sets the value of the cpu property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapacityWithUsage }
     *     
     */
    public void setCpu(CapacityWithUsage value) {
        this.cpu = value;
    }

    /**
     * Gets the value of the memory property.
     * 
     * @return
     *     possible object is
     *     {@link CapacityWithUsage }
     *     
     */
    public CapacityWithUsage getMemory() {
        return memory;
    }

    /**
     * Sets the value of the memory property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapacityWithUsage }
     *     
     */
    public void setMemory(CapacityWithUsage value) {
        this.memory = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ComputeCapacity that = ComputeCapacity.class.cast(o);
      return equal(cpu, that.cpu) && 
           equal(memory, that.memory);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(cpu, 
           memory);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("cpu", cpu)
            .add("memory", memory).toString();
   }

}
