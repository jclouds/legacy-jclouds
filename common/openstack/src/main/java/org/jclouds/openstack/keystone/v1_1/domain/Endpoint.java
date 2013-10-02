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
package org.jclouds.openstack.keystone.v1_1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * An network-accessible address, usually described by URL, where a service may
 * be accessed. If using an extension for templates, you can create an endpoint
 * template, which represents the templates of all the consumable services that
 * are available across the regions.
 * 
 * @author AdrianCole
 * @see <a href=
 *      "http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguv1Defaulte/content/Authentication-d1e699.html#Authenticate-d1e171"
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

      protected boolean v1Default;
      protected String region;
      protected URI publicURL;
      protected URI internalURL;

      /**
       * @see Endpoint#isV1Default()
       */
      public Builder v1Default(boolean v1Default) {
         this.v1Default = v1Default;
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
      public Builder internalURL(@Nullable URI internalURL) {
         this.internalURL = internalURL;
         return this;
      }

      public Endpoint build() {
         return new Endpoint(v1Default, region, publicURL, internalURL);
      }

      public Builder fromEndpoint(Endpoint from) {
         return v1Default(from.isV1Default()).region(from.getRegion()).publicURL(from.getPublicURL())
               .internalURL(from.getInternalURL());
      }
   }

   protected Endpoint() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   protected boolean v1Default;
   protected String region;
   protected URI publicURL;
   protected URI internalURL;

   protected Endpoint(boolean v1Default, @Nullable String region, @Nullable  URI publicURL, @Nullable URI internalURL) {
      this.v1Default = v1Default;
      this.region = region;
      this.publicURL = publicURL;
      this.internalURL = internalURL;
   }

   /**
    * The v1Default attribute denotes that an endpoint is being returned in
    * version 1.0 of the Cloud Authentication Service. The default value of
    * v1Default is false; clients should assume the value is false when the
    * attribute is missing. Auth 1.0 does not offer support for regional
    * endpoints and therefore only returns one endpoint per service. Resources
    * stored in endpoints where v1Default is false will not be seen by Auth 1.0
    * clients.
    * 
    * @return whether this endpoint is visible to v1.0 clients
    */
   public boolean isV1Default() {
      return v1Default;
   }

   /**
    * A service may expose endpoints in different regions. Regional endpoints
    * allow clients to provision resources in a manner that provides high
    * availability. <br/>
    * <h3>Note</h3> Some services are not region-specific. These services supply
    * a single non-regional endpoint and do not provide access to internal URLs.
    * 
    * @return the region of the endpoint
    */
   @Nullable
   public String getRegion() {
      return region;
   }

   /**
    * A public URL is accessible from anywhere. Access to a public URL usually incurs traffic
    * charges.
    * 
    * @return the public endpoint of the service
    */
   @Nullable
   public URI getPublicURL() {
      return publicURL;
   }

   /**
    * Internal URLs are only accessible to services within the same region.
    * Access to an internal URL is free of charge.
    * 
    * @return the internal url of the endpoint
    */
   @Nullable
   public URI getInternalURL() {
      return internalURL;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof Endpoint) {
         final Endpoint other = Endpoint.class.cast(object);
         return equal(v1Default, other.v1Default) && equal(region, other.region) && equal(publicURL, other.publicURL)
               && equal(internalURL, other.internalURL);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(v1Default, region, publicURL, internalURL);
   }

   @Override
   public String toString() {
      return toStringHelper("").add("v1Default", v1Default).add("region", region).add("publicURL", publicURL)
            .add("internalURL", internalURL).toString();
   }

   @Override
   public int compareTo(Endpoint that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.publicURL.compareTo(that.publicURL);
   }

}
