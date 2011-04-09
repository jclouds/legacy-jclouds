/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.jclouds.aws.s3.config.AWSS3RestClientModule;
import org.jclouds.aws.s3.functions.ETagFromHttpResponseViaRegex;
import org.jclouds.aws.s3.functions.UploadIdFromHttpResponseViaRegex;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.s3.domain.ObjectMetadata;
import org.jclouds.s3.domain.ObjectMetadataBuilder;
import org.jclouds.s3.functions.ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState;
import org.jclouds.s3.options.PutBucketOptions;
import org.jclouds.s3.options.PutObjectOptions;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSS3AsyncClientTest")
public class AWSS3AsyncClientTest extends org.jclouds.s3.S3AsyncClientTest<AWSS3AsyncClient> {

   public AWSS3AsyncClientTest() {
      this.provider = "aws-s3";
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AWSS3AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AWSS3AsyncClient>>() {
      };
   }

   @Override
   protected Properties getProperties() {
      return RestContextFactory.getPropertiesFromResource("/rest.properties");
   }

   public void testInitiateMultipartUpload() throws SecurityException, NegativeArraySizeException,
            NoSuchMethodException {
      Method method = AWSS3AsyncClient.class.getMethod("initiateMultipartUpload", String.class, ObjectMetadata.class,
               PutObjectOptions[].class);
      HttpRequest request = processor.createRequest(method, "bucket", ObjectMetadataBuilder.create().key("foo")
               .contentMD5(new byte[] { 1, 2, 3, 4 }).build());

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploads HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Content-MD5: AQIDBA==\nContent-Type: binary/octet-stream\nHost: bucket."
               + url + "\n");
      assertPayloadEquals(request, null, null, false);

      // as this is a payload-related command, but with no payload, be careful that we check
      // filtering and do not ignore if this fails later.
      request = request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploads HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "Authorization: AWS identity:Sp1FX4svL9P2u2bFJwroaYpSANo=\nContent-MD5: AQIDBA==\n"
                        + "Content-Type: binary/octet-stream\nDate: 2009-11-08T15:54:08.897Z\nHost: bucket." + url
                        + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, UploadIdFromHttpResponseViaRegex.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testAbortMultipartUpload() throws SecurityException, NegativeArraySizeException, NoSuchMethodException {
      Method method = AWSS3AsyncClient.class
               .getMethod("abortMultipartUpload", String.class, String.class, String.class);
      HttpRequest request = processor.createRequest(method, "bucket", "foo", "asdsadasdas", 1, Payloads
               .newStringPayload(""));

      assertRequestLineEquals(request, "DELETE https://bucket." + url + "/foo?uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "", "application/unknown", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUploadPart() throws SecurityException, NegativeArraySizeException, NoSuchMethodException {
      Method method = AWSS3AsyncClient.class.getMethod("uploadPart", String.class, String.class, int.class,
               String.class, Payload.class);
      HttpRequest request = processor.createRequest(method, "bucket", "foo", 1, "asdsadasdas", Payloads
               .newStringPayload(""));

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/foo?partNumber=1&uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request, "", "application/unknown", false);

      assertResponseParserClassEquals(method, request, ParseETagHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testCompleteMultipartUpload() throws SecurityException, NegativeArraySizeException,
            NoSuchMethodException {
      Method method = AWSS3AsyncClient.class.getMethod("completeMultipartUpload", String.class, String.class,
               String.class, Map.class);
      HttpRequest request = processor.createRequest(method, "bucket", "foo", "asdsadasdas", ImmutableMap
               .<Integer, String> of(1, "\"a54357aff0632cce46d942af68356b38\""));

      assertRequestLineEquals(request, "POST https://bucket." + url + "/foo?uploadId=asdsadasdas HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(
               request,
               "<CompleteMultipartUpload><Part><PartNumber>1</PartNumber><ETag>\"a54357aff0632cce46d942af68356b38\"</ETag></Part></CompleteMultipartUpload>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ETagFromHttpResponseViaRegex.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testPutBucketEu() throws ArrayIndexOutOfBoundsException, SecurityException, IllegalArgumentException,
            NoSuchMethodException, IOException {
      Method method = AWSS3AsyncClient.class.getMethod("putBucketInRegion", String.class, String.class, Array
               .newInstance(PutBucketOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "EU", "bucket");

      assertRequestLineEquals(request, "PUT https://bucket." + url + "/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: bucket." + url + "\n");
      assertPayloadEquals(request,
               "<CreateBucketConfiguration><LocationConstraint>EU</LocationConstraint></CreateBucketConfiguration>",
               "text/xml", false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState.class);

      checkFilters(request);
   }

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestAWSS3RestClientModule extends AWSS3RestClientModule {

      public TestAWSS3RestClientModule() {
         super();
      }

      @Override
      protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
         return "2009-11-08T15:54:08.897Z";
      }
   }

   @Override
   protected Module createModule() {
      return new TestAWSS3RestClientModule();
   }

}
