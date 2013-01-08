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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.reflect.Invokable;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * Tests behavior of {@code ElasticIPAddressAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ElasticIPAddressAsyncClientTest")
public class ElasticIPAddressAsyncClientTest extends BaseEC2AsyncClientTest<ElasticIPAddressAsyncClient> {

   public void testDisassociateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(ElasticIPAddressAsyncClient.class.getMethod("disassociateAddressInRegion", String.class,
            String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "127.0.0.1"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DisassociateAddress&PublicIp=127.0.0.1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest associateAddress = HttpRequest.builder().method("POST")
                                             .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                             .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                             .addFormParam("Action", "AssociateAddress")
                                             .addFormParam("InstanceId", "me")
                                             .addFormParam("PublicIp", "127.0.0.1")
                                             .addFormParam("Signature", "YmPyvEljuFw0INSUbQx5xAhC/1GQ4a1Ht6TdoXeMc9Y%3D")
                                             .addFormParam("SignatureMethod", "HmacSHA256")
                                             .addFormParam("SignatureVersion", "2")
                                             .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                             .addFormParam("Version", "2010-06-15")
                                             .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAssociateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(ElasticIPAddressAsyncClient.class.getMethod("associateAddressInRegion", String.class,
            String.class, String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "127.0.0.1", "me"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, associateAddress.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testReleaseAddress() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(ElasticIPAddressAsyncClient.class.getMethod("releaseAddressInRegion", String.class, String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "127.0.0.1"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=ReleaseAddress&PublicIp=127.0.0.1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeAddresses() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(ElasticIPAddressAsyncClient.class.getMethod("describeAddressesInRegion", String.class,
            String[].class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "127.0.0.1"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeAddresses&PublicIp.1=127.0.0.1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeAddressesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAllocateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(ElasticIPAddressAsyncClient.class.getMethod("allocateAddressInRegion", String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=AllocateAddress", "application/x-www-form-urlencoded",
            false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AllocateAddressResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
