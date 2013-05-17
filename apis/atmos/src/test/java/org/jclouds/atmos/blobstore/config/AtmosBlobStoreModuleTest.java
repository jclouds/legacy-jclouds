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
package org.jclouds.atmos.blobstore.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.atmos.AtmosApiMetadata;
import org.jclouds.atmos.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AtmosBlobStoreModuleTest {

   Injector createInjector() {
      return ContextBuilder
            .newBuilder(new AtmosApiMetadata())
            .credentials("uid", "key")
            .modules(
                  ImmutableSet.<Module> of(new MockModule(),new NullLoggingModule()))
            .buildInjector();
   }

   @Test
   void testContextImpl() {

      Injector injector = createInjector();
      BlobStoreContext handler = injector.getInstance(BlobStoreContext.class);
      assertEquals(handler.getClass(), BlobStoreContextImpl.class);
      ContainsValueInListStrategy valueList = injector
               .getInstance(ContainsValueInListStrategy.class);

      assertEquals(valueList.getClass(), FindMD5InUserMetadata.class);
   }

}
