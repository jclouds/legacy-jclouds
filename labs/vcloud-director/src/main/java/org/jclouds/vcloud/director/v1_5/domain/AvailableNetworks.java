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

import java.util.Collection;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;


/**
 * Represents a list of references to available networks.
 * <p/>
 * <p/>
 * <p>Java class for AvailableNetworks complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="AvailableNetworks">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Network" type="{http://www.vmware.com/vcloud/v1.5}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "AvailableNetworks", propOrder = {
      "networks"
})
public class AvailableNetworks {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromAvailableNetworks(this);
   }

   public static class Builder {
      private Set<Reference> networks = Sets.newLinkedHashSet();

      /**
       * @see AvailableNetworks#getNetworks()
       */
      public Builder networks(Collection<Reference> networks) {
         this.networks = Sets.newLinkedHashSet(checkNotNull(networks, "networks"));
         return this;
      }

      /**
       * @see AvailableNetworks#getNetworks()
       */
      public Builder network(Reference network) {
         networks.add(checkNotNull(network, "network"));
         return this;
      }

      public AvailableNetworks build() {
         AvailableNetworks availableNetworks = new AvailableNetworks(networks);
         return availableNetworks;
      }


      public Builder fromAvailableNetworks(AvailableNetworks in) {
         return networks(in.getNetworks());
      }
   }

   @XmlElement(name = "Network")
   protected Set<Reference> networks = Sets.newLinkedHashSet();

   private AvailableNetworks(Set<Reference> networks) {
      this.networks = networks;
   }

   private AvailableNetworks() {
      // For JAXB
   }

   /**
    * Gets the value of the network property.
    */
   public Set<Reference> getNetworks() {
      return this.networks;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      AvailableNetworks that = AvailableNetworks.class.cast(o);
      return equal(networks, that.networks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(networks);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("network", networks).toString();
   }

}
