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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a list of references to resource entities.
 *             
 * 
 * <p>Java class for ResourceEntities complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceEntities">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="ResourceEntity" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType<?>Type" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "ResourceEntities", propOrder = {
    "resourceEntity"
})
public class ResourceEntities {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromResourceEntities(this);
   }

   public static class Builder {
      
      private List<ReferenceType<?>> resourceEntity;

      /**
       * @see ResourceEntities#getResourceEntity()
       */
      public Builder resourceEntity(List<ReferenceType<?>> resourceEntity) {
         this.resourceEntity = resourceEntity;
         return this;
      }


      public ResourceEntities build() {
         ResourceEntities resourceEntities = new ResourceEntities(resourceEntity);
         return resourceEntities;
      }


      public Builder fromResourceEntities(ResourceEntities in) {
         return resourceEntity(in.getResourceEntity());
      }
   }

   private ResourceEntities() {
      // For JAXB and builder use
   }

   private ResourceEntities(List<ReferenceType<?>> resourceEntity) {
      this.resourceEntity = resourceEntity;
   }


    @XmlElement(name = "ResourceEntity")
    protected List<ReferenceType<?>> resourceEntity;

    /**
     * Gets the value of the resourceEntity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceEntity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceEntity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType<?>Type }
     * 
     * 
     */
    public List<ReferenceType<?>> getResourceEntity() {
        if (resourceEntity == null) {
            resourceEntity = new ArrayList<ReferenceType<?>>();
        }
        return this.resourceEntity;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntities that = ResourceEntities.class.cast(o);
      return equal(resourceEntity, that.resourceEntity);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(resourceEntity);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("resourceEntity", resourceEntity).toString();
   }

}
