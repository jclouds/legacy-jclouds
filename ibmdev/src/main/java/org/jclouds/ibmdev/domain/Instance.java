/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the ;License;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an ;AS IS; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.domain;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 
 * The current state of the instance.
 * 
 * @author Adrian Cole
 */
public class Instance {
   public static enum Status {
      NEW, PROVISIONING, FAILED, REMOVED, REJECTED, ACTIVE, UNKNOWN, DEPROVISIONING, RESTARTING, STARTING, STOPPING, STOPPED;
      public static Status fromValue(int v) {
         switch (v) {
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
            default:
               throw new IllegalArgumentException("invalid state:" + v);
         }
      }
   }

   public static class Software {
      private String version;
      private String type;
      private String name;

      public Software(String version, String type, String name) {
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

      public void setVersion(String version) {
         this.version = version;
      }

      public String getType() {
         return type;
      }

      public void setType(String type) {
         this.type = type;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return "Software [name=" + name + ", type=" + type + ", version=" + version + "]";
      }
   }

   private Date launchTime;
   private Set<Software> software = Sets.newLinkedHashSet();
   private String ip;
   private String requestId;
   private String keyName;
   private String name;
   private String instanceType;
   private int status;
   private String owner;
   private String hostname;
   private String location;
   private String imageId;
   private Set<String> productCodes;
   private String requestName;
   private String id;
   private Date expirationTime;

   public Instance(Date launchTime, Set<Software> software, String ip, String requestId,
            String keyName, String name, String instanceType, int status, String owner,
            String hostname, String location, String imageId, Set<String> productCodes,
            String requestName, String id, Date expirationTime) {
      this.launchTime = launchTime;
      this.software = software;
      this.ip = ip;
      this.requestId = requestId;
      this.keyName = keyName;
      this.name = name;
      this.instanceType = instanceType;
      this.status = status;
      this.owner = owner;
      this.hostname = hostname;
      this.location = location;
      this.imageId = imageId;
      this.productCodes = productCodes;
      this.requestName = requestName;
      this.id = id;
      this.expirationTime = expirationTime;
   }

   public Instance() {
   }

   public Date getLaunchTime() {
      return launchTime;
   }

   public void setLaunchTime(Date launchTime) {
      this.launchTime = launchTime;
   }

   public Set<Software> getSoftware() {
      return software;
   }

   public void setSoftware(Set<Software> software) {
      this.software = software;
   }

   public String getIp() {
      return ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public String getRequestId() {
      return requestId;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }

   public String getKeyName() {
      return keyName;
   }

   public void setKeyName(String keyName) {
      this.keyName = keyName;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getInstanceType() {
      return instanceType;
   }

   public void setInstanceType(String instanceType) {
      this.instanceType = instanceType;
   }

   public Status getStatus() {
      return Status.fromValue(status);
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public String getOwner() {
      return owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getHostname() {
      return hostname;
   }

   public void setHostname(String hostname) {
      this.hostname = hostname;
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getImageId() {
      return imageId;
   }

   public void setImageId(String imageId) {
      this.imageId = imageId;
   }

   public Set<String> getProductCodes() {
      return productCodes;
   }

   public void setProductCodes(Set<String> productCodes) {
      this.productCodes = productCodes;
   }

   public String getRequestName() {
      return requestName;
   }

   public void setRequestName(String requestName) {
      this.requestName = requestName;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Date getExpirationTime() {
      return expirationTime;
   }

   public void setExpirationTime(Date expirationTime) {
      this.expirationTime = expirationTime;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((ip == null) ? 0 : ip.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((launchTime == null) ? 0 : launchTime.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((owner == null) ? 0 : owner.hashCode());
      result = prime * result + ((productCodes == null) ? 0 : productCodes.hashCode());
      result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
      result = prime * result + ((requestName == null) ? 0 : requestName.hashCode());
      result = prime * result + ((software == null) ? 0 : software.hashCode());
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
      if (hostname == null) {
         if (other.hostname != null)
            return false;
      } else if (!hostname.equals(other.hostname))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      if (ip == null) {
         if (other.ip != null)
            return false;
      } else if (!ip.equals(other.ip))
         return false;
      if (keyName == null) {
         if (other.keyName != null)
            return false;
      } else if (!keyName.equals(other.keyName))
         return false;
      if (launchTime == null) {
         if (other.launchTime != null)
            return false;
      } else if (!launchTime.equals(other.launchTime))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (owner == null) {
         if (other.owner != null)
            return false;
      } else if (!owner.equals(other.owner))
         return false;
      if (productCodes == null) {
         if (other.productCodes != null)
            return false;
      } else if (!productCodes.equals(other.productCodes))
         return false;
      if (requestId == null) {
         if (other.requestId != null)
            return false;
      } else if (!requestId.equals(other.requestId))
         return false;
      if (requestName == null) {
         if (other.requestName != null)
            return false;
      } else if (!requestName.equals(other.requestName))
         return false;
      if (software == null) {
         if (other.software != null)
            return false;
      } else if (!software.equals(other.software))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "Instance [id=" + id + ", name=" + name + ", ip=" + ip + ", hostname=" + hostname
               + ", status=" + getStatus() + ", instanceType=" + instanceType + ", location="
               + location + ", imageId=" + imageId + ", software=" + software + ", keyName="
               + keyName + ", launchTime=" + launchTime + ", expirationTime=" + expirationTime
               + ", owner=" + owner + ", productCodes=" + productCodes + ", requestId=" + requestId
               + ", requestName=" + requestName + "]";
   }

}