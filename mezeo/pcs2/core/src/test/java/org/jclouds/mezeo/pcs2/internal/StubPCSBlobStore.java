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
package org.jclouds.mezeo.pcs2.internal;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.util.DateService;

/**
 * Implementation of {@link PCSBlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
public class StubPCSBlobStore extends StubBlobStore<ContainerMetadata, FileMetadata, PCSFile>
         implements PCSBlobStore {

   @Override
   public Future<Boolean> deleteContainer(final String container) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            getContainerToBlobs().remove(container);
            return true;
         }
      };
   }

   @Inject
   protected StubPCSBlobStore(Map<String, Map<String, PCSFile>> containerToBlobs,
            DateService dateService, Provider<ContainerMetadata> containerMetaProvider,
            Provider<PCSFile> blobProvider) {
      super(containerToBlobs, dateService, containerMetaProvider, blobProvider);
   }
}
