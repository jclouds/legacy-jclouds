/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.aws.domain.Region;
import org.jclouds.http.HttpRequest;
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

   @Override
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Region,
               "this binder is only valid for Region!");
      Region constraint = (Region) input;
      String value = null;
      switch (constraint) {
         case US_STANDARD:
         case US_EAST_1:
         case DEFAULT:// TODO get this from the url
            return;
         case EU_WEST_1:
            value = "EU";
            break;
         case US_WEST_1:
            value = "us-west-1";
            break;
         default:
            throw new IllegalStateException("unimplemented location: " + this);
      }
      super
               .bindToRequest(
                        request,
                        String
                                 .format(
                                          "<CreateBucketConfiguration><LocationConstraint>%s</LocationConstraint></CreateBucketConfiguration>",
                                          value));
   }
}
