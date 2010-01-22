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
package org.jclouds.vcloud.terremark;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.disabled;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.domain.InternetServiceConfiguration;
import org.jclouds.vcloud.terremark.domain.NodeConfiguration;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.options.AddInternetServiceOptions;
import org.jclouds.vcloud.terremark.options.AddNodeOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.xml.ComputeOptionsHandler;
import org.jclouds.vcloud.terremark.xml.CustomizationParametersHandler;
import org.jclouds.vcloud.terremark.xml.InternetServiceHandler;
import org.jclouds.vcloud.terremark.xml.InternetServicesHandler;
import org.jclouds.vcloud.terremark.xml.IpAddressesHandler;
import org.jclouds.vcloud.terremark.xml.NodeHandler;
import org.jclouds.vcloud.terremark.xml.NodesHandler;
import org.jclouds.vcloud.terremark.xml.TerremarkVDCHandler;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TerremarkVCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "vcloud.TerremarkVCloudAsyncClientTest")
public class TerremarkVCloudAsyncClientTest extends RestClientTest<TerremarkVCloudAsyncClient> {
   /**
    * ignore parameter of catalog id since this doesn't work
    */
   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getCatalog", String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1");

      assertRequestLineEquals(httpMethod, "GET http://catalog HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetIpAddressesForNetwork() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getIpAddressesForNetwork",
               String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "2");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/network/2/ipAddresses HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, IpAddressesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDefaultVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getDefaultVDC");
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://vdc HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getVDC", String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vdc/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerremarkVDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testInstantiateVAppTemplate() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC",
               String.class, String.class, String.class, Array.newInstance(
                        InstantiateVAppTemplateOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1", "name", 3 + "");

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.vApp+xml\nContent-Length: 1657\nContent-Type: application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams-test.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testInstantiateVAppTemplateOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC",
               String.class, String.class, String.class, Array.newInstance(
                        InstantiateVAppTemplateOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1", "name", 3 + "", TerremarkInstantiateVAppTemplateOptions.Builder.processorCount(
                        1).memory(512).inRow("row").inGroup("group").withPassword("password")
                        .inNetwork(URI.create("http://network")));

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.vApp+xml\nContent-Length: 1920\nContent-Type: application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/InstantiateVAppTemplateParams-options-test.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddInternetService() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addInternetServiceToVDC",
               String.class, String.class, Protocol.class, int.class, Array.newInstance(
                        AddInternetServiceOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1", "name", Protocol.TCP, 22);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vdc/1/internetServices HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 303\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService-test2.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddInternetServiceOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addInternetServiceToVDC",
               String.class, String.class, Protocol.class, int.class, Array.newInstance(
                        AddInternetServiceOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1", "name", Protocol.TCP, 22, disabled().withDescription("yahoo"));

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vdc/1/internetServices HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 341\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService-options-test.xml")));
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetAllInternetServices() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getAllInternetServicesInVDC",
               String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vdc/1/internetServices HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServicesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetInternetService() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getInternetService", int.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "GET http://vcloud/internetServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteInternetService() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class
               .getMethod("deleteInternetService", int.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "DELETE http://vcloud/internetServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddInternetServiceToExistingIp() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addInternetServiceToExistingIp",
               int.class, String.class, Protocol.class, int.class, Array.newInstance(
                        AddInternetServiceOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, "name", Protocol.TCP, 22);

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/publicIps/12/InternetServices HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 303\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService-test2.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddInternetServiceToExistingIpOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addInternetServiceToExistingIp",
               int.class, String.class, Protocol.class, int.class, Array.newInstance(
                        AddInternetServiceOptions.class, 0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, "name", Protocol.TCP, 22, disabled().withDescription("yahoo"));

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/publicIps/12/InternetServices HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 341\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService-options-test.xml")));
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addNode", int.class,
               InetAddress.class, String.class, int.class, Array.newInstance(AddNodeOptions.class,
                        0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, InetAddress.getByName("10.2.2.2"), "name", 22);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/internetServices/12/nodes HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 298\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService-test2.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddNodeOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("addNode", int.class,
               InetAddress.class, String.class, int.class, Array.newInstance(AddNodeOptions.class,
                        0).getClass());
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, InetAddress.getByName("10.2.2.2"), "name", 22, AddNodeOptions.Builder.disabled()
                        .withDescription("yahoo"));

      assertRequestLineEquals(httpMethod, "POST http://vcloud/internetServices/12/nodes HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 336\nContent-Type: application/xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService-options-test.xml")));
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getNode", int.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "GET http://vcloud/nodeServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testConfigureNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("configureNode", int.class,
               NodeConfiguration.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, new NodeConfiguration().changeDescriptionTo("eggs"));

      assertRequestLineEquals(httpMethod, "PUT http://vcloud/nodeServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 145\nContent-Type: application/xml\n");
      assertPayloadEquals(
               httpMethod,
               "<NodeService xmlns=\"urn:tmrk:vCloudExpress-1.0\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><Description>eggs</Description></NodeService>");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testConfigureInternetService() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("configureInternetService",
               int.class, InternetServiceConfiguration.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12, new InternetServiceConfiguration().changeDescriptionTo("eggs"));

      assertRequestLineEquals(httpMethod, "PUT http://vcloud/internetServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Accept: application/xml\nContent-Length: 153\nContent-Type: application/xml\n");
      assertPayloadEquals(
               httpMethod,
               "<InternetService xmlns=\"urn:tmrk:vCloudExpress-1.0\" xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\"><Description>eggs</Description></InternetService>");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InternetServiceHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetNodes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getNodes", int.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "GET http://vcloud/internetServices/12/nodes HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NodesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteNode() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("deleteNode", int.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "DELETE http://vcloud/nodeServices/12 HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetComputeOptionsOfVApp() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getComputeOptionsOfVApp",
               String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vapp/12/options/compute HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ComputeOptionsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetCustomizationOptionsOfVApp() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getCustomizationOptionsOfVApp",
               String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod,
               "GET http://vcloud/vapp/12/options/customization HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetComputeOptionsOfCatalogItem() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod("getComputeOptionsOfCatalogItem",
               String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod,
               "GET http://vcloud/catalogItem/12/options/compute HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ComputeOptionsHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetCustomizationOptionsOfCatalogItem() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = TerremarkVCloudAsyncClient.class.getMethod(
               "getCustomizationOptionsOfCatalogItem", String.class);
      GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod = processor.createRequest(method,
               12);

      assertRequestLineEquals(httpMethod,
               "GET http://vcloud/catalogItem/12/options/customization HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CustomizationParametersHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<TerremarkVCloudAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TerremarkVCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TerremarkVCloudAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Properties props = new Properties();
            props.put(PROPERTY_VCLOUD_DEFAULT_NETWORK,
                     "https://vcloud.safesecureweb.com/network/1990");
            Jsr330.bindProperties(binder(), new TerremarkVCloudPropertiesBuilder(props).build());
            bind(URI.class).annotatedWith(Catalog.class).toInstance(URI.create("http://catalog"));
            bind(String.class).annotatedWith(CatalogItemRoot.class)
                     .toInstance("http://catalogItem");
            bind(URI.class).annotatedWith(VCloudApi.class).toInstance(URI.create("http://vcloud"));
            bind(String.class).annotatedWith(VAppRoot.class).toInstance("http://vapp");
            bind(URI.class).annotatedWith(VDC.class).toInstance(URI.create("http://vdc"));
            bind(URI.class).annotatedWith(Network.class).toInstance(URI.create("http://network"));
            bind(SetVCloudTokenCookie.class).toInstance(
                     new SetVCloudTokenCookie(new Provider<String>() {

                        public String get() {
                           return "token";
                        }

                     }));

            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @Named("CreateInternetService")
         String provideCreateInternetService() throws IOException {
            return Utils.toStringAndClose(getClass().getResourceAsStream(
                     "/terremark/CreateInternetService.xml"));
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         @Named("CreateNodeService")
         String provideCreateNodeService() throws IOException {
            return Utils.toStringAndClose(getClass().getResourceAsStream(
                     "/terremark/CreateNodeService.xml"));
         }
      };
   }

}
