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
package org.jclouds.osgi;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.apis.JcloudsTestComputeApiMetadata;
import org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata;
import org.jclouds.providers.JcloudsTestComputeProviderMetadata;
import org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.osgi.framework.Bundle;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BundlesTest {

   @Test
   public void testInstantiateAvailableClassesWhenAllAssignable() throws ClassNotFoundException {
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata")).andReturn(
            JcloudsTestBlobStoreProviderMetadata.class);
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestComputeProviderMetadata")).andReturn(
            JcloudsTestComputeProviderMetadata.class);
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata")).andReturn(
            JcloudsTestYetAnotherComputeProviderMetadata.class);
      replay(bundle);

      Iterable<ProviderMetadata> providers = Bundles.instantiateAvailableClasses(bundle, ImmutableSet.of(
            "org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata",
            "org.jclouds.providers.JcloudsTestComputeProviderMetadata",
            "org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata"), ProviderMetadata.class);
      assertEquals(providers, ImmutableSet.of(new JcloudsTestBlobStoreProviderMetadata(),
            new JcloudsTestComputeProviderMetadata(), new JcloudsTestYetAnotherComputeProviderMetadata()));

      verify(bundle);
   }

   @Test
   public void testInstantiateAvailableClassesWhenNotAllAssignable() throws ClassNotFoundException {
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata")).andReturn(
            JcloudsTestBlobStoreProviderMetadata.class);
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestComputeApiMetadata")).andReturn(
            JcloudsTestComputeApiMetadata.class);
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata")).andReturn(
            JcloudsTestYetAnotherComputeProviderMetadata.class);
      replay(bundle);

      Iterable<ProviderMetadata> providers = Bundles.instantiateAvailableClasses(bundle, ImmutableSet.of(
            "org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata",
            "org.jclouds.apis.JcloudsTestComputeApiMetadata",
            "org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata"), ProviderMetadata.class);
      assertEquals(providers, ImmutableSet.of(new JcloudsTestBlobStoreProviderMetadata(),
            new JcloudsTestYetAnotherComputeProviderMetadata()));

      verify(bundle);
   }

   @Test
   public void testStringsForResourcesInBundleWhenNoResources() throws Exception {

      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.apis.ApiMetadata")).andReturn(null);
      replay(bundle);

      assertEquals(Bundles.stringsForResourceInBundle("/META-INF/services/org.jclouds.apis.ApiMetadata", bundle),
            ImmutableSet.of());

      verify(bundle);
   }

   @Test
   public void testStringsForResourcesInBundleWhenResourcePresent() throws Exception {

      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.providers.ProviderMetadata"));
      replay(bundle);

      assertEquals(Bundles.stringsForResourceInBundle("/META-INF/services/org.jclouds.providers.ProviderMetadata",
            bundle), ImmutableSet.of("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata",
            "org.jclouds.providers.JcloudsTestComputeProviderMetadata",
            "org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata"));

      verify(bundle);
   }
}
