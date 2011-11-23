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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.tmrk.enterprisecloud.functions.ReturnEmptyVirtualMachinesOnNotFoundOr404;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests annotation parsing of {@code TaskAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "VirtualMachineAsyncClientTest")
public class VirtualMachineAsyncClientTest extends BaseTerremarkEnterpriseCloudAsyncClientTest<VirtualMachineAsyncClient> {

   public void testGetVirtualMachine() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = VirtualMachineAsyncClient.class.getMethod("getVirtualMachine", URI.class);
      HttpRequest httpRequest = processor.createRequest(method,new URI("/cloudapi/ecloud/virtualMachines/1"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/virtualMachines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/vnd.tmrk.cloud.virtualMachine\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetVirtualMachines() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = VirtualMachineAsyncClient.class.getMethod("getVirtualMachines", URI.class);
      HttpRequest httpRequest = processor.createRequest(method,new URI("/cloudapi/ecloud/virtualMachines/computePools/567"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/virtualMachines/computePools/567 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/vnd.tmrk.cloud.virtualMachine; type=collection\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnEmptyVirtualMachinesOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetAssignedIpAddresses() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = VirtualMachineAsyncClient.class.getMethod("getAssignedIpAddresses", URI.class);
      HttpRequest httpRequest = processor.createRequest(method,new URI("/cloudapi/ecloud/virtualMachines/1/assignedips"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/virtualMachines/1/assignedips HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/vnd.tmrk.cloud.virtualMachineAssignedIps\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetConfigurationOptions() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = VirtualMachineAsyncClient.class.getMethod("getConfigurationOptions", URI.class);
      HttpRequest httpRequest = processor.createRequest(method,new URI("/cloudapi/ecloud/virtualmachines/5504/configurationoptions"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/virtualmachines/5504/configurationoptions HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/vnd.tmrk.cloud.virtualMachineConfigurationOptions\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetHardwareConfiguration() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = VirtualMachineAsyncClient.class.getMethod("getHardwareConfiguration", URI.class);
      HttpRequest httpRequest = processor.createRequest(method,new URI("/cloudapi/ecloud/virtualmachines/5504/hardwareconfiguration"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/virtualmachines/5504/hardwareconfiguration HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/vnd.tmrk.cloud.virtualMachineHardware\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VirtualMachineAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VirtualMachineAsyncClient>>() {
      };
   }
}
