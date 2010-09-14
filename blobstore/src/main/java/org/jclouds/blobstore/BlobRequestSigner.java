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

package org.jclouds.blobstore;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.RequestSigningUnsupported;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.payloads.PhantomPayload;

import com.google.inject.ImplementedBy;

/**
 * Generates signed requests for blobs. useful in other tools such as backup utilities.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(RequestSigningUnsupported.class)
public interface BlobRequestSigner {

   /**
    * gets a signed request, including headers as necessary, to access a blob from an external
    * client.
    * 
    * @param container
    *           container where the blob resides
    * @param directory
    *           full path to the blob
    * @throws UnsupportedOperationException
    *            if not supported by the provider
    */
   HttpRequest signGetBlob(String container, String name);

   /**
    * gets a signed request, including headers as necessary, to delete a blob from an external
    * client.
    * 
    * @param container
    *           container where the blob resides
    * @param directory
    *           full path to the blob
    * @throws UnsupportedOperationException
    *            if not supported by the provider
    */
   HttpRequest signRemoveBlob(String container, String name);

   /**
    * gets a signed request, including headers as necessary, to upload a blob from an external
    * client.
    * 
    * <pre>
    * Blob blob = context.getBlobStore.newBlob();
    * blob.getMetadata().setName(&quot;name&quot;);
    * blob.setPayload(new PhantomPayload(length, md5));
    * blob.getPayload().setContentType(&quot;text/plain&quot;);
    * </pre>
    * 
    * @param container
    *           container where the blob resides
    * @param blob
    *           what to upload
    * @throws UnsupportedOperationException
    *            if not supported by the provider
    * @see PhantomPayload
    */
   HttpRequest signPutBlob(String container, Blob blob);
}
