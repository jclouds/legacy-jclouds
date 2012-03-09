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

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Defines general org settings.
 *             
 * 
 * <p>Java class for OrgGeneralSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
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
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OrgGeneralSettings")
@XmlType(propOrder = {
    "canPublishCatalogs",
    "deployedVMQuota",
    "storedVmQuota",
    "useServerBootSequence",
    "delayAfterPowerOnSeconds"
})
public class OrgGeneralSettings extends ResourceType<OrgGeneralSettings> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgGeneralSettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgGeneralSettings> {
      
      private Boolean canPublishCatalogs;
      private Integer deployedVMQuota;
      private Integer storedVmQuota;
      private Boolean useServerBootSequence;
      private Integer delayAfterPowerOnSeconds;

      /**
       * @see OrgGeneralSettings#getCanPublishCatalogs()
       */
      public Builder canPublishCatalogs(Boolean canPublishCatalogs) {
         this.canPublishCatalogs = canPublishCatalogs;
         return this;
      }

      /**
       * @see OrgGeneralSettings#getDeployedVMQuota()
       */
      public Builder deployedVMQuota(Integer deployedVMQuota) {
         this.deployedVMQuota = deployedVMQuota;
         return this;
      }

      /**
       * @see OrgGeneralSettings#getStoredVmQuota()
       */
      public Builder storedVmQuota(Integer storedVmQuota) {
         this.storedVmQuota = storedVmQuota;
         return this;
      }

      /**
       * @see OrgGeneralSettings#getUseServerBootSequence()
       */
      public Builder useServerBootSequence(Boolean useServerBootSequence) {
         this.useServerBootSequence = useServerBootSequence;
         return this;
      }

      /**
       * @see OrgGeneralSettings#getDelayAfterPowerOnSeconds()
       */
      public Builder delayAfterPowerOnSeconds(Integer delayAfterPowerOnSeconds) {
         this.delayAfterPowerOnSeconds = delayAfterPowerOnSeconds;
         return this;
      }


      public OrgGeneralSettings build() {
         return new OrgGeneralSettings(href, type, links, 
               canPublishCatalogs, deployedVMQuota, 
               storedVmQuota, useServerBootSequence, delayAfterPowerOnSeconds);
      }

      
      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromResourceType(ResourceType<OrgGeneralSettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgGeneralSettings(OrgGeneralSettings in) {
         return fromResourceType(in)
            .canPublishCatalogs(in.canPublishCatalogs())
            .deployedVMQuota(in.getDeployedVMQuota())
            .storedVmQuota(in.getStoredVmQuota())
            .useServerBootSequence(in.useServerBootSequence())
            .delayAfterPowerOnSeconds(in.getDelayAfterPowerOnSeconds());
      }
   }

   @SuppressWarnings("unused")
   private OrgGeneralSettings() {
      // For JAXB
   }

    public OrgGeneralSettings(URI href, String type, Set<Link> links, 
          Boolean canPublishCatalogs, Integer deployedVMQuota, 
          Integer storedVmQuota, Boolean useServerBootSequence, Integer delayAfterPowerOnSeconds) {
      super(href, type, links);
      this.canPublishCatalogs = canPublishCatalogs;
      this.deployedVMQuota = deployedVMQuota;
      this.storedVmQuota = storedVmQuota;
      this.useServerBootSequence = useServerBootSequence;
      this.delayAfterPowerOnSeconds = delayAfterPowerOnSeconds;
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
