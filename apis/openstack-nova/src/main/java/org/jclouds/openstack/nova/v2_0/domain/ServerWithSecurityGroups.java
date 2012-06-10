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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

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
      private Set<String> securityGroupNames = ImmutableSet.of();

      /**
       * @see ServerWithSecurityGroups#getSecurityGroupNames()
       */
      public T securityGroupNames(Set<String> securityGroupNames) {
         this.securityGroupNames = securityGroupNames;
         return self();
      }

      public ServerWithSecurityGroups build() {
         return new ServerWithSecurityGroups(this);
      }

      public T fromServerWithSecurityGroups(ServerWithSecurityGroups in) {
         return super.fromServer(in).securityGroupNames(in.getSecurityGroupNames());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected ServerWithSecurityGroups() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   @SerializedName(value="security_groups")
   private Set<String> securityGroupNames = ImmutableSet.of();

   protected ServerWithSecurityGroups(Builder<?> builder) {
      super(builder);
      this.securityGroupNames = ImmutableSet.copyOf(checkNotNull(builder.securityGroupNames, "securityGroupNames"));
   }

   /**
    */
   public Set<String> getSecurityGroupNames() {
      return Collections.unmodifiableSet(this.securityGroupNames);
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