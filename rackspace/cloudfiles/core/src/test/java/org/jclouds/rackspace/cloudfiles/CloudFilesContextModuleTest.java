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
package org.jclouds.rackspace.cloudfiles;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.BlobStoreMapsModule;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.rackspace.StubRackspaceAuthenticationModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule.CloudFilesContextImpl;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.integration.StubCloudFilesBlobStoreModule;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.CloudFilesContextModuleTest")
public class CloudFilesContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new StubCloudFilesBlobStoreModule(),
               new StubRackspaceAuthenticationModule(), BlobStoreMapsModule.Builder.newBuilder(
                        new TypeLiteral<ContainerMetadata>() {
                        }, new TypeLiteral<BlobMetadata>() {
                        }, new TypeLiteral<Blob<BlobMetadata>>() {
                        }).build(), new CloudFilesContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(CloudFilesConstants.PROPERTY_RACKSPACE_USER)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(CloudFilesConstants.PROPERTY_RACKSPACE_KEY)).to("key");
                     super.configure();
                  }
               });
   }

   @Test
   void testContextImpl() {
      CloudFilesContext context = createInjector().getInstance(CloudFilesContext.class);
      assertEquals(context.getClass(), CloudFilesContextImpl.class);
   }

}