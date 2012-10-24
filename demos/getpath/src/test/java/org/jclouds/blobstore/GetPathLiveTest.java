/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class GetPathLiveTest {

   public static final String PROPERTY_GETPATH_CONTAINER = "jclouds.getpath.container";
   public static final String PROPERTY_GETPATH_PATH = "jclouds.getpath.path";

   private Set<BlobStoreContext> contexts;
   private String container;
   private String path;

   private String tmpDirectory;

   protected static final String XML_STRING_FORMAT = "<apples><apple name=\"%s\"></apple> </apples>";

   protected Map<String, String> fiveStrings = ImmutableMap.of("one.txt", String.format(
            XML_STRING_FORMAT, "apple"), "two.txt", String.format(XML_STRING_FORMAT, "bear"),
            "three.txt", String.format(XML_STRING_FORMAT, "candy"), "four.txt", String.format(
                     XML_STRING_FORMAT, "dogma"), "five.txt", String.format(XML_STRING_FORMAT,
                     "emma"));

   List<String> urisToTest = Lists.newArrayList();

   @BeforeClass(groups = { "integration", "live" })
   void clearAndCreateContainers() throws InterruptedException, ExecutionException,
            TimeoutException {
      container = checkNotNull(System.getProperty(PROPERTY_GETPATH_CONTAINER));
      path = checkNotNull(System.getProperty(PROPERTY_GETPATH_PATH));

      contexts = Sets.newLinkedHashSet();
      BlobStoreContextFactory factory = new BlobStoreContextFactory();
      for (String provider : new String[] { "s3", "cloudfiles", "azureblob" }) {
         String identity = checkNotNull(System.getProperty("test.identity." + provider),
                  "test.identity." + provider);
         String credential = checkNotNull(
                  System.getProperty("test.credential." + provider),
                  "test.credential." + provider);

         contexts.add(factory.createContext(provider, identity, credential, ImmutableSet
                  .of(new Log4JLoggingModule())));

         urisToTest.add(String.format("blobstore://%s:%s@%s/%s/%s", identity, credential,
                  provider, container, path));

      }

      boolean deleted = false;
      for (BlobStoreContext context : contexts) {
         if (context.getBlobStore().containerExists(container)) {
            System.err.printf("deleting container %s at %s%n", container, context
                     .getProviderSpecificContext().getEndpoint());
            context.getBlobStore().deleteContainer(container);
            deleted = true;
         }
      }
      if (deleted) {
         System.err.println("sleeping 30 seconds to allow containers to clear");
         Thread.sleep(30000);
      }
      for (BlobStoreContext context : contexts) {
         System.err.printf("creating container %s at %s%n", container, context
                  .getProviderSpecificContext().getEndpoint());
         context.getBlobStore().createContainerInLocation(null, container);
      }
      if (deleted) {
         System.err.println("sleeping 5 seconds to allow containers to create");
         Thread.sleep(30000);
      }
      for (BlobStoreContext context : contexts) {
         System.err.printf("creating directory %s in container %s at %s%n", container, path,
                  context.getProviderSpecificContext().getEndpoint());
         context.getBlobStore().createDirectory(container, path);
      }
   }

   @BeforeClass(dependsOnMethods = "clearAndCreateContainers", groups = { "integration", "live" })
   protected void addFiles() {
      for (BlobStoreContext context : contexts) {
         System.err.printf("adding files to container %s at %s%n", container, context
                  .getProviderSpecificContext().getEndpoint());
         context.createInputStreamMap(container + "/" + path).putAllStrings(fiveStrings);
      }
   }

   @BeforeClass(groups = { "integration", "live" })
   @Parameters( { "basedir" })
   protected void setUpTempDir(@Optional String basedir) throws InterruptedException,
            ExecutionException, FileNotFoundException, IOException, TimeoutException {
      if (basedir == null) {
         basedir = System.getProperty("java.io.tmpdir");
      }
      tmpDirectory = basedir + File.separator + "target" + File.separator + "testFiles"
               + File.separator + getClass().getSimpleName();
      new File(tmpDirectory).mkdirs();
   }

   @Test
   public void testAllContainers() throws IOException, InterruptedException {

      for (String uriKey : urisToTest) {
         System.out.println("storing at context: " + uriKey);
         new File(tmpDirectory).delete();
         new File(tmpDirectory).mkdirs();
         GetPath.main(uriKey, tmpDirectory);
         for (Entry<String, String> entry : fiveStrings.entrySet()) {
            assertEquals(Files.toString(new File(tmpDirectory, entry.getKey()),
                  Charsets.UTF_8), entry.getValue());
         }
      }

   }

   @AfterTest
   public void closeContexts() {
      for (BlobStoreContext context : contexts) {
         context.close();
      }
   }
}
