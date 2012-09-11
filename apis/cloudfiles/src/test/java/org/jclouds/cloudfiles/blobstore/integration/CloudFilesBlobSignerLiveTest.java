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
package org.jclouds.cloudfiles.blobstore.integration;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.swift.blobstore.integration.SwiftBlobSignerLiveTest;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Adrian Cole
 */
@Test(groups = {"live"})
public class CloudFilesBlobSignerLiveTest extends SwiftBlobSignerLiveTest {
   public CloudFilesBlobSignerLiveTest() {
      provider = "cloudfiles";
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
      } finally {
         returnContainer(container);
      }
   }
}
