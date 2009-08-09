/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.domain;

/**
 * 
 * @author James Murty
 * 
 */
public class ContainerCDNMetadata {
   public static final ContainerCDNMetadata NOT_FOUND = new ContainerCDNMetadata();

   private String name;
   private long ttl;
   private boolean cdn_enabled;
   private String cdn_uri;

   public ContainerCDNMetadata(String name, boolean cdnEnabled, long ttl, String cdnUri) {
      this.name = name;
      this.cdn_enabled = cdnEnabled;
      this.ttl = ttl;
      this.cdn_uri = cdnUri;
   }

   public ContainerCDNMetadata() {
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("ContainerCDNMetadata [name=").append(name)
         .append(", cdn_enabled=").append(cdn_enabled)
         .append(", ttl=").append(ttl)
         .append(", cdn_uri=").append(cdn_uri).append("]");
      return builder.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (cdn_enabled ? 1 : 0);
      result = prime * result + (int) (ttl ^ (ttl >>> 32));
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
      if (cdn_enabled != other.cdn_enabled)
         return false;
      if (ttl != other.ttl)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (cdn_uri == null) {
         if (other.cdn_uri != null)
            return false;
      } else if (!cdn_uri.equals(other.cdn_uri))
         return false;
      return true;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * Beware: The container name is not available from HEAD CDN responses and will be null.
    * return
    * the name of the container to which these CDN settings apply.
    */
   public String getName() {
      return name;
   }

   public void setCdnUri(String cdnUri) {
      this.cdn_uri = cdnUri;
   }

   public String getCdnUri() {
      return cdn_uri;
   }

   public void setTtl(long ttl) {
      this.ttl = ttl;
   }

   public long getTtl() {
      return ttl;
   }

   public void setCdnEnabled(boolean cdnEnabled) {
      this.cdn_enabled = cdnEnabled;
   }

   public boolean isCdnEnabled() {
      return cdn_enabled;
   }

}
