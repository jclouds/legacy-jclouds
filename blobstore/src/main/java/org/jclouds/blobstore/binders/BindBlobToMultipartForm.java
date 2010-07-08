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
package org.jclouds.blobstore.binders;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.payloads.MultipartForm;
import org.jclouds.http.payloads.Part;
import org.jclouds.http.payloads.Part.PartOptions;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindBlobToMultipartForm implements Binder {

   public void bindToRequest(HttpRequest request, Object payload) {
      Blob blob = (Blob) payload;

      Part part = Part.create(blob.getMetadata().getName(), blob.getPayload(),
               new PartOptions().contentType(blob.getMetadata().getContentType()));

      request.setPayload(new MultipartForm(part));
   }
}
