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
package org.jclouds.openstack.swift.blobstore;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit")
public class SwiftBlobStoreTest {
   @Test
   public void testSplitContainerAndKey() {
      String container = "test-container";
      String key = "key/with/some/slashes/in/it/and/a/trailing/slash/";

      String containerAndKey = container + "/" + key;

      String[] split = SwiftBlobStore.splitContainerAndKey(containerAndKey);
      String actualContainer = split[0];
      String actualKey = split[1];

      assertEquals(actualContainer, container);
      assertEquals(actualKey, key);
   }

   @Test(expectedExceptions = IllegalArgumentException.class,
         expectedExceptionsMessageRegExp = "No / separator found in \"not-a-container-and-key\"")
   public void testSplitContainerAndKeyWithNoSeparator() {
      SwiftBlobStore.splitContainerAndKey("not-a-container-and-key");
   }
}
