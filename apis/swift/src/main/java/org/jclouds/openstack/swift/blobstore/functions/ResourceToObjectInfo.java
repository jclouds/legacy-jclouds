/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.blobstore.functions;

import java.util.Map.Entry;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.openstack.swift.domain.MutableObjectInfoWithMetadata;
import org.jclouds.openstack.swift.domain.internal.MutableObjectInfoWithMetadataImpl;

import com.google.common.base.Function;
import org.jclouds.openstack.swift.utils.ETagUtils;

/**
 * @author Adrian Cole
 */
@Singleton
public class ResourceToObjectInfo implements Function<StorageMetadata, MutableObjectInfoWithMetadata> {

   public MutableObjectInfoWithMetadata apply(StorageMetadata from) {
      if (from == null)
         return null;
      MutableObjectInfoWithMetadata to = new MutableObjectInfoWithMetadataImpl();
      if (from.getType() == StorageType.BLOB) {
         to.setContentType(((BlobMetadata) from).getContentMetadata().getContentType());
         to.setBytes(((BlobMetadata) from).getContentMetadata().getContentLength());
         to.setHash(((BlobMetadata) from).getContentMetadata().getContentMD5());
      } else if (from.getType() == StorageType.RELATIVE_PATH) {
         to.setContentType("application/directory");
      }
      if (from.getETag() != null && to.getHash() == null)
         to.setHash(ETagUtils.convertHexETagToByteArray(from.getETag()));
      to.setName(from.getName());
      to.setLastModified(from.getLastModified());
      if (from.getUserMetadata() != null) {
         for (Entry<String, String> entry : from.getUserMetadata().entrySet())
            to.getMetadata().put(entry.getKey().toLowerCase(), entry.getValue());
      }
      return to;
   }
}
