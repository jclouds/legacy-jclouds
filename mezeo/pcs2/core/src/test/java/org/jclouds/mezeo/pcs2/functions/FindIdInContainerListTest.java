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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.mezeo.pcs2.PCSConnection;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.options.PutBlockOptions;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code ContainerResourceId}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FindIdInContainerList")
public class FindIdInContainerListTest {
   private DateService dateService = new DateService();
   private final PCSConnection connection = new PCSConnection() {

      public Future<URI> createContainer(String container) {
         return null;
      }

      public Future<URI> createContainer(URI parent, String container) {
         return null;
      }

      public Future<Void> deleteContainer(URI container) {
         return null;
      }

      public Future<Void> deleteFile(URI file) {
         return null;
      }

      public Future<InputStream> downloadFile(URI file) {
         return null;
      }

      public SortedSet<ContainerMetadata> listContainers() {
         return null;
      }

      public Future<? extends SortedSet<ContainerMetadata>> listContainers(URI container) {
         if (container.equals(URI.create("https://localhost/root"))) {
            return createFuture(ImmutableSortedSet.of(new ContainerMetadata("apple", URI
                     .create("https://localhost/containers/rootapple"), URI
                     .create("https://localhost/root"), dateService.fromSeconds(1254008225),
                     dateService.fromSeconds(1254008226), dateService.fromSeconds(1254008227),
                     "adrian@jclouds.org", true, false, 1, 1024)));
         } else if (container.equals(URI.create("https://localhost/containers/rootapple"))) {
            return createFuture(ImmutableSortedSet.of(new ContainerMetadata("apple", URI
                     .create("https://localhost/containers/appleapple"), URI
                     .create("https://localhost/containers/rootapple"), dateService
                     .fromSeconds(1254008225), dateService.fromSeconds(1254008226), dateService
                     .fromSeconds(1254008227), "adrian@jclouds.org", true, false, 1, 1024)));
         } else {
            return createFuture(ImmutableSortedSet.<ContainerMetadata> of());
         }
      }

      <T> Future<T> createFuture(final T data) {
         return new Future<T>() {

            public boolean cancel(boolean mayInterruptIfRunning) {
               return false;
            }

            public T get() throws InterruptedException, ExecutionException {
               return data;
            }

            public T get(long timeout, TimeUnit unit) throws InterruptedException,
                     ExecutionException, TimeoutException {
               return data;
            }

            public boolean isCancelled() {
               return false;
            }

            public boolean isDone() {
               return false;
            }

         };
      }

      public Future<? extends SortedSet<FileMetadata>> listFiles(URI container) {
         return null;
      }

      public Future<URI> uploadFile(URI container, PCSFile object) {
         return null;
      }

      public Future<URI> createFile(URI container, PCSFile object) {
         return null;
      }

      public Future<Void> uploadBlock(URI file, PCSFile object, PutBlockOptions... options) {
         throw new UnsupportedOperationException();
      }

   };
   FindIdInContainerList binder = new FindIdInContainerList(connection, URI
            .create("https://localhost/root"));

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testBad() {
      binder.apply("hello");
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testBad2() {
      binder.apply("apple/hello");
   }

   public void testGood() {
      assertEquals(binder.apply("apple"), "rootapple");
   }

   public void testSub() {
      assertEquals(binder.apply("apple/apple"), "appleapple");
   }
}
