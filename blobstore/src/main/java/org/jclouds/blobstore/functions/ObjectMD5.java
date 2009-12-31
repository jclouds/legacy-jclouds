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
package org.jclouds.blobstore.functions;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.Payloads;

import com.google.common.base.Function;

public class ObjectMD5 implements Function<Object, byte[]> {

   protected final Blob.Factory blobFactory;

   @Inject
   ObjectMD5(Blob.Factory blobFactory) {
      this.blobFactory = blobFactory;
   }

   public byte[] apply(Object from) {
      Blob object;
      if (from instanceof Blob) {
         object = (Blob) from;
      } else {
         object = blobFactory.create(null);
         object.setPayload(Payloads.newPayload(from));
      }
      if (object.getMetadata().getContentMD5() == null)
         object.generateMD5();
      return object.getMetadata().getContentMD5();
   }

}
