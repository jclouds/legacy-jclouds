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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * A shared IP group is a collection of servers that can share IPs with other members of the group.
 * Any server in a group can share one or more public IPs with any other server in the group. With
 * the exception of the first server in a shared IP group, servers must be launched into shared IP
 * groups. A server may only be a member of one shared IP group.
 * 
 * @author Adrian Cole
*/
public class SharedIpGroup {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromSharedIpGroup(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected List<Integer> servers = null;
   
      /** 
       * @see SharedIpGroup#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /** 
       * @see SharedIpGroup#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see SharedIpGroup#getServers()
       */
      public T servers(List<Integer> servers) {
         this.servers = ImmutableList.copyOf(checkNotNull(servers, "servers"));     
         return self();
      }

      public T servers(Integer... in) {
         return servers(ImmutableList.copyOf(in));
      }

      public SharedIpGroup build() {
         return new SharedIpGroup(id, name, servers);
      }
      
      public T fromSharedIpGroup(SharedIpGroup in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .servers(in.getServers());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final List<Integer> servers;

   @ConstructorProperties({
      "id", "name", "servers"
   })
   protected SharedIpGroup(int id, String name, @Nullable List<Integer> servers) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.servers = servers == null ? null : ImmutableList.copyOf(servers);     
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public List<Integer> getServers() {
      return this.servers;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, servers);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SharedIpGroup that = SharedIpGroup.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.servers, that.servers);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("servers", servers);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
