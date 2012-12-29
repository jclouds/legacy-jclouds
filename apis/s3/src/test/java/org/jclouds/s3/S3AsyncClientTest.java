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
package org.jclouds.s3;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.domain.Region;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.FalseOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.BlobStoreFallbacks.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.s3.S3Fallbacks.TrueOn404OrNotFoundFalseOnIllegalState;
import org.jclouds.s3.config.S3RestClientModule;
import org.jclouds.s3.domain.AccessControlList;
import org.jclouds.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.s3.domain.AccessControlList.Grant;
import org.jclouds.s3.domain.AccessControlList.Permission;
import org.jclouds.s3.domain.BucketLogging;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.Payer;
import org.jclouds.s3.domain.S3Object;
import org.jclouds.s3.fallbacks.FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists;
import org.jclouds.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.s3.internal.BaseS3AsyncClientTest;
import org.jclouds.s3.options.CopyObjectOptions;
import org.jclouds.s3.options.ListBucketOptions;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.jclouds.s3.xml.AccessControlListHandler;
import org.jclouds.s3.xml.BucketLoggingHandler;
import org.jclouds.s3.xml.CopyObjectHandler;
import org.jclouds.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.s3.xml.ListBucketHandler;
import org.jclouds.s3.xml.LocationConstraintHandler;
import org.jclouds.s3.xml.PayerHandler;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code S3AsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "S3AsyncClientTest")
public abstract class S3AsyncClientTest<T extends S3AsyncClient> extends BaseS3AsyncClientTest<T> {

   protected String url = "s3.amazonaws.com";

   public void testAllRegions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketInRegion", String.class, String.class, Array.newInstance(
               PutBucketOptions.class, 0).getClass());
      for (String region : Region.DEFAULT_S3) {
         processor.createRequest(method, region, "bucket-" + region);
      }
   }

   public void testGetBucketLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketLocation", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?location HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      request = filter.filter(request);

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?location HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "Authorization: AWS identity:2fFTeYJTDwiJmaAkKj732RjNbOg=\nDate: 2009-11-08T15:54:08.897Z\nHost: bucket."
                        + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, LocationConstraintHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBucketPayer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketPayer", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PayerHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBucketPayerOwner() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("setBucketPayer", String.class, Payer.class);
      HttpRequest request = processor.createRequest(method, "bucket", Payer.BUCKET_OWNER);

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<RequestPaymentConfiguration xmlns=\"http://" + url
               + "/doc/2006-03-01/\"><Payer>BucketOwner</Payer></RequestPaymentConfiguration>", "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBucketPayerRequester() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("setBucketPayer", String.class, Payer.class);
      HttpRequest request = processor.createRequest(method, "bucket", Payer.REQUESTER);

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?requestPayment HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<RequestPaymentConfiguration xmlns=\"http://" + url
               + "/doc/2006-03-01/\"><Payer>Requester</Payer></RequestPaymentConfiguration>", "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListBucket() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("listBucket", String.class, Array.newInstance(
               ListBucketOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListBucketHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testBucketExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("bucketExists", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?max-keys=0 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnContainerNotFound.class);

      checkFilters(request);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testCopyObjectInvalidName() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("copyObject", String.class, String.class, String.class,
               String.class, Array.newInstance(CopyObjectOptions.class, 0).getClass());
      processor.createRequest(method, "sourceBucket", "sourceObject", "destinationBucket", "destinationObject");

   }

   public void testCopyObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("copyObject", String.class, String.class, String.class,
               String.class, Array.newInstance(CopyObjectOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "sourceBucket", "sourceObject", "destinationbucket",
               "destinationObject");

      assertRequestLineEquals(request, "PUT https://destinationbucket." + url + "/destinationObject HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: destinationbucket." + url
               + "\nx-amz-copy-source: /sourceBucket/sourceObject\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CopyObjectHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteBucketIfEmpty() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("deleteBucketIfEmpty", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, TrueOn404OrNotFoundFalseOnIllegalState.class);

      checkFilters(request);
   }

   public void testDeleteObject() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("deleteObject", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "bucket", "object");

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testGetBucketACL() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("getBucketACL", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testGetObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getObject", String.class, String.class, GetOptions[].class);
      HttpRequest request = processor.createRequest(method, "bucket", "object");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testGetObjectACL() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("getObjectACL", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "bucket", "object");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/object?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertFallbackClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(request);
   }

   public void testObjectExists() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("objectExists", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "bucket", "object");

      assertRequestLineEquals(request, "HEAD https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testHeadObject() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("headObject", String.class, String.class);
      HttpRequest request = processor.createRequest(method, "bucket", "object");

      assertRequestLineEquals(request, "HEAD https://bucket." + url + "/object HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseObjectMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);

      checkFilters(request);
   }

   public void testListOwnedBuckets() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("listOwnedBuckets");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://" + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: " + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListAllMyBucketsHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testNewS3Object() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("newS3Object");
      assertEquals(method.getReturnType(), S3Object.class);
   }

   public void testPutBucketACL() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketACL", String.class, AccessControlList.class);
      HttpRequest request = processor.createRequest(method, "bucket", AccessControlList.fromCannedAccessPolicy(
               CannedAccessPolicy.PRIVATE, "1234"));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
               request,
               "<AccessControlPolicy xmlns=\"http://"
                        + url
                        + "/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testPutBucketDefault() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketInRegion", String.class, String.class, Array.newInstance(
               PutBucketOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null, "bucket");

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfBucketAlreadyOwnedByYouOrOperationAbortedWhenBucketExists.class);

      checkFilters(request);
   }

   public void testPutObject() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class
               .getMethod("putObject", String.class, S3Object.class, PutObjectOptions[].class);
      HttpRequest request = processor.createRequest(method, "bucket", blobToS3Object
               .apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/hello HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "hello", "text/plain", false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testPutObjectACL() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class
               .getMethod("putObjectACL", String.class, String.class, AccessControlList.class);
      HttpRequest request = processor.createRequest(method, "bucket", "key", AccessControlList.fromCannedAccessPolicy(
               CannedAccessPolicy.PRIVATE, "1234"));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/key?acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
               request,
               "<AccessControlPolicy xmlns=\"http://"
                        + url
                        + "/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBucketLogging() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketLogging", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "GET https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BucketLoggingHandler.class);
      assertFallbackClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(request);
   }

   public void testDisableBucketLogging() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("disableBucketLogging", String.class);
      HttpRequest request = processor.createRequest(method, "bucket");

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "<BucketLoggingStatus xmlns=\"http://" + url + "/doc/2006-03-01/\"/>", "text/xml",
               false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testEnableBucketLoggingOwner() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("enableBucketLogging", String.class, BucketLogging.class);
      HttpRequest request = processor
               .createRequest(method, "bucket", new BucketLogging("mylogs", "access_log-", ImmutableSet
                        .<Grant> of(new Grant(new EmailAddressGrantee("adrian@jclouds.org"), Permission.FULL_CONTROL))));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/?logging HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, Strings2.toStringAndClose(getClass().getResourceAsStream("/bucket_logging.xml")),
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   @ConfiguresRestClient
   private static final class TestS3RestClientModule extends S3RestClientModule<S3Client, S3AsyncClient> {

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestS3RestClientModule();
   }

}
