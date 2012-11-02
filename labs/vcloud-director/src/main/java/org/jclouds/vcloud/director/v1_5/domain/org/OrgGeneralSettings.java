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
package org.jclouds.vcloud.director.v1_5.domain.org;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Defines general org settings.
 * 
 * <pre>
 * &lt;complexType name="OrgGeneralSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="CanPublishCatalogs" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DeployedVMQuota" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StoredVmQuota" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="UseServerBootSequence" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DelayAfterPowerOnSeconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "GeneralOrgSettings")
@XmlType(propOrder = {
    "canPublishCatalogs",
    "deployedVMQuota",
    "storedVmQuota",
    "useServerBootSequence",
    "delayAfterPowerOnSeconds"
})
public class OrgGeneralSettings extends Resource {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgGeneralSettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private Boolean canPublishCatalogs;
      private Integer deployedVMQuota;
      private Integer storedVmQuota;
      private Boolean useServerBootSequence;
      private Integer delayAfterPowerOnSeconds;

      /**
       * @see OrgGeneralSettings#getCanPublishCatalogs()
       */
      public B canPublishCatalogs(Boolean canPublishCatalogs) {
         this.canPublishCatalogs = canPublishCatalogs;
         return self();
      }

      /**
       * @see OrgGeneralSettings#getDeployedVMQuota()
       */
      public B deployedVMQuota(Integer deployedVMQuota) {
         this.deployedVMQuota = deployedVMQuota;
         return self();
      }

      /**
       * @see OrgGeneralSettings#getStoredVmQuota()
       */
      public B storedVmQuota(Integer storedVmQuota) {
         this.storedVmQuota = storedVmQuota;
         return self();
      }

      /**
       * @see OrgGeneralSettings#getUseServerBootSequence()
       */
      public B useServerBootSequence(Boolean useServerBootSequence) {
         this.useServerBootSequence = useServerBootSequence;
         return self();
      }

      /**
       * @see OrgGeneralSettings#getDelayAfterPowerOnSeconds()
       */
      public B delayAfterPowerOnSeconds(Integer delayAfterPowerOnSeconds) {
         this.delayAfterPowerOnSeconds = delayAfterPowerOnSeconds;
         return self();
      }

      @Override
      public OrgGeneralSettings build() {
         return new OrgGeneralSettings(this);
      }

      public B fromOrgGeneralSettings(OrgGeneralSettings in) {
         return fromResource(in)
            .canPublishCatalogs(in.canPublishCatalogs())
            .deployedVMQuota(in.getDeployedVMQuota())
            .storedVmQuota(in.getStoredVmQuota())
            .useServerBootSequence(in.useServerBootSequence())
            .delayAfterPowerOnSeconds(in.getDelayAfterPowerOnSeconds());
      }
   }

   protected OrgGeneralSettings() {
      // For JAXB
   }

   protected OrgGeneralSettings(Builder<?> builder) {
      super(builder);
      this.canPublishCatalogs = builder.canPublishCatalogs;
      this.deployedVMQuota = builder.deployedVMQuota;
      this.storedVmQuota = builder.storedVmQuota;
      this.useServerBootSequence = builder.useServerBootSequence;
      this.delayAfterPowerOnSeconds = builder.delayAfterPowerOnSeconds;
   }

   @XmlElement(name = "CanPublishCatalogs")
    protected Boolean canPublishCatalogs;
    @XmlElement(name = "DeployedVMQuota")
    protected Integer deployedVMQuota;
    @XmlElement(name = "StoredVmQuota")
    protected Integer storedVmQuota;
    @XmlElement(name = "UseServerBootSequence")
    protected Boolean useServerBootSequence;
    @XmlElement(name = "DelayAfterPowerOnSeconds")
    protected Integer delayAfterPowerOnSeconds;

    /**
     * Gets the value of the canPublishCatalogs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean canPublishCatalogs() {
        return canPublishCatalogs;
    }

    /**
     * Gets the value of the deployedVMQuota property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeployedVMQuota() {
        return deployedVMQuota;
    }

    /**
     * Gets the value of the storedVmQuota property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStoredVmQuota() {
        return storedVmQuota;
    }

    /**
     * Gets the value of the useServerBootSequence property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean useServerBootSequence() {
        return useServerBootSequence;
    }

    /**
     * Gets the value of the delayAfterPowerOnSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDelayAfterPowerOnSeconds() {
        return delayAfterPowerOnSeconds;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgGeneralSettings that = OrgGeneralSettings.class.cast(o);
      return super.equals(that) && 
           equal(canPublishCatalogs, that.canPublishCatalogs) && 
           equal(deployedVMQuota, that.deployedVMQuota) && 
           equal(storedVmQuota, that.storedVmQuota) && 
           equal(useServerBootSequence, that.useServerBootSequence) && 
           equal(delayAfterPowerOnSeconds, that.delayAfterPowerOnSeconds);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           canPublishCatalogs, 
           deployedVMQuota, 
           storedVmQuota, 
           useServerBootSequence, 
           delayAfterPowerOnSeconds);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("canPublishCatalogs", canPublishCatalogs)
            .add("deployedVMQuota", deployedVMQuota)
            .add("storedVmQuota", storedVmQuota)
            .add("useServerBootSequence", useServerBootSequence)
            .add("delayAfterPowerOnSeconds", delayAfterPowerOnSeconds);
   }

}
