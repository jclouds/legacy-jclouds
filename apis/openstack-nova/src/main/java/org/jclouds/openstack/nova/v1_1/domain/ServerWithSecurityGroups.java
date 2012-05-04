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
package org.jclouds.openstack.nova.v1_1.domain;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * Extended server returned by ServerWithSecurityGroupsClient
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v1_1.extensions.ServerWithSecurityGroupsClient
 * @see <a href=
 *           "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
 *      />
 */
public class ServerWithSecurityGroups extends Server {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromServerWithSecurityGroups(this);
   }

   public static class Builder extends Server.Builder  {
      private Set<String> securityGroupNames = Sets.newLinkedHashSet();

      /**
       * @see ServerWithSecurityGroups#getSecurityGroupNames()
       */
      public Builder securityGroupNames(Set<String> securityGroupNames) {
         this.securityGroupNames = securityGroupNames;
         return this;
      }

      public Builder fromServerWithSecurityGroups(ServerWithSecurityGroups in) {
         return fromServer(in).securityGroupNames(in.getSecurityGroupNames());
      }

      @Override
      public ServerWithSecurityGroups build() {
         return new ServerWithSecurityGroups(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6,
               status, configDrive, image, flavor, adminPass, keyName, addresses, metadata, securityGroupNames);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromServer(Server in) {
         return Builder.class.cast(super.fromServer(in));
      }
      
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Link... links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder tenantId(String tenantId) {
         return Builder.class.cast(super.tenantId(tenantId));
      }
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder userId(String userId) {
         return Builder.class.cast(super.userId(userId));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder updated(Date updated) {
         return Builder.class.cast(super.updated(updated));
      }
   
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder created(Date created) {
         return Builder.class.cast(super.created(created));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder hostId(String hostId) {
         return Builder.class.cast(super.hostId(hostId));
      }
   
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder accessIPv4(String accessIPv4) {
         return Builder.class.cast(super.accessIPv4(accessIPv4));
      }
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder accessIPv6(String accessIPv6) {
         return Builder.class.cast(super.accessIPv6(accessIPv6));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder status(Status status) {
         return Builder.class.cast(super.status(status));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder configDrive(String configDrive) {
         return Builder.class.cast(super.configDrive(configDrive));
      } 
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder image(Resource image) {
         return Builder.class.cast(super.image(image));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder flavor(Resource flavor) {
         return Builder.class.cast(super.flavor(flavor));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder adminPass(String adminPass) {
         return Builder.class.cast(super.adminPass(adminPass));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder keyName(String keyName) {
         return Builder.class.cast(super.keyName(keyName));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder addresses(Multimap<String, Address> addresses) {
         return Builder.class.cast(super.addresses(addresses));
      }
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder metadata(Map<String, String> metadata) {
         return Builder.class.cast(super.metadata(metadata));
      }
   }

   @SerializedName("security_groups")
   private final Set<String> securityGroupNames;

   public ServerWithSecurityGroups(String id, String name, Set<Link> links, @Nullable String uuid, String tenantId, 
                                   String userId, Date updated, Date created, @Nullable String hostId, 
                                   @Nullable String accessIPv4, @Nullable String accessIPv6, Status status, 
                                   @Nullable String configDrive, Resource image, Resource flavor, String adminPass, 
                                   @Nullable String keyName, Multimap<String, Address> addresses, 
                                   Map<String, String> metadata, Set<String> securityGroupNames) {
      super(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status, configDrive, image, flavor, adminPass, keyName, addresses, metadata);
      this.securityGroupNames = ImmutableSet.copyOf(securityGroupNames);
   }

   public Set<String> getSecurityGroupNames() {
      return Collections.unmodifiableSet(securityGroupNames);
   }

   protected ToStringHelper string() {
      return super.string().add("securityGroupNames", securityGroupNames);
   }

}