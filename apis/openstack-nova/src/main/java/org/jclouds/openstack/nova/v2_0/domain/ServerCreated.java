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

import java.beans.ConstructorProperties;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * Server Resource with administrative password returned by ServerApi#CreateServer calls
 * 
 * @author Adam Lowe
 * @see <a href=
      "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
      />
*/
public class ServerCreated extends Resource {

   public static Builder builder() { 
      return new Builder();
   }
   
   public Builder toBuilder() { 
      return builder().fromServerCreated(this);
   }

   public final static class Builder extends Resource.Builder<Builder>  {
      protected String adminPass;
   
      /** 
       * @see ServerCreated#getAdminPass()
       */
      public Builder adminPass(String adminPass) {
         this.adminPass = adminPass;
         return self();
      }

      public ServerCreated build() {
         return new ServerCreated(id, name, links, adminPass);
      }
      
      public Builder fromServerCreated(ServerCreated in) {
         return super.fromResource(in).adminPass(in.getAdminPass().orNull());
      }

      @Override
      protected Builder self() {
         return this;
      }
   }

   private final Optional<String> adminPass;

   @ConstructorProperties({
      "id", "name", "links", "adminPass"
   })
   protected ServerCreated(String id, @Nullable String name, Set<Link> links, @Nullable String adminPass) {
      super(id, name, links);
      this.adminPass = Optional.fromNullable(adminPass);
   }

   /**
    * present unless the nova install was configured with the option {@code enable_instance_password=false}
    */
   public Optional<String> getAdminPass() {
      return this.adminPass;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(adminPass);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerCreated that = ServerCreated.class.cast(obj);
      return super.equals(that) && Objects.equal(this.adminPass, that.adminPass);
   }

   @Override
   protected ToStringHelper string() {
      return super.string().add("adminPass", adminPass.orNull());
   }
}
