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

package org.jclouds.blobstore.domain.internal;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.http.HttpUtils;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.payloads.BaseMutableContentMetadata;

/**
 * System and user Metadata for the {@link Blob}.
 * 
 * @author Adrian Cole
 */
public class MutableBlobMetadataImpl extends MutableStorageMetadataImpl implements MutableBlobMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = -5932618957134612231L;
   private MutableContentMetadata contentMetadata;

   public MutableBlobMetadataImpl() {
      super();
      this.setType(StorageType.BLOB);
      this.contentMetadata = new BaseMutableContentMetadata();
   }

   public MutableBlobMetadataImpl(BlobMetadata from) {
      super(from);
      this.contentMetadata = new BaseMutableContentMetadata();
      HttpUtils.copy(from.getContentMetadata(), this.contentMetadata);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public MutableContentMetadata getContentMetadata() {
      return contentMetadata;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setContentMetadata(MutableContentMetadata contentMetadata) {
      this.contentMetadata = contentMetadata;
   }

}