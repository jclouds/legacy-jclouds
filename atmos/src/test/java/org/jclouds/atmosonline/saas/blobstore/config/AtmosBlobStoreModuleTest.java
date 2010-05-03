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
package org.jclouds.atmosonline.saas.blobstore.config;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.Constants;
import org.jclouds.atmosonline.saas.blobstore.strategy.FindMD5InUserMetadata;
import org.jclouds.atmosonline.saas.config.AtmosStorageStubClientModule;
import org.jclouds.atmosonline.saas.reference.AtmosStorageConstants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "emcsaas.AtmosBlobStoreModuleTest")
public class AtmosBlobStoreModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new ExecutorServiceModule(sameThreadExecutor(),
               sameThreadExecutor()), new JDKLoggingModule(), new AtmosStorageStubClientModule(),
               new AtmosBlobStoreContextModule("atmos") {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_UID)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_KEY)).to("key");
                     bindConstant().annotatedWith(
                              Jsr330.named(AtmosStorageConstants.PROPERTY_EMCSAAS_ENDPOINT)).to(
                              "http://localhost");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_IO_WORKER_THREADS)).to("1");
                     bindConstant().annotatedWith(Jsr330.named(Constants.PROPERTY_USER_THREADS))
                              .to("1");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT)).to("0");
                     bindConstant().annotatedWith(
                              Jsr330.named(Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST)).to("1");
                     super.configure();
                  }
               });
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