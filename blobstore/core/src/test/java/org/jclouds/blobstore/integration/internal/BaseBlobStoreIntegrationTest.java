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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.util.BlobStoreUtils;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.util.Utils;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;
import org.testng.collections.Lists;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

public class BaseBlobStoreIntegrationTest<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>> {
   protected static final String LOCAL_ENCODING = System.getProperty("file.encoding");
   protected static final String TEST_STRING = "<apples><apple name=\"fuji\"></apple> </apples>";

   public static long INCONSISTENCY_WINDOW = 5000;
   protected static volatile AtomicInteger containerIndex = new AtomicInteger(0);

   protected byte[] goodETag;
   protected byte[] badETag;

   protected volatile BlobStoreContext<S, C, M, B> context;
   protected static volatile int containerCount = 20;
   public static final String CONTAINER_PREFIX = System.getProperty("user.name") + "-blobstore";
   /**
    * two test groups integration and live.
    */
   private volatile static BlockingQueue<String> containerJsr330 = new ArrayBlockingQueue<String>(
            containerCount);

   @BeforeGroups(groups = { "integration", "live" })
   public void setupTags() throws IOException {
      goodETag = HttpUtils.md5(TEST_STRING);
      badETag = HttpUtils.md5("alf");
   }

   /**
    * There are a lot of retries here mainly from experience running inside amazon EC2.
    */
   @BeforeSuite
   public void setUpResourcesForAllThreads(ITestContext testContext) throws Exception {
      createContainersSharedByAllThreads(getCloudResources(testContext), testContext);
   }

   @SuppressWarnings("unchecked")
   private BlobStoreContext<S, C, M, B> getCloudResources(ITestContext testContext)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            Exception {
      String initializerClass = checkNotNull(System.getProperty("jclouds.test.initializer"),
               "jclouds.test.initializer");
      Class<BaseTestInitializer<S, C, M, B>> clazz = (Class<BaseTestInitializer<S, C, M, B>>) Class
               .forName(initializerClass);
      BaseTestInitializer<S, C, M, B> initializer = clazz.newInstance();
      return initializer.init(createHttpModule(), testContext);
   }

   protected ExecutorService exec;

   /**
    * we are doing this at a class level, as the context.getApi() object is going to be shared for
    * all methods in the class. We don't want to do this for group, as some test classes may want to
    * have a different implementation of context.getApi(). For example, one class may want
    * non-blocking i/o and another class google appengine.
    */
   @BeforeClass(groups = { "integration", "live" })
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      context = getCloudResources(testContext);
      exec = Executors.newCachedThreadPool();
   }

   @AfterClass(groups = { "integration", "live" })
   protected void tearDownClient() throws Exception {
      exec.shutdown();
      exec.awaitTermination(60, TimeUnit.SECONDS);
      context.close();
   }

   private static volatile boolean initialized = false;

   protected void createContainersSharedByAllThreads(BlobStoreContext<S, C, M, B> context,
            ITestContext testContext) throws Exception {
      while (!initialized) {
         synchronized (BaseBlobStoreIntegrationTest.class) {
            if (!initialized) {
               deleteEverything(context);
               for (; containerIndex.get() < containerCount; containerIndex.incrementAndGet()) {
                  String containerName = CONTAINER_PREFIX + containerIndex;
                  if (blackListContainers.contains(containerName)) {
                     containerCount++;
                  } else {
                     try {
                        createContainerAndEnsureEmpty(context, containerName);
                        containerJsr330.put(containerName);
                     } catch (Throwable e) {
                        // throw away the container and try again with the next index
                        deleteContainerOrWarnIfUnable(context, containerName);
                        containerCount++;
                     }
                  }
               }
               testContext.setAttribute("containerJsr330", containerJsr330);
               System.err.printf("*** containers to test: %s%n", containerJsr330);
               // careful not to keep too many files open
               context.close();
               initialized = true;
            }
         }
      }
   }

   private static void deleteContainerOrWarnIfUnable(BlobStoreContext<?, ?, ?, ?> context,
            String containerName) {
      try {
         deleteContainer(context, containerName);
      } catch (Throwable ex) {
         System.err.printf("unable to delete container %s, ignoring...%n", containerName);
         blackListContainers.add(containerName);
      }
   }

   private static final List<String> blackListContainers = Lists.newArrayList();

   /**
    * Tries to delete all containers, runs up to two times
    */
   @SuppressWarnings("unchecked")
   protected static void deleteEverything(final BlobStoreContext<?, ?, ?, ?> context)
            throws Exception {
      try {
         for (int i = 0; i < 2; i++) {
            Iterable<ContainerMetadata> testContainers = Iterables.filter(
                     (List<ContainerMetadata>) context.getApi().listContainers(),
                     new Predicate<ContainerMetadata>() {
                        public boolean apply(ContainerMetadata input) {
                           return input.getName().startsWith(CONTAINER_PREFIX.toLowerCase());
                        }
                     });
            if (testContainers.iterator().hasNext()) {
               ExecutorService executor = Executors.newCachedThreadPool();
               for (final ContainerMetadata metaDatum : testContainers) {
                  executor.execute(new Runnable() {
                     public void run() {
                        deleteContainerOrWarnIfUnable(context, metaDatum.getName());
                     }
                  });
               }
               executor.shutdown();
               executor.awaitTermination(60, TimeUnit.SECONDS);
            }
         } // try twice
      } catch (CancellationException e) {
         throw e;
      }
   }

   /**
    * two test groups integration and live.
    */

   public static boolean SANITY_CHECK_RETURNED_BUCKET_NAME = false;

   /**
    * Due to eventual consistency, container commands may not return correctly immediately. Hence,
    * we will try up to the inconsistency window to see if the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) throws InterruptedException {
      AssertionError error = null;
      for (int i = 0; i < 10; i++) {
         try {
            assertion.run();
            return;
         } catch (AssertionError e) {
            error = e;
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 10);
      }
      if (error != null)
         throw error;
   }

   protected static void createContainerAndEnsureEmpty(BlobStoreContext<?, ?, ?, ?> context,
            final String containerName) throws InterruptedException, ExecutionException,
            TimeoutException {
      attemptToCreateContainerButRetryOn409(context, containerName);
      emptyContainer(context, containerName);
   }

   /**
    * 409 could be a resolvable conflict, ex. container delete in progress. FIXME Comment this
    * 
    * @param context
    *           .getApi()
    * @param containerName
    * @throws InterruptedException
    * @throws TimeoutException
    * @throws ExecutionException
    */
   private static void attemptToCreateContainerButRetryOn409(
            BlobStoreContext<? extends BlobStore<?, ?, ?>, ?, ?, ?> context,
            final String containerName) throws InterruptedException, TimeoutException,
            ExecutionException {
      ExecutionException error = null;
      OUTER: for (int i = 0; i < 10; i++) {
         try {
            context.getApi().createContainer(containerName).get(10, TimeUnit.SECONDS);
            break OUTER;
         } catch (ExecutionException e) {
            error = e;
            if (e.getCause() instanceof HttpResponseException) {
               HttpResponseException ex = (HttpResponseException) e.getCause();
               if (ex.getResponse().getStatusCode() != 409) {
                  break OUTER;
               }
            }
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 10);
      }
      if (error != null)
         throw error;
   }

   protected void createContainerAndEnsureEmpty(String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      createContainerAndEnsureEmpty(context, containerName);
   }

   protected void addBlobToContainer(String sourceContainer, String key)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      B sourceObject = context.newBlob(key);
      sourceObject.getMetadata().setContentType("text/xml");
      sourceObject.setData(TEST_STRING);
      addBlobToContainer(sourceContainer, sourceObject);
   }

   protected void addBlobToContainer(String sourceContainer, B object) throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      context.getApi().putBlob(sourceContainer, object).get(10, TimeUnit.SECONDS);
   }

   protected B validateContent(String sourceContainer, String key) throws InterruptedException,
            ExecutionException, TimeoutException, IOException {
      assertEventuallyContainerSize(sourceContainer, 1);
      B newObject = context.getApi().getBlob(sourceContainer, key).get(10, TimeUnit.SECONDS);
      assert newObject != null;
      assertEquals(BlobStoreUtils.getContentAsStringAndClose(newObject), TEST_STRING);
      return newObject;
   }

   protected void assertEventuallyContainerSize(final String containerName, final int count)
            throws InterruptedException {
      assertEventually(new Runnable() {
         public void run() {
            try {
               assertEquals(context.getApi().listBlobs(containerName).get(10, TimeUnit.SECONDS)
                        .size(), count);
            } catch (Exception e) {
               Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
            }
         }
      });
   }

   public String getContainerName() throws InterruptedException, ExecutionException,
            TimeoutException {
      String containerName = containerJsr330.poll(30, TimeUnit.SECONDS);
      assert containerName != null : "unable to get a container for the test";
      if (!emptyContainer(containerName))
         this.createContainerAndEnsureEmpty(containerName);
      return containerName;
   }

   /**
    * requestor will create a container using the name returned from this. This method will take
    * care not to exceed the maximum containers permitted by a service by deleting an existing
    * container first.
    */
   public String getScratchContainerName() throws InterruptedException, ExecutionException,
            TimeoutException {
      return allocateNewContainerName(getContainerName());
   }

   public void returnContainer(final String containerName) throws InterruptedException,
            ExecutionException, TimeoutException {
      if (containerName != null) {
         containerJsr330.add(containerName);
         /*
          * Ensure that any returned container name actually exists on the server. Return of a
          * non-existent container introduces subtle testing bugs, where later unrelated tests will
          * fail.
          * 
          * NOTE: This sanity check should only be run for Stub-based Integration testing -- it will
          * *substantially* slow down tests on a real server over a network.
          */
         if (SANITY_CHECK_RETURNED_BUCKET_NAME) {
            if (!Iterables.any(context.getApi().listContainers(),
                     new Predicate<ContainerMetadata>() {
                        public boolean apply(ContainerMetadata md) {
                           return containerName.equals(md.getName());
                        }
                     })) {
               throw new IllegalStateException(
                        "Test returned the name of a non-existent container: " + containerName);
            }
         }
      }
   }

   /**
    * abandon old container name instead of waiting for the container to be created.
    */
   public void destroyContainer(String scratchContainer) throws InterruptedException,
            ExecutionException, TimeoutException {
      if (scratchContainer != null) {
         recycleContainerAndAddToPool(scratchContainer);
      }
   }

   protected void recycleContainerAndAddToPool(String scratchContainer)
            throws InterruptedException, ExecutionException, TimeoutException {
      String newScratchContainer = recycleContainer(scratchContainer);
      returnContainer(newScratchContainer);
   }

   protected String recycleContainer(final String container) throws InterruptedException,
            ExecutionException, TimeoutException {
      String newScratchContainer = allocateNewContainerName(container);
      createContainerAndEnsureEmpty(newScratchContainer);
      return newScratchContainer;
   }

   private String allocateNewContainerName(final String container) {
      exec.submit(new Runnable() {
         public void run() {
            deleteContainerOrWarnIfUnable(context, container);
         }
      });
      String newScratchContainer = container + containerIndex.incrementAndGet();
      System.err.printf("*** allocated new container %s...%n", container);
      return newScratchContainer;
   }

   protected Module createHttpModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   protected boolean emptyContainer(String name) throws InterruptedException, ExecutionException,
            TimeoutException {
      return emptyContainer(context, name);
   }

   /**
    * Remove any objects in a container, leaving it empty.
    * 
    * @return
    */
   protected static boolean emptyContainer(final BlobStoreContext<?, ?, ?, ?> context,
            final String name) throws InterruptedException, ExecutionException, TimeoutException {
      if (context.getApi().containerExists(name)) {
         // This can fail to be zero length because of stale container lists. Ex.
         // context.getApi().listContainer()
         // could return 9 keys, when there are 10. When all the deletions finish, one entry would
         // be left in this case. Instead of failing, we will attempt this entire container deletion
         // operation multiple times to ensure we can acheive a zero length container.
         assertEventually(new Runnable() {
            public void run() {
               try {
                  Map<String, InputStream> map = context.createInputStreamMap(name);
                  Set<String> keys = map.keySet();
                  if (keys.size() > 0) {
                     map.clear();
                     assertEquals(
                              map.size(),
                              0,
                              String
                                       .format(
                                                "deleting %s, we still have %s left in container %s, using encoding %s",
                                                keys, map.keySet(), name, LOCAL_ENCODING));
                  }
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
         return true;
      }
      return false;
   }

   protected static void deleteContainer(
            final BlobStoreContext<? extends BlobStore<?, ?, ?>, ?, ?, ?> context, final String name)
            throws InterruptedException, ExecutionException, TimeoutException {
      if (context.getApi().containerExists(name)) {
         System.err.printf("*** deleting container %s...%n", name);
         emptyContainer(context, name);
         context.getApi().deleteContainer(name).get(10, TimeUnit.SECONDS);
         assertEventually(new Runnable() {
            public void run() {
               try {
                  assert !context.getApi().containerExists(name) : "container " + name
                           + " still exists";
               } catch (Exception e) {
                  Utils.<RuntimeException> rethrowIfRuntimeOrSameType(e);
               }
            }
         });
      }
   }

}