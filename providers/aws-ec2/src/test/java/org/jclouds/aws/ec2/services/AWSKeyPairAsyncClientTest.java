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
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.ec2.xml.KeyPairResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AWSKeyPairAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSKeyPairAsyncClientTest")
public class AWSKeyPairAsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSKeyPairAsyncClient> {

   public void testCreateKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSKeyPairAsyncClient.class.getMethod("createKeyPairInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "mykey");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreateKeyPair&KeyName=mykey",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testImportKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSKeyPairAsyncClient.class.getMethod("importKeyPairInRegion", String.class, String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "mykey", "ssh-rsa AA");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=ImportKeyPair&PublicKeyMaterial=c3NoLXJzYSBBQQ%3D%3D&KeyName=mykey",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSKeyPairAsyncClient.class.getMethod("deleteKeyPairInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "mykey");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteKeyPair&KeyName=mykey",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSKeyPairAsyncClient.class.getMethod("describeKeyPairsInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeKeyPairs", "application/x-www-form-urlencoded",
            false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeKeyPairsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSKeyPairAsyncClient.class.getMethod("describeKeyPairsInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeKeyPairs&KeyName.1=1&KeyName.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AWSKeyPairAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AWSKeyPairAsyncClient>>() {
      };
   }

}
