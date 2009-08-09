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
package org.jclouds.aws.s3;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests connection by copying the contents of a url into the bucket.
 * 
 * @author Adrian Cole
 */
@Test(groups = { "live" }, testName = "s3.S3ConnectionLiveTest")
public class S3ConnectionLiveTest extends S3IntegrationTest {

   private static final String sysHttpStreamUrl = System.getProperty("jclouds.s3.httpstream.url");
   private static final String sysHttpStreamETag = System.getProperty("jclouds.s3.httpstream.md5");

   @Test
   @Parameters( { "jclouds.s3.httpstream.url", "jclouds.s3.httpstream.md5" })
   public void testCopyUrl(@Optional String httpStreamUrl, @Optional String httpStreamETag)
            throws Exception {
      httpStreamUrl = checkNotNull(httpStreamUrl != null ? httpStreamUrl : sysHttpStreamUrl,
               "httpStreamUrl");

      httpStreamETag = checkNotNull(httpStreamETag != null ? httpStreamETag : sysHttpStreamETag,
               "httpStreamMd5");

      String key = "hello";

      URL url = new URL(httpStreamUrl);
      byte[] eTag = HttpUtils.fromHexString(httpStreamETag);

      URLConnection connection = url.openConnection();
      int length = connection.getContentLength();
      InputStream input = connection.getInputStream();

      S3Object object = new S3Object(key, input);
      object.setContentLength(length);
      object.getMetadata().setETag(eTag);
      object.getMetadata().setSize(length);
      String bucketName = getBucketName();
      try {
         byte[] newETag = client.putObject(bucketName, object).get(180, TimeUnit.SECONDS);
         assertEquals(newETag, eTag);
      } finally {
         returnBucket(bucketName);
      }
   }
}