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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * <p>Java class for StaticRoute complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="StaticRoute">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}VCloudExtensibleType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Network" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NextHopIp" type="{http://www.vmware.com/vcloud/v1.5}IpAddressType"/>
 *         &lt;element name="Interface" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticRoute", propOrder = {
      "name",
      "network",
      "nextHopIp",
      "_interface"
})
public class StaticRoute {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromStaticRoute(this);
   }

   public static class Builder {
      private String name;
      private String network;
      private String nextHopIp;
      private String _interface;

      /**
       * @see StaticRoute#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see StaticRoute#getNetwork()
       */
      public Builder network(String network) {
         this.network = network;
         return this;
      }

      /**
       * @see StaticRoute#getNextHopIp()
       */
      public Builder nextHopIp(String nextHopIp) {
         this.nextHopIp = nextHopIp;
         return this;
      }

      /**
       * @see StaticRoute#getInterface()
       */
      public Builder _interface(String _interface) {
         this._interface = _interface;
         return this;
      }

      public StaticRoute build() {
         return new StaticRoute(name, network, nextHopIp, _interface);
      }

      public Builder fromStaticRoute(StaticRoute in) {
         return name(in.getName())
               .network(in.getNetwork())
               .nextHopIp(in.getNextHopIp())
               ._interface(in.getInterface());
      }
   }

   private StaticRoute(String name, String network, String nextHopIp, String _interface) {
      this.name = checkNotNull(name, "name");
      this.network = checkNotNull(network, "network");
      this.nextHopIp = checkNotNull(nextHopIp, "nextHopIp");
      this._interface = checkNotNull(_interface, "interface");
   }

   private StaticRoute() {
      // for JAXB
   }

   private StaticRoute(String _interface) {
      this._interface = _interface;
   }

   @XmlElement(name = "Name", required = true)
   protected String name;
   @XmlElement(name = "Network", required = true)
   protected String network;
   @XmlElement(name = "NextHopIp", required = true)
   protected String nextHopIp;
   @XmlElement(name = "Interface", required = true)
   protected String _interface;

   /**
    * Gets the value of the name property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the value of the network property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getNetwork() {
      return network;
   }

   /**
    * Gets the value of the nextHopIp property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getNextHopIp() {
      return nextHopIp;
   }

   /**
    * Gets the value of the interface property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getInterface() {
      return _interface;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      StaticRoute that = StaticRoute.class.cast(o);
      return equal(name, that.name) &&
            equal(network, that.network) &&
            equal(nextHopIp, that.nextHopIp) &&
            equal(_interface, that._interface);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name,
            network,
            nextHopIp,
            _interface);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("name", name)
            .add("network", network)
            .add("nextHopIp", nextHopIp)
            .add("_interface", _interface).toString();
   }
}
