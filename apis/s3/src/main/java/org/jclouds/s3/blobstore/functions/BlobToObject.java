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
package org.jclouds.s3.blobstore.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.s3.domain.S3Object;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObject implements Function<Blob, S3Object> {
   private final BlobToObjectMetadata blob2ObjectMd;
   private final S3Object.Factory objectProvider;

   @Inject
   BlobToObject(BlobToObjectMetadata blob2ObjectMd, S3Object.Factory objectProvider) {
      this.blob2ObjectMd = blob2ObjectMd;
      this.objectProvider = objectProvider;
   }

   public S3Object apply(Blob from) {
      if (from == null)
         return null;
      S3Object object = objectProvider.create(blob2ObjectMd.apply(from.getMetadata()));
      object.setPayload(checkNotNull(from.getPayload(), "payload: " + from));
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}
