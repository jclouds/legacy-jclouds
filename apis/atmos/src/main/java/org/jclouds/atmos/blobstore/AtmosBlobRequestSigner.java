/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmos.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.blobstore.util.BlobStoreUtils.cleanRequest;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.blobstore.functions.BlobToObject;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.internal.RestAnnotationProcessor;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AtmosBlobRequestSigner implements BlobRequestSigner {
   private final RestAnnotationProcessor<AtmosAsyncClient> processor;
   private final BlobToObject blobToObject;
   private final Method getMethod;
   private final Method deleteMethod;
   private final Method createMethod;

   @Inject
   public AtmosBlobRequestSigner(RestAnnotationProcessor<AtmosAsyncClient> processor, BlobToObject blobToObject)
            throws SecurityException, NoSuchMethodException {
      this.processor = checkNotNull(processor, "processor");
      this.blobToObject = checkNotNull(blobToObject, "blobToObject");
      this.getMethod = AtmosAsyncClient.class.getMethod("readFile", String.class, GetOptions[].class);
      this.deleteMethod = AtmosAsyncClient.class.getMethod("deletePath", String.class);
      this.createMethod = AtmosAsyncClient.class.getMethod("createFile", String.class, AtmosObject.class);

   }

   @Override
   public HttpRequest signGetBlob(String container, String name) {
      return cleanRequest(processor.createRequest(getMethod, getPath(container, name)));
   }

   @Override
   public HttpRequest signPutBlob(String container, Blob blob) {
      return cleanRequest(processor.createRequest(createMethod, container, blobToObject.apply(blob)));
   }

   @Override
   public HttpRequest signRemoveBlob(String container, String name) {
      return cleanRequest(processor.createRequest(deleteMethod, getPath(container, name)));
   }

   private String getPath(String container, String name) {
      return checkNotNull(container, "container") + "/" + checkNotNull(name, "name");
   }

}
