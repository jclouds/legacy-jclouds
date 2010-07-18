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
package org.jclouds.aws.ec2.domain;

import java.util.Set;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-IpPermissionType.html"
 *      />
 * @author Adrian Cole
 */
public class IpPermission implements Comparable<IpPermission> {
   private final int fromPort;
   private final int toPort;
   private final Set<UserIdGroupPair> groups;
   private final IpProtocol ipProtocol;
   private final Set<String> ipRanges;

   public IpPermission(int fromPort, int toPort, Set<UserIdGroupPair> groups,
            IpProtocol ipProtocol, Set<String> ipRanges) {
      this.fromPort = fromPort;
      this.toPort = toPort;
      this.groups = groups;
      this.ipProtocol = ipProtocol;
      this.ipRanges = ipRanges;
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(IpPermission o) {
      return (this == o) ? 0 : getIpProtocol().compareTo(o.getIpProtocol());
   }

   /**
    * Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP type number
    * of -1 indicates a wildcard (i.e., any ICMP type number).
    */
   public int getFromPort() {
      return fromPort;
   }

   /**
    * End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1 indicates
    * a wildcard (i.e., any ICMP code).
    */
   public int getToPort() {
      return toPort;
   }

   /**
    * List of security group and user ID pairs.
    */
   public Set<UserIdGroupPair> getGroups() {
      return groups;
   }

   /**
    * IP protocol
    */
   public IpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * IP ranges.
    */
   public Set<String> getIpRanges() {
      return ipRanges;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + fromPort;
      result = prime * result + ((groups == null) ? 0 : groups.hashCode());
      result = prime * result + ((ipProtocol == null) ? 0 : ipProtocol.hashCode());
      result = prime * result + ((ipRanges == null) ? 0 : ipRanges.hashCode());
      result = prime * result + toPort;
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
      IpPermission other = (IpPermission) obj;
      if (fromPort != other.fromPort)
         return false;
      if (groups == null) {
         if (other.groups != null)
            return false;
      } else if (!groups.equals(other.groups))
         return false;
      if (ipProtocol == null) {
         if (other.ipProtocol != null)
            return false;
      } else if (!ipProtocol.equals(other.ipProtocol))
         return false;
      if (ipRanges == null) {
         if (other.ipRanges != null)
            return false;
      } else if (!ipRanges.equals(other.ipRanges))
         return false;
      if (toPort != other.toPort)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "IpPermission [fromPort=" + fromPort + ", groups=" + groups + ", ipProtocol="
               + ipProtocol + ", ipRanges=" + ipRanges + ", toPort=" + toPort + "]";
   }

}