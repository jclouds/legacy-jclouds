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
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests annotation parsing of {@code InternetServiceAsyncClient}
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "LayoutAsyncClientTest")
public class InternetServiceAsyncClientTest extends BaseTerremarkEnterpriseCloudAsyncClientTest<InternetServiceAsyncClient> {

   private URI uri = URI.create("/cloudapi/ecloud/internetservices/797");
   private InternetServicePersistenceType persistenceType;
   private InternetService service;
   
   @BeforeMethod
   public void setUp() {

      persistenceType = InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build();
      
      service =  InternetService.builder().href(uri)
            .name("testName")
            .enabled(true)
            .persistence(persistenceType).build();  
   }
   
   public void testGetInternetService() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = InternetServiceAsyncClient.class.getMethod("getInternetService", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, URI.create("/cloudapi/ecloud/internetservices/797"));

      String requestLine = "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/internetservices/797 HTTP/1.1";
      assertRequestLineEquals(httpRequest, requestLine);
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.internetService\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testEditInternetService() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = InternetServiceAsyncClient.class.getMethod("editInternetService", InternetService.class);
      
      HttpRequest httpRequest = processor.createRequest(method, service);

      String requestLine = "PUT https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/internetservices/797 HTTP/1.1";
      String payload = "<InternetService name='testName'><Enabled>true</Enabled><Persistence><Type>None</Type></Persistence></InternetService>".replaceAll("'","\"");
      assertRequestLineEquals(httpRequest, requestLine);
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.task\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, payload, MediaType.APPLICATION_XML, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }


   public void testCreateInternetService() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = InternetServiceAsyncClient.class.getMethod("createInternetService", URI.class, InternetService.class);
      
      URI uri = URI.create("/cloudapi/ecloud/internetServices/publicIps/123/action/createInternetService");
      InternetService createData = service.toBuilder().href(URI.create("")).protocol(Protocol.HTTP).port(2020).build();

      HttpRequest httpRequest = processor.createRequest(method, uri, createData);

      String requestLine = "POST https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/internetServices/publicIps/123/action/createInternetService HTTP/1.1";
      String payload = "<CreateInternetService name='testName'><Protocol>HTTP</Protocol><Port>2020</Port><Enabled>true</Enabled><Persistence><Type>None</Type></Persistence></CreateInternetService>".replaceAll("'","\"");
      assertRequestLineEquals(httpRequest, requestLine);
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.internetService\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, payload, MediaType.APPLICATION_XML, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
   
   @Override
   protected TypeLiteral<RestAnnotationProcessor<InternetServiceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InternetServiceAsyncClient>>() {
      };
   }
}
