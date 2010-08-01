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
package org.jclouds.atmosonline.saas;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AtmosStorageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "emcsaas.AtmosStorageClientLiveTest")
public class AtmosStorageClientLiveTest {

   private static final class HeadMatches implements Runnable {
      private final AtmosStorageClient connection;
      private final String name;
      private final String metadataValue;

      private HeadMatches(AtmosStorageClient connection, String name, String metadataValue) {
         this.connection = connection;
         this.name = name;
         this.metadataValue = metadataValue;
      }

      public void run() {
         try {
            verifyHeadObject(connection, name, metadataValue);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private static final class ObjectMatches implements Runnable {
      private final AtmosStorageClient connection;
      private final String name;
      private final String metadataValue;
      private final String compare;

      private ObjectMatches(AtmosStorageClient connection, String name, String metadataValue, String compare) {
         this.connection = connection;
         this.name = name;
         this.metadataValue = metadataValue;
         this.compare = compare;
      }

      public void run() {
         try {
            verifyObject(connection, name, compare, metadataValue);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private static final int INCONSISTENCY_WINDOW = 5000;
   protected AtmosStorageClient connection;
   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX + "live";

   URI container1;
   URI container2;
   private BlobStoreContext context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException, IOException {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");
      context = new BlobStoreContextFactory().createContext("atmosonline", identity, credential, ImmutableSet
               .<Module> of(new Log4JLoggingModule()));
      RestContext<AtmosStorageClient, AtmosStorageAsyncClient> restContext = context.getProviderSpecificContext();
      connection = restContext.getApi();
      for (DirectoryEntry entry : connection.listDirectories()) {
         if (entry.getObjectName().startsWith(containerPrefix)) {
            context.getBlobStore().deleteContainer(entry.getObjectName());
         }
      }
   }

   @Test
   public void testListDirectorys() throws Exception {
      BoundedSet<? extends DirectoryEntry> response = connection.listDirectories();
      assert null != response;
   }

   String privateDirectory;
   String publicDirectory;
   String identity;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateDirectory() throws Exception {
      boolean created = false;
      while (!created) {
         privateDirectory = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createDirectory(privateDirectory) != null;
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      BoundedSet<? extends DirectoryEntry> response = connection.listDirectories();
      for (DirectoryEntry id : response) {
         BoundedSet<? extends DirectoryEntry> r2 = connection.listDirectory(id.getObjectName());
         assert r2 != null;
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateDirectory" })
   public void testListOptions() throws Exception {
      createOrReplaceObject("object2", "here is my data!", "meta-value1");
      createOrReplaceObject("object3", "here is my data!", "meta-value1");
      createOrReplaceObject("object4", "here is my data!", "meta-value1");
      BoundedSet<? extends DirectoryEntry> r2 = connection
               .listDirectory(privateDirectory, ListOptions.Builder.limit(1));
      assertEquals(r2.size(), 1);
      assert r2.getToken() != null;
      assertEquals(Iterables.getLast(Sets.newTreeSet(r2)).getObjectName(), "object2");
      r2 = connection.listDirectory(privateDirectory, ListOptions.Builder.token(r2.getToken()));
      assertEquals(r2.size(), 2);
      assert r2.getToken() == null;
      assertEquals(Iterables.getLast(Sets.newTreeSet(r2)).getObjectName(), "object4");
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testListOptions" })
   public void testFileOperations() throws Exception {
      // create the object
      System.err.printf("creating%n");
      createOrReplaceObject("object", "here is my data!", "meta-value1");
      assertEventuallyObjectMatches("object", "here is my data!", "meta-value1");
      assertEventuallyHeadMatches("object", "meta-value1");

      // try overwriting the object
      System.err.printf("overwriting%n");
      createOrReplaceObject("object", "here is my data?", "meta-value?");
      assertEventuallyObjectMatches("object", "here is my data?", "meta-value?");

      // loop to gather metrics
      for (boolean stream : new Boolean[] { true, false }) {
         for (int i = 0; i < 10; i++) {
            System.err.printf("upload/delete/create attempt %d type %s%n", i + 1, stream ? "stream" : "string");
            // try updating
            createOrUpdateWithErrorLoop(stream, "there is my data", "2");

            deleteConfirmed(privateDirectory + "/object");
            // now create
            createOrUpdateWithErrorLoop(stream, "where is my data", "3");

         }
      }
   }

   private void createOrUpdateWithErrorLoop(boolean stream, String data, String metadataValue) throws Exception {
      createOrReplaceObject("object", makeData(data, stream), metadataValue);
      assertEventuallyObjectMatches("object", data, metadataValue);
   }

   Object makeData(String in, boolean stream) {
      return stream ? Utils.toInputStream(in) : in;
   }

   private void createOrReplaceObject(String name, Object data, String metadataValue) throws Exception {
      // Test PUT with string data, ETag hash, and a piece of metadata
      AtmosObject object = connection.newObject();
      object.getContentMetadata().setName(name);
      object.setPayload(Payloads.newPayload(data));
      object.getContentMetadata().setContentLength(16l);
      Payloads.calculateMD5(object);
      object.getContentMetadata().setContentType("text/plain");
      object.getUserMetadata().getMetadata().put("Metadata", metadataValue);
      replaceObject(object);
   }

   /**
    * Due to eventual consistency, container commands may not return correctly immediately. Hence,
    * we will try up to the inconsistency window to see if the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) throws InterruptedException {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System.currentTimeMillis() - start,
                        assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 30);
      }
      if (error != null)
         throw error;

   }

   protected void assertEventuallyObjectMatches(final String name, final String compare, final String metadataValue)
            throws InterruptedException {
      assertEventually(new ObjectMatches(connection, privateDirectory + "/" + name, metadataValue, compare));
   }

   protected void assertEventuallyHeadMatches(final String name, final String metadataValue)
            throws InterruptedException {
      assertEventually(new HeadMatches(connection, privateDirectory + "/" + name, metadataValue));
   }

   private static void verifyHeadObject(AtmosStorageClient connection, String path, String metadataValue)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      AtmosObject getBlob = connection.headFile(path);
      assertEquals(Utils.toStringAndClose(getBlob.getPayload().getInput()), "");
      verifyMetadata(metadataValue, getBlob);
   }

   private static void verifyObject(AtmosStorageClient connection, String path, String compare, String metadataValue)
            throws InterruptedException, ExecutionException, TimeoutException, IOException {
      AtmosObject getBlob = connection.readFile(path);
      assertEquals(Utils.toStringAndClose(getBlob.getPayload().getInput()), compare);
      verifyMetadata(metadataValue, getBlob);
   }

   private static void verifyMetadata(String metadataValue, AtmosObject getBlob) {
      assertEquals(getBlob.getContentMetadata().getContentLength(), new Long(16));
      assert getBlob.getContentMetadata().getContentType().startsWith("text/plain");
      assertEquals(getBlob.getUserMetadata().getMetadata().get("Metadata"), metadataValue);
      SystemMetadata md = getBlob.getSystemMetadata();
      assertEquals(md.getSize(), 16);
      assert md.getGroupID() != null;
      assertEquals(md.getHardLinkCount(), 1);
      assert md.getInceptionTime() != null;
      assert md.getLastAccessTime() != null;
      assert md.getLastMetadataModification() != null;
      assert md.getLastUserDataModification() != null;
      assert md.getObjectID() != null;
      assertEquals(md.getObjectName(), "object");
      assert md.getPolicyName() != null;
      assertEquals(md.getType(), FileType.REGULAR);
      assert md.getUserID() != null;

      try {
         Utils.toStringAndClose(URI.create(
                  "http://accesspoint.emccis.com/rest/objects/" + getBlob.getSystemMetadata().getObjectID()).toURL()
                  .openStream());
         assert false : "shouldn't have worked, since it is private";
      } catch (IOException e) {

      }
   }

   private void replaceObject(AtmosObject object) throws Exception {
      alwaysDeleteFirstReplaceStrategy(object);
      // retryAndCheckSystemMetadataAndPutIfPresentReplaceStrategy(object); // HEAD 200 followed by
      // PUT = 404!
   }

   private void alwaysDeleteFirstReplaceStrategy(AtmosObject object) throws Exception {
      deleteConfirmed(privateDirectory + "/" + object.getContentMetadata().getName());
      long time = System.currentTimeMillis();
      try {
         connection.createFile(privateDirectory, object);
         System.err.printf("%s %s; %dms%n", "created", object.getPayload() instanceof InputStreamPayload ? "stream"
                  : "string", System.currentTimeMillis() - time);
      } catch (Exception e) {
         String message = Throwables.getRootCause(e).getMessage();
         System.err.printf("failure %s %s; %dms: [%s]%n", "creating",
                  object.getPayload() instanceof InputStreamPayload ? "stream" : "string", System.currentTimeMillis()
                           - time, message);
         throw e;
      }
   }

   private void deleteConfirmed(final String path) throws InterruptedException, ExecutionException, TimeoutException {
      long time = System.currentTimeMillis();
      deleteConsistencyAware(path);
      System.err.printf("confirmed deletion after %dms%n", System.currentTimeMillis() - time);
   }

   protected void deleteImmediateAndVerifyWithHead(final String path) throws InterruptedException, ExecutionException,
            TimeoutException {
      try {
         connection.deletePath(path);
      } catch (KeyNotFoundException ex) {
      }
      assert !connection.pathExists(path);
      System.err.printf("path %s doesn't exist%n", path);
      assert !connection.pathExists(path);
      System.err.printf("path %s doesn't exist%n", path);

   }

   protected void deleteConsistencyAware(final String path) throws InterruptedException, ExecutionException,
            TimeoutException {
      try {
         connection.deletePath(path);
      } catch (KeyNotFoundException ex) {
      }
      assert Utils.eventuallyTrue(new Supplier<Boolean>() {
         public Boolean get() {
            return !connection.pathExists(path);
         }
      }, INCONSISTENCY_WINDOW);
   }

   protected void retryAndCheckSystemMetadataAndPutIfPresentReplaceStrategy(AtmosObject object) throws Exception {

      int failures = 0;
      while (true) {
         try {
            checkSystemMetadataAndPutIfPresentReplaceStrategy(object);
            break;
         } catch (ExecutionException e1) {// bug
            if (!(e1.getCause() instanceof KeyAlreadyExistsException))
               throw e1;
            else
               failures++;
         }
      }
      if (failures > 0)
         System.err.printf("%d failures create/replacing %s%n", failures,
                  object.getPayload() instanceof InputStreamPayload ? "stream" : "string");
   }

   private void checkSystemMetadataAndPutIfPresentReplaceStrategy(AtmosObject object) throws Exception {
      long time = System.currentTimeMillis();
      boolean update = true;
      try {
         connection.getSystemMetadata(privateDirectory + "/object");
      } catch (KeyNotFoundException ex) {
         update = false;
      }
      try {
         if (update)
            connection.updateFile(privateDirectory, object);
         else
            connection.createFile(privateDirectory, object);
         System.err.printf("%s %s; %dms%n", update ? "updated" : "created",
                  object.getPayload() instanceof InputStreamPayload ? "stream" : "string", System.currentTimeMillis()
                           - time);
      } catch (Exception e) {
         String message = Throwables.getRootCause(e).getMessage();
         System.err.printf("failure %s %s; %dms: [%s]%n", update ? "updating" : "creating",
                  object.getPayload() instanceof InputStreamPayload ? "stream" : "string", System.currentTimeMillis()
                           - time, message);
         throw e;
      }
   }
}
