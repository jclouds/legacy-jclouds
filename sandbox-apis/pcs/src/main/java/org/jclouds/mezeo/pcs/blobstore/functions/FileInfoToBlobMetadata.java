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

import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.mezeo.pcs.domain.FileInfo;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class FileInfoToBlobMetadata implements Function<FileInfo, MutableBlobMetadata> {
   public static final Pattern OBJECTS_PATTERN = Pattern.compile(".*objects/");

   @Override
   public MutableBlobMetadata apply(FileInfo from) {
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      if (from.getUrl() != null) {
         to.setId(Strings2.replaceAll(from.getUrl().getPath(), OBJECTS_PATTERN, ""));
      }
      to.setUri(from.getUrl());
      to.setName(from.getName());
      if (from.getMimeType() != null)
         to.getContentMetadata().setContentType(from.getMimeType());
      if (from.getModified() != null)
         to.setLastModified(from.getModified());
      if (from.getBytes() != null)
         to.getContentMetadata().setContentLength(from.getBytes());
      to.setType(StorageType.BLOB);
      return to;
   }
}
