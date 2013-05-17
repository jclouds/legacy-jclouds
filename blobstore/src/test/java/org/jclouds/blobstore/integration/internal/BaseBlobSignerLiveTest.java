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

import static org.jclouds.blobstore.options.GetOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Strings2;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all signature commands.
 * <p/>
 * Each test uses a different container name, so it should be perfectly fine to run in parallel.
 *
 * @author Adrian Cole
 */
@Test(groups = {"live"})
public class BaseBlobSignerLiveTest extends BaseBlobStoreIntegrationTest {

   @Test
   public void testSignGetUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name);
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toString(view.utils().http().invoke(request).getPayload()), text);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrlOptions() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name, range(0, 1));
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toString(view.utils().http().invoke(request).getPayload()), "fo");
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrlWithTime() throws InterruptedException, IOException {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signGetBlob(container, name, 3 /* seconds */);
         
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toString(view.utils().http().invoke(request).getPayload()), text);

         TimeUnit.SECONDS.sleep(4);
         try {
            Strings2.toString(view.utils().http().invoke(request).getPayload());
            fail("Temporary URL did not expire as expected");
         } catch (AuthorizationException expected) {
         }
      } catch (UnsupportedOperationException ignore) {
         throw new SkipException("signGetUrl with a time limit is not supported on " + provider);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignPutUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         HttpRequest request = view.getSigner().signPutBlob(container, blob);
         assertEquals(request.getFilters().size(), 0);
         Strings2.toString(view.utils().http().invoke(request).getPayload());
         assertConsistencyAwareContainerSize(container, 1);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignPutUrlWithTime() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         HttpRequest request = view.getSigner().signPutBlob(container, blob, 3 /* seconds */);
         assertEquals(request.getFilters().size(), 0);

         Strings2.toString(view.utils().http().invoke(request).getPayload());
         assertConsistencyAwareContainerSize(container, 1);

         view.getBlobStore().removeBlob(container, name);
         assertConsistencyAwareContainerSize(container, 0);

         TimeUnit.SECONDS.sleep(4);
         try {
            Strings2.toString(view.utils().http().invoke(request).getPayload());
            fail("Temporary URL did not expire as expected");
         } catch (AuthorizationException expected) {
         }
      } catch (UnsupportedOperationException ignore) {
         throw new SkipException("signPutUrl with a time limit is not supported on " + provider);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignRemoveUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = view.getBlobStore().blobBuilder(name).payload(text).contentType("text/plain").build();
      String container = getContainerName();
      try {
         view.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = view.getSigner().signRemoveBlob(container, name);
         assertEquals(request.getFilters().size(), 0);
         view.utils().http().invoke(request);
         assert !view.getBlobStore().blobExists(container, name);
      } finally {
         returnContainer(container);
      }
   }
}
