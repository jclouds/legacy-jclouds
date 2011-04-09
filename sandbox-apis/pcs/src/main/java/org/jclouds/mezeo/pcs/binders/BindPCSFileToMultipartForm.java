/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.binders.BindBlobToMultipartForm;
import org.jclouds.http.HttpRequest;
import org.jclouds.mezeo.pcs.blobstore.functions.PCSFileToBlob;
import org.jclouds.mezeo.pcs.domain.PCSFile;
import org.jclouds.rest.Binder;

@Singleton
public class BindPCSFileToMultipartForm implements Binder {
   private final BindBlobToMultipartForm blobBinder;
   private final PCSFileToBlob file2Blob;

   @Inject
   public BindPCSFileToMultipartForm(PCSFileToBlob file2Blob, BindBlobToMultipartForm blobBinder) {
      this.blobBinder = blobBinder;
      this.file2Blob = file2Blob;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      PCSFile file = (PCSFile) input;
      checkNotNull(file.getPayload().getContentMetadata().getContentLength(), "contentLength");
      checkArgument(file.getPayload().getContentMetadata().getContentLength() <= 2l * 1024 * 1024 * 1024,
            "maximum size for POST request is 2GB");
      return blobBinder.bindToRequest(request, file2Blob.apply(file));
   }
}
