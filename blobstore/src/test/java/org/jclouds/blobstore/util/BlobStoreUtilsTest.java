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
package org.jclouds.blobstore.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.blobstore.util.BlobStoreUtils.createParentIfNeededAsync;
import static org.jclouds.blobstore.util.BlobStoreUtils.getNameFor;
import static org.jclouds.reflect.Reflection2.method;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
/**
 * Tests behavior of {@code BlobStoreUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BlobStoreUtilsTest {

   public void testCreateParentIfNeededAsyncNoPath() {
      AsyncBlobStore asyncBlobStore = createMock(AsyncBlobStore.class);
      String container = "container";
      Blob blob = createMock(Blob.class);
      MutableBlobMetadata md = createMock(MutableBlobMetadata.class);

      expect(blob.getMetadata()).andReturn(md).atLeastOnce();
      expect(md.getName()).andReturn("hello").atLeastOnce();

      replay(asyncBlobStore);
      replay(blob);
      replay(md);

      createParentIfNeededAsync(asyncBlobStore, container, blob);

      verify(asyncBlobStore);
      verify(blob);
      verify(md);
   }

   public void testCreateParentIfNeededAsyncSinglePath() {
      AsyncBlobStore asyncBlobStore = createMock(AsyncBlobStore.class);
      String container = "container";
      Blob blob = createMock(Blob.class);
      MutableBlobMetadata md = createMock(MutableBlobMetadata.class);

      expect(blob.getMetadata()).andReturn(md).atLeastOnce();
      expect(md.getName()).andReturn("rootpath/hello").atLeastOnce();
      expect(asyncBlobStore.createDirectory("container", "rootpath")).andReturn(null);

      replay(asyncBlobStore);
      replay(blob);
      replay(md);

      createParentIfNeededAsync(asyncBlobStore, container, blob);

      verify(asyncBlobStore);
      verify(blob);
      verify(md);
   }

   public void testCreateParentIfNeededAsyncNestedPath() {
      AsyncBlobStore asyncBlobStore = createMock(AsyncBlobStore.class);
      String container = "container";
      Blob blob = createMock(Blob.class);
      MutableBlobMetadata md = createMock(MutableBlobMetadata.class);

      expect(blob.getMetadata()).andReturn(md).atLeastOnce();
      expect(md.getName()).andReturn("rootpath/subpath/hello").atLeastOnce();
      expect(asyncBlobStore.createDirectory("container", "rootpath/subpath")).andReturn(null);

      replay(asyncBlobStore);
      replay(blob);
      replay(md);

      createParentIfNeededAsync(asyncBlobStore, container, blob);

      verify(asyncBlobStore);
      verify(blob);
      verify(md);
   }

   public void testGetKeyForAzureS3AndRackspace() {
      GeneratedHttpRequest request = requestForEndpointAndArgs(
            "https://jclouds.blob.core.windows.net/adriancole-blobstore0/five",
            ImmutableList.<Object> of("adriancole-blobstore0", "five"));
      assertEquals(getNameFor(request), "five");
   }

   public void testGetKeyForAtmos() {
      GeneratedHttpRequest request = requestForEndpointAndArgs(
            "https://storage4.clouddrive.com/v1/MossoCloudFS_dc1f419c-5059-4c87-a389-3f2e33a77b22/adriancole-blobstore0/four",
            ImmutableList.<Object> of("adriancole-blobstore0/four"));
      assertEquals(getNameFor(request), "four");
   }

   GeneratedHttpRequest requestForEndpointAndArgs(String endpoint, List<Object> args) {
      try {
         Invocation invocation = Invocation.create(method(String.class, "toString"), args);
         return GeneratedHttpRequest.builder().method("POST").endpoint(URI.create(endpoint)).invocation(invocation)
               .build();
      } catch (SecurityException e) {
         throw Throwables.propagate(e);
      }
   }
}
