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

import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-LaunchPermissionItemType.html"
 *      />
 * @author Adrian Cole
 */
public class Permission {
   private final Set<String> groups = Sets.newHashSet();
   private final Set<String> userIds = Sets.newHashSet();

   public Permission(Iterable<String> userIds, Iterable<String> groups) {
      Iterables.addAll(this.userIds, checkNotNull(userIds, "userIds"));
      Iterables.addAll(this.groups, checkNotNull(groups, "groups"));
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((groups == null) ? 0 : groups.hashCode());
      result = prime * result + ((userIds == null) ? 0 : userIds.hashCode());
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
      Permission other = (Permission) obj;
      if (groups == null) {
         if (other.groups != null)
            return false;
      } else if (!groups.equals(other.groups))
         return false;
      if (userIds == null) {
         if (other.userIds != null)
            return false;
      } else if (!userIds.equals(other.userIds))
         return false;
      return true;
   }

   /**
    * 
    * @return Name of the group. Currently supports \"all.\"
    */
   public Set<String> getGroups() {
      return groups;
   }

   /**
    * 
    * @return AWS Access Key ID.
    */
   public Set<String> getUserIds() {
      return userIds;
   }

   @Override
   public String toString() {
      return "LaunchPermission [groups=" + groups + ", userIds=" + userIds + "]";
   }

}
