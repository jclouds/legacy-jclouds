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
package org.jclouds.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import org.testng.annotations.Test;

import com.google.common.base.Splitter;

/**
 * Tests parsing of a request
 * 
 * @author Adrian Cole
 */
@Test(testName = "blobstore.BlobStoreContextFactoryTest")
public class BlobStoreContextFactoryTest {

   public void test() throws IOException {
      URI blobStore = URI.create("provider://identity:key@container/path");
      assertEquals(blobStore.getScheme(), "provider");
      Iterator<String> identityKey = Splitter.on(":").split(
               checkNotNull(blobStore.getUserInfo(), "userInfo")).iterator();
      assertEquals(identityKey.next(), "identity");
      assertEquals(identityKey.next(), "key");
      assertEquals(blobStore.getHost(), "container");
      assertEquals(blobStore.getPath(), "/path");
   }

   public void testNoPassword() throws IOException {
      URI blobStore = URI.create("provider://identity@container/path");
      assertEquals(blobStore.getScheme(), "provider");
      Iterator<String> identityKey = Splitter.on(":").split(
               checkNotNull(blobStore.getUserInfo(), "userInfo")).iterator();
      assertEquals(identityKey.next(), "identity");
      assertEquals(blobStore.getHost(), "container");
      assertEquals(blobStore.getPath(), "/path");
   }

}
