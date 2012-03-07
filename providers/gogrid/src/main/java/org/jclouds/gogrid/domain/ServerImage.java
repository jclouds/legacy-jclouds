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
package org.jclouds.gogrid.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.util.Date;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.primitives.Longs;
import com.google.gson.annotations.SerializedName;

/**
 * @author Oleksiy Yarmula
 */
public class ServerImage implements Comparable<ServerImage> {

   private long id;
   private String name;
   private String friendlyName;
   private String description;
   private Option os;
   private Option architecture;
   private ServerImageType type;
   private ServerImageState state;
   private double price;
   private String location;
   private boolean isActive;
   private boolean isPublic;
   private Date createdTime;
   private Date updatedTime;
   @SerializedName("billingtokens")
   private Set<BillingToken> billingTokens;
   private Customer owner;

   /**
    * A no-args constructor is required for deserialization
    */
   public ServerImage() {
   }

   public ServerImage(long id, String name, String friendlyName, String description, Option os, Option architecture,
            ServerImageType type, ServerImageState state, double price, String location, boolean active,
            boolean aPublic, Date createdTime, Date updatedTime, Set<BillingToken> billingTokens, Customer owner) {
      this.id = id;
      this.name = name;
      this.friendlyName = friendlyName;
      this.description = description;
      this.os = os;
      this.architecture = architecture;
      this.type = type;
      this.state = state;
      this.price = price;
      this.location = location;
      isActive = active;
      isPublic = aPublic;
      this.createdTime = createdTime;
      this.updatedTime = updatedTime;
      this.billingTokens = billingTokens;
      this.owner = owner;
   }

   public long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getFriendlyName() {
      return friendlyName;
   }

   public String getDescription() {
      if (description == null)
         return "";
      return description;
   }

   public Option getOs() {
      return os;
   }

   public Option getArchitecture() {
      return architecture;
   }

   public ServerImageType getType() {
      return type;
   }

   public ServerImageState getState() {
      return state;
   }

   public double getPrice() {
      return price;
   }

   public String getLocation() {
      return location;
   }

   public boolean isActive() {
      return isActive;
   }

   public boolean isPublic() {
      return isPublic;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public Date getUpdatedTime() {
      return updatedTime;
   }

   public Set<BillingToken> getBillingTokens() {
      return billingTokens;
   }

   public Customer getOwner() {
      return owner;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof ServerImage) {
         final ServerImage other = ServerImage.class.cast(object);
         return equal(id, other.id) && equal(name, other.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public int compareTo(ServerImage that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return Longs.compare(id, that.getId());
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("friendlyName", friendlyName).add("friendlyName",
               friendlyName).add("description", description).add("os", os).add("architecture", architecture).add(
               "type", type).add("state", state).add("price", price).add("location", location)
               .add("isActive", isActive).add("isPublic", isPublic).add("createdTime", createdTime).add("updatedTime",
                        updatedTime).add("billingTokens", billingTokens).add("owner", owner).toString();
   }
}
