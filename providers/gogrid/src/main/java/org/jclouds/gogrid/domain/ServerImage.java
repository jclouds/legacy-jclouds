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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Longs;

/**
 * Class ServerImage
 * 
 * @author Oleksiy Yarmula
*/
public class ServerImage implements Comparable<ServerImage> {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromServerImage(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected long id;
      protected String name;
      protected String friendlyName;
      protected String description;
      protected Option os;
      protected Option architecture;
      protected ServerImageType type;
      protected ServerImageState state;
      protected double price;
      protected String location;
      protected boolean isActive;
      protected boolean isPublic;
      protected Date createdTime;
      protected Date updatedTime;
      protected Set<BillingToken> billingTokens = ImmutableSet.of();
      protected Customer owner;
   
      /** 
       * @see ServerImage#getId()
       */
      public T id(long id) {
         this.id = id;
         return self();
      }

      /** 
       * @see ServerImage#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see ServerImage#getFriendlyName()
       */
      public T friendlyName(String friendlyName) {
         this.friendlyName = friendlyName;
         return self();
      }

      /** 
       * @see ServerImage#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      /** 
       * @see ServerImage#getOs()
       */
      public T os(Option os) {
         this.os = os;
         return self();
      }

      /** 
       * @see ServerImage#getArchitecture()
       */
      public T architecture(Option architecture) {
         this.architecture = architecture;
         return self();
      }

      /** 
       * @see ServerImage#getType()
       */
      public T type(ServerImageType type) {
         this.type = type;
         return self();
      }

      /** 
       * @see ServerImage#getState()
       */
      public T state(ServerImageState state) {
         this.state = state;
         return self();
      }

      /** 
       * @see ServerImage#getPrice()
       */
      public T price(double price) {
         this.price = price;
         return self();
      }

      /** 
       * @see ServerImage#getLocation()
       */
      public T location(String location) {
         this.location = location;
         return self();
      }

      /** 
       * @see ServerImage#isActive()
       */
      public T isActive(boolean isActive) {
         this.isActive = isActive;
         return self();
      }

      /** 
       * @see ServerImage#isPublic()
       */
      public T isPublic(boolean isPublic) {
         this.isPublic = isPublic;
         return self();
      }

      /** 
       * @see ServerImage#getCreatedTime()
       */
      public T createdTime(Date createdTime) {
         this.createdTime = createdTime;
         return self();
      }

      /** 
       * @see ServerImage#getUpdatedTime()
       */
      public T updatedTime(Date updatedTime) {
         this.updatedTime = updatedTime;
         return self();
      }

      /** 
       * @see ServerImage#getBillingTokens()
       */
      public T billingTokens(Set<BillingToken> billingTokens) {
         this.billingTokens = ImmutableSet.copyOf(checkNotNull(billingTokens, "billingTokens"));      
         return self();
      }

      public T billingTokens(BillingToken... in) {
         return billingTokens(ImmutableSet.copyOf(in));
      }

      /** 
       * @see ServerImage#getOwner()
       */
      public T owner(Customer owner) {
         this.owner = owner;
         return self();
      }

      public ServerImage build() {
         return new ServerImage(id, name, friendlyName, description, os, architecture, type, state, price, location, isActive, isPublic, createdTime, updatedTime, billingTokens, owner);
      }
      
      public T fromServerImage(ServerImage in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .friendlyName(in.getFriendlyName())
                  .description(in.getDescription())
                  .os(in.getOs())
                  .architecture(in.getArchitecture())
                  .type(in.getType())
                  .state(in.getState())
                  .price(in.getPrice())
                  .location(in.getLocation())
                  .isActive(in.isActive())
                  .isPublic(in.isPublic())
                  .createdTime(in.getCreatedTime())
                  .updatedTime(in.getUpdatedTime())
                  .billingTokens(in.getBillingTokens())
                  .owner(in.getOwner());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long id;
   private final String name;
   private final String friendlyName;
   private final String description;
   private final Option os;
   private final Option architecture;
   private final ServerImageType type;
   private final ServerImageState state;
   private final double price;
   private final String location;
   private final boolean isActive;
   private final boolean isPublic;
   private final Date createdTime;
   private final Date updatedTime;
   private final Set<BillingToken> billingTokens;
   private final Customer owner;

   @ConstructorProperties({
      "id", "name", "friendlyName", "description", "os", "architecture", "type", "state", "price", "location", "isActive", "isPublic", "createdTime", "updatedTime", "billingtokens", "owner"
   })
   protected ServerImage(long id, String name, String friendlyName, @Nullable String description, Option os, @Nullable Option architecture,
                         ServerImageType type, ServerImageState state, double price, String location, boolean isActive,
                         boolean isPublic, @Nullable Date createdTime, @Nullable Date updatedTime, Set<BillingToken> billingTokens, Customer owner) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.friendlyName = checkNotNull(friendlyName, "friendlyName");
      this.description = Strings.nullToEmpty(description);
      this.os = checkNotNull(os, "os");
      this.architecture = architecture;
      this.type = checkNotNull(type, "type");
      this.state = checkNotNull(state, "state");
      this.price = price;
      this.location = checkNotNull(location, "location");
      this.isActive = isActive;
      this.isPublic = isPublic;
      this.createdTime = createdTime;
      this.updatedTime = updatedTime;
      this.billingTokens = ImmutableSet.copyOf(checkNotNull(billingTokens, "billingTokens"));      
      this.owner = checkNotNull(owner, "owner");
   }

   public long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getFriendlyName() {
      return this.friendlyName;
   }
   
   public String getDescription() {
      return this.description;
   }

   public Option getOs() {
      return this.os;
   }

   @Nullable
   public Option getArchitecture() {
      return this.architecture;
   }

   public ServerImageType getType() {
      return this.type;
   }

   public ServerImageState getState() {
      return this.state;
   }

   public double getPrice() {
      return this.price;
   }

   public String getLocation() {
      return this.location;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   @Nullable
   public Date getCreatedTime() {
      return this.createdTime;
   }

   @Nullable
   public Date getUpdatedTime() {
      return this.updatedTime;
   }

   public Set<BillingToken> getBillingTokens() {
      return this.billingTokens;
   }

   public Customer getOwner() {
      return this.owner;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, friendlyName, description, os, architecture, type, state, price, location, isActive, isPublic, createdTime, updatedTime, billingTokens, owner);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ServerImage that = ServerImage.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.friendlyName, that.friendlyName)
               && Objects.equal(this.description, that.description)
               && Objects.equal(this.os, that.os)
               && Objects.equal(this.architecture, that.architecture)
               && Objects.equal(this.type, that.type)
               && Objects.equal(this.state, that.state)
               && Objects.equal(this.price, that.price)
               && Objects.equal(this.location, that.location)
               && Objects.equal(this.isActive, that.isActive)
               && Objects.equal(this.isPublic, that.isPublic)
               && Objects.equal(this.createdTime, that.createdTime)
               && Objects.equal(this.updatedTime, that.updatedTime)
               && Objects.equal(this.billingTokens, that.billingTokens)
               && Objects.equal(this.owner, that.owner);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("friendlyName", friendlyName).add("description", description).add("os", os).add("architecture", architecture).add("type", type).add("state", state).add("price", price).add("location", location).add("isActive", isActive).add("isPublic", isPublic).add("createdTime", createdTime).add("updatedTime", updatedTime).add("billingTokens", billingTokens).add("owner", owner);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int compareTo(ServerImage that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return Longs.compare(id, that.getId());
   }
}
