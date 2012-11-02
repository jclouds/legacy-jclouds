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
 * Defines default lease durations and policies for an organization.
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
 */
@XmlRootElement(name = "VAppLeaseSettings")
@XmlType(propOrder = {
    "deleteOnStorageLeaseExpiration",
    "deploymentLeaseSeconds",
    "storageLeaseSeconds"
})
public class OrgLeaseSettings extends Resource {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromOrgLeaseSettings(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private Boolean deleteOnStorageLeaseExpiration;
      private Integer deploymentLeaseSeconds;
      private Integer storageLeaseSeconds;

      /**
       * @see OrgLeaseSettings#getDeleteOnStorageLeaseExpiration()
       */
      public B deleteOnStorageLeaseExpiration(Boolean deleteOnStorageLeaseExpiration) {
         this.deleteOnStorageLeaseExpiration = deleteOnStorageLeaseExpiration;
         return self();
      }

      /**
       * @see OrgLeaseSettings#getDeploymentLeaseSeconds()
       */
      public B deploymentLeaseSeconds(Integer deploymentLeaseSeconds) {
         this.deploymentLeaseSeconds = deploymentLeaseSeconds;
         return self();
      }

      /**
       * @see OrgLeaseSettings#getStorageLeaseSeconds()
       */
      public B storageLeaseSeconds(Integer storageLeaseSeconds) {
         this.storageLeaseSeconds = storageLeaseSeconds;
         return self();
      }

      @Override
      public OrgLeaseSettings build() {
         return new OrgLeaseSettings(this);
      }

      public B fromOrgLeaseSettings(OrgLeaseSettings in) {
         return fromResource(in)
            .deleteOnStorageLeaseExpiration(in.deleteOnStorageLeaseExpiration())
            .deploymentLeaseSeconds(in.getDeploymentLeaseSeconds())
            .storageLeaseSeconds(in.getStorageLeaseSeconds());
      }
   }

   protected OrgLeaseSettings() {
      // For JAXB
   }

   protected OrgLeaseSettings(Builder<?> builder) {
      super(builder);
      this.deleteOnStorageLeaseExpiration = builder.deleteOnStorageLeaseExpiration;
      this.deploymentLeaseSeconds = builder.deploymentLeaseSeconds;
      this.storageLeaseSeconds = builder.storageLeaseSeconds;
   }

   @XmlElement(name = "DeleteOnStorageLeaseExpiration")
    private Boolean deleteOnStorageLeaseExpiration;
    @XmlElement(name = "DeploymentLeaseSeconds")
    private Integer deploymentLeaseSeconds;
    @XmlElement(name = "StorageLeaseSeconds")
    private Integer storageLeaseSeconds;

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
