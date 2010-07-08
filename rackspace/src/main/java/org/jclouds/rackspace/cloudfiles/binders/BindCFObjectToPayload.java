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
package org.jclouds.rackspace.cloudfiles.binders;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.binders.BindUserMetadataToHeadersWithPrefix;
import org.jclouds.http.HttpRequest;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rest.Binder;

@Singleton
public class BindCFObjectToPayload implements Binder {

   private final BindUserMetadataToHeadersWithPrefix mdBinder;
   private final ObjectToBlob object2Blob;

   @Inject
   public BindCFObjectToPayload(ObjectToBlob object2Blob,
            BindUserMetadataToHeadersWithPrefix mdBinder) {
      this.mdBinder = mdBinder;
      this.object2Blob = object2Blob;
   }

   public void bindToRequest(HttpRequest request, Object payload) {
      CFObject object = (CFObject) payload;
      if (object.getPayload().getContentType() == null)
         object.getPayload().setContentType(MediaType.APPLICATION_OCTET_STREAM);
      if (object.getPayload().getContentLength() != null
               && object.getPayload().getContentLength() >= 0) {
         checkArgument(object.getPayload().getContentLength() <= 5l * 1024 * 1024 * 1024,
                  "maximum size for put object is 5GB");
      } else {
         // Enable "chunked"/"streamed" data, where the size needn't be known in advance.
         request.getHeaders().put("Transfer-Encoding", "chunked");
      }
      mdBinder.bindToRequest(request, object2Blob.apply(object));
   }

}
