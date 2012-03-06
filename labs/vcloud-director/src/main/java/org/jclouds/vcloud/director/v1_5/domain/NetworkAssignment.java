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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * Represents mapping between a VM and vApp network.
 * <p/>
 * <p/>
 * <p>Java class for NetworkAssignment complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="NetworkAssignment">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;attribute name="innerNetwork" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="containerNetwork" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "NetworkAssignment")
public class NetworkAssignment


{
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkAssignment(this);
   }

   public static class Builder {

      private String innerNetwork;
      private String containerNetwork;

      /**
       * @see NetworkAssignment#getInnerNetwork()
       */
      public Builder innerNetwork(String innerNetwork) {
         this.innerNetwork = innerNetwork;
         return this;
      }

      /**
       * @see NetworkAssignment#getContainerNetwork()
       */
      public Builder containerNetwork(String containerNetwork) {
         this.containerNetwork = containerNetwork;
         return this;
      }

      public NetworkAssignment build() {
         return new NetworkAssignment(innerNetwork, containerNetwork);
      }

      public Builder fromNetworkAssignment(NetworkAssignment in) {
         return innerNetwork(in.getInnerNetwork())
               .containerNetwork(in.getContainerNetwork());
      }
   }

   private NetworkAssignment(String innerNetwork, String containerNetwork) {
      this.innerNetwork = innerNetwork;
      this.containerNetwork = containerNetwork;
   }

   private NetworkAssignment() {
      // for JAXB
   }


   @XmlAttribute(required = true)
   protected String innerNetwork;
   @XmlAttribute(required = true)
   protected String containerNetwork;

   /**
    * Gets the value of the innerNetwork property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getInnerNetwork() {
      return innerNetwork;
   }

   /**
    * Gets the value of the containerNetwork property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getContainerNetwork() {
      return containerNetwork;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkAssignment that = NetworkAssignment.class.cast(o);
      return equal(innerNetwork, that.innerNetwork) &&
            equal(containerNetwork, that.containerNetwork);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(innerNetwork,
            containerNetwork);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("innerNetwork", innerNetwork)
            .add("containerNetwork", containerNetwork).toString();
   }

}
