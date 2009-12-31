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
package org.jclouds.mezeo.pcs2.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.mezeo.pcs2.domain.MutableFileInfo;
import org.jclouds.mezeo.pcs2.domain.internal.MutableFileInfoImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToFileInfo implements Function<BlobMetadata, MutableFileInfo> {
   public MutableFileInfo apply(BlobMetadata base) {
      MutableFileInfo to = new MutableFileInfoImpl();
      to.setUrl(base.getLocation());
      to.setMimeType(base.getContentType());
      to.setName(base.getName());
      to.setModified(base.getLastModified());
      if (base.getSize() != null)
         to.setBytes(base.getSize());
      return to;
   }

}