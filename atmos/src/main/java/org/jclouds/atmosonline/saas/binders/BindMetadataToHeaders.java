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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.crypto.Crypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

@Singleton
public class BindMetadataToHeaders implements Binder {
   private final BindUserMetadataToHeaders metaBinder;

   @Inject
   protected BindMetadataToHeaders(BindUserMetadataToHeaders metaBinder, Crypto crypto) {
      this.metaBinder = metaBinder;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      AtmosObject object = (AtmosObject) payload;
      checkArgument(object.getPayload().getContentMetadata().getContentLength() != null,
               "contentLength must be set, streaming not supported");
      metaBinder.bindToRequest(request, object.getUserMetadata());
   }
}
