/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.util.S3Utils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests connection by listing all the buckets and their size
 * 
 * @author Adrian Cole
 */
@Test(testName = "s3.S3ConnectionIntegrationTest")
public class S3ConnectionIntegrationTest extends S3IntegrationTest {

   @Test(groups = { "integration" })
   void testListBuckets() throws Exception {
      client.listOwnedBuckets().get(10, TimeUnit.SECONDS);
   }

   private static final String sysHttpStreamUrl = System.getProperty("jclouds.s3.httpstream.url");
   private static final String sysHttpStreamMd5 = System.getProperty("jclouds.s3.httpstream.md5");

   @Test(groups = { "integration" })
   @Parameters( { "jclouds.s3.httpstream.url", "jclouds.s3.httpstream.md5" })
   public void testCopyUrl(@Optional String httpStreamUrl, @Optional String httpStreamMd5)
            throws Exception {
      httpStreamUrl = checkNotNull(httpStreamUrl != null ? httpStreamUrl : sysHttpStreamUrl,
               "httpStreamUrl");

      httpStreamMd5 = checkNotNull(httpStreamMd5 != null ? httpStreamMd5 : sysHttpStreamMd5,
               "httpStreamMd5");

      String bucketName = bucketPrefix + "tcu";
      createBucketAndEnsureEmpty(bucketName);
      String key = "hello";

      URL url = new URL(httpStreamUrl);
      byte[] md5 = S3Utils.fromHexString(httpStreamMd5);

      URLConnection connection = url.openConnection();
      int length = connection.getContentLength();
      InputStream input = connection.getInputStream();

      S3Object object = new S3Object(key, input);
      object.setContentLength(length);
      object.getMetadata().setMd5(md5);
      object.getMetadata().setSize(length);

      byte[] newMd5 = client.putObject(bucketName, object).get(30, TimeUnit.SECONDS);
      assertEquals(newMd5, md5);
   }
}