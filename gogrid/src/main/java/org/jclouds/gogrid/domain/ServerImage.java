/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.gogrid.domain;

import java.util.Date;
import java.util.Set;

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

   public ServerImage(long id, String name, String friendlyName, String description, Option os,
            Option architecture, ServerImageType type, ServerImageState state, double price,
            String location, boolean active, boolean aPublic, Date createdTime, Date updatedTime,
            Set<BillingToken> billingTokens, Customer owner) {
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
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      ServerImage that = (ServerImage) o;

      if (id != that.id)
         return false;
      if (isActive != that.isActive)
         return false;
      if (isPublic != that.isPublic)
         return false;
      if (Double.compare(that.price, price) != 0)
         return false;
      if (architecture != null ? !architecture.equals(that.architecture)
               : that.architecture != null)
         return false;
      if (billingTokens != null ? !billingTokens.equals(that.billingTokens)
               : that.billingTokens != null)
         return false;
      if (!createdTime.equals(that.createdTime))
         return false;
      if (description != null ? !description.equals(that.description) : that.description != null)
         return false;
      if (friendlyName != null ? !friendlyName.equals(that.friendlyName)
               : that.friendlyName != null)
         return false;
      if (location != null ? !location.equals(that.location) : that.location != null)
         return false;
      if (!name.equals(that.name))
         return false;
      if (!os.equals(that.os))
         return false;
      if (owner != null ? !owner.equals(that.owner) : that.owner != null)
         return false;
      if (!state.equals(that.state))
         return false;
      if (!type.equals(that.type))
         return false;
      if (updatedTime != null ? !updatedTime.equals(that.updatedTime) : that.updatedTime != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = (int) (id ^ (id >>> 32));
      result = 31 * result + name.hashCode();
      result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + os.hashCode();
      result = 31 * result + type.hashCode();
      result = 31 * result + state.hashCode();
      temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (location != null ? location.hashCode() : 0);
      result = 31 * result + (isActive ? 1 : 0);
      result = 31 * result + (isPublic ? 1 : 0);
      result = 31 * result + createdTime.hashCode();
      result = 31 * result + (updatedTime != null ? updatedTime.hashCode() : 0);
      result = 31 * result + (billingTokens != null ? billingTokens.hashCode() : 0);
      result = 31 * result + (owner != null ? owner.hashCode() : 0);
      return result;
   }

   @Override
   public int compareTo(ServerImage o) {
      return Longs.compare(id, o.getId());
   }

   @Override
   public String toString() {
      return "ServerImage{" + "id=" + id + ", name='" + name + '\'' + ", friendlyName='"
               + friendlyName + '\'' + ", description='" + description + '\'' + ", os=" + os
               + ", architecture=" + architecture + ", type=" + type + ", state=" + state
               + ", price=" + price + ", location='" + location + '\'' + ", isActive=" + isActive
               + ", isPublic=" + isPublic + ", createdTime=" + createdTime + ", updatedTime="
               + updatedTime + ", billingTokens=" + billingTokens + ", owner=" + owner + '}';
   }
}
