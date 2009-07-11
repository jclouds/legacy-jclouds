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
package org.jclouds.aws.s3.commands;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all PutObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run in parallel.
 * 
 * @author Adrian Cole
 */
@Test(testName = "s3.PutObjectIntegrationTest")
public class PutObjectIntegrationTest extends S3IntegrationTest {
   @DataProvider(name = "putTests")
   public Object[][] createData1() throws IOException {

      String realObject = IOUtils.toString(new FileInputStream("pom.xml"));

      return new Object[][] { { "file", "text/xml", new File("pom.xml"), realObject },
               { "string", "text/xml", realObject, realObject },
               { "bytes", "application/octet-stream", realObject.getBytes(), realObject } };
   }

   @Test(dataProvider = "putTests", groups = { "integration", "live" })
   void testPutObject(String key, String type, Object content, Object realObject) throws Exception {
      S3Object object = new S3Object(key);
      object.getMetadata().setContentType(type);
      object.setData(content);
      if (content instanceof InputStream) {
         object.generateETag();
      }
      String bucketName = getBucketName();
      try {
         assertNotNull(client.putObject(bucketName, object).get(10, TimeUnit.SECONDS));
         object = client.getObject(bucketName, object.getKey()).get(10, TimeUnit.SECONDS);
         String returnedString = S3Utils.getContentAsStringAndClose(object);
         assertEquals(returnedString, realObject);
         assertEquals(client.listBucket(bucketName).get(10, TimeUnit.SECONDS).getContents().size(),
                  1);
      } finally {
         returnBucket(bucketName);
      }
   }

   @Test(groups = { "integration", "live" })
   void testMetadata() throws Exception {
      String key = "hello";

      S3Object object = new S3Object(key, TEST_STRING);
      object.getMetadata().setCacheControl("no-cache");
      object.getMetadata().setContentType("text/plain");
      object.getMetadata().setContentEncoding("x-compress");
      object.getMetadata().setSize(TEST_STRING.length());
      object.getMetadata().setContentDisposition("attachment; filename=hello.txt");
      object.getMetadata().getUserMetadata().put(S3Headers.USER_METADATA_PREFIX + "adrian",
               "powderpuff");
      object.getMetadata().setETag(HttpUtils.eTag(TEST_STRING.getBytes()));
      String bucketName = getBucketName();
      try {
         addObjectToBucket(bucketName, object);
         S3Object newObject = validateContent(bucketName, key);

         assertEquals(newObject.getMetadata().getContentType(), "text/plain");
         assertEquals(newObject.getMetadata().getContentEncoding(), "x-compress");
         assertEquals(newObject.getMetadata().getContentDisposition(),
                  "attachment; filename=hello.txt");
         assertEquals(newObject.getMetadata().getCacheControl(), "no-cache");
         assertEquals(newObject.getMetadata().getSize(), TEST_STRING.length());
         assertEquals(newObject.getMetadata().getUserMetadata().values().iterator().next(),
                  "powderpuff");
         assertEquals(newObject.getMetadata().getETag(), HttpUtils.eTag(TEST_STRING.getBytes()));
      } finally {
         returnBucket(bucketName);
      }
   }

}