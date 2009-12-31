/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.domain;

import java.net.InetAddress;

/**
 * Specifies a set of Network Address Translation rules using a pair of IP addresses and a pair of
 * IP port numbers.
 * 
 * @author Adrian Cole
 */
public class NatRule {
   private final InetAddress externalIP;
   private final Integer externalPort;
   private final InetAddress internalIP;
   private final Integer internalPort;

   public NatRule(InetAddress externalIP, Integer externalPort, InetAddress IntegerernalIP, Integer IntegerernalPort) {
      this.externalIP = externalIP;
      this.externalPort = externalPort;
      this.internalIP = IntegerernalIP;
      this.internalPort = IntegerernalPort;
   }

   /**
    * The externally‐visible IP address.
    */
   public InetAddress getExternalIP() {
      return externalIP;
   }

   /**
    * The externally‐visible IP port.
    */
   public Integer getExternalPort() {
      return externalPort;
   }

   /**
    * The Integerernally‐visible (non‐routable) IP address.
    */
   public InetAddress getInternalIP() {
      return internalIP;
   }

   /**
    * The Integerernally‐visible IP port.
    */
   public Integer getInternalPort() {
      return internalPort;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((externalIP == null) ? 0 : externalIP.hashCode());
      result = prime * result + ((externalPort == null) ? 0 : externalPort.hashCode());
      result = prime * result + ((internalIP == null) ? 0 : internalIP.hashCode());
      result = prime * result + ((internalPort == null) ? 0 : internalPort.hashCode());
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
      NatRule other = (NatRule) obj;
      if (externalIP == null) {
         if (other.externalIP != null)
            return false;
      } else if (!externalIP.equals(other.externalIP))
         return false;
      if (externalPort == null) {
         if (other.externalPort != null)
            return false;
      } else if (!externalPort.equals(other.externalPort))
         return false;
      if (internalIP == null) {
         if (other.internalIP != null)
            return false;
      } else if (!internalIP.equals(other.internalIP))
         return false;
      if (internalPort == null) {
         if (other.internalPort != null)
            return false;
      } else if (!internalPort.equals(other.internalPort))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "NatRule [externalIP=" + externalIP + ", externalPort=" + externalPort
               + ", internalIP=" + internalIP + ", internalPort=" + internalPort + "]";
   }

}