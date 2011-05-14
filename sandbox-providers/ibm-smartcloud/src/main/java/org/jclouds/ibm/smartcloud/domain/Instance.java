/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibm.smartcloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * The current state of the instance.
 * 
 * @author Adrian Cole
 */
public class Instance implements Comparable<Instance> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Date launchTime;
      private Set<Software> software = Sets.newLinkedHashSet();
      private IP primaryIP;
      private Set<IP> secondaryIPs = Sets.newLinkedHashSet();
      private String requestId;
      private String keyName;
      private String name;
      private String instanceType;
      private Status status;
      private String owner;
      private String location;
      private String imageId;
      private Set<String> productCodes = Sets.newLinkedHashSet();
      private String requestName;
      private String id;
      private Date expirationTime;
      private Vlan vlan;
      private int diskSize;

      private boolean rootOnly;
      private String antiCollocationInstance;

      public Builder launchTime(Date launchTime) {
         this.launchTime = launchTime;
         return this;
      }

      public Builder software(Iterable<Software> software) {
         this.software = ImmutableSet.<Software> copyOf(checkNotNull(software, "software"));
         return this;
      }

      public Builder primaryIP(IP primaryIP) {
         this.primaryIP = primaryIP;
         return this;
      }

      public Builder secondaryIPs(Iterable<IP> secondaryIPs) {
         this.secondaryIPs = ImmutableSet.<IP> copyOf(checkNotNull(secondaryIPs, "secondaryIPs"));
         return this;
      }

      public Builder requestId(String requestId) {
         this.requestId = requestId;
         return this;
      }

      public Builder keyName(String keyName) {
         this.keyName = keyName;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder instanceType(String instanceType) {
         this.instanceType = instanceType;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder owner(String owner) {
         this.owner = owner;
         return this;
      }

      public Builder location(String location) {
         this.location = location;
         return this;
      }

      public Builder imageId(String imageId) {
         this.imageId = imageId;
         return this;
      }

      public Builder productCodes(Iterable<String> productCodes) {
         this.productCodes = ImmutableSet.<String> copyOf(checkNotNull(productCodes, "productCodes"));
         return this;
      }

      public Builder requestName(String requestName) {
         this.requestName = requestName;
         return this;
      }

      public Builder id(String id) {
         this.id = id;
         return this;
      }

      public Builder expirationTime(Date expirationTime) {
         this.expirationTime = expirationTime;
         return this;
      }

      public Builder vlan(Vlan vlan) {
         this.vlan = vlan;
         return this;
      }

      public Builder diskSize(int diskSize) {
         this.diskSize = diskSize;
         return this;
      }

      public Builder rootOnly(boolean rootOnly) {
         this.rootOnly = rootOnly;
         return this;
      }

      public Builder antiCollocationInstance(String antiCollocationInstance) {
         this.antiCollocationInstance = antiCollocationInstance;
         return this;
      }

      public Instance build() {
         return new Instance(launchTime, software, primaryIP, secondaryIPs, requestId, keyName, name, instanceType,
                  status, owner, location, imageId, productCodes, requestName, id, expirationTime, vlan, diskSize,
                  rootOnly, antiCollocationInstance);
      }
   }

   public static enum Status {
      NEW, PROVISIONING, FAILED, REMOVED, REJECTED, ACTIVE, UNKNOWN, DEPROVISIONING, RESTARTING, STARTING, STOPPING, STOPPED, DEPROVISION_PENDING, UNRECOGNIZED;

      public static Status fromValue(String v) {
         switch (Integer.parseInt(v)) {
            case 0:
               return NEW;
            case 1:
               return PROVISIONING;
            case 2:
               return FAILED;
            case 3:
               return REMOVED;
            case 4:
               return REJECTED;
            case 5:
               return ACTIVE;
            case 6:
               return UNKNOWN;
            case 7:
               return DEPROVISIONING;
            case 8:
               return RESTARTING;
            case 9:
               return STARTING;
            case 10:
               return STOPPING;
            case 11:
               return STOPPED;
            case 12:
               return DEPROVISION_PENDING;
            default:
               return UNRECOGNIZED;
         }
      }
   }

   public static class Software {
      private String version;
      private String type;
      private String name;

      public Software(String name, String type, String version) {
         this.version = version;
         this.type = type;
         this.name = name;
      }

      public Software() {

      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((type == null) ? 0 : type.hashCode());
         result = prime * result + ((version == null) ? 0 : version.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Software other = (Software) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         if (type == null) {
            if (other.type != null)
               return false;
         } else if (!type.equals(other.type))
            return false;
         if (version == null) {
            if (other.version != null)
               return false;
         } else if (!version.equals(other.version))
            return false;
         return true;
      }

      public String getVersion() {
         return version;
      }

      public String getType() {
         return type;
      }

      public String getName() {
         return name;
      }

      @Override
      public String toString() {
         return "[name=" + name + ", type=" + type + ", version=" + version + "]";
      }
   }

   private Date launchTime;
   private Set<Software> software = Sets.newLinkedHashSet();
   private IP primaryIP;
   @SerializedName("secondaryIP")
   private Set<IP> secondaryIPs = Sets.newLinkedHashSet();
   private String requestId;
   private String keyName;
   private String name;
   private String instanceType;
   private Status status;
   private String owner;
   private String location;
   private String imageId;
   private Set<String> productCodes = Sets.newLinkedHashSet();
   private String requestName;
   private String id;
   private Date expirationTime;
   private Vlan vlan;
   private int diskSize;
   @SerializedName("root-only")
   private boolean rootOnly;
   private String antiCollocationInstance;

   Instance() {
   }

   public Instance(Date launchTime, Iterable<Software> software, IP primaryIP, Iterable<IP> secondaryIPs,
            String requestId, String keyName, String name, String instanceType, Status status, String owner,
            String location, String imageId, Iterable<String> productCodes, String requestName, String id,
            Date expirationTime, Vlan vlan, int diskSize, boolean rootOnly, String antiCollocationInstance) {
      this.launchTime = launchTime;
      this.software = ImmutableSet.copyOf(software);
      this.primaryIP = primaryIP;
      this.secondaryIPs = ImmutableSet.copyOf(secondaryIPs);
      this.requestId = requestId;
      this.keyName = keyName;
      this.name = name;
      this.instanceType = instanceType;
      this.status = status;
      this.owner = owner;
      this.location = location;
      this.imageId = imageId;
      this.productCodes = ImmutableSet.copyOf(productCodes);
      this.requestName = requestName;
      this.id = id;
      this.expirationTime = expirationTime;
      this.vlan = vlan;
      this.diskSize = diskSize;
      this.rootOnly = rootOnly;
      this.antiCollocationInstance = antiCollocationInstance;
   }

   public Date getLaunchTime() {
      return launchTime;
   }

   public Set<Software> getSoftware() {
      return software;
   }

   public IP getPrimaryIP() {
      return primaryIP;
   }

   public Set<IP> getSecondaryIPs() {
      return secondaryIPs;
   }

   public String getRequestId() {
      return requestId;
   }

   public String getKeyName() {
      return keyName;
   }

   public String getName() {
      return name;
   }

   public String getInstanceType() {
      return instanceType;
   }

   public Status getStatus() {
      return status;
   }

   public String getOwner() {
      return owner;
   }

   public String getLocation() {
      return location;
   }

   public String getImageId() {
      return imageId;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public String getRequestName() {
      return requestName;
   }

   public String getId() {
      return id;
   }

   public Date getExpirationTime() {
      return expirationTime;
   }

   public Vlan getVlan() {
      return vlan;
   }

   public int getDiskSize() {
      return diskSize;
   }

   public boolean isRootOnly() {
      return rootOnly;
   }

   public String getAntiCollocationInstance() {
      return antiCollocationInstance;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Instance other = (Instance) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return String
               .format(
                        "[id=%s, instanceType=%s, owner=%s, name=%s, location=%s, status=%s, imageId=%s, primaryIP=%s, secondaryIPs=%s, diskSize=%s, keyName=%s, launchTime=%s, rootOnly=%s, vlan=%s, software=%s, expirationTime=%s, antiCollocationInstance=%s, requestId=%s, requestName=%s, productCodes=%s]",
                        id, instanceType, owner, name, location, status, imageId, primaryIP, secondaryIPs, diskSize,
                        keyName, launchTime, rootOnly, vlan, software, expirationTime, antiCollocationInstance,
                        requestId, requestName, productCodes);
   }

   @Override
   public int compareTo(Instance arg0) {
      return id.compareTo(arg0.getId());
   }
}
