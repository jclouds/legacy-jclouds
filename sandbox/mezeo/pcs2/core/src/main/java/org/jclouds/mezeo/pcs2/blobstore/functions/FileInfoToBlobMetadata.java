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

package org.jclouds.mezeo.pcs2.blobstore.functions;

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.mezeo.pcs2.domain.FileInfo;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class FileInfoToBlobMetadata implements Function<FileInfo, MutableBlobMetadata> {
   public static final Pattern OBJECTS_PATTERN = Pattern.compile(".*objects/");

   public MutableBlobMetadata apply(FileInfo from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getUrl() != null) {
         to.setId(Utils.replaceAll(from.getUrl().getPath(), OBJECTS_PATTERN, ""));
      }
      to.setUri(from.getUrl());
      to.setName(from.getName());
      if (from.getMimeType() != null)
         to.setContentType(from.getMimeType());
      if (from.getModified() != null)
         to.setLastModified(from.getModified());
      if (from.getBytes() != null)
         to.setSize(from.getBytes());
      to.setType(StorageType.BLOB);
      return to;
   }
}