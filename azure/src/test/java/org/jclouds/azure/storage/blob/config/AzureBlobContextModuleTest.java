/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.azure.storage.blob.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.reference.AzureBlobConstants;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobContextModuleTest")
public class AzureBlobContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new AzureBlobStubClientModule(), new JDKLoggingModule(),
               new AzureBlobContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to(
                              "user");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZURESTORAGE_KEY)).to("key");
                     bindConstant().annotatedWith(
                              Jsr330.named(AzureBlobConstants.PROPERTY_AZUREBLOB_ENDPOINT)).to(
                              "http://localhost");
                     super.configure();
                  }
               });
   }

   @Test
   void testContextImpl() {
      Injector injector = createInjector();
      RestContext<AzureBlobClient> handler = injector.getInstance(Key
               .get(new TypeLiteral<RestContext<AzureBlobClient>>() {
               }));
      assertEquals(handler.getClass(), RestContextImpl.class);

   }

}