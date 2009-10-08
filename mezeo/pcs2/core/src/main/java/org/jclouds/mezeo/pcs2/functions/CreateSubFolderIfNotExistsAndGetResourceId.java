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
package org.jclouds.mezeo.pcs2.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.ComputationException;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateSubFolderIfNotExistsAndGetResourceId implements Function<Object, String> {

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   private final ConcurrentMap<String, String> finder;
   private final PCSConnection blobStore;
   private final URI rootContainer;

   @Inject
   public CreateSubFolderIfNotExistsAndGetResourceId(PCSConnection blobStore,
            ConcurrentMap<String, String> finder, @RootContainer URI rootContainer) {
      this.finder = finder;
      this.blobStore = blobStore;
      this.rootContainer = rootContainer;
   }

   @SuppressWarnings("unchecked")
   public String apply(Object from) {
      checkState(checkNotNull(from, "args") instanceof Object[],
               "this must be applied to a method!");
      Object[] args = (Object[]) from;
      checkArgument(args[0] instanceof String, "arg[0] must be a container name");
      checkArgument(args[1] instanceof Blob, "arg[1] must be a pcsfile");
      Key key = PCSUtils.parseKey(new Key(args[0].toString(), ((Blob) args[1]).getKey()));
      try {
         return finder.get(key.getContainer());
      } catch (ComputationException e) {
         if (e.getCause() instanceof ContainerNotFoundException) {
            String[] containerTree = key.getContainer().split("/");

            SortedSet<ContainerMetadata> response = blobStore.listContainers();
            URI containerUri;
            try {
               containerUri = urlForNameInListOrCreate(rootContainer, containerTree[0],
                        response);
            } catch (Exception e1) {

               Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e1);
               throw new BlobRuntimeException("error creating container at: " + containerTree[0], e1);
            }
            if (containerTree.length != 1) {
               for (int i = 1; i < containerTree.length; i++) {
                  try {
                     response = blobStore.listContainers(containerUri).get(
                              requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
                     containerUri = urlForNameInListOrCreate(containerUri, containerTree[i],
                              response);
                  } catch (Exception e2) {
                     Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e2);
                     throw new BlobRuntimeException("error listing container at: " + containerUri,
                              e2);
                  }
               }
            }
            return PCSUtils.getContainerId(containerUri);
         }

         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw e;
      }
   }

   @VisibleForTesting
   URI urlForNameInListOrCreate(URI parent, String toFind,
            SortedSet<ContainerMetadata> containerMetadataList) throws InterruptedException,
            ExecutionException, TimeoutException {
      for (ContainerMetadata data : containerMetadataList) {
         if (toFind.equals(data.getName())) {
            return data.getUrl();
         }
      }
      return blobStore.createContainer(parent, toFind).get(requestTimeoutMilliseconds,
               TimeUnit.MILLISECONDS);
   }

}