/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.s3.filters;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.internal.BaseS3AsyncClientTest;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.reference.S3Headers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

/**
 * Tests behavior of {@code RequestAuthorizeSignature}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RequestAuthorizeSignatureTest")
public class RequestAuthorizeSignatureTest extends BaseS3AsyncClientTest<S3AsyncClient> {

   @DataProvider(parallel = true)
   public Object[][] dataProvider() throws NoSuchMethodException {
      return new Object[][] { { listOwnedBuckets() }, { putObject() }, { putBucketAcl() }

      };
   }

   /**
    * NOTE this test is dependent on how frequently the timestamp updates. At the time of writing,
    * this was once per second. If this timestamp update interval is increased, it could make this
    * test appear to hang for a long time.
    */
   @Test(threadPoolSize = 3, dataProvider = "dataProvider", timeOut = 10000)
   void testIdempotent(HttpRequest request) {
      request = filter.filter(request);
      String signature = request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION);
      String date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
      int iterations = 1;
      while (request.getFirstHeaderOrNull(HttpHeaders.DATE).equals(date)) {
         date = request.getFirstHeaderOrNull(HttpHeaders.DATE);
         request = filter.filter(request);
         if (request.getFirstHeaderOrNull(HttpHeaders.DATE).equals(date))
            assert signature.equals(request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION)) : String.format(
                     "sig: %s != %s on attempt %s", signature, request.getFirstHeaderOrNull(HttpHeaders.AUTHORIZATION),
                     iterations);
         else
            iterations++;

      }
      System.out.printf("%s: %d iterations before the timestamp updated %n", Thread.currentThread().getName(),
               iterations);
   }

   @Test
   void testAppendBucketNameHostHeader() throws SecurityException, NoSuchMethodException {
      HttpRequest request = processor.createRequest(S3AsyncClient.class.getMethod("getBucketLocation", String.class),
               "bucket");
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "/bucket");
   }

   @Test
   void testAclQueryString() throws SecurityException, NoSuchMethodException {
      HttpRequest request = putBucketAcl();
      StringBuilder builder = new StringBuilder();
      filter.appendUriPath(request, builder);
      assertEquals(builder.toString(), "/?acl");
   }

   private GeneratedHttpRequest putBucketAcl() throws NoSuchMethodException {
      return processor.createRequest(S3AsyncClient.class.getMethod("putBucketACL", String.class,
               AccessControlList.class), "bucket", AccessControlList.fromCannedAccessPolicy(CannedAccessPolicy.PRIVATE,
               "1234"));
   }

   // "?acl", "?location", "?logging", "?uploads", or "?torrent"

   @Test
   void testAppendBucketNameHostHeaderService() throws SecurityException, NoSuchMethodException {
      HttpRequest request = listOwnedBuckets();
      StringBuilder builder = new StringBuilder();
      filter.appendBucketName(request, builder);
      assertEquals(builder.toString(), "");
   }

   private GeneratedHttpRequest listOwnedBuckets() throws NoSuchMethodException {
      return processor.createRequest(S3AsyncClient.class.getMethod("listOwnedBuckets"));
   }

   @Test
   void testHeadersGoLowercase() throws SecurityException, NoSuchMethodException {
      HttpRequest request = putObject();
      SortedSetMultimap<String, String> canonicalizedHeaders = TreeMultimap.create();
      filter.appendHttpHeaders(request, canonicalizedHeaders);
      StringBuilder builder = new StringBuilder();
      filter.appendAmzHeaders(canonicalizedHeaders, builder);
      assertEquals(builder.toString(), S3Headers.USER_METADATA_PREFIX + "adrian:foo\n");
   }

   private HttpRequest putObject() throws NoSuchMethodException {
      S3Object object = blobToS3Object.apply(BindBlobToMultipartFormTest.TEST_BLOB);

      object.getMetadata().getUserMetadata().put("Adrian", "foo");
      HttpRequest request = processor.createRequest(S3AsyncClient.class.getMethod("putObject", String.class,
               S3Object.class, PutObjectOptions[].class), "bucket", object);
      return request;
   }

   @Test
   void testAppendBucketNameURIHost() throws SecurityException, NoSuchMethodException {
      HttpRequest request = processor.createRequest(S3AsyncClient.class.getMethod("getBucketLocation", String.class),
               "bucket");
      assertEquals(request.getEndpoint().getHost(), "bucket.s3.amazonaws.com");
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.setProperty(PROPERTY_SESSION_INTERVAL, 1 + "");
      return overrides;
   }
}
