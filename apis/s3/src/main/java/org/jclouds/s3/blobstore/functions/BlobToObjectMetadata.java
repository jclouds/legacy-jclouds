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

package org.jclouds.s3.blobstore.functions;

import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.s3.domain.MutableObjectMetadata;
import org.jclouds.s3.domain.internal.MutableObjectMetadataImpl;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObjectMetadata implements Function<BlobMetadata, MutableObjectMetadata> {
   public MutableObjectMetadata apply(BlobMetadata from) {
      if (from == null)
         return null;
      MutableObjectMetadata to = new MutableObjectMetadataImpl();
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      to.setETag(from.getETag());
      to.setKey(from.getName());
      to.setLastModified(from.getLastModified());
      if (from.getUserMetadata() != null) {
         for (Entry<String, String> entry : from.getUserMetadata().entrySet())
            to.getUserMetadata().put(entry.getKey().toLowerCase(), entry.getValue());
      }
      return to;
   }
}
