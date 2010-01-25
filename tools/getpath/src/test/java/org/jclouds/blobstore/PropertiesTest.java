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
package org.jclouds.blobstore;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.aws.s3.S3ContextBuilder;
import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.azure.storage.blob.AzureBlobContextBuilder;
import org.jclouds.rackspace.cloudfiles.CloudFilesContextBuilder;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
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
      properties.load(Resources.newInputStreamSupplier(Resources.getResource("blobstore.properties"))
               .getInput());
   }

   public void testAzure() {
      assertEquals(properties.getProperty("azureblob.contextbuilder"),
               AzureBlobContextBuilder.class.getName());
      assertEquals(properties.getProperty("azureblob.propertiesbuilder"),
               AzureBlobPropertiesBuilder.class.getName());
   }

   public void testCloudFiles() {
      assertEquals(properties.getProperty("cloudfiles.contextbuilder"),
               CloudFilesContextBuilder.class.getName());
      assertEquals(properties.getProperty("cloudfiles.propertiesbuilder"),
               CloudFilesPropertiesBuilder.class.getName());
   }

   public void testBlobStore() {
      assertEquals(properties.getProperty("s3.contextbuilder"), S3ContextBuilder.class
               .getName());
      assertEquals(properties.getProperty("s3.propertiesbuilder"), S3PropertiesBuilder.class
               .getName());
   }
}
