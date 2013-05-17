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
package org.jclouds.azureblob.blobstore.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.azureblob.AzureBlobProviderMetadata;
import org.jclouds.azureblob.blobstore.strategy.FindMD5InBlobProperties;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AzureBlobStoreModuleTest {

   @Test
   void testContextImpl() {

      Injector injector = ContextBuilder.newBuilder(new AzureBlobProviderMetadata()).credentials("foo", "bar")
            .buildInjector();
      ContainsValueInListStrategy valueList = injector.getInstance(ContainsValueInListStrategy.class);

      assertEquals(valueList.getClass(), FindMD5InBlobProperties.class);
   }

}
