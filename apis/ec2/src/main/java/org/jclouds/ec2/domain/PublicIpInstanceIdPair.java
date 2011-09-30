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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-DescribeAddressesResponseInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class PublicIpInstanceIdPair implements Comparable<PublicIpInstanceIdPair> {

   private final String region;
   @Nullable
   private final String instanceId;
   private final String publicIp;

   public PublicIpInstanceIdPair(String region, String publicIp, @Nullable String instanceId) {
      this.region = checkNotNull(region, "region");
      this.instanceId = instanceId;
      this.publicIp = checkNotNull(publicIp, "publicIp");
   }

   /**
    * Elastic IP addresses are tied to a Region and cannot be mapped across Regions.
    */
   public String getRegion() {
      return region;
   }

   /**
    * {@inheritDoc}
    */
   public int compareTo(PublicIpInstanceIdPair o) {
      return (this == o) ? 0 : getPublicIp().compareTo(o.getPublicIp());
   }

   /**
    * The ID of the instance.
    */
   public String getInstanceId() {
      return instanceId;
   }

   /**
    * The public IP address.
    */
   public String getPublicIp() {
      return publicIp;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((publicIp == null) ? 0 : publicIp.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
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
      PublicIpInstanceIdPair other = (PublicIpInstanceIdPair) obj;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (publicIp == null) {
         if (other.publicIp != null)
            return false;
      } else if (!publicIp.equals(other.publicIp))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      return true;
   }

}
