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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.testng.collections.Lists;

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
 *         &lt;element name="ResourceEntity" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
    "resourceEntities"
})
public class ResourceEntities {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromResourceEntities(this);
   }

   public static class Builder {
      
      private List<Reference> resourceEntities = Lists.newArrayList();
      
      /**
       * @see ResourceEntities#getResourceEntities()
       */
      public Builder resourceEntities(List<Reference> resourceEntities) {
         this.resourceEntities = Lists.newArrayList(checkNotNull(resourceEntities, "resourceEntities"));
         return this;
      }

      /**
       * @see ResourceEntities#getResourceEntities()
       */
      public Builder resourceEntity(Reference resourceEntity) {
         resourceEntities.add(checkNotNull(resourceEntity, "resourceEntity"));
         return this;
      }

      public ResourceEntities build() {
         ResourceEntities resourceEntities = new ResourceEntities(this.resourceEntities);
         return resourceEntities;
      }


      public Builder fromResourceEntities(ResourceEntities in) {
         return resourceEntities(in.getResourceEntities());
      }
   }

   private ResourceEntities() {
      // For JAXB and builder use
   }

   private ResourceEntities(List<Reference> resourceEntity) {
      this.resourceEntities = resourceEntity;
   }


    @XmlElement(name = "ResourceEntity")
    protected List<Reference> resourceEntities;

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
     * {@link ReferenceType }
     * 
     * 
     */
    public List<Reference> getResourceEntities() {
        if (resourceEntities == null) {
            resourceEntities = Lists.newArrayList();
        }
        return this.resourceEntities;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntities that = ResourceEntities.class.cast(o);
      return equal(resourceEntities, that.resourceEntities);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(resourceEntities);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("resourceEntity", resourceEntities).toString();
   }

}
