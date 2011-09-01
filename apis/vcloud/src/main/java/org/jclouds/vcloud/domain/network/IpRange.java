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
package org.jclouds.vcloud.domain.network;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The IpRange element defines a range of IP addresses available on a network.
 * 
 */
public class IpRange {
   private final String startAddress;
   private final String endAddress;

   public IpRange(String startAddress, String endAddress) {
      this.startAddress = checkNotNull(startAddress, "startAddress");
      this.endAddress = checkNotNull(endAddress, "endAddress");
   }

   /**
    * @return lowest IP address in the range
    * 
    * @since vcloud api 0.9
    */
   public String getStartAddress() {
      return startAddress;
   }

   /**
    * @return highest IP address in the range
    * 
    * @since vcloud api 0.9
    */
   public String getEndAddress() {
      return endAddress;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endAddress == null) ? 0 : endAddress.hashCode());
      result = prime * result + ((startAddress == null) ? 0 : startAddress.hashCode());
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
      IpRange other = (IpRange) obj;
      if (endAddress == null) {
         if (other.endAddress != null)
            return false;
      } else if (!endAddress.equals(other.endAddress))
         return false;
      if (startAddress == null) {
         if (other.startAddress != null)
            return false;
      } else if (!startAddress.equals(other.startAddress))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[startAddress=" + startAddress + ", endAddress=" + endAddress + "]";
   }
}