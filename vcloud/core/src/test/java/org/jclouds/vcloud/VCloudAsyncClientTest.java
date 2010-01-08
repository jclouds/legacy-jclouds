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
package org.jclouds.vcloud;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.inject.Provider;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.util.Utils;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.jclouds.vcloud.endpoints.internal.VAppRoot;
import org.jclouds.vcloud.endpoints.internal.VAppTemplateRoot;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.jclouds.vcloud.xml.NetworkHandler;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VAppTemplateHandler;
import org.jclouds.vcloud.xml.VDCHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudAsyncClientTest")
public class VCloudAsyncClientTest extends RestClientTest<VCloudAsyncClient> {

   public void testInstantiateVAppTemplate() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC",
               String.class, String.class, String.class, Array.newInstance(
                        InstantiateVAppTemplateOptions.class, 0).getClass());
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1",
               "CentOS 01", 3 + "");

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.vApp+xml\nContent-Length: 638\nContent-Type: application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/newvapp-hosting.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testInstantiateVAppTemplateOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("instantiateVAppTemplateInVDC",
               String.class, String.class, String.class, Array.newInstance(
                        InstantiateVAppTemplateOptions.class, 0).getClass());
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1",
               "CentOS 01", 3 + "", processorCount(1).memory(512).disk(1024).inNetwork(
                        URI.create("https://vcloud.safesecureweb.com/network/1990")));

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vdc/1/action/instantiateVAppTemplate HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.vApp+xml\nContent-Length: 2022\nContent-Type: application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/newvapp-hostingcpumemdisk.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCloneVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cloneVAppInVDC", String.class,
               String.class, String.class, Array.newInstance(CloneVAppOptions.class, 0).getClass());
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1",
               "4181", "New Name");

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vdc/1/action/cloneVApp HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.task+xml\nContent-Length: 398\nContent-Type: application/vnd.vmware.vcloud.cloneVAppParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/cloneVApp-default.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCloneVAppOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cloneVAppInVDC", String.class,
               String.class, String.class, Array.newInstance(CloneVAppOptions.class, 0).getClass());
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1",
               "201", "New Linux Server", new CloneVAppOptions().deploy().powerOn()
                        .withDescription("The description of the new vApp"));

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vdc/1/action/cloneVApp HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Accept: application/vnd.vmware.vcloud.task+xml\nContent-Length: 461\nContent-Type: application/vnd.vmware.vcloud.cloneVAppParams+xml\n");
      assertPayloadEquals(httpMethod, Utils.toStringAndClose(getClass().getResourceAsStream(
               "/cloneVApp.xml")));

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDefaultOrganization() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("getDefaultOrganization");
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://org HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testOrganization() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getOrganization", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/org/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.org+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, OrgHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDefaultCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getDefaultCatalog");
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://catalog HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getCatalog", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/catalog/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getNetwork", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "2");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/network/2 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.network+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, NetworkHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCatalogItem() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getCatalogItem", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "2");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/catalogItem/2 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testVAppTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getVAppTemplate", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "2");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vAppTemplate/2 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vAppTemplate+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppTemplateHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDefaultVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getDefaultVDC");
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://vdc HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getVDC", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vdc/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDefaultTasksList() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudAsyncClient.class.getMethod("getDefaultTasksList");
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://tasksList HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetTasksList() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getTasksList", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/tasksList/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("deployVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vApp/1/action/deploy HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "GET http://vcloud/vApp/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vApp+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VAppHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testUndeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("undeployVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vApp/1/action/undeploy HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("deleteVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "DELETE http://vcloud/vApp/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPowerOn() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("powerOnVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vApp/1/power/action/powerOn HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPowerOff() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("powerOffVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vApp/1/power/action/powerOff HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testReset() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("resetVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vApp/1/power/action/reset HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSuspend() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("suspendVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod, "POST http://vcloud/vApp/1/power/action/suspend HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testShutdown() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("shutdownVApp", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, 1);

      assertRequestLineEquals(httpMethod,
               "POST http://vcloud/vApp/1/power/action/shutdown HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("getTask", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "GET http://vcloud/task/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCancelTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudAsyncClient.class.getMethod("cancelTask", String.class);
      GeneratedHttpRequest<VCloudAsyncClient> httpMethod = processor.createRequest(method, "1");

      assertRequestLineEquals(httpMethod, "POST http://vcloud/task/1/action/cancel HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<VCloudAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudAsyncClient>>() {
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
            Jsr330.bindProperties(binder(), new VCloudPropertiesBuilder(props).build());
            bind(URI.class).annotatedWith(Org.class).toInstance(URI.create("http://org"));
            bind(URI.class).annotatedWith(Catalog.class).toInstance(URI.create("http://catalog"));
            bind(String.class).annotatedWith(CatalogItemRoot.class)
                     .toInstance("http://catalogItem");
            bind(String.class).annotatedWith(VAppTemplateRoot.class).toInstance(
                     "https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate");
            bind(String.class).annotatedWith(VAppRoot.class).toInstance(
                     "https://services.vcloudexpress.terremark.com/api/vapp");
            bind(URI.class).annotatedWith(VDC.class).toInstance(URI.create("http://vdc"));
            bind(URI.class).annotatedWith(TasksList.class).toInstance(
                     URI.create("http://tasksList"));
            bind(URI.class).annotatedWith(VCloudApi.class).toInstance(URI.create("http://vcloud"));
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
      };
   }

}
