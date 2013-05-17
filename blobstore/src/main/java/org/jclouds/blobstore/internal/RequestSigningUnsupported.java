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
package org.jclouds.blobstore.internal;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.http.HttpRequest;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RequestSigningUnsupported implements BlobRequestSigner {

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, GetOptions options) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

}
