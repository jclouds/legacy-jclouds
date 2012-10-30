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
package org.jclouds.vcloud.domain.network.nat.rules;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.domain.network.nat.NatProtocol;
import org.jclouds.vcloud.domain.network.nat.NatRule;

import com.google.common.base.Objects;

/**
 * The PortForwardingRule element describes a NAT rule that maps an IP address and port in an
 * organization network to an external IP address and port.
 * 
 * @since vcloud 0.8
 * @author Adrian Cole
 */
public class PortForwardingRule implements NatRule {
   private final String externalIP;
   private final int externalPort;
   private final String internalIP;
   private final int internalPort;
   private final NatProtocol protocol;

   public PortForwardingRule(String externalIP, int externalPort, String internalIP, int internalPort,
            NatProtocol protocol) {
      this.externalIP = checkNotNull(externalIP, "externalIP");
      this.externalPort = externalPort;
      this.internalIP = checkNotNull(internalIP, "internalIP");
      this.internalPort = internalPort;
      this.protocol = checkNotNull(protocol, "protocol");
   }

   /**
    * IP address to which this NAT rule maps the IP address specified in the InternalIp element.
    */
   @Override
   public String getExternalIP() {
      return externalIP;
   }

   /**
    * network port to which this NAT rule maps the port number specified in the InternalPort element
    */
   public int getExternalPort() {
      return externalPort;
   }

   /**
    * IP address to which this NAT rule maps the IP address specified in the ExternalIp element.
    */
   public String getInternalIP() {
      return internalIP;
   }

   /**
    * network port to which this NAT rule maps the port number specified in the ExternalPort
    * element.
    */
   public int getInternalPort() {
      return internalPort;
   }

   /**
    * specifies the network protocol to which this rule applies
    */
   public NatProtocol getProtocol() {
      return protocol;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      PortForwardingRule that = PortForwardingRule.class.cast(o);
      return equal(this.externalIP, that.externalIP) && equal(this.externalPort, that.externalPort)
            && equal(this.internalIP, that.internalIP) && equal(this.internalPort, that.internalPort)
            && equal(this.protocol, that.protocol);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIP, externalPort, internalIP, internalPort, protocol);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("externalIP", externalIP)
            .add("externalPort", externalPort).add("internalIP", internalIP).add("internalPort", internalPort)
            .add("protocol", protocol).toString();
   }

}
