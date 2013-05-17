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
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.DeleteDirectoryStrategy;
import org.jclouds.logging.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

/**
 * Key-value implementations of BlobStore, such as S3, do not have directories. In following the
 * rackspace cloud files project, we use an empty object '#{dirpath}' with content type set to
 * 'application/directory'.
 * 
 * <p/>
 * To interoperate with other S3 tools, we accept the following ways to tell if the directory
 * exists:
 * <ul>
 * <li>an object named '#{dirpath}_$folder$' or '#{dirpath}/' denoting a directory marker</li>
 * <li>an object with content type set to 'application/directory' denoting a directory marker</li>
 * <li>if there exists any objects with the prefix "#{dirpath}/", then the directory is said to
 * exist</li>
 * <li>if both a file with the name of a directory and a marker for that directory exists, then the
 * *file masks the directory*, and the directory is never returned.</li>
 * </ul>
 * 
 * @see MarkerFileMkdirStrategy
 * @author Adrian Cole
 */
@Singleton
public class MarkersDeleteDirectoryStrategy implements DeleteDirectoryStrategy {

   private final AsyncBlobStore ablobstore;
   private final BlobStore blobstore;
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
   MarkersDeleteDirectoryStrategy(
            @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
            AsyncBlobStore ablobstore, BlobStore blobstore) {
      this.userExecutor = userExecutor;
      this.ablobstore = ablobstore;
      this.blobstore = blobstore;
   }

   public void execute(String containerName, String directory) {
      Set<String> names = Sets.newHashSet();
      names.add(directory);
      for (String suffix : BlobStoreConstants.DIRECTORY_SUFFIXES) {
         names.add(directory + suffix);
      }
      Map<String, ListenableFuture<?>> responses = Maps.newHashMap();
      for (String name : names) {
         responses.put(name, ablobstore.removeBlob(containerName, name));
      }
      String message = String.format("deleting directory %s in containerName: %s", directory,
               containerName);
      Map<String, Exception> exceptions;
      try {
         exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, message);
      } catch (TimeoutException te) {
         throw propagate(te);
      }
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("error %s: %s", message, exceptions));
      assert !blobstore.directoryExists(containerName, directory) : String.format(
               "still exists %s: %s", message, exceptions);
   }
}
