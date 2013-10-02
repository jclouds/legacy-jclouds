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
package org.jclouds.atmos.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class ObjectToBlob implements Function<AtmosObject, Blob> {
   private final Factory blobFactory;
   private final ObjectToBlobMetadata object2BlobMd;

   @Inject
   ObjectToBlob(Factory blobFactory, ObjectToBlobMetadata object2BlobMd) {
      this.blobFactory = blobFactory;
      this.object2BlobMd = object2BlobMd;
   }

   public Blob apply(AtmosObject from) {
      if (from == null)
         return null;
      Blob blob = blobFactory.create(object2BlobMd.apply(from));
      blob.setPayload(checkNotNull(from.getPayload(), "payload: " + from));
      HttpUtils.copy(from.getContentMetadata(), blob.getPayload().getContentMetadata());
      blob.setAllHeaders(from.getAllHeaders());
      return blob;
   }
}
