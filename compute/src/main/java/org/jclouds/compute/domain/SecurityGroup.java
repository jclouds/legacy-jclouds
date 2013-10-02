/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.internal.ComputeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;

/**
 * Describes a security group containing a set of @{link IpPermission}s
 * 
 * @author Andrew Bayer
 */
public class SecurityGroup extends ComputeMetadataImpl {

   private final Set<IpPermission> ipPermissions;
   private final String ownerId;
   
   public SecurityGroup(String providerId, String name, String id, @Nullable Location location, URI uri,
                        Map<String, String> userMetadata, Set<String> tags,
                        Iterable<IpPermission> ipPermissions,
                        @Nullable String ownerId) { 
      super(ComputeType.SECURITYGROUP, providerId, name, id, location, uri, userMetadata, tags);
      this.ipPermissions = ImmutableSet.copyOf(checkNotNull(ipPermissions, "ipPermissions"));
      this.ownerId = ownerId;
   }

   /**
    * 
    * @return The set of @{link IpPermission}s for this security group
    */
   public Set<IpPermission> getIpPermissions() {
      return ipPermissions;
   }

   /**
    *
    * @return the owner ID. Can be null.
    */
   public String getOwnerId() {
      return ownerId;
   }
   
   @Override
   protected ToStringHelper string() {
      ToStringHelper helper = computeToStringPrefix();
      if (ipPermissions.size() > 0)
         helper.add("ipPermissions", ipPermissions);
      return addComputeToStringSuffix(helper);
   }

}
