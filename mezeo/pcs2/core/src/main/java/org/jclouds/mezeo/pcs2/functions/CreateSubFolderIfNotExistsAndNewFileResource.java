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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.util.PCSUtils;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateSubFolderIfNotExistsAndNewFileResource implements Function<Object, String> {

   private final PCSConnection connection;
   private final URI pcs;
   private final CreateSubFolderIfNotExistsAndGetResourceId subFolderMaker;
   private final ConcurrentMap<Key, String> fileCache;

   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   @Inject
   public CreateSubFolderIfNotExistsAndNewFileResource(ConcurrentMap<Key, String> fileCache,
            CreateSubFolderIfNotExistsAndGetResourceId subFolderMaker, PCSConnection connection,
            @PCS URI pcs) {
      this.fileCache = fileCache;
      this.subFolderMaker = subFolderMaker;
      this.connection = connection;
      this.pcs = pcs;
   }

   public String apply(Object from) {
      checkState(checkNotNull(from, "args") instanceof Object[],
               "this must be applied to a method!");
      Object[] args = (Object[]) from;
      checkArgument(args[0] instanceof String, "arg[0] must be a container name");
      checkArgument(args[1] instanceof PCSFile, "arg[1] must be a pcsfile");
      String container = args[0].toString();
      PCSFile file = (PCSFile) args[1];

      try {
         String containerId = subFolderMaker.apply(args);
         URI containerUri = UriBuilder.fromUri(pcs).path("containers/" + containerId).build();
         URI newFile = connection.createFile(containerUri, file).get(
                  this.requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         String id = PCSUtils.getFileId(newFile);
         fileCache.put(new Key(container, file.getKey()), id);
         return id;
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         Utils.<KeyNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("error creating file %s in container %s",
                  file, container), e);
      }
   }
}