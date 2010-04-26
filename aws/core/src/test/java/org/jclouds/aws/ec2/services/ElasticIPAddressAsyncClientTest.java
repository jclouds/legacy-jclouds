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
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.InetAddress;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ElasticIPAddressAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.ElasticIPAddressAsyncClientTest")
public class ElasticIPAddressAsyncClientTest extends
         BaseEC2AsyncClientTest<ElasticIPAddressAsyncClient> {

   public void testDisassociateAddress() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticIPAddressAsyncClient.class.getMethod("disassociateAddressInRegion",
               Region.class, InetAddress.class);
      GeneratedHttpRequest<ElasticIPAddressAsyncClient> httpMethod = processor.createRequest(
               method, null, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 64\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DisassociateAddress&PublicIp=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAssociateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticIPAddressAsyncClient.class.getMethod("associateAddressInRegion",
               Region.class, InetAddress.class, String.class);
      GeneratedHttpRequest<ElasticIPAddressAsyncClient> httpMethod = processor.createRequest(
               method, null, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), "me");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 75\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=AssociateAddress&InstanceId=me&PublicIp=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testReleaseAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticIPAddressAsyncClient.class.getMethod("releaseAddressInRegion",
               Region.class, InetAddress.class);
      GeneratedHttpRequest<ElasticIPAddressAsyncClient> httpMethod = processor.createRequest(
               method, null, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 59\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=ReleaseAddress&PublicIp=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeAddresses() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticIPAddressAsyncClient.class.getMethod("describeAddressesInRegion",
               Region.class, Array.newInstance(InetAddress.class, 0).getClass());
      GeneratedHttpRequest<ElasticIPAddressAsyncClient> httpMethod = processor.createRequest(
               method, null, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeAddresses&PublicIp.1=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeAddressesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAllocateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticIPAddressAsyncClient.class.getMethod("allocateAddressInRegion",
               Region.class);
      GeneratedHttpRequest<ElasticIPAddressAsyncClient> httpMethod = processor.createRequest(
               method, (Region) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=AllocateAddress");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AllocateAddressResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ElasticIPAddressAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ElasticIPAddressAsyncClient>>() {
      };
   }

}
