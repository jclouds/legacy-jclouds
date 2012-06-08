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

import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects.ToStringHelper;

/**
 * Server Resource with administrative password returned by ServerClient#CreateServer calls
 * 
 * @author Adam Lowe
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Get_Server_Details-d1e2623.html"
 *      />
 */
public class ServerCreated extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromServerCreated(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      private String adminPass;

      /**
       * @see ServerCreated#getAdminPass()
       */
      public T adminPass(String adminPass) {
         this.adminPass = adminPass;
         return self();
      }

      public T fromServerCreated(ServerCreated in) {
         return super.fromResource(in).adminPass(in.getAdminPass());
      }

      public ServerCreated build() {
         return new ServerCreated(this);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected ServerCreated() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   private String adminPass;

   protected ServerCreated(Builder<?> builder) {
      super(builder);
      this.adminPass = builder.adminPass;
   }

   /**
    * @return the administrative password for this server. Note: this is not available in Server responses.
    */
   public String getAdminPass() {
      return adminPass;
   }

   // hashCode/equals from super is ok

   @Override
   protected ToStringHelper string() {
      return super.string().add("adminPass", adminPass);
   }
}
