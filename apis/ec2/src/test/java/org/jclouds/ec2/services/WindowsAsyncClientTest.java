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
package org.jclouds.ec2.services;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.ec2.options.BundleInstanceS3StorageOptions;
import org.jclouds.ec2.xml.BundleTaskHandler;
import org.jclouds.ec2.xml.DescribeBundleTasksResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code WindowsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "WindowsAsyncClientTest")
public class WindowsAsyncClientTest extends BaseEC2AsyncClientTest<WindowsAsyncClient> {

   public void testBundleInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = WindowsAsyncClient.class.getMethod("bundleInstanceInRegion", String.class, String.class,
               String.class, String.class, String.class, BundleInstanceS3StorageOptions[].class);
      HttpRequest request = processor
               .createRequest(
                        method,
                        null,
                        "i-e468cd8d",
                        "winami",
                        "my-bucket",
                        "{\"expiration\": \"2008-08-30T08:49:09Z\",\"conditions\": [{\"bucket\": \"my-bucket\"},[\"starts-with\", \"$key\", \"my-new-image\"]]}");
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      String payload = "Action=BundleInstance&Storage.S3.Prefix=winami&InstanceId=i-e468cd8d&Storage.S3.Bucket=my-bucket&Storage.S3.UploadPolicy=eyJleHBpcmF0aW9uIjogIjIwMDgtMDgtMzBUMDg6NDk6MDlaIiwiY29uZGl0aW9ucyI6IFt7ImJ1Y2tldCI6ICJteS1idWNrZXQifSxbInN0YXJ0cy13aXRoIiwgIiRrZXkiLCAibXktbmV3LWltYWdlIl1dfQ%3D%3D&Storage.S3.UploadPolicySignature=ih%2FiohGe0A7y4QVRbKaq6BZShzUsmBEJEa9AdFbxM6Y%3D";
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, payload, "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BundleTaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testBundleInstanceInRegionOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = WindowsAsyncClient.class.getMethod("bundleInstanceInRegion", String.class, String.class,
               String.class, String.class, String.class, BundleInstanceS3StorageOptions[].class);
      HttpRequest request = processor
               .createRequest(
                        method,
                        null,
                        "i-e468cd8d",
                        "winami",
                        "my-bucket",
                        "{\"expiration\": \"2008-08-30T08:49:09Z\",\"conditions\": [{\"bucket\": \"my-bucket\"},[\"starts-with\", \"$key\", \"my-new-image\"]]}",
                        BundleInstanceS3StorageOptions.Builder.bucketOwnedBy("10QMXFEV71ZS32XQFTR2"));
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      String payload = "Action=BundleInstance&Storage.S3.Prefix=winami&InstanceId=i-e468cd8d&Storage.S3.Bucket=my-bucket&Storage.S3.AWSAccessKeyId=10QMXFEV71ZS32XQFTR2&Storage.S3.UploadPolicy=eyJleHBpcmF0aW9uIjogIjIwMDgtMDgtMzBUMDg6NDk6MDlaIiwiY29uZGl0aW9ucyI6IFt7ImJ1Y2tldCI6ICJteS1idWNrZXQifSxbInN0YXJ0cy13aXRoIiwgIiRrZXkiLCAibXktbmV3LWltYWdlIl1dfQ%3D%3D&Storage.S3.UploadPolicySignature=ih%2FiohGe0A7y4QVRbKaq6BZShzUsmBEJEa9AdFbxM6Y%3D";
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, payload, "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BundleTaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeBundleTasks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = WindowsAsyncClient.class.getMethod("describeBundleTasksInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeBundleTasks",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeBundleTasksResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeBundleTasksArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = WindowsAsyncClient.class.getMethod("describeBundleTasksInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeBundleTasks&BundleId.1=1&BundleId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeBundleTasksResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<WindowsAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<WindowsAsyncClient>>() {
      };
   }

}
