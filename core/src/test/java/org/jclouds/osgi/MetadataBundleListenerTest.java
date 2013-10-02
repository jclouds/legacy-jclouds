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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.JcloudsTestBlobStoreApiMetadata;
import org.jclouds.apis.JcloudsTestComputeApiMetadata;
import org.jclouds.apis.JcloudsTestYetAnotherComputeApiMetadata;
import org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata;
import org.jclouds.providers.JcloudsTestComputeProviderMetadata;
import org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * 
 * @author iocanel
 * 
 */
public class MetadataBundleListenerTest {

   @Test
   public void testSanity() throws MalformedURLException, ClassNotFoundException {
      // We are checking here that the class loader we create and use in this test series is indeed different and
      // isolated from our tests classloader.
      ClassLoader loader = createIsolatedClassLoader();
      assertFalse(ProviderMetadata.class.isAssignableFrom(loader
            .loadClass("org.jclouds.providers.JcloudsTestComputeProviderMetadata")));
   }

   @Test
   public void testGetProviderMetadata() throws Exception {
      MetadataBundleListener listener = new MetadataBundleListener();
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.providers.ProviderMetadata")).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata")).andReturn(
            JcloudsTestBlobStoreProviderMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestComputeProviderMetadata")).andReturn(
            JcloudsTestComputeProviderMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata")).andReturn(
            JcloudsTestYetAnotherComputeProviderMetadata.class).anyTimes();
      replay(bundle);
      List<ProviderMetadata> providerMetadataList = Lists.newArrayList(listener.listProviderMetadata(bundle));
      assertNotNull(providerMetadataList);
      assertEquals(3, providerMetadataList.size());
      assertTrue(providerMetadataList.contains(new JcloudsTestBlobStoreProviderMetadata()));
      assertTrue(providerMetadataList.contains(new JcloudsTestComputeProviderMetadata()));
      assertTrue(providerMetadataList.contains(new JcloudsTestYetAnotherComputeProviderMetadata()));
      verify(bundle);
   }

   @Test
   public void testProviderListener() throws Exception {
      MetadataBundleListener listener = new MetadataBundleListener();
      ProviderListener providerListener = createMock(ProviderListener.class);
      listener.addProviderListener(providerListener);

      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getBundleId()).andReturn(10L).anyTimes();
      expect(bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.providers.ProviderMetadata")).anyTimes();
      expect(bundle.getEntry("/META-INF/services/org.jclouds.apis.ApiMetadata")).andReturn(null).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata")).andReturn(
            JcloudsTestBlobStoreProviderMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestComputeProviderMetadata")).andReturn(
            JcloudsTestComputeProviderMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata")).andReturn(
            JcloudsTestYetAnotherComputeProviderMetadata.class).anyTimes();

      providerListener.added(anyObject(JcloudsTestBlobStoreProviderMetadata.class));
      expectLastCall().times(1);
      providerListener.added(anyObject(JcloudsTestComputeProviderMetadata.class));
      expectLastCall().times(1);
      providerListener.added(anyObject(JcloudsTestYetAnotherComputeProviderMetadata.class));
      expectLastCall().times(1);
      replay(bundle, providerListener);

      BundleEvent event = new BundleEvent(BundleEvent.STARTED, bundle);
      listener.bundleChanged(event);
      verify(bundle, providerListener);
   }

   @Test
   public void testGetProviderMetadataFromMultipleClassLoaders() throws Exception {
      ClassLoader isolatedClassLoader = createIsolatedClassLoader();
      MetadataBundleListener listener = new MetadataBundleListener();
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.providers.ProviderMetadata")).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestBlobStoreProviderMetadata")).andReturn(
            isolatedClassLoader.loadClass(JcloudsTestBlobStoreProviderMetadata.class.getName())).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestComputeProviderMetadata")).andReturn(
            JcloudsTestComputeProviderMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.providers.JcloudsTestYetAnotherComputeProviderMetadata")).andReturn(
            JcloudsTestYetAnotherComputeProviderMetadata.class).anyTimes();

      replay(bundle);
      List<ProviderMetadata> providerMetadataList = Lists.newArrayList(listener.listProviderMetadata(bundle));
      assertNotNull(providerMetadataList);
      assertEquals(2, providerMetadataList.size());
      assertFalse(providerMetadataList.contains(new JcloudsTestBlobStoreProviderMetadata()));
      assertTrue(providerMetadataList.contains(new JcloudsTestComputeProviderMetadata()));
      assertTrue(providerMetadataList.contains(new JcloudsTestYetAnotherComputeProviderMetadata()));
      verify(bundle);
   }

   @Test
   public void testGetApiMetadata() throws Exception {
      MetadataBundleListener listener = new MetadataBundleListener();
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.apis.ApiMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.apis.ApiMetadata")).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestBlobStoreApiMetadata")).andReturn(
            JcloudsTestBlobStoreApiMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestComputeApiMetadata")).andReturn(
            JcloudsTestComputeApiMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestYetAnotherComputeApiMetadata")).andReturn(
            JcloudsTestYetAnotherComputeApiMetadata.class).anyTimes();

      replay(bundle);
      List<ApiMetadata> apiMetadataList = Lists.newArrayList(listener.listApiMetadata(bundle));
      assertNotNull(apiMetadataList);
      assertEquals(3, apiMetadataList.size());
      assertTrue(apiMetadataList.contains(new JcloudsTestBlobStoreApiMetadata()));
      assertTrue(apiMetadataList.contains(new JcloudsTestComputeApiMetadata()));
      assertTrue(apiMetadataList.contains(new JcloudsTestYetAnotherComputeApiMetadata()));
      verify(bundle);
   }

   @Test
   public void testApiListener() throws Exception {
      MetadataBundleListener listener = new MetadataBundleListener();
      ApiListener apiListener = createMock(ApiListener.class);
      listener.addApiListenerListener(apiListener);

      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getBundleId()).andReturn(10L).anyTimes();
      expect(bundle.getEntry("/META-INF/services/org.jclouds.providers.ProviderMetadata")).andReturn(null).anyTimes();
      expect(bundle.getEntry("/META-INF/services/org.jclouds.apis.ApiMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.apis.ApiMetadata")).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestBlobStoreApiMetadata")).andReturn(
            JcloudsTestBlobStoreApiMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestComputeApiMetadata")).andReturn(
            JcloudsTestComputeApiMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestYetAnotherComputeApiMetadata")).andReturn(
            JcloudsTestYetAnotherComputeApiMetadata.class).anyTimes();

      apiListener.added(anyObject(JcloudsTestBlobStoreApiMetadata.class));
      expectLastCall().times(1);
      apiListener.added(anyObject(JcloudsTestBlobStoreApiMetadata.class));
      expectLastCall().times(1);
      apiListener.added(anyObject(JcloudsTestComputeApiMetadata.class));
      expectLastCall().times(1);
      replay(bundle, apiListener);

      BundleEvent event = new BundleEvent(BundleEvent.STARTED, bundle);
      listener.bundleChanged(event);
      verify(bundle, apiListener);
   }

   @Test
   public void testGetApiMetadataFromMultipleClassLoaders() throws Exception {
      ClassLoader isolatedClassLoader = createIsolatedClassLoader();
      MetadataBundleListener listener = new MetadataBundleListener();
      Bundle bundle = createMock(Bundle.class);
      expect(bundle.getEntry("/META-INF/services/org.jclouds.apis.ApiMetadata")).andReturn(
            getClass().getResource("/META-INF/services/org.jclouds.apis.ApiMetadata")).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestBlobStoreApiMetadata")).andReturn(
            isolatedClassLoader.loadClass(JcloudsTestBlobStoreApiMetadata.class.getName())).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestComputeApiMetadata")).andReturn(
            JcloudsTestComputeApiMetadata.class).anyTimes();
      expect(bundle.loadClass("org.jclouds.apis.JcloudsTestYetAnotherComputeApiMetadata")).andReturn(
            JcloudsTestYetAnotherComputeApiMetadata.class).anyTimes();

      replay(bundle);

      List<ApiMetadata> apiMetadataList = Lists.newArrayList(listener.listApiMetadata(bundle));
      assertNotNull(apiMetadataList);
      assertEquals(2, apiMetadataList.size());
      assertFalse(apiMetadataList.contains(new JcloudsTestBlobStoreApiMetadata()));
      assertTrue(apiMetadataList.contains(new JcloudsTestComputeApiMetadata()));
      assertTrue(apiMetadataList.contains(new JcloudsTestYetAnotherComputeApiMetadata()));
      verify(bundle);
   }

   /**
    * Creates a different {@link ClassLoader}.
    * 
    * @return
    */
   private ClassLoader createIsolatedClassLoader() throws MalformedURLException {
      URLClassLoader testClassLoader = (URLClassLoader) getClass().getClassLoader();
      URL[] urls = testClassLoader.getURLs();
      URLClassLoader loader = new URLClassLoader(urls, null);
      return loader;
   }
}
