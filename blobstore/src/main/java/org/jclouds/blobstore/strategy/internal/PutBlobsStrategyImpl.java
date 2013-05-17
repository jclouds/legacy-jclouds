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
package org.jclouds.blobstore.strategy.internal;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.PutBlobsStrategy;
import org.jclouds.logging.Logger;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class PutBlobsStrategyImpl implements PutBlobsStrategy {

   private final AsyncBlobStore ablobstore;
   private final ListeningExecutorService userExecutor;
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   PutBlobsStrategyImpl(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            AsyncBlobStore ablobstore) {
      this.userExecutor = userExecutor;
      this.ablobstore = ablobstore;
   }

   @Override
   public void execute(String containerName, Iterable<? extends Blob> blobs) {
      Map<Blob, ListenableFuture<?>> responses = Maps.newLinkedHashMap();
      for (Blob blob : blobs) {
         responses.put(blob, ablobstore.putBlob(containerName, blob));
      }
      Map<Blob, Exception> exceptions;
      try {
         exceptions = awaitCompletion(responses, userExecutor, maxTime, logger,
                  String.format("putting into containerName: %s", containerName));
      } catch (TimeoutException te) {
         throw propagate(te);
      }
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("error putting into container %s: %s",
                  containerName, exceptions));
   }

}
