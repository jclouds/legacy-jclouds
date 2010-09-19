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

package org.jclouds.blobstore.domain;

import javax.annotation.Nullable;

import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;

import com.google.inject.ImplementedBy;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(MutableBlobMetadataImpl.class)
public interface MutableBlobMetadata extends BlobMetadata, MutableStorageMetadata {

   /**
    * A standard MIME type describing the format of the contents. If none is
    * provided, the default is binary/octet-stream.
    * 
    * @see <a href=
    *      "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.17." />
    */
   void setContentType(@Nullable String type);

   void setContentMD5(@Nullable byte[] md5);

   void setContentDisposition(@Nullable String contentDisposition);

   void setContentEncoding(@Nullable String contentEncoding);

   void setContentLanguage(@Nullable String contentLanguage);

}