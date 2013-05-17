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

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlobToObject implements Function<Blob, AtmosObject> {
   private final BlobMetadataToObject blobMd2Object;

   @Inject
   BlobToObject(BlobMetadataToObject blobMd2Object) {
      this.blobMd2Object = blobMd2Object;
   }

   public AtmosObject apply(Blob from) {
      if (from == null)
         return null;
      AtmosObject object = blobMd2Object.apply(from.getMetadata());
      object.setPayload(checkNotNull(from.getPayload(), "payload: " + from));
      object.setAllHeaders(from.getAllHeaders());
      return object;
   }
}
