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
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.config.PCSContextModule;
import org.jclouds.mezeo.pcs2.config.StubPCSBlobStoreModule;
import org.jclouds.mezeo.pcs2.config.PCSContextModule.PCSContextImpl;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSContextModuleTest")
public class PCSContextModuleTest {
   Injector createInjector() {
      return Guice.createInjector(new StubPCSBlobStoreModule(), BlobStoreMapsModule.Builder
               .newBuilder(new TypeLiteral<ContainerMetadata>() {
               }, new TypeLiteral<FileMetadata>() {
               }, new TypeLiteral<PCSFile>() {
               }).build(), new PCSContextModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_USER)).to("user");
            bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_PASSWORD)).to(
                     "key");
            bindConstant().annotatedWith(Jsr330.named(PCSConstants.PROPERTY_PCS2_ENDPOINT)).to(
                     "http://localhost");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
            super.configure();
         }
      });
   }

   @Test
   void testContextImpl() {
      PCSContext handler = createInjector().getInstance(PCSContext.class);
      assertEquals(handler.getClass(), PCSContextImpl.class);
   }

}