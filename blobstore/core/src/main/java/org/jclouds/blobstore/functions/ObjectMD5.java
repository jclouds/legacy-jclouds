/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.functions;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.internal.BlobRuntimeException;

import com.google.common.base.Function;

public class ObjectMD5<M extends BlobMetadata, B extends Blob<M>> implements
         Function<Object, byte[]> {

   protected final Provider<B> blobFactory;

   @Inject
   ObjectMD5(Provider<B> blobFactory) {
      this.blobFactory = blobFactory;
   }

   public byte[] apply(Object from) {
      Blob<?> object;
      if (from instanceof Blob<?>) {
         object = (Blob<?>) from;
      } else {
         object = blobFactory.get();
         object.setData(from);
      }
      if (object.getMetadata().getContentMD5() == null)
         try {
            object.generateMD5();
         } catch (IOException e) {
            throw new BlobRuntimeException("couldn't get MD5 for: " + from, e);
         }
      return object.getMetadata().getContentMD5();
   }

}
