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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Represents a range of IP addresses, start and end inclusive.
 * 
 * @author danikov
 */
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "IpRange")
public class IpRange {
   
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromIpRange(this);
   }

   public static class Builder {
      
      private String startAddress;
      private String endAddress;

      /**
       * @see IpRange#getStartAddress()
       */
      public Builder startAddress(String startAddress) {
         this.startAddress = startAddress;
         return this;
      }

      /**
       * @see IpRange#getEndAddress()
       */
      public Builder endAddress(String endAddress) {
         this.endAddress = endAddress;
         return this;
      }

      public IpRange build() {
         return new IpRange(startAddress, endAddress);
      }

      public Builder fromIpRange(IpRange in) {
         return startAddress(in.getStartAddress()).endAddress(in.getEndAddress());
      }
   }
   
   private IpRange() {
      // For JAXB and builder use
   }

   private IpRange(String startAddress, String endAddress) {
      this.startAddress = startAddress;
      this.endAddress = endAddress;
   }
   
   
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "StartAddress")
   private String startAddress;
   @XmlElement(namespace = VCLOUD_1_5_NS, name = "EndAddress")
   private String endAddress;
   
   /**
    * @return Start address of the IP range.
    */
   public String getStartAddress() {
      return startAddress;
   }
   
   /**
    * @return End address of the IP range.
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
      return equal(startAddress, that.startAddress) && equal(endAddress, that.endAddress);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(startAddress, endAddress);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("startAddress", startAddress)
            .add("endAddress", endAddress).toString();
   }

}
