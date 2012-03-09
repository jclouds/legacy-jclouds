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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;


/**
 * 
 *                 Defines default lease durations and policies for an organization.
 *             
 * 
 * <p>Java class for OrgLeaseSettings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrgLeaseSettings">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceType">
 *       &lt;sequence>
 *         &lt;element name="DeleteOnStorageLeaseExpiration" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="DeploymentLeaseSeconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="StorageLeaseSeconds" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
@XmlType(name = "OrgLeaseSettings", propOrder = {
    "deleteOnStorageLeaseExpiration",
    "deploymentLeaseSeconds",
    "storageLeaseSeconds"
})
public class OrgLeaseSettings extends ResourceType<OrgLeaseSettings> {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOrgLeaseSettings(this);
   }

   public static class Builder extends ResourceType.Builder<OrgLeaseSettings> {
      
      private Boolean deleteOnStorageLeaseExpiration;
      private Integer deploymentLeaseSeconds;
      private Integer storageLeaseSeconds;

      /**
       * @see OrgLeaseSettings#getDeleteOnStorageLeaseExpiration()
       */
      public Builder deleteOnStorageLeaseExpiration(Boolean deleteOnStorageLeaseExpiration) {
         this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
         return this;
      }

      /**
       * @see OrgLeaseSettings#getDeploymentLeaseSeconds()
       */
      public Builder deploymentLeaseSeconds(Integer deploymentLeaseSeconds) {
         this.deploymentLeaseSeconds = deploymentLeaseSeconds;
         return this;
      }

      /**
       * @see OrgLeaseSettings#getStorageLeaseSeconds()
       */
      public Builder storageLeaseSeconds(Integer storageLeaseSeconds) {
         this.storageLeaseSeconds = storageLeaseSeconds;
         return this;
      }


      public OrgLeaseSettings build() {
         return new OrgLeaseSettings(href, type, links, deleteOnStorageLeaseExpiration, 
               deploymentLeaseSeconds, storageLeaseSeconds);
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
      public Builder fromResourceType(ResourceType<OrgLeaseSettings> in) {
          return Builder.class.cast(super.fromResourceType(in));
      }
      public Builder fromOrgLeaseSettings(OrgLeaseSettings in) {
         return fromResourceType(in)
            .deleteOnStorageLeaseExpiration(in.deleteOnStorageLeaseExpiration())
            .deploymentLeaseSeconds(in.getDeploymentLeaseSeconds())
            .storageLeaseSeconds(in.getStorageLeaseSeconds());
      }
   }

   @SuppressWarnings("unused")
   private OrgLeaseSettings() {
      // For JAXB
   }

    public OrgLeaseSettings(URI href, String type, Set<Link> links,
         Boolean deleteOnStorageLeaseExpiration,
         Integer deploymentLeaseSeconds, Integer storageLeaseSeconds) {
      super(href, type, links);
      this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
      this.deploymentLeaseSeconds = deploymentLeaseSeconds;
      this.storageLeaseSeconds = storageLeaseSeconds;
   }

   @XmlElement(name = "DeleteOnStorageLeaseExpiration")
    protected Boolean deleteOnStorageLeaseExpiration;
    @XmlElement(name = "DeploymentLeaseSeconds")
    protected Integer deploymentLeaseSeconds;
    @XmlElement(name = "StorageLeaseSeconds")
    protected Integer storageLeaseSeconds;

    /**
     * Gets the value of the deleteOnStorageLeaseExpiration property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean deleteOnStorageLeaseExpiration() {
        return deleteOnStorageLeaseExpiration;
    }

    /**
     * Gets the value of the deploymentLeaseSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDeploymentLeaseSeconds() {
        return deploymentLeaseSeconds;
    }

    /**
     * Gets the value of the storageLeaseSeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getStorageLeaseSeconds() {
        return storageLeaseSeconds;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OrgLeaseSettings that = OrgLeaseSettings.class.cast(o);
      return super.equals(that) && 
           equal(deleteOnStorageLeaseExpiration, that.deleteOnStorageLeaseExpiration) && 
           equal(deploymentLeaseSeconds, that.deploymentLeaseSeconds) && 
           equal(storageLeaseSeconds, that.storageLeaseSeconds);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), 
           deleteOnStorageLeaseExpiration, 
           deploymentLeaseSeconds, 
           storageLeaseSeconds);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("deleteOnStorageLeaseExpiration", deleteOnStorageLeaseExpiration)
            .add("deploymentLeaseSeconds", deploymentLeaseSeconds)
            .add("storageLeaseSeconds", storageLeaseSeconds);
   }

}
