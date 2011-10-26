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

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-UserIdGroupPairType.html"
 *      />
 * @author Adrian Cole
 */
public class UserIdGroupPair implements Comparable<UserIdGroupPair> {
   private final String userId;
   private final String groupName;

   public UserIdGroupPair(String userId, String groupName) {
      this.userId = checkNotNull(userId,"userId");
      this.groupName = checkNotNull(groupName,"groupName");
   }


   @Override
   public String toString() {
      return "[userId=" + userId + ", groupName=" + groupName + "]";
   }


   /**
    * {@inheritDoc}
    */
   public int compareTo(UserIdGroupPair o) {
      return (this == o) ? 0 : getUserId().compareTo(o.getUserId());
   }


   /**
    * AWS User ID of an identity. Cannot be used when specifying a CIDR IP address.
    */
   public String getUserId() {
      return userId;
   }


   /**
    * Name of the security group. Cannot be used when specifying a CIDR IP address.
    */
   public String getGroupName() {
      return groupName;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
      UserIdGroupPair other = (UserIdGroupPair) obj;
      if (groupName == null) {
         if (other.groupName != null)
            return false;
      } else if (!groupName.equals(other.groupName))
         return false;
      if (userId == null) {
         if (other.userId != null)
            return false;
      } else if (!userId.equals(other.userId))
         return false;
      return true;
   }

}
