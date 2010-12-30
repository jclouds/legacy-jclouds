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

package org.jclouds.atmosonline.saas.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * @author Adrian Cole
 */
@Singleton
public class BindMetadataToHeaders implements Binder {
   private final BindUserMetadataToHeaders metaBinder;

   @Inject
   protected BindMetadataToHeaders(BindUserMetadataToHeaders metaBinder) {
      this.metaBinder = metaBinder;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof AtmosObject, "this binder is only valid for AtmosObject!");
      checkNotNull(request, "request");

      AtmosObject object = AtmosObject.class.cast(input);
      checkNotNull(object.getPayload(), "object payload");
      checkArgument(object.getPayload().getContentMetadata().getContentLength() != null,
            "contentLength must be set, streaming not supported");
      return metaBinder.bindToRequest(request, object.getUserMetadata());
   }
}
