/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.blobstore.integration;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.jclouds.blobstore.BlobMap;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.LiveBlobMap;
import org.jclouds.blobstore.LiveInputStreamMap;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest.BlobStoreObjectFactory;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.testng.ITestContext;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * 
 * @author Adrian Cole
 */
public class StubTestInitializer
         implements
         BaseBlobStoreIntegrationTest.TestInitializer<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

   public BaseBlobStoreIntegrationTest.TestInitializer.Result<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> init(
            Module configurationModule, ITestContext testContext) throws Exception {

      final StubBlobStoreContext context = createStubContext();
      assert context != null;

      final BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> client = context
               .getApi();
      assert client != null;

      final BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>> objectFactory = new BaseBlobStoreIntegrationTest.BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>>() {

         public Blob<BlobMetadata> createBlob(String key) {
            return new Blob<BlobMetadata>(key);

         }

         public ContainerMetadata createContainerMetadata(String key) {
            return new ContainerMetadata(key);
         }

      };
      assert objectFactory != null;

      return new BaseBlobStoreIntegrationTest.TestInitializer.Result<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>() {

         public BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> getClient() {
            return client;
         }

         public BlobStoreContext<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, BlobMetadata, Blob<BlobMetadata>> getContext() {
            return context;
         }

         public BlobStoreObjectFactory<ContainerMetadata, Blob<BlobMetadata>> getObjectFactory() {
            return objectFactory;
         }

      };
   }

   protected StubBlobStoreContext createStubContext() {
      return new StubContextBuilder().buildContext();
   }

   public static interface BlobMapFactory {
      BlobMap<BlobMetadata, Blob<BlobMetadata>> createMapView(String bucket);
   }

   public static interface InputStreamMapFactory {
      InputStreamMap<BlobMetadata> createMapView(String bucket);
   }

   public static class GuiceStubBlobStoreContext implements StubBlobStoreContext {

      @Resource
      private Logger logger = Logger.NULL;
      private final Injector injector;
      private final InputStreamMapFactory s3InputStreamMapFactory;
      private final BlobMapFactory s3ObjectMapFactory;
      private final Closer closer;

      @Inject
      private GuiceStubBlobStoreContext(Injector injector, Closer closer,
               BlobMapFactory s3ObjectMapFactory, InputStreamMapFactory s3InputStreamMapFactory) {
         this.injector = injector;
         this.s3InputStreamMapFactory = s3InputStreamMapFactory;
         this.s3ObjectMapFactory = s3ObjectMapFactory;
         this.closer = closer;
      }

      /**
       * {@inheritDoc}
       */
      public InputStreamMap<BlobMetadata> createInputStreamMap(String bucket) {
         getApi().createContainer(bucket);
         return s3InputStreamMapFactory.createMapView(bucket);
      }

      /**
       * {@inheritDoc}
       */
      public BlobMap<BlobMetadata, Blob<BlobMetadata>> createBlobMap(String bucket) {
         getApi().createContainer(bucket);
         return s3ObjectMapFactory.createMapView(bucket);
      }

      /**
       * {@inheritDoc}
       * 
       * @see Closer
       */
      public void close() {
         try {
            closer.close();
         } catch (IOException e) {
            logger.error(e, "error closing content");
         }
      }

      public String getAccount() {
         // throw org.jboss.util.NotImplementedException("FIXME NYI getAccount");
         return null;
      }

      public URI getEndPoint() {
         // throw org.jboss.util.NotImplementedException("FIXME NYI getEndPoint");
         return null;
      }

      public BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> getApi() {
         return injector
                  .getInstance(Key
                           .get(new TypeLiteral<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                           }));
      }

   }

   public interface StubBlobStoreContext
            extends
            BlobStoreContext<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>, BlobMetadata, Blob<BlobMetadata>> {

   }

   public static class StubContextBuilder extends CloudContextBuilder<StubBlobStoreContext> {

      public StubContextBuilder() {
         super(new Properties());
      }

      public void authenticate(String id, String secret) {
      }

      public StubBlobStoreContext buildContext() {
         return buildInjector().getInstance(StubBlobStoreContext.class);
      }

      protected void addParserModule(List<Module> modules) {
      }

      protected void addContextModule(List<Module> modules) {
         modules.add(new AbstractModule() {

            @Override
            protected void configure() {

               requireBinding(TestStubBlobStore.class);
               bind(URI.class).toInstance(URI.create("http://localhost:8080"));
               bind(new TypeLiteral<BlobMapFactory>() {
               })
                        .toProvider(
                                 FactoryProvider
                                          .newFactory(
                                                   new TypeLiteral<BlobMapFactory>() {
                                                   },
                                                   new TypeLiteral<LiveBlobMap<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                                                   }));
               bind(new TypeLiteral<InputStreamMapFactory>() {
               }).toProvider(FactoryProvider.newFactory(new TypeLiteral<InputStreamMapFactory>() {
               }, new TypeLiteral<StubInputStreamMap>() {
               }));
               bind(StubBlobStoreContext.class).to(GuiceStubBlobStoreContext.class);
            }
         });
      }

      protected void addConnectionModule(List<Module> modules) {
         modules.add(new AbstractModule() {

            @Override
            protected void configure() {
               bind(
                        new TypeLiteral<BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>>>() {
                        }).to(TestStubBlobStore.class).in(Scopes.SINGLETON);
            }

         });
      }
   }

   public static class StubInputStreamMap extends
            LiveInputStreamMap<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {

      @Inject
      public StubInputStreamMap(
               BlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> connection,
               @Assisted String container) {
         super(connection, container);
      }

      @Override
      protected Blob<BlobMetadata> createBlob(String s) {
         return new Blob<BlobMetadata>(s);
      }

   }

   public static class TestStubBlobStore extends
            StubBlobStore<ContainerMetadata, BlobMetadata, Blob<BlobMetadata>> {
      /**
       * note this must be final and static so that tests coming from multiple threads will pass.
       */
      private static final Map<String, Map<String, Blob<BlobMetadata>>> containerToBlobs = new ConcurrentHashMap<String, Map<String, Blob<BlobMetadata>>>();

      @Override
      public Map<String, Map<String, Blob<BlobMetadata>>> getContainerToBlobs() {
         return containerToBlobs;
      }

      @Override
      protected Blob<BlobMetadata> createBlob(String name) {
         return new Blob<BlobMetadata>(name);
      }

      @Override
      protected Blob<BlobMetadata> createBlob(BlobMetadata metadata) {
         return new Blob<BlobMetadata>(metadata);
      }

      @Override
      protected ContainerMetadata createContainerMetadata(String name) {
         return new ContainerMetadata(name);
      }

   }
}