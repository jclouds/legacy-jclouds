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
package org.jclouds.blobstore.util;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.blobstore.util.internal.BlobStoreUtilsImpl;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BlobStoreUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "blobstore.BlobStoreUtilsTest")
public class BlobStoreUtilsTest {

   public void testGetKeyForAzureS3AndRackspace() {

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);

      HttpResponse from = createMock(HttpResponse.class);
      expect(request.getEndpoint()).andReturn(
               URI.create("https://jclouds.blob.core.windows.net/adriancole-blobstore0/five"));
      expect(request.getArgs()).andReturn(new Object[] { "adriancole-blobstore0", "five" }).atLeastOnce();

      replay(request);
      replay(from);

      assertEquals(BlobStoreUtilsImpl.getKeyFor(request, from), "five");
   }

   public void testGetKeyForAtmos() {

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);

      HttpResponse from = createMock(HttpResponse.class);
      expect(request.getEndpoint()).andReturn(
               URI.create("https://storage4.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22/adriancole-blobstore0/four"));
      expect(request.getArgs()).andReturn(new Object[] { "adriancole-blobstore0/four" }).atLeastOnce();

      replay(request);
      replay(from);

      assertEquals(BlobStoreUtilsImpl.getKeyFor(request, from), "four");
   }
   public void testGetContainer() {
      String container = BlobStoreUtilsImpl.parseContainerFromPath("foo");
      assertEquals(container, "foo");
      container = BlobStoreUtilsImpl.parseContainerFromPath("foo/");
      assertEquals(container, "foo");
      container = BlobStoreUtilsImpl.parseContainerFromPath("foo/bar");
      assertEquals(container, "foo");
   }

   public void testGetPrefix() {
      String prefix = BlobStoreUtilsImpl.parsePrefixFromPath("foo");
      assertEquals(prefix, null);
      prefix = BlobStoreUtilsImpl.parsePrefixFromPath("foo/");
      assertEquals(prefix, null);
      prefix = BlobStoreUtilsImpl.parsePrefixFromPath("foo/bar");
      assertEquals(prefix, "bar");
   }

}
