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

package org.jclouds.cloudfiles.domain;

import java.net.URI;


/**
 * 
 * @author James Murty
 * 
 */
public class ContainerCDNMetadata implements Comparable<ContainerCDNMetadata> {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8373435988423605652L;
   private String name;
   private boolean cdn_enabled;
   private long ttl;
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
      result = prime * result + (cdn_enabled ? 1231 : 1237);
      result = prime * result + ((cdn_uri == null) ? 0 : cdn_uri.hashCode());
      result = prime * result + (log_retention ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (int) (ttl ^ (ttl >>> 32));
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
      if (cdn_uri == null) {
         if (other.cdn_uri != null)
            return false;
      } else if (!cdn_uri.equals(other.cdn_uri))
         return false;
      if (log_retention != other.log_retention)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (ttl != other.ttl)
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
      return "ContainerCDNMetadata [cdn_enabled=" + cdn_enabled + ", cdn_uri=" + cdn_uri
               + ", log_retention=" + log_retention + ", name=" + name + ", referrer_acl="
               + referrer_acl + ", ttl=" + ttl + ", useragent_acl=" + useragent_acl + "]";
   }
}
