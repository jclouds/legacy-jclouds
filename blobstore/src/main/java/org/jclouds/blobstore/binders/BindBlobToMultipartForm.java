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

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.MultipartForm;
import org.jclouds.http.MultipartForm.Part;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
public class BindBlobToMultipartForm implements Binder {

   public static final String BOUNDARY = "--JCLOUDS--";

   public void bindToRequest(HttpRequest request, Object payload) {
      Blob object = (Blob) payload;
      
      Part part = Part.create(object.getMetadata().getName(), object.getPayload(), object
               .getMetadata().getContentType());
      
      MultipartForm form = new MultipartForm(BOUNDARY, part);
      request.setPayload(form.getData());
      request.getHeaders().put(HttpHeaders.CONTENT_TYPE,
               "multipart/form-data; boundary=" + BOUNDARY);
      request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, form.getSize() + "");
   }
}
