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

import static com.google.common.base.Objects.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents vApp/VM deployment parameters.
 *
 * <pre>
 * &lt;complexType name="DeployVAppParams" /&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeployVAppParams")
public class DeployVAppParams {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromDeployVAppParams(this);
   }

   public static class Builder {
      
      private Boolean powerOn = Boolean.TRUE;
      private Integer deploymentLeaseSeconds;
      private Boolean forceCustomization = Boolean.FALSE;

      /**
       * @see DeployVAppParams#isPowerOn()
       */
      public Builder powerOn(Boolean powerOn) {
         this.powerOn = powerOn;
         return this;
      }

      /**
       * @see DeployVAppParams#isPowerOn()
       */
      public Builder powerOn() {
         this.powerOn = Boolean.TRUE;
         return this;
      }

      /**
       * @see DeployVAppParams#isPowerOn()
       */
      public Builder notPowerOn() {
         this.powerOn = Boolean.FALSE;
         return this;
      }

      /**
       * @see DeployVAppParams#getDeploymentLeaseSeconds()
       */
      public Builder deploymentLeaseSeconds(Integer deploymentLeaseSeconds) {
         this.deploymentLeaseSeconds = deploymentLeaseSeconds;
         return this;
      }

      /**
       * @see DeployVAppParams#isForceCustomization()
       */
      public Builder forceCustomization(Boolean forceCustomization) {
         this.forceCustomization = forceCustomization;
         return this;
      }

      /**
       * @see DeployVAppParams#isForceCustomization()
       */
      public Builder forceCustomization() {
         this.forceCustomization = Boolean.TRUE;
         return this;
      }

      /**
       * @see DeployVAppParams#isForceCustomization()
       */
      public Builder notForceCustomization() {
         this.forceCustomization = Boolean.FALSE;
         return this;
      }

      public DeployVAppParams build() {
         DeployVAppParams deployVAppParams = new DeployVAppParams(powerOn, deploymentLeaseSeconds, forceCustomization);
         return deployVAppParams;
      }


      public Builder fromDeployVAppParams(DeployVAppParams in) {
         return powerOn(in.isPowerOn())
            .deploymentLeaseSeconds(in.getDeploymentLeaseSeconds())
            .forceCustomization(in.isForceCustomization());
      }
   }

   protected DeployVAppParams() {
      // For JAXB and builder use
   }

   public DeployVAppParams(Boolean powerOn, Integer deploymentLeaseSeconds, Boolean forceCustomization) {
      this.powerOn = powerOn;
      this.deploymentLeaseSeconds = deploymentLeaseSeconds;
      this.forceCustomization = forceCustomization;
   }

    @XmlAttribute
    protected Boolean powerOn;
    @XmlAttribute
    protected Integer deploymentLeaseSeconds;
    @XmlAttribute
    protected Boolean forceCustomization;

    /**
     * Used to specify whether to power on vapp on deployment, if not set default value is true.
     */
    public Boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(Boolean value) {
        this.powerOn = value;
    }

    /**
     * Lease in seconds for deployment.
     */
    public Integer getDeploymentLeaseSeconds() {
        return deploymentLeaseSeconds;
    }

    public void setDeploymentLeaseSeconds(Integer value) {
        this.deploymentLeaseSeconds = value;
    }

    /**
     * Used to specify whether to force customization on deployment, if not set default value is false.
     */
    public Boolean isForceCustomization() {
        return forceCustomization;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      DeployVAppParams that = DeployVAppParams.class.cast(o);
      return equal(powerOn, that.powerOn) && 
           equal(deploymentLeaseSeconds, that.deploymentLeaseSeconds) && 
           equal(forceCustomization, that.forceCustomization);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(powerOn, 
           deploymentLeaseSeconds, 
           forceCustomization);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("powerOn", powerOn)
            .add("deploymentLeaseSeconds", deploymentLeaseSeconds)
            .add("forceCustomization", forceCustomization).toString();
   }

}
