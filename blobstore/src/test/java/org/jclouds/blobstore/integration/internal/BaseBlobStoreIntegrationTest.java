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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagateIfPossible;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.BaseViewLiveTest;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.domain.Location;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Module;

public class BaseBlobStoreIntegrationTest extends BaseViewLiveTest<BlobStoreContext> {
   
   protected static final String LOCAL_ENCODING = System.getProperty("file.encoding");
   protected static final String XML_STRING_FORMAT = "<apples><apple name=\"%s\"></apple> </apples>";
   protected static final String TEST_STRING = String.format(XML_STRING_FORMAT, "apple");

   protected Map<String, String> fiveStrings = ImmutableMap.of("one", String.format(XML_STRING_FORMAT, "apple"), "two",
         String.format(XML_STRING_FORMAT, "bear"), "three", String.format(XML_STRING_FORMAT, "candy"), "four",
         String.format(XML_STRING_FORMAT, "dogma"), "five", String.format(XML_STRING_FORMAT, "emma"));

   protected Map<String, String> fiveStringsUnderPath = ImmutableMap.of("path/1",
         String.format(XML_STRING_FORMAT, "apple"), "path/2", String.format(XML_STRING_FORMAT, "bear"), "path/3",
         String.format(XML_STRING_FORMAT, "candy"), "path/4", String.format(XML_STRING_FORMAT, "dogma"), "path/5",
         String.format(XML_STRING_FORMAT, "emma"));

   public static long INCONSISTENCY_WINDOW = 10000;
   protected static volatile AtomicInteger containerIndex = new AtomicInteger(0);

   protected static volatile int containerCount = Integer.parseInt(System.getProperty("test.blobstore.container-count",
         "10"));
   public static final String CONTAINER_PREFIX = (System.getProperty("user.name") + "-blobstore").toLowerCase();
   /**
    * two test groups integration and live.
    */
   private volatile static BlockingQueue<String> containerNames = new ArrayBlockingQueue<String>(containerCount);

   /**
    * There are a lot of retries here mainly from experience running inside amazon EC2.
    */
   @BeforeSuite
   public void setUpResourcesForAllThreads(ITestContext testContext) throws Exception {
      setupContext();
      createContainersSharedByAllThreads(view, testContext);
      view.close();
      view = null;
   }
   
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), createHttpModule());
   }

   protected ListeningExecutorService exec;

   /**
    * we are doing this at a class level, as the context.getBlobStore() object is going to be shared
    * for all methods in the class. We don't want to do this for group, as some test classes may
    * want to have a different implementation of context.getBlobStore(). For example, one class may
    * want non-blocking i/o and another class google appengine.
    */
   @BeforeClass(groups = { "integration", "live" }, dependsOnMethods = "setupContext")
   public void setUpResourcesOnThisThread(ITestContext testContext) throws Exception {
      exec = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
   }

   
   @AfterClass(groups = { "integration", "live" })
   @Override
   protected void tearDownContext() {
      if (exec != null) {
         exec.shutdownNow();
      }
      view.close();
   }

   private static volatile boolean initialized = false;

   protected void createContainersSharedByAllThreads(BlobStoreContext context, ITestContext testContext)
         throws Exception {
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
                        if (context.getBlobStore().containerExists(containerName))
                           containerNames.put(containerName);
                        else {
                           deleteContainerOrWarnIfUnable(context, containerName);
                           containerCount++;
                        }
                     } catch (Throwable e) {
                        e.printStackTrace();
                        // throw away the container and try again with the next
                        // index
                        deleteContainerOrWarnIfUnable(context, containerName);
                        containerCount++;
                     }
                  }
               }
               testContext.setAttribute("containerNames", containerNames);
               System.err.printf("*** containers to test: %s%n", containerNames);
               // careful not to keep too many files open
               context.close();
               initialized = true;
            }
         }
      }
   }

   private static void deleteContainerOrWarnIfUnable(BlobStoreContext context, String containerName) {
      try {
         context.getBlobStore().deleteContainer(containerName);
      } catch (Throwable ex) {
         System.err.printf("unable to delete container %s, ignoring...%n", containerName);
         ex.printStackTrace();
         blackListContainers.add(containerName);
      }
   }

   private static final Set<String> blackListContainers = Sets.newHashSet();

   /**
    * Tries to delete all containers, runs up to two times
    */
   protected static void deleteEverything(final BlobStoreContext context) throws Exception {
      try {
         for (int i = 0; i < 2; i++) {
            Iterable<? extends StorageMetadata> testContainers = Iterables.filter(context.getBlobStore().list(),
                  new Predicate<StorageMetadata>() {
                     public boolean apply(StorageMetadata input) {
                        return (input.getType() == StorageType.CONTAINER || input.getType() == StorageType.FOLDER)
                              && input.getName().startsWith(CONTAINER_PREFIX);
                     }
                  });
            for (StorageMetadata container : testContainers) {
               deleteContainerOrWarnIfUnable(context, container.getName());
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
   protected static void assertConsistencyAware(BlobStoreContext context, Runnable assertion)
         throws InterruptedException {
      if (context.getConsistencyModel() == ConsistencyModel.STRICT) {
         assertion.run();
         return;
      } else {

         AssertionError error = null;
         for (int i = 0; i < 30; i++) {
            try {
               assertion.run();
               return;
            } catch (AssertionError e) {
               error = e;
            }
            Thread.sleep(INCONSISTENCY_WINDOW / 30);
         }
         if (error != null)
            throw error;
      }
   }

   protected void assertConsistencyAware(Runnable assertion) throws InterruptedException {
      assertConsistencyAware(view, assertion);
   }

   protected static void createContainerAndEnsureEmpty(BlobStoreContext context, final String containerName)
         throws InterruptedException {
      context.getBlobStore().createContainerInLocation(null, containerName);
      if (context.getConsistencyModel() == ConsistencyModel.EVENTUAL)
         Thread.sleep(1000);
      context.getBlobStore().clearContainer(containerName);
   }

   protected void createContainerAndEnsureEmpty(String containerName) throws InterruptedException {
      createContainerAndEnsureEmpty(view, containerName);
   }

   protected String addBlobToContainer(String sourceContainer, String key) {
      return addBlobToContainer(sourceContainer, key, TEST_STRING, MediaType.TEXT_XML);
   }

   protected String addBlobToContainer(String sourceContainer, String key, String payload, String contentType) {
      Blob sourceObject = view.getBlobStore().blobBuilder(key).payload(payload).contentType(contentType).build();
      return addBlobToContainer(sourceContainer, sourceObject);
   }

   protected void add5BlobsUnderPathAnd5UnderRootToContainer(String sourceContainer) {
      for (Entry<String, String> entry : Iterables.concat(fiveStrings.entrySet(), fiveStringsUnderPath.entrySet())) {
         Blob sourceObject = view.getBlobStore().blobBuilder(entry.getKey()).payload(entry.getValue())
               .contentType("text/xml").build();
         addBlobToContainer(sourceContainer, sourceObject);
      }
   }

   protected String addBlobToContainer(String sourceContainer, Blob object) {
      return view.getBlobStore().putBlob(sourceContainer, object);
   }

   protected <T extends BlobMetadata> T validateMetadata(T md, String container, String name) {
      assertEquals(md.getName(), name);
      assertEquals(md.getContainer(), container);
      assert md.getUri() != null;
      return md;
   }

   protected Blob validateContent(String container, String name) throws InterruptedException {
      assertConsistencyAwareContainerSize(container, 1);
      Blob newObject = view.getBlobStore().getBlob(container, name);
      assert newObject != null;
      validateMetadata(newObject.getMetadata(), container, name);
      try {
         assertEquals(getContentAsStringOrNullAndClose(newObject), TEST_STRING);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return newObject;
   }

   protected void assertConsistencyAwareContainerSize(final String containerName, final int count)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert view.getBlobStore().countBlobs(containerName) == count : String.format(
                     "expected only %d values in %s: %s", count, containerName, ImmutableSet.copyOf(Iterables
                           .transform(view.getBlobStore().list(containerName),
                                 new Function<StorageMetadata, String>() {

                                    public String apply(StorageMetadata from) {
                                       return from.getName();
                                    }

                                 })));
            } catch (Exception e) {
               Throwables.propagateIfPossible(e);
            }
         }
      });
   }

   protected void assertConsistencyAwareBlobExists(final String containerName, final String name)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert view.getBlobStore().blobExists(containerName, name) : String.format(
                     "could not find %s in %s: %s", name, containerName, ImmutableSet.copyOf(Iterables.transform(
                           view.getBlobStore().list(containerName), new Function<StorageMetadata, String>() {

                              public String apply(StorageMetadata from) {
                                 return from.getName();
                              }

                           })));
            } catch (Exception e) {
               Throwables.propagateIfPossible(e);
            }
         }
      });
   }

   protected void assertConsistencyAwareBlobDoesntExist(final String containerName, final String name)
         throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert !view.getBlobStore().blobExists(containerName, name) : String.format("found %s in %s", name,
                     containerName);
            } catch (Exception e) {
               Throwables.propagateIfPossible(e);
            }
         }
      });
   }

   protected void assertConsistencyAwareContainerExists(final String containerName) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert view.getBlobStore().containerExists(containerName) : String.format("container %s doesn't exist", containerName);
            } catch (Exception e) {
               Throwables.propagate(e);
            }
         }
      });
   }

   protected void assertConsistencyAwareContainerInLocation(final String containerName, final Location loc)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               StorageMetadata container = Iterables.find(view.getBlobStore().list(), new Predicate<StorageMetadata>() {

                  @Override
                  public boolean apply(@Nullable StorageMetadata input) {
                     return input.getName().equals(containerName);
                  }

               });
               Location actualLoc = container.getLocation();

               assert loc.equals(actualLoc) : String.format("blob %s, in location %s instead of %s", containerName,
                        actualLoc, loc);
            } catch (Exception e) {
               Throwables.propagate(e);
            }
         }
      });
   }
   
   protected void assertConsistencyAwareBlobExpiryMetadata(final String containerName, final String blobName,
            final Date expectedExpires) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               Blob blob = view.getBlobStore().getBlob(containerName, blobName);
               Date actualExpires = blob.getPayload().getContentMetadata().getExpires();
               assert expectedExpires.equals(actualExpires) : "expires=" + actualExpires + "; expected="
                        + expectedExpires;
            } catch (Exception e) {
               Throwables.propagateIfPossible(e);
            }
         }
      });
   }

   protected void assertConsistencyAwareBlobInLocation(final String containerName, final String blobName, final Location loc)
            throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               Location actualLoc = view.getBlobStore().getBlob(containerName, blobName).getMetadata().getLocation();
               
               assert loc.equals(actualLoc) : String.format(
                     "blob %s in %s, in location %s instead of %s", blobName, containerName, actualLoc, loc);
            } catch (Exception e) {
               Throwables.propagate(e);
            }
         }
      });
   }

   public String getContainerName() throws InterruptedException {
      String containerName = containerNames.poll(30, TimeUnit.SECONDS);
      assert containerName != null : "unable to get a container for the test";
      createContainerAndEnsureEmpty(containerName);
      return containerName;
   }

   /**
    * requestor will create a container using the name returned from this. This method will take
    * care not to exceed the maximum containers permitted by a provider by deleting an existing
    * container first.
    * 
    * @throws InterruptedException
    */
   public String getScratchContainerName() throws InterruptedException {
      return allocateNewContainerName(getContainerName());
   }

   public void returnContainer(final String containerName) {
      if (containerName != null) {
         containerNames.add(containerName);
         /*
          * Ensure that any returned container name actually exists on the server. Return of a
          * non-existent container introduces subtle testing bugs, where later unrelated tests will
          * fail.
          * 
          * NOTE: This sanity check should only be run for Stub-based Integration testing -- it will
          * *substantially* slow down tests on a real server over a network.
          */
         if (SANITY_CHECK_RETURNED_BUCKET_NAME) {
            if (!Iterables.any(view.getBlobStore().list(), new Predicate<StorageMetadata>() {
               public boolean apply(StorageMetadata md) {
                  return containerName.equals(md.getName());
               }
            })) {
               throw new IllegalStateException("Test returned the name of a non-existent container: " + containerName);
            }
         }
      }
   }

   protected void assertNotExists(final String containerName) throws InterruptedException {
      assertConsistencyAware(new Runnable() {
         public void run() {
            try {
               assert !view.getBlobStore().containerExists(containerName) : "container " + containerName
                     + " still exists";
            } catch (Exception e) {
               propagateIfPossible(e);
            }
         }
      });
   }

   /**
    * abandon old container name instead of waiting for the container to be created.
    * 
    * @throws InterruptedException
    */
   public void destroyContainer(String scratchContainer) throws InterruptedException {
      if (scratchContainer != null) {
         recycleContainerAndAddToPool(scratchContainer);
      }
   }

   protected void recycleContainerAndAddToPool(String scratchContainer) throws InterruptedException {
      String newScratchContainer = recycleContainer(scratchContainer);
      returnContainer(newScratchContainer);
   }

   protected String recycleContainer(final String container) throws InterruptedException {
      String newScratchContainer = allocateNewContainerName(container);
      createContainerAndEnsureEmpty(newScratchContainer);
      return newScratchContainer;
   }

   private String allocateNewContainerName(final String container) {
      exec.submit(new Runnable() {
         public void run() {
            deleteContainerOrWarnIfUnable(view, container);
         }
      });
      String newScratchContainer = container + new SecureRandom().nextLong();
      System.err.printf("*** allocated new container %s...%n", container);
      return newScratchContainer;
   }

   public static String getContentAsStringOrNullAndClose(Blob blob) throws IOException {
      checkNotNull(blob, "blob");
      checkNotNull(blob.getPayload(), "blob.payload");
      if (blob.getPayload().getInput() == null)
         return null;
      Object o = blob.getPayload().getInput();
      if (o instanceof InputStream) {
         return Strings2.toStringAndClose((InputStream) o);
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }
   protected Module createHttpModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   @Override
   protected TypeToken<BlobStoreContext> viewType() {
      return typeToken(BlobStoreContext.class);
   }

}
