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
package org.jclouds.blobstore.internal;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * Tests retry logic.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BaseBlobMapTest {

   BlobStoreContext context;

   InputStreamMapImpl map;

   @BeforeClass
   void addDefaultObjectsSoThatTestsWillPass() {
      context =  ContextBuilder.newBuilder("transient").build(BlobStoreContext.class);
      map = (InputStreamMapImpl) context.createInputStreamMap("test");
   }

   public void testTypes() {
      TypeLiteral<Map<String, Map<String, Blob>>> type0 = new TypeLiteral<Map<String, Map<String, Blob>>>() {
      };
      TypeLiteral<?> type1 = TypeLiteral.get(Types.newParameterizedType(Map.class, String.class,
            Types.newParameterizedType(Map.class, String.class, Blob.class)));
      assertEquals(type0, type1);

   }

}
