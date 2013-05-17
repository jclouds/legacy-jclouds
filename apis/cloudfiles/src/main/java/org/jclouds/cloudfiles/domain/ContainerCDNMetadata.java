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
package org.jclouds.cloudfiles.domain;

import java.net.URI;

/**
 * 
 * @author James Murty
 * 
 */
public class ContainerCDNMetadata implements Comparable<ContainerCDNMetadata> {

   private String name;
   private boolean cdn_enabled;
   private boolean log_retention;
   private long ttl;
   private URI cdn_uri;
   private URI cdn_ssl_uri;
   private URI cdn_streaming_uri;
   private String referrer_acl;
   private String useragent_acl;

   public ContainerCDNMetadata(String name, boolean cdnEnabled, boolean logRetention, long ttl, URI cdnUri, URI cdnSslUri, URI cdnStreamingUri) {
      this.name = name;
      this.cdn_enabled = cdnEnabled;
      this.log_retention = logRetention;
      this.ttl = ttl;
      this.cdn_uri = cdnUri;
      this.cdn_ssl_uri = cdnSslUri;
      this.cdn_streaming_uri = cdnStreamingUri;
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

   public boolean isCDNEnabled() {
      return cdn_enabled;
   }

   public boolean isLogRetention() {
      return log_retention;
   }

   public long getTTL() {
      return ttl;
   }

   public URI getCDNUri() {
      return cdn_uri;
   }

   public URI getCDNSslUri() {
      return cdn_ssl_uri;
   }

   public URI getCDNStreamingUri() {
      return cdn_streaming_uri;
   }

   public String getReferrerACL() {
      return referrer_acl;
   }

   public String getUseragentACL() {
      return useragent_acl;
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

   @Override
   public String toString() {
      return String.format(
               "[name=%s, cdn_enabled=%s, log_retention=%s, ttl=%s, cdn_uri=%s, cdn_ssl_uri=%s, cdn_streaming_uri=%s, referrer_acl=%s, useragent_acl=%s]",
                 name, cdn_enabled, log_retention, ttl, cdn_uri, cdn_ssl_uri, cdn_streaming_uri, referrer_acl, useragent_acl);
   }
}
