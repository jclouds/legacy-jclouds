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
package org.jclouds.s3.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.options.PutObjectOptions;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class S3BlobRequestSigner implements BlobRequestSigner {
   private final RestAnnotationProcessor processor;
   private final BlobToObject blobToObject;
   private final BlobToHttpGetOptions blob2HttpGetOptions;

   private final Invokable<?, ?> getMethod;
   private final Invokable<?, ?> deleteMethod;
   private final Invokable<?, ?> createMethod;

   @Inject
   public S3BlobRequestSigner(RestAnnotationProcessor.Factory processor, BlobToObject blobToObject,
         BlobToHttpGetOptions blob2HttpGetOptions) throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor").declaring(S3AsyncClient.class);
      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.blob2HttpGetOptions = checkNotNull(blob2HttpGetOptions, "blob2HttpGetOptions");
      this.getMethod = Invokable.from(S3AsyncClient.class.getMethod("getObject", String.class, String.class,
            GetOptions[].class));
      this.deleteMethod = Invokable.from(S3AsyncClient.class.getMethod("deleteObject", String.class, String.class));
      this.createMethod = Invokable.from(S3AsyncClient.class.getMethod("putObject", String.class, S3Object.class,
            PutObjectOptions[].class));

   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.createRequest(getMethod, ImmutableList.<Object> of(container, name)));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      checkNotNull(container, "container");
      checkNotNull(blob, "blob");
      return cleanRequest(processor.createRequest(createMethod,
            ImmutableList.<Object> of(container, blobToObject.apply(blob))));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob, long timeInSeconds) {
      throw new UnsupportedOperationException();
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.createRequest(deleteMethod, ImmutableList.<Object> of(container, name)));
   }

   @Override
   public HttpRequest signGetBlob(String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      checkNotNull(container, "container");
      checkNotNull(name, "name");
      return cleanRequest(processor.createRequest(getMethod,
            ImmutableList.of(container, name, blob2HttpGetOptions.apply(checkNotNull(options, "options")))));
   }
}
