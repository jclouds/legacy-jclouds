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
package org.jclouds.simpledb;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.simpledb.config.SimpleDBRestClientModule;
import org.jclouds.simpledb.options.ListDomainsOptions;
import org.jclouds.simpledb.xml.ListDomainsResponseHandler;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SimpleDBAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SimpleDBAsyncClientTest")
public class SimpleDBAsyncClientTest extends RestClientTest<SimpleDBAsyncClient> {

   protected String provider = "simpledb";

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestSimpleDBRestClientModule extends SimpleDBRestClientModule {
   }

   public void testListDomainsInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SimpleDBAsyncClient.class.getMethod("listDomainsInRegion", String.class,
               ListDomainsOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "POST https://sdb.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sdb.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-04-15&Action=ListDomains", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ListDomainsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateDomainInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SimpleDBAsyncClient.class.getMethod("createDomainInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, null, "domainName");

      assertRequestLineEquals(request, "POST https://sdb.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sdb.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-04-15&Action=CreateDomain&DomainName=domainName",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   // TODO fix this test as it has the wrong arg type
   @Test(enabled = false)
   public void testPutAttributes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SimpleDBAsyncClient.class.getMethod("putAttributes", String.class, String.class, Map.class);
      GeneratedHttpRequest request = processor.createRequest(method, null, "domainName");

      assertRequestLineEquals(request, "POST https://sdb.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sdb.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-04-15&Action=PutAttributes&DomainName=domainName&ItemName=itemName"
               + "&Attribute.1.Name=name" + "&Attribute.1.Value=fuzzy" + "&Attribute.1.Replace=true",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SimpleDBAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SimpleDBAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new TestSimpleDBRestClientModule();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      // TODO take this out, when the service is registered in jclouds-core/rest.properties
      Properties restProperties = new Properties();
      restProperties.setProperty(provider + ".contextbuilder", SimpleDBContextBuilder.class.getName());
      restProperties.setProperty(provider + ".propertiesbuilder", SimpleDBPropertiesBuilder.class.getName());
      return new RestContextFactory(restProperties).createContextSpec(provider, "foo", "bar", getProperties());
   }

}
