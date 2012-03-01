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

import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;


/**
 * Represents a list of references to resource entities.
 * <p/>
 * <p/>
 * <p>Java class for ResourceEntities complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
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
 */
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

      private Set<Reference> resourceEntities = Sets.newLinkedHashSet();

      /**
       * @see ResourceEntities#getResourceEntities()
       */
      public Builder resourceEntities(Set<Reference> resourceEntities) {
         this.resourceEntities = checkNotNull(resourceEntities, "resourceEntities");
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
         return new ResourceEntities(this.resourceEntities);
      }


      public Builder fromResourceEntities(ResourceEntities in) {
         return resourceEntities(in.getResourceEntities());
      }
   }

   private ResourceEntities() {
      // For JAXB and builder use
   }

   private ResourceEntities(Set<Reference> resourceEntity) {
      this.resourceEntities = ImmutableSet.copyOf(resourceEntity);
   }


   @XmlElement(name = "ResourceEntity")
   private Set<Reference> resourceEntities = Sets.newLinkedHashSet();

   /**
    * Gets the value of the resourceEntity property.
    */
   public Set<Reference> getResourceEntities() {
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
