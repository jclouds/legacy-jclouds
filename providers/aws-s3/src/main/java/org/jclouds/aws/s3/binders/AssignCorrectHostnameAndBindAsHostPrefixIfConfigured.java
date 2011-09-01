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
package org.jclouds.aws.s3.binders;

import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_SERVICE_PATH;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.binders.BindAsHostPrefix;
import org.jclouds.s3.Bucket;
import org.jclouds.s3.binders.BindAsHostPrefixIfConfigured;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AssignCorrectHostnameAndBindAsHostPrefixIfConfigured extends BindAsHostPrefixIfConfigured {
   private final Map<String, String> bucketToRegion;
   private final RegionToEndpointOrProviderIfNull r2;

   @Inject
   public AssignCorrectHostnameAndBindAsHostPrefixIfConfigured(BindAsHostPrefix bindAsHostPrefix,
         @Named(PROPERTY_S3_VIRTUAL_HOST_BUCKETS) boolean isVhostStyle,
         @Named(PROPERTY_S3_SERVICE_PATH) String servicePath, RegionToEndpointOrProviderIfNull r2,
         Provider<UriBuilder> uriBuilderProvider, @Bucket Map<String, String> bucketToRegion) {
      super(bindAsHostPrefix, isVhostStyle, servicePath, uriBuilderProvider);
      this.bucketToRegion = bucketToRegion;
      this.r2 = r2;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      String bucket = payload.toString();
      String region = bucketToRegion.get(bucket);
      if (region != null) {
         URI endpoint = r2.apply(region);
         request = ModifyRequest.endpoint(
               request,
               uriBuilderProvider.get().uri(endpoint).path(request.getEndpoint().getPath())
                     .replaceQuery(request.getEndpoint().getQuery()).build());
      }
      return super.bindToRequest(request, payload);
   }
}
