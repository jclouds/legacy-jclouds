/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Name 2.0 (the
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

package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * An network-accessible address, usually described by URL, where a service may be accessed. If
 * using an extension for templates, you can create an endpoint template, which represents the
 * templates of all the consumable services that are available across the regions.
 * 
 * @author AdrianCole
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Identity-Endpoint-Concepts-e1362.html"
 *      />
 */
public class Endpoint implements Comparable<Endpoint> {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromEndpoint(this);
   }

   public static class Builder {

      protected String versionId;
      protected String region;
      protected URI publicURL;
      protected URI internalURL;
      protected String tenantId;

      /**
       * @see Endpoint#getVersionId()
       */
      public Builder versionId(String versionId) {
         this.versionId = checkNotNull(versionId, "versionId");
         return this;
      }

      /**
       * @see Endpoint#getRegion()
       */
      public Builder region(String region) {
         this.region = checkNotNull(region, "region");
         return this;
      }

      /**
       * @see Endpoint#getPublicURL()
       */
      public Builder publicURL(URI publicURL) {
         this.publicURL = checkNotNull(publicURL, "publicURL");
         return this;
      }

      /**
       * @see Endpoint#getInternalURL()
       */
      public Builder internalURL(URI internalURL) {
         this.internalURL = checkNotNull(internalURL, "internalURL");
         return this;
      }

      /**
       * @see Endpoint#getTenantId()
       */
      public Builder tenantId(@Nullable String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      public Endpoint build() {
         return new Endpoint(versionId, region, publicURL, internalURL, tenantId);
      }

      public Builder fromEndpoint(Endpoint from) {
         return versionId(from.getVersionId()).region(from.getRegion()).publicURL(from.getPublicURL()).internalURL(
                  from.getInternalURL()).tenantId(from.getTenantId());
      }
   }
   // renamed half-way through
   @Deprecated
   protected String id;
   protected final String versionId;
   protected final String region;
   protected final URI publicURL;
   protected final URI internalURL;
   // renamed half-way through
   @Deprecated
   protected String tenantName;
   protected final String tenantId;

   protected Endpoint(String versionId, String region, @Nullable URI publicURL, @Nullable URI internalURL,
            @Nullable String tenantId) {
      this.versionId = checkNotNull(versionId, "versionId");
      this.region = checkNotNull(region, "region");
      this.publicURL = publicURL;
      this.internalURL = internalURL;
      this.tenantId = tenantId;
   }

   /**
    * When provversionIding an ID, it is assumed that the endpoint exists in the current OpenStack
    * deployment
    * 
    * @return the versionId of the endpoint in the current OpenStack deployment
    */
   public String getVersionId() {
      return versionId != null ? versionId : id;
   }

   /**
    * @return the region of the endpoint
    */
   public String getRegion() {
      return region;
   }

   /**
    * @return the public url of the endpoint
    */
   @Nullable
   public URI getPublicURL() {
      return publicURL;
   }

   /**
    * @return the internal url of the endpoint
    */
   @Nullable
   public URI getInternalURL() {
      return internalURL;
   }

   /**
    * @return the tenant versionId of the endpoint or null
    */
   @Nullable
   public String getTenantId() {
      return tenantId != null ? tenantId : tenantName;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Endpoint) {
         final Endpoint other = Endpoint.class.cast(object);
         return equal(getVersionId(), other.getVersionId()) && equal(region, other.region) && equal(publicURL, other.publicURL)
                  && equal(internalURL, other.internalURL) && equal(getTenantId(), other.getTenantId());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getVersionId(), region, publicURL, internalURL, getTenantId());
   }

   @Override
   public String toString() {
      return toStringHelper("").add("versionId", getVersionId()).add("region", region).add("publicURL", publicURL).add("internalURL",
               internalURL).add("tenantId", getTenantId()).toString();
   }

   @Override
   public int compareTo(Endpoint that) {
      return ComparisonChain.start().compare(this.getTenantId(), that.getTenantId()).compare(this.getVersionId(), that.getVersionId())
               .compare(this.region, that.region).result();
   }

}
