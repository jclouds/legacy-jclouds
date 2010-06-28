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
package org.jclouds.vcloud.hostingdotcom;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.inject.Named;

import org.jclouds.http.RequiresHttp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.hostingdotcom.config.HostingDotComVCloudRestClientModule;
import org.jclouds.vcloud.internal.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient.VCloudSession;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code HostingDotComVCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "vcloud.HostingDotComVCloudAsyncClientTest")
public class HostingDotComVCloudAsyncClientTest extends
         RestClientTest<HostingDotComVCloudAsyncClient> {
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = HostingDotComVCloudAsyncClient.class.getMethod("getDefaultCatalog");
      GeneratedHttpRequest<HostingDotComVCloudAsyncClient> httpMethod = processor
               .createRequest(method);

      assertRequestLineEquals(httpMethod, "GET https://catalog HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.catalog+xml\nContent-Type: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<HostingDotComVCloudAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<HostingDotComVCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<HostingDotComVCloudAsyncClient>>() {
      };
   }
   @Override
   protected Module createModule() {
      return new HostingDotComVCloudRestClientModuleExtension();
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("hostingdotcom", "identity", "credential",
               new Properties());
   }

   @RequiresHttp
   @ConfiguresRestClient
   protected static class HostingDotComVCloudRestClientModuleExtension extends
            HostingDotComVCloudRestClientModule {
      @Override
      protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
               @Named(PROPERTY_API_VERSION) String version) {
         return URI.create("https://vcloud/login");
      }

      @Override
      protected URI provideOrg(@Org Iterable<NamedResource> orgs) {
         return URI.create("https://org");

      }

      @Override
      protected URI provideCatalog(Organization org, @Named(PROPERTY_IDENTITY) String user) {
         return URI.create("https://catalog");

      }

      @Override
      protected Organization provideOrganization(VCloudClient discovery) {
         return null;
      }

      @Override
      protected Iterable<NamedResource> provideOrgs(Supplier<VCloudSession> cache,
               @Named(PROPERTY_IDENTITY) String user) {
         return null;
      }

      @Override
      protected URI provideDefaultTasksList(Organization org) {
         return URI.create("https://taskslist");
      }

      @Override
      protected URI provideDefaultVDC(Organization org) {
         return URI.create("https://vdc/1");
      }

      @Override
      protected URI provideDefaultNetwork(VCloudClient client) {
         return URI.create("https://vcloud.safesecureweb.com/network/1990");
      }

   }
}
