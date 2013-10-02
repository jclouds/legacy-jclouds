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
package org.jclouds.s3.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.location.Region;
import org.jclouds.logging.Logger;
import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.s3.Bucket;

import com.google.common.base.Supplier;

/**
 * 
 * Depending on your latency and legal requirements, you can specify a location
 * constraint that will affect where your data physically resides.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindRegionToXmlPayload extends BindToStringPayload {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Supplier<String> defaultRegionForEndpointSupplier;
   private final Supplier<String> defaultRegionForServiceSupplier;
   private final Supplier<Set<String>> regionsSupplier;

   @Inject
   public BindRegionToXmlPayload(@Region  Supplier<String> defaultRegionForEndpointSupplier,
          @Bucket Supplier<String> defaultRegionForServiceSupplier, @Region Supplier<Set<String>> regionsSupplier) {
      this.defaultRegionForEndpointSupplier = defaultRegionForEndpointSupplier;
      this.defaultRegionForServiceSupplier = defaultRegionForServiceSupplier;
      this.regionsSupplier = checkNotNull(regionsSupplier, "regions");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      String defaultRegionForEndpoint = defaultRegionForEndpointSupplier.get();
      if (defaultRegionForEndpoint == null)
         return request;
      input = input == null ? defaultRegionForEndpoint : input;
      checkArgument(input instanceof String, "this binder is only valid for Region!");
      String constraint = (String) input;
      String value = null;
      String defaultRegionForService = defaultRegionForServiceSupplier.get();
      Set<String> regions = regionsSupplier.get();
      if ((defaultRegionForService == null && constraint == null)
            || (defaultRegionForService != null && defaultRegionForService.equals(constraint))) {
         // nothing to bind as this is default.
         return request;
      } else if (regions.contains(constraint)) {
         value = constraint;
      } else {
         logger.warn("region %s not in %s ", constraint, regions);
         value = constraint;
      }
      String payload = String
            .format(
                  "<CreateBucketConfiguration><LocationConstraint>%s</LocationConstraint></CreateBucketConfiguration>",
                  value);
      request = super.bindToRequest(request, payload);
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_XML);
      return request;
   }
}
