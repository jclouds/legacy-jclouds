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
package org.jclouds.atmos.blobstore.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.concurrent.FutureIterables.awaitCompletion;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.atmos.AtmosAsyncClient;
import org.jclouds.atmos.domain.AtmosObject;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobsInContainer;
import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Searches Content-MD5 tag for the value associated with the value
 * 
 * @author Adrian Cole
 */
@Singleton
public class FindMD5InUserMetadata implements ContainsValueInListStrategy {
   @Resource
   protected Logger logger = Logger.NULL;
   protected final ObjectMD5 objectMD5;
   protected final ListBlobsInContainer getAllBlobMetadata;
   private final AtmosAsyncClient client;
   private final ListeningExecutorService userExecutor;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   FindMD5InUserMetadata(@Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor, ObjectMD5 objectMD5,
         ListBlobsInContainer getAllBlobMetadata, AtmosAsyncClient client) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.client = client;
      this.userExecutor = userExecutor;
   }

   @Override
   public boolean execute(final String containerName, Object value, ListContainerOptions options) {
      final byte[] toSearch = objectMD5.apply(value);
      final BlockingQueue<Boolean> queue = new SynchronousQueue<Boolean>();
      Map<String, ListenableFuture<?>> responses = Maps.newHashMap();
      for (BlobMetadata md : getAllBlobMetadata.execute(containerName, options)) {
         final ListenableFuture<AtmosObject> future = client.headFile(containerName + "/" + md.getName());
         future.addListener(new Runnable() {
            public void run() {
               try {
                  AtmosObject object = future.get();
                  checkNotNull(object.getSystemMetadata(), object + " has no content metadata");
                  if (object.getSystemMetadata().getContentMD5() != null) {
                     if (Arrays.equals(toSearch, object.getSystemMetadata().getContentMD5())) {
                        queue.put(true);
                     }
                  } else {
                     logger.debug("object %s has no content md5", object.getSystemMetadata().getObjectID());
                  }
               } catch (InterruptedException e) {
                  Throwables.propagate(e);
               } catch (ExecutionException e) {
                  Throwables.propagate(e);
               }
            }
         }, userExecutor);
         responses.put(md.getName(), future);
      }
      Map<String, Exception> exceptions;
      try {
         exceptions = awaitCompletion(responses, userExecutor, maxTime, logger,
               String.format("searching for md5 in container %s", containerName));
      } catch (TimeoutException te) {
         throw propagate(te);
      }
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("searching for md5 in container %s: %s", containerName,
               exceptions));
      try {
         return queue.poll(1, TimeUnit.MICROSECONDS) != null;
      } catch (InterruptedException e) {
         Throwables.propagate(e);
         return false;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format("Error searching for ETAG of value: [%s] in container:%s", value,
               containerName), e);
      }
   }
}
