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

package org.jclouds.s3.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.logging.Logger;
import org.jclouds.rest.binders.BindToStringPayload;

/**
 * 
 * Depending on your latency and legal requirements, you can specify a location constraint that will
 * affect where your data physically resides.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class BindRegionToXmlPayload extends BindToStringPayload {
   @Resource
   protected Logger logger = Logger.NULL;

   private final String defaultRegion;
   private final Set<String> regions;

   @Inject
   BindRegionToXmlPayload(@org.jclouds.location.Region @Nullable String defaultRegion,
            @org.jclouds.location.Region Set<String> regions) {
      this.defaultRegion = defaultRegion;
      this.regions = regions;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      if (defaultRegion == null)
         return request;
      input = input == null ? defaultRegion : input;
      checkArgument(input instanceof String, "this binder is only valid for Region!");
      String constraint = (String) input;
      String value = null;
      if (defaultRegion.equals(constraint)) {
         // nothing to bind as this is default.
         return request;
      } else if (regions.contains(constraint)) {
         value = constraint;
      } else {
         logger.warn("region %s not in %s ", constraint, regions);
         value = constraint;
      }
      String payload = String.format(
               "<CreateBucketConfiguration><LocationConstraint>%s</LocationConstraint></CreateBucketConfiguration>",
               value);
      request = super.bindToRequest(request, payload);
      request.getPayload().getContentMetadata().setContentType(MediaType.TEXT_XML);
      return request;
   }
}
