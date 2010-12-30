/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.simpledb;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.simpledb.config.SimpleDBRestClientModule;
import org.jclouds.aws.simpledb.options.ListDomainsOptions;
import org.jclouds.aws.simpledb.xml.ListDomainsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
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

   @RequiresHttp
   @ConfiguresRestClient
   private static final class TestSimpleDBRestClientModule extends SimpleDBRestClientModule {
      @Override
      protected void configure() {
         super.configure();
      }

   }

   public void testListDomainsInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SimpleDBAsyncClient.class.getMethod("listDomainsInRegion", String.class,
            ListDomainsOptions[].class);
      HttpRequest request = processor.createRequest(method);

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
      HttpRequest request = processor.createRequest(method, null, "domainName");

      assertRequestLineEquals(request, "POST https://sdb.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: sdb.amazonaws.com\n");
      assertPayloadEquals(request, "Version=2009-04-15&Action=CreateDomain&DomainName=domainName",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAllRegions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SimpleDBAsyncClient.class.getMethod("createDomainInRegion", String.class, String.class);
      for (String region : Region.ALL_SIMPLEDB) {
         processor.createRequest(method, region, "domainName");
      }
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
      return new RestContextFactory().createContextSpec("simpledb", "identity", "credential", new Properties());
   }

}
