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

package org.jclouds.aws.s3.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_DEFAULT_REGIONS;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

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

   private final Iterable<String> defaultRegions;
   private final Iterable<String> regions;

   @Inject
   BindRegionToXmlPayload(@Named(PROPERTY_DEFAULT_REGIONS) String defaultRegions,
            @Named(PROPERTY_REGIONS) String regions) {
      this.defaultRegions = Splitter.on(',').split(defaultRegions);
      this.regions = Splitter.on(',').split(regions);
   }

   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      input = input == null ? Iterables.get(defaultRegions, 0) : input;
      checkArgument(input instanceof String, "this binder is only valid for Region!");
      String constraint = (String) input;
      String value = null;
      if (Iterables.contains(defaultRegions, constraint)) {
         // nothing to bind as this is default.
         return;
      } else if (Iterables.contains(regions, constraint)) {
         value = constraint;
      } else {
         throw new IllegalStateException("unimplemented location: " + constraint);
      }
      String payload = String
               .format(
                        "<CreateBucketConfiguration><LocationConstraint>%s</LocationConstraint></CreateBucketConfiguration>",
                        value);
      super.bindToRequest(request, payload);
      request.getPayload().setContentType(MediaType.TEXT_XML);
   }
}
