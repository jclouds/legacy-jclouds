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
package org.jclouds.hpcloud.objectstorage.domain;

import java.net.URI;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author James Murty
 * 
 */
public class ContainerCDNMetadata implements Comparable<ContainerCDNMetadata> {

   private String name;
   private boolean cdn_enabled;
   private long ttl;
   @SerializedName("x-cdn-uri")
   private URI cdn_uri;
   private String referrer_acl;
   private String useragent_acl;
   private boolean log_retention;

   public ContainerCDNMetadata(String name, boolean cdnEnabled, long ttl, URI cdnUri) {
      this.name = name;
      this.cdn_enabled = cdnEnabled;
      this.ttl = ttl;
      this.cdn_uri = cdnUri;
   }

   public ContainerCDNMetadata() {
   }

   /**
    * Beware: The container name is not available from HEAD CDN responses and will be null. return
    * the name of the container to which these CDN settings apply.
    */
   public String getName() {
      return name;
   }

   public URI getCDNUri() {
      return cdn_uri;
   }

   public long getTTL() {
      return ttl;
   }

   public boolean isCDNEnabled() {
      return cdn_enabled;
   }

   public int compareTo(ContainerCDNMetadata o) {
      if (getName() == null)
         return -1;
      return (this == o) ? 0 : getName().compareTo(o.getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((cdn_uri == null) ? 0 : cdn_uri.hashCode());
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
      ContainerCDNMetadata other = (ContainerCDNMetadata) obj;
      if (cdn_uri == null) {
         if (other.cdn_uri != null)
            return false;
      } else if (!cdn_uri.equals(other.cdn_uri))
         return false;
      return true;
   }

   public String getReferrerACL() {
      return referrer_acl;
   }

   public String getUseragentACL() {
      return useragent_acl;
   }

   public boolean isLogRetention() {
      return log_retention;
   }

   @Override
   public String toString() {
      return String.format(
               "[name=%s, cdn_uri=%s, cdn_enabled=%s, log_retention=%s, referrer_acl=%s, ttl=%s, useragent_acl=%s]",
               name, cdn_uri, cdn_enabled, log_retention, referrer_acl, ttl, useragent_acl);
   }
}
