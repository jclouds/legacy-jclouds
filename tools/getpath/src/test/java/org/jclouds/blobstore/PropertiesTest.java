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
package org.jclouds.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.aws.s3.blobstore.S3BlobStoreContextBuilder;
import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.azure.storage.blob.blobstore.AzureBlobStoreContextBuilder;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStoreContextBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "getpath.PropertiesTest")
public class PropertiesTest {
   private Properties properties;

   @BeforeTest
   public void setUp() throws IOException {
      properties = new Properties();
      properties.load(Resources.newInputStreamSupplier(Resources.getResource("jclouds.properties"))
               .getInput());
   }

   public void testAzure() {
      assertEquals(properties.getProperty("azureblob.contextBuilder"),
               AzureBlobStoreContextBuilder.class.getName());
      assertEquals(properties.getProperty("azureblob.propertiesBuilder"),
               AzureBlobPropertiesBuilder.class.getName());
   }

   public void testCloudFiles() {
      assertEquals(properties.getProperty("cloudfiles.contextBuilder"),
               CloudFilesBlobStoreContextBuilder.class.getName());
      assertEquals(properties.getProperty("cloudfiles.propertiesBuilder"),
               CloudFilesPropertiesBuilder.class.getName());
   }

   public void testBlobStore() {
      assertEquals(properties.getProperty("s3.contextBuilder"), S3BlobStoreContextBuilder.class
               .getName());
      assertEquals(properties.getProperty("s3.propertiesBuilder"), S3PropertiesBuilder.class
               .getName());
   }
}
