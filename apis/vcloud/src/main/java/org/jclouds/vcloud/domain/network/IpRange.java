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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      IpRange that = IpRange.class.cast(o);
      return equal(this.startAddress, that.startAddress) && equal(this.endAddress, that.endAddress);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(startAddress, endAddress);
   }

   @Override
   public String toString() {
      return  Objects.toStringHelper("").omitNullValues().add("startAddress", startAddress)
            .add("endAddress", endAddress).toString();
   }

}
