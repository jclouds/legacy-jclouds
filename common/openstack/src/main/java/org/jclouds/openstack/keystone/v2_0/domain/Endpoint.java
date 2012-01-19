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

      protected String id;
      protected String region;
      protected URI publicURL;
      protected String tenantId;

      /**
       * @see Endpoint#getId()
       */
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
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
       * @see Endpoint#getTenantId()
       */
      public Builder tenantId(@Nullable String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      public Endpoint build() {
         return new Endpoint(id, region, publicURL, tenantId);
      }

      public Builder fromEndpoint(Endpoint from) {
         return id(from.getId()).region(from.getRegion()).publicURL(from.getPublicURL()).tenantId(from.getTenantId());
      }
   }

   protected final String id;
   protected final String region;
   protected final URI publicURL;
   protected final String tenantId;

   protected Endpoint(String id, String region, URI publicURL, @Nullable String tenantId) {
      this.id = checkNotNull(id, "id");
      this.region = checkNotNull(region, "region");
      this.publicURL = checkNotNull(publicURL, "publicURL");
      this.tenantId = tenantId;
   }

   /**
    * When providing an ID, it is assumed that the endpoint exists in the current OpenStack
    * deployment
    * 
    * @return the id of the endpoint in the current OpenStack deployment
    */
   public String getId() {
      return id;
   }

   /**
    * @return the region of the endpoint
    */
   public String getRegion() {
      return region;
   }

   /**
    * @return the service id of the endpoint
    */

   public URI getPublicURL() {
      return publicURL;
   }

   /**
    * @return the tenant id of the endpoint or null
    */
   @Nullable
   public String getTenantId() {
      return tenantId;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Endpoint) {
         final Endpoint other = Endpoint.class.cast(object);
         return equal(id, other.id) && equal(region, other.region) && equal(publicURL, other.publicURL)
                  && equal(tenantId, other.tenantId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, region, publicURL, tenantId);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("region", region).add("publicURL", publicURL).add("tenantId",
               tenantId).toString();
   }

   @Override
   public int compareTo(Endpoint that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.id.compareTo(that.id);
   }

}
