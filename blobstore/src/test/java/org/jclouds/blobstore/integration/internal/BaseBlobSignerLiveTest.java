/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.blobstore.integration.internal;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all signature commands.
 * <p/>
 * Each test uses a different container name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" })
public class BaseBlobSignerLiveTest extends BaseBlobStoreIntegrationTest {

   @Test
   public void testSignRemoveUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = context.getBlobStore().newBlob(name);
      blob.setPayload(text);
      blob.getPayload().getContentMetadata().setContentType("text/plain");
      String container = getContainerName();
      try {
         context.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = context.getSigner().signRemoveBlob(container, name);
         assertEquals(request.getFilters().size(), 0);
         context.utils().http().invoke(request);
         assert !context.getBlobStore().blobExists(container, name);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignGetUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = context.getBlobStore().newBlob(name);
      blob.setPayload(text);
      blob.getPayload().getContentMetadata().setContentType("text/plain");
      String container = getContainerName();
      try {
         context.getBlobStore().putBlob(container, blob);
         assertConsistencyAwareContainerSize(container, 1);
         HttpRequest request = context.getSigner().signGetBlob(container, name);
         assertEquals(request.getFilters().size(), 0);
         assertEquals(Strings2.toStringAndClose(context.utils().http().invoke(request).getPayload().getInput()), text);
      } finally {
         returnContainer(container);
      }
   }

   @Test
   public void testSignPutUrl() throws Exception {
      String name = "hello";
      String text = "fooooooooooooooooooooooo";

      Blob blob = context.getBlobStore().newBlob(name);
      blob.setPayload(text);
      blob.getPayload().getContentMetadata().setContentType("text/plain");
      String container = getContainerName();
      try {
         HttpRequest request = context.getSigner().signPutBlob(container, blob);
         assertEquals(request.getFilters().size(), 0);
         Strings2.toStringAndClose(context.utils().http().invoke(request).getPayload().getInput());
         assertConsistencyAwareContainerSize(container, 1);
      } finally {
         returnContainer(container);
      }
   }

}