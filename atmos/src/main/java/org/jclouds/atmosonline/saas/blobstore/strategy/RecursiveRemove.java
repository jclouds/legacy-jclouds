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

import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Recursively remove a path.
 * 
 * @author Adrian Cole
 */
@Singleton
public class RecursiveRemove implements ClearListStrategy, ClearContainerStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_HTTP_REQUEST_TIMEOUT)
   protected Long maxTime;
   protected final AtmosStorageAsyncClient async;
   protected final AtmosStorageClient sync;
   private final ExecutorService userExecutor;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public RecursiveRemove(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            AtmosStorageAsyncClient connection, AtmosStorageClient sync) {
      this.async = connection;
      this.sync = sync;
      this.userExecutor = userExecutor;
   }

   public void execute(String containerName) {
      logger.debug("clearing container ", containerName);
      execute(containerName, new ListContainerOptions().recursive());
      logger.trace("cleared container " + containerName);
   }

   private ListenableFuture<Void> rm(final String fullPath, FileType type, boolean recursive) {
      Map<String, ListenableFuture<?>> responses = Maps.newHashMap();
      if ((type == FileType.DIRECTORY) && recursive) {
         for (DirectoryEntry child : sync.listDirectory(fullPath)) {
            responses.put(fullPath + "/" + child.getObjectName(), rm(fullPath + "/"
                     + child.getObjectName(), child.getType(), true));
         }
      }
      Map<String, Exception> exceptions = awaitCompletion(responses, userExecutor, maxTime, logger,
               String.format("deleting from path: %s", fullPath));
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("deleting from path %s: %s", fullPath,
                  exceptions));

      return Futures.compose(async.deletePath(fullPath), new Function<Void, Void>() {

         public Void apply(Void from) {
            try {
               if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
                  public Boolean get() {
                     return !sync.pathExists(fullPath);
                  }
               }, maxTime != null ? maxTime : 1000)) {
                  throw new IllegalStateException(fullPath + " still exists after deleting!");
               }
               return null;
            } catch (InterruptedException e) {
               throw new IllegalStateException(fullPath + " still exists after deleting!", e);
            }
         }

      });
   }

   public void execute(String path, ListContainerOptions options) {
      if (options.getDir() != null)
         path += "/" + options.getDir();
      Map<String, ListenableFuture<?>> responses = Maps.newHashMap();
      for (DirectoryEntry md : sync.listDirectory(path)) {
         responses.put(path + "/" + md.getObjectName(), rm(path + "/" + md.getObjectName(), md
                  .getType(), options.isRecursive()));
      }
      Map<String, Exception> exceptions = awaitCompletion(responses, userExecutor, maxTime, logger,
               String.format("deleting from path: %s", path));
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String
                  .format("deleting from path %s: %s", path, exceptions));
   }

}