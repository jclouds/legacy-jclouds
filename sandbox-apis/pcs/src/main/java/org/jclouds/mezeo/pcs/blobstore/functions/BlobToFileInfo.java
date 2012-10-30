/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.mezeo.pcs.blobstore.functions;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.mezeo.pcs.domain.MutableFileInfo;
import org.jclouds.mezeo.pcs.domain.internal.MutableFileInfoImpl;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToFileInfo implements Function<BlobMetadata, MutableFileInfo> {
   @Override
   public MutableFileInfo apply(BlobMetadata base) {
      MutableFileInfo to = new MutableFileInfoImpl();
      to.setUrl(base.getUri());
      to.setMimeType(base.getContentMetadata().getContentType());
      to.setName(base.getName());
      to.setModified(base.getLastModified());
      if (base.getContentMetadata().getContentLength() != null)
         to.setBytes(base.getContentMetadata().getContentLength());
      return to;
   }

}
