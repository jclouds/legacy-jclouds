/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.atmosonline.saas.blobstore.strategy;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ObjectMD5;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
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
   protected final ListBlobMetadataStrategy getAllBlobMetadata;
   private final AtmosStorageAsyncClient client;
   private final ExecutorService userExecutor;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_HTTP_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   private FindMD5InUserMetadata(
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            ObjectMD5 objectMD5, ListBlobMetadataStrategy getAllBlobMetadata,
            AtmosStorageAsyncClient client) {
      this.objectMD5 = objectMD5;
      this.getAllBlobMetadata = getAllBlobMetadata;
      this.client = client;
      this.userExecutor = userExecutor;
   }

   public boolean execute(final String containerName, Object value, ListContainerOptions options) {
      final byte[] toSearch = objectMD5.apply(value);
      final BlockingQueue<Boolean> queue = new SynchronousQueue<Boolean>();

      SortedSet<? extends BlobMetadata> allMd = getAllBlobMetadata.execute(containerName, options);

      final CountDownLatch doneSignal = new CountDownLatch(allMd.size());

      for (final ListenableFuture<AtmosObject> future : Iterables.transform(getAllBlobMetadata
               .execute(containerName, options),
               new Function<BlobMetadata, ListenableFuture<AtmosObject>>() {
                  @Override
                  public ListenableFuture<AtmosObject> apply(BlobMetadata from) {
                     return client.headFile(containerName + "/" + from.getName());
                  }

               })) {
         future.addListener(new Runnable() {
            public void run() {
               try {
                  future.get();
                  doneSignal.countDown();
                  if (Arrays.equals(toSearch, future.get().getContentMetadata().getContentMD5())) {
                     queue.put(true);
                  }
               } catch (Exception e) {
                  doneSignal.countDown();
               }
            }
         }, userExecutor);
      }
      try {
         if (maxTime != null) {
            return queue.poll(maxTime, TimeUnit.MILLISECONDS);
         } else {
            doneSignal.await();
            return queue.poll(1, TimeUnit.MICROSECONDS);
         }
      } catch (InterruptedException e) {
         return false;
      } catch (Exception e) {
         Throwables.propagateIfPossible(e, BlobRuntimeException.class);
         throw new BlobRuntimeException(String.format(
                  "Error searching for ETAG of value: [%s] in container:%s", value, containerName),
                  e);
      }
   }
}
