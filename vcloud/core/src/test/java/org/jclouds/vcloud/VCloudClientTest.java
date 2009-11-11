/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Provider;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.endpoints.VDC;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.TaskHandler;
import org.jclouds.vcloud.xml.TasksListHandler;
import org.jclouds.vcloud.xml.VDCHandler;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudClientTest")
public class VCloudClientTest extends RestClientTest<VCloudClient> {

   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("getCatalog");
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://catalog HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDefaultVDC() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("getDefaultVDC");
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://vdc HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.vdc+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, VDCHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDefaultTasksList() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = VCloudClient.class.getMethod("getDefaultTasksList");
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "GET http://tasksList HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.tasksList+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TasksListHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("deploy", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/action/deploy HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testUndeployVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("undeploy", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/action/undeploy HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteVApp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("delete", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "DELETE https://services.vcloudexpress.terremark.com/vapp/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPowerOn() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("powerOn", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/power/action/powerOn HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testPowerOff() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("powerOff", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/power/action/powerOff HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testReset() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("reset", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/power/action/reset HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSuspend() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("suspend", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/power/action/suspend HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testShutdown() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("shutdown", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/vapp/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/vapp/1/power/action/shutdown HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("getTask", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/task/1"));

      assertRequestLineEquals(httpMethod,
               "GET https://services.vcloudexpress.terremark.com/task/1 HTTP/1.1");
      assertHeadersEqual(httpMethod, "Accept: application/vnd.vmware.vcloud.task+xml\n");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TaskHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCancelTask() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudClient.class.getMethod("cancelTask", URI.class);
      GeneratedHttpRequest<VCloudClient> httpMethod = processor.createRequest(method, URI
               .create("https://services.vcloudexpress.terremark.com/task/1"));

      assertRequestLineEquals(httpMethod,
               "POST https://services.vcloudexpress.terremark.com/task/1/action/cancel HTTP/1.1");
      assertHeadersEqual(httpMethod, "");
      assertEntityEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<VCloudClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(Catalog.class).toInstance(URI.create("http://catalog"));
            bind(URI.class).annotatedWith(VDC.class).toInstance(URI.create("http://vdc"));
            bind(URI.class).annotatedWith(TasksList.class).toInstance(
                     URI.create("http://tasksList"));
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
