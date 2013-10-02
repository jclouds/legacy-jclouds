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

import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.net.domain.IpPermission;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Andrew Bayer
 */
public class SecurityGroupBuilder extends ComputeMetadataBuilder {
   private ImmutableSet.Builder<IpPermission> ipPermissions = ImmutableSet.<IpPermission> builder();
   private String ownerId;
   
   public SecurityGroupBuilder() {
      super(ComputeType.SECURITYGROUP);
   }

   public SecurityGroupBuilder ipPermissions() {
      this.ipPermissions = ImmutableSet.<IpPermission> builder();
      return this;
   }

   public SecurityGroupBuilder ipPermissions(Iterable<IpPermission> ipPermissions) {
      this.ipPermissions.addAll(checkNotNull(ipPermissions, "ipPermissions"));
      return this;
   }

   public SecurityGroupBuilder ipPermission(IpPermission ipPermission) {
      this.ipPermissions.add(checkNotNull(ipPermission, "ipPermission"));
      return this;
   }

   public SecurityGroupBuilder ownerId(String ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   @Override
   public SecurityGroupBuilder id(String id) {
      return SecurityGroupBuilder.class.cast(super.id(id));
   }

   @Override
   public SecurityGroupBuilder tags(Iterable<String> tags) {
      return SecurityGroupBuilder.class.cast(super.tags(tags));
   }

   @Override
   public SecurityGroupBuilder ids(String id) {
      return SecurityGroupBuilder.class.cast(super.ids(id));
   }

   @Override
   public SecurityGroupBuilder providerId(String providerId) {
      return SecurityGroupBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public SecurityGroupBuilder name(String name) {
      return SecurityGroupBuilder.class.cast(super.name(name));
   }

   @Override
   public SecurityGroupBuilder location(Location location) {
      return SecurityGroupBuilder.class.cast(super.location(location));
   }

   @Override
   public SecurityGroupBuilder uri(URI uri) {
      return SecurityGroupBuilder.class.cast(super.uri(uri));
   }

   @Override
   public SecurityGroupBuilder userMetadata(Map<String, String> userMetadata) {
      return SecurityGroupBuilder.class.cast(super.userMetadata(userMetadata));
   }

   @Override
   public SecurityGroup build() {
      return new SecurityGroup(providerId, name, id, location, uri, userMetadata, tags,
                               ipPermissions.build(), ownerId);
   }

   public static SecurityGroupBuilder fromSecurityGroup(SecurityGroup group) {
      return new SecurityGroupBuilder().providerId(group.getProviderId())
         .name(group.getName())
         .id(group.getId())
         .location(group.getLocation())
         .uri(group.getUri())
         .userMetadata(group.getUserMetadata())
         .tags(group.getTags())
         .ipPermissions(group.getIpPermissions())
         .ownerId(group.getOwnerId());
   }

}
