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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.domain.network.nat.NatProtocol;
import org.jclouds.vcloud.domain.network.nat.NatRule;

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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((externalIP == null) ? 0 : externalIP.hashCode());
      result = prime * result + externalPort;
      result = prime * result + ((internalIP == null) ? 0 : internalIP.hashCode());
      result = prime * result + internalPort;
      result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PortForwardingRule other = (PortForwardingRule) obj;
      if (externalIP == null) {
         if (other.externalIP != null)
            return false;
      } else if (!externalIP.equals(other.externalIP))
         return false;
      if (externalPort != other.externalPort)
         return false;
      if (internalIP == null) {
         if (other.internalIP != null)
            return false;
      } else if (!internalIP.equals(other.internalIP))
         return false;
      if (internalPort != other.internalPort)
         return false;
      if (protocol == null) {
         if (other.protocol != null)
            return false;
      } else if (!protocol.equals(other.protocol))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[externalIP=" + externalIP + ", externalPort=" + externalPort + ", internalIP=" + internalIP
               + ", internalPort=" + internalPort + ", protocol=" + protocol + "]";
   }

}