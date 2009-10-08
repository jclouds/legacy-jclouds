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

import java.net.URI;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.endpoints.RootContainer;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

public class FindIdInContainerList implements Function<String, String> {
   private final PCSConnection connection;
   private final URI rootContainerUri;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Inject
   public FindIdInContainerList(PCSConnection connection, @RootContainer URI rootContainerURI) {
      this.connection = connection;
      this.rootContainerUri = rootContainerURI;
   }

   public String apply(String key) {
      String[] containerTree = key.split("/");
      URI containerUri = rootContainerUri;
      try {
         containerUri = urlForNameInListOrException(containerTree[0], rootContainerUri, connection
                  .listContainers(rootContainerUri).get(requestTimeoutMilliseconds,
                           TimeUnit.MILLISECONDS));
         if (containerTree.length != 1) {
            for (int i = 1; i < containerTree.length; i++) {
               containerUri = urlForNameInListOrException(containerTree[i], containerUri,
                        listContainers(containerUri));
            }
         }
         return PCSUtils.getContainerId(containerUri);
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("error listing container at: " + containerUri, e);
      }
   }

   private SortedSet<ContainerMetadata> listContainers(URI containerUri)
            throws InterruptedException, ExecutionException, TimeoutException {
      return connection.listContainers(containerUri).get(requestTimeoutMilliseconds,
               TimeUnit.MILLISECONDS);
   }

   @VisibleForTesting
   URI urlForNameInListOrException(String toFind, URI parent,
            SortedSet<ContainerMetadata> containerMetadataList) {
      for (ContainerMetadata data : containerMetadataList) {
         if (toFind.equals(data.getName()) && parent.equals(data.getParent())) {
            return data.getUrl();
         }
      }
      throw new ContainerNotFoundException(toFind);
   }

}