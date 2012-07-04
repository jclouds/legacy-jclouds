/*
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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Extended server returned by ServerWithSecurityGroupsClient
 * 
 * @author Adam Lowe
 * @see <a href=
"http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
/>
*/
public class ServerWithSecurityGroups extends Server {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromServerWithSecurityGroups(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Server.Builder<T>  {
      protected Set<String> securityGroupNames = ImmutableSet.of();
   
      /** 
       * @see ServerWithSecurityGroups#getSecurityGroupNames()
       */
      public T securityGroupNames(Set<String> securityGroupNames) {
         this.securityGroupNames = ImmutableSet.copyOf(checkNotNull(securityGroupNames, "securityGroupNames"));      
         return self();
      }

      public T securityGroupNames(String... in) {
         return securityGroupNames(ImmutableSet.copyOf(in));
      }

      public ServerWithSecurityGroups build() {
         return new ServerWithSecurityGroups(id, name, links, uuid, tenantId, userId, updated, created, hostId,
               accessIPv4, accessIPv6, status, image, flavor, keyName, configDrive, addresses,
               metadata, extendedStatus, extendedAttributes, diskConfig, securityGroupNames);
      }
      
      public T fromServerWithSecurityGroups(ServerWithSecurityGroups in) {
         return super.fromServer(in)
                  .securityGroupNames(in.getSecurityGroupNames());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   @Named("security_groups")
   private final Set<String> securityGroupNames;

   @ConstructorProperties({
      "id", "name", "links", "uuid", "tenant_id", "user_id", "updated", "created", "hostId", "accessIPv4", "accessIPv6", "status", "image", "flavor", "key_name", "config_drive", "addresses", "metadata", "extendedStatus", "extendedAttributes", "OS-DCF:diskConfig", "security_groups"
   })
   protected ServerWithSecurityGroups(String id, @Nullable String name, Set<Link> links, @Nullable String uuid,
                                      String tenantId, String userId, Date updated, Date created, @Nullable String hostId,
                                      @Nullable String accessIPv4, @Nullable String accessIPv6, Server.Status status, Resource image,
                                      Resource flavor, @Nullable String keyName, @Nullable String configDrive,
                                      Multimap<String, Address> addresses, Map<String, String> metadata, 
                                      @Nullable ServerExtendedStatus extendedStatus, @Nullable ServerExtendedAttributes extendedAttributes,
                                      @Nullable String diskConfig, Set<String> securityGroupNames) {
      super(id, name, links, uuid, tenantId, userId, updated, created, hostId, accessIPv4, accessIPv6, status, image, flavor, keyName, configDrive, addresses, metadata, extendedStatus, extendedAttributes, diskConfig);
      this.securityGroupNames = ImmutableSet.copyOf(checkNotNull(securityGroupNames, "securityGroupNames"));      
   }

   public Set<String> getSecurityGroupNames() {
      return this.securityGroupNames;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(securityGroupNames);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerWithSecurityGroups that = ServerWithSecurityGroups.class.cast(obj);
      return super.equals(that) && Objects.equal(this.securityGroupNames, that.securityGroupNames);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("securityGroupNames", securityGroupNames);
   }
   
}
