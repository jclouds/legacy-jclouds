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
package org.jclouds.aws.s3;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.config.S3ObjectModule;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketLogging;
import org.jclouds.aws.s3.domain.CannedAccessPolicy;
import org.jclouds.aws.s3.domain.Payer;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.AccessControlList.EmailAddressGrantee;
import org.jclouds.aws.s3.domain.AccessControlList.Grant;
import org.jclouds.aws.s3.domain.AccessControlList.Permission;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.aws.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.aws.s3.functions.ReturnFalseIfBucketAlreadyOwnedByYou;
import org.jclouds.aws.s3.functions.ReturnTrueOn404OrNotFoundFalseIfNotEmpty;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.aws.s3.xml.AccessControlListHandler;
import org.jclouds.aws.s3.xml.BucketLoggingHandler;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.aws.s3.xml.LocationConstraintHandler;
import org.jclouds.aws.s3.xml.PayerHandler;
import org.jclouds.blobstore.binders.BindBlobToMultipartFormTest;
import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.blobstore.functions.ReturnFalseOnContainerNotFound;
import org.jclouds.blobstore.functions.ReturnFalseOnKeyNotFound;
import org.jclouds.blobstore.functions.ReturnNullOnKeyNotFound;
import org.jclouds.blobstore.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code S3Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ClientTest")
public class S3AsyncClientTest extends RestClientTest<S3AsyncClient> {

   public void testGetBucketLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketLocation", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/?location HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, LocationConstraintHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetBucketPayer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketPayer", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/?requestPayment HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PayerHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetBucketPayerOwner() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = S3AsyncClient.class.getMethod("setBucketPayer", String.class, Payer.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               Payer.BUCKET_OWNER);

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/?requestPayment HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 133\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(
               httpMethod,
               "<RequestPaymentConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Payer>BucketOwner</Payer></RequestPaymentConfiguration>");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetBucketPayerRequester() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = S3AsyncClient.class.getMethod("setBucketPayer", String.class, Payer.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               Payer.REQUESTER);

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/?requestPayment HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 131\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(
               httpMethod,
               "<RequestPaymentConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Payer>Requester</Payer></RequestPaymentConfiguration>");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testListBucket() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("listBucket", String.class, Array.newInstance(
               ListBucketOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/ HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListBucketHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testBucketExists() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("bucketExists", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "HEAD http://bucket.stub:8080/?max-keys=0 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnContainerNotFound.class);

      checkFilters(httpMethod);
   }

   public void testCopyObject() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class
               .getMethod("copyObject", String.class, String.class, String.class, String.class,
                        Array.newInstance(CopyObjectOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method,
               "sourceBucket", "sourceObject", "destinationBucket", "destinationObject");

      assertRequestLineEquals(httpMethod,
               "PUT http://destinationBucket.stub:8080/destinationObject HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 0\nHost: destinationBucket.stub\nx-amz-copy-source: /sourceBucket/sourceObject\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CopyObjectHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteBucketIfEmpty() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = S3AsyncClient.class.getMethod("deleteBucketIfEmpty", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "DELETE http://bucket.stub:8080/ HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnTrueOn404OrNotFoundFalseIfNotEmpty.class);

      checkFilters(httpMethod);
   }

   public void testDeleteObject() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("deleteObject", String.class, String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "object");

      assertRequestLineEquals(httpMethod, "DELETE http://bucket.stub:8080/object HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpMethod);
   }

   public void testGetBucketACL() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("getBucketACL", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/?acl HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertExceptionParserClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testGetObject() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getObject", String.class, String.class, Array
               .newInstance(GetOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "object");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/object HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod,
               ParseObjectFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnKeyNotFound.class);

      checkFilters(httpMethod);
   }

   public void testGetObjectACL() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("getObjectACL", String.class, String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "object");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/object?acl HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccessControlListHandler.class);
      assertExceptionParserClassEquals(method, ThrowKeyNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testObjectExists() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("objectExists", String.class, String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "object");

      assertRequestLineEquals(httpMethod, "HEAD http://bucket.stub:8080/object HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnKeyNotFound.class);

      checkFilters(httpMethod);
   }

   public void testHeadObject() throws SecurityException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("headObject", String.class, String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "object");

      assertRequestLineEquals(httpMethod, "HEAD http://bucket.stub:8080/object HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseObjectMetadataFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnKeyNotFound.class);

      checkFilters(httpMethod);
   }

   public void testListOwnedBuckets() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("listOwnedBuckets");
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://stub:8080/ HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListAllMyBucketsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testNewS3Object() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("newS3Object");
      assertEquals(method.getReturnType(), S3Object.class);
   }

   public void testPutBucketACL() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketACL", String.class,
               AccessControlList.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               AccessControlList.fromCannedAccessPolicy(CannedAccessPolicy.PRIVATE, "1234"));

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/?acl HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 321\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(
               httpMethod,
               "<AccessControlPolicy xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>");

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPutBucketDefault() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketInRegion", Region.class,
               String.class, Array.newInstance(PutBucketOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "bucket");

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/ HTTP/1.1");
      assertHeadersEqual(httpMethod, "Content-Length: 0\nHost: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfBucketAlreadyOwnedByYou.class);

      checkFilters(httpMethod);
   }

   public void testPutBucketEu() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putBucketInRegion", Region.class,
               String.class, Array.newInstance(PutBucketOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method,
               Region.EU_WEST_1, "bucket");

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 98\nContent-Type: application/unknown\nHost: bucket.stub\n");
      assertPayloadEquals(
               httpMethod,
               "<CreateBucketConfiguration><LocationConstraint>EU</LocationConstraint></CreateBucketConfiguration>");

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfBucketAlreadyOwnedByYou.class);

      checkFilters(httpMethod);
   }

   public void testPutObject() throws ArrayIndexOutOfBoundsException, SecurityException,
            IllegalArgumentException, NoSuchMethodException, IOException {

      Method method = S3AsyncClient.class.getMethod("putObject", String.class, S3Object.class,
               Array.newInstance(PutObjectOptions.class, 0).getClass());
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               blobToS3Object.apply(BindBlobToMultipartFormTest.TEST_BLOB));

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/hello HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 5\nContent-Type: text/plain\nHost: bucket.stub\n");
      assertPayloadEquals(httpMethod, "hello");

      assertResponseParserClassEquals(method, httpMethod, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPutObjectACL() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("putObjectACL", String.class, String.class,
               AccessControlList.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               "key", AccessControlList.fromCannedAccessPolicy(CannedAccessPolicy.PRIVATE, "1234"));

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/key?acl HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 321\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(
               httpMethod,
               "<AccessControlPolicy xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Owner><ID>1234</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\"><ID>1234</ID></Grantee><Permission>FULL_CONTROL</Permission></Grant></AccessControlList></AccessControlPolicy>");

      assertResponseParserClassEquals(method, httpMethod, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetBucketLogging() throws SecurityException, NoSuchMethodException, IOException {
      Method method = S3AsyncClient.class.getMethod("getBucketLogging", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "GET http://bucket.stub:8080/?logging HTTP/1.1");
      assertHeadersEqual(httpMethod, "Host: bucket.stub\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BucketLoggingHandler.class);
      assertExceptionParserClassEquals(method, ThrowContainerNotFoundOn404.class);

      checkFilters(httpMethod);
   }

   public void testDisableBucketLogging() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = S3AsyncClient.class.getMethod("disableBucketLogging", String.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket");

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/?logging HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 70\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(httpMethod,
               "<BucketLoggingStatus xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"/>");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testEnableBucketLoggingOwner() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = S3AsyncClient.class.getMethod("enableBucketLogging", String.class,
               BucketLogging.class);
      GeneratedHttpRequest<S3AsyncClient> httpMethod = processor.createRequest(method, "bucket",
               new BucketLogging("mylogs", "access_log-", ImmutableSet.<Grant> of(new Grant(
                        new EmailAddressGrantee("adrian@jclouds.org"), Permission.FULL_CONTROL))));

      assertRequestLineEquals(httpMethod, "PUT http://bucket.stub:8080/?logging HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 433\nContent-Type: text/xml\nHost: bucket.stub\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/s3/bucket_logging.xml")));

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   BlobToObject blobToS3Object;

   @Override
   protected void checkFilters(GeneratedHttpRequest<S3AsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), RequestAuthorizeSignature.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<S3AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<S3AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() {
      super.setupFactory();
      blobToS3Object = injector.getInstance(BlobToObject.class);
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            install(new BlobStoreObjectModule());
            install(new S3ObjectModule());
            bind(URI.class).annotatedWith(S3.class).toInstance(URI.create("http://stub:8080"));
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bindConstant().annotatedWith(
                     Jsr330.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to("prefix");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String provide() {
            return "timestamp";
         }
      };
   }
}
