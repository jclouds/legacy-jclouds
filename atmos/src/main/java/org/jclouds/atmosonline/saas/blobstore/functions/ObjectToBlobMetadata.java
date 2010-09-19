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

package org.jclouds.atmosonline.saas.blobstore.functions;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.functions.AtmosObjectName;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlobMetadata implements Function<AtmosObject, MutableBlobMetadata> {
   private final AtmosObjectName objectName;
   private static final Set<String> systemMetadata = ImmutableSet.of("atime", "mtime", "ctime",
            "itime", "type", "uid", "gid", "objectid", "objname", "size", "nlink", "policyname",
            "content-md5");

   @Inject
   protected ObjectToBlobMetadata(AtmosObjectName objectName) {
      this.objectName = objectName;
   }

   public MutableBlobMetadata apply(AtmosObject from) {
      if (from == null)
         return null;
      MutableBlobMetadata to = new MutableBlobMetadataImpl();
      to.setId(from.getSystemMetadata().getObjectID());
      to.setLastModified(from.getSystemMetadata().getLastUserDataModification());
      HttpUtils.copy(from.getContentMetadata(), to.getContentMetadata());
      to.setName(objectName.apply(from));
      if (from.getSystemMetadata().getType() == FileType.DIRECTORY) {
         to.setType(StorageType.FOLDER);
      } else {
         to.setType(StorageType.BLOB);
      }
      Map<String, String> lowerKeyMetadata = Maps.newHashMap();
      for (Entry<String, String> entry : from.getUserMetadata().getMetadata().entrySet()) {
         String key = entry.getKey().toLowerCase();
         if (!systemMetadata.contains(key))
            lowerKeyMetadata.put(key, entry.getValue());
      }
      to.setUserMetadata(lowerKeyMetadata);
      return to;
   }
}