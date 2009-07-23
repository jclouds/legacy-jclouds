/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudservers;

import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withMetadata;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withSharedIpGroup;
import static org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.rackspace.cloudservers.options.RebuildServerOptions.Builder.withImage;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.changesSince;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rackspace.Authentication;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.DailyBackup;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.jclouds.rackspace.cloudservers.domain.WeeklyBackup;
import org.jclouds.rackspace.cloudservers.functions.ParseAddressesFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseBackupScheduleFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseInetAddressListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupListFromGsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ReturnFlavorNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnImageNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnServerNotFoundOn404;
import org.jclouds.rackspace.cloudservers.functions.ReturnSharedIpGroupNotFoundOn404;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.cloudservers.options.RebuildServerOptions;
import org.jclouds.rest.JaxrsAnnotationProcessor;
import org.jclouds.rest.config.JaxrsModule;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code CloudServersConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.CloudServersConnectionTest")
public class CloudServersConnectionTest {

   JaxrsAnnotationProcessor.Factory factory;

   private static final Class<? extends ListOptions[]> listOptionsVarargsClass = new ListOptions[] {}
            .getClass();

   private static final Class<? extends CreateServerOptions[]> createServerOptionsVarargsClass = new CreateServerOptions[] {}
            .getClass();

   public void testCreateServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               2, 1 });
      assertEquals("{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1}}", httpMethod
               .getEntity());
      validateCreateServer(method, httpMethod);
   }

   public void testCreateServerWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               2, 1, withSharedIpGroup(2) });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2}}",
               httpMethod.getEntity());
      validateCreateServer(method, httpMethod);
   }

   public void testCreateServerWithFile() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               2, 1, new CreateServerOptions[] { withFile("/etc/jclouds", "foo".getBytes()) } });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"personality\":[{\"path\":\"/etc/jclouds\",\"contents\":\"Zm9v\"}]}}",
               httpMethod.getEntity());
      validateCreateServer(method, httpMethod);
   }

   public void testCreateServerWithMetadata() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               2, 1, withMetadata(ImmutableMap.of("foo", "bar")) });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"metadata\":{\"foo\":\"bar\"}}}",
               httpMethod.getEntity());
      validateCreateServer(method, httpMethod);
   }

   public void testCreateServerWithIpGroupAndSharedIp() throws SecurityException,
            NoSuchMethodException, UnknownHostException {
      Method method = CloudServersConnection.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {
               "ralphie",
               2,
               1,
               withSharedIpGroup(2).withSharedIp(
                        InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 })) });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2,\"addresses\":{\"public\":[\"127.0.0.1\"]}}}",
               httpMethod.getEntity());
      validateCreateServer(method, httpMethod);
   }

   private void validateCreateServer(Method method, HttpRequest httpMethod) {
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseServerFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertNotNull(processor.getMapEntityBinderOrNull(method, new Object[] { "", 1, 2,
               new CreateServerOptions[] { CreateServerOptions.Builder.withSharedIpGroup(1) } }));
   }

   public void testListServers() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listServers", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseServerListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   DateTime now = new DateTime();

   public void testListServersOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listServers", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseServerListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListServersDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listServers", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseServerListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testGetServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getServer", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseServerFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnServerNotFoundOn404.class);
   }

   public void testListFlavors() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listFlavors", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseFlavorListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListFlavorsOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listFlavors", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseFlavorListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListFlavorsDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listFlavors", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseFlavorListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListFlavorsDetailOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class
               .getMethod("listFlavors", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails().changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseFlavorListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testGetFlavor() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getFlavor", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/flavors/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseFlavorFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFlavorNotFoundOn404.class);
   }

   public void testListImages() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImages", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListImagesDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImages", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListImagesOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImages", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListImagesDetailOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listImages", listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails().changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testGetImage() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getImage", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnImageNotFoundOn404.class);
   }

   public void testDeleteServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("deleteServer", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testShareIpNoConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersConnection.class.getMethod("shareIp", InetAddress.class,
               int.class, int.class, boolean.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {
               InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), 2, 3, false });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"shareIp\":{\"sharedIpGroupId\":3}}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testShareIpConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersConnection.class.getMethod("shareIp", InetAddress.class,
               int.class, int.class, boolean.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {
               InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), 2, 3, true });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"shareIp\":{\"sharedIpGroupId\":3,\"configureServer\":true}}", httpMethod
               .getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testUnshareIpNoConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersConnection.class.getMethod("unshareIp", InetAddress.class,
               int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {
               InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), 2, 3, false });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testReplaceBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("replaceBackupSchedule", int.class,
               BackupSchedule.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2,
               new BackupSchedule(WeeklyBackup.MONDAY, DailyBackup.H_0800_1000, true) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(
               "{\"backupSchedule\":{\"daily\":\"H_0800_1000\",\"enabled\":true,\"weekly\":\"MONDAY\"}}",
               httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testDeleteBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("deleteBackupSchedule", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testChangeAdminPass() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("changeAdminPass", int.class,
               String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2, "foo" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"server\":{\"adminPass\":\"foo\"}}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testChangeServerName() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("renameServer", int.class,
               String.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2, "foo" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"server\":{\"name\":\"foo\"}}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testListSharedIpGroups() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListSharedIpGroupsOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListSharedIpGroupsDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails() });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListSharedIpGroupsDetailOptions() throws SecurityException,
            NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method,
               new Object[] { withDetails().changesSince(now).maxResults(1).startAt(2) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups/detail");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json" + "&limit=1&changes-since="
               + now.getMillis() / 1000 + "&offset=2");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupListFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testGetSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("getSharedIpGroup", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups/2");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnSharedIpGroupNotFoundOn404.class);
   }

   private static final Class<? extends CreateSharedIpGroupOptions[]> createSharedIpGroupOptionsVarargsClass = new CreateSharedIpGroupOptions[] {}
            .getClass();

   public void testCreateSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createSharedIpGroup", String.class,
               createSharedIpGroupOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor
               .createRequest(endpoint, method, new Object[] { "ralphie" });
      assertEquals("{\"sharedIpGroup\":{\"name\":\"ralphie\"}}", httpMethod.getEntity());
      validateCreateSharedIpGroup(method, httpMethod);
   }

   public void testCreateSharedIpGroupWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createSharedIpGroup", String.class,
               createSharedIpGroupOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               withServer(2) });
      assertEquals("{\"sharedIpGroup\":{\"name\":\"ralphie\",\"server\":2}}", httpMethod
               .getEntity());
      validateCreateSharedIpGroup(method, httpMethod);
   }

   private void validateCreateSharedIpGroup(Method method, HttpRequest httpMethod) {
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseSharedIpGroupFromGsonResponse.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      assertNotNull(processor.getMapEntityBinderOrNull(method, new Object[] { "",
               new CreateSharedIpGroupOptions[] { withServer(2) } }));
   }

   public void testDeleteSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("deleteSharedIpGroup", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/shared_ip_groups/2");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testListAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listAddresses", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseAddressesFromGsonResponse.class);
   }

   public void testListPublicAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listPublicAddresses", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips/public");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseInetAddressListFromGsonResponse.class);
   }

   public void testListPrivateAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listPrivateAddresses", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/ips/private");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseInetAddressListFromGsonResponse.class);
   }

   public void testListBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("listBackupSchedule", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseBackupScheduleFromGsonResponse.class);
   }

   public void testCreateImageWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("createImageFromServer", String.class,
               int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { "ralphie",
               2 });
      assertEquals("{\"image\":{\"serverId\":2,\"name\":\"ralphie\"}}", httpMethod.getEntity());
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/images");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method).getClass(),
               ParseImageFromGsonResponse.class);
      assertNotNull(processor.createExceptionParserOrNullIfNotFound(method));
      assertNotNull(processor.getMapEntityBinderOrNull(method, new Object[] { "", 2 }));
   }

   private static final Class<? extends RebuildServerOptions[]> rebuildServerOptionsVarargsClass = new RebuildServerOptions[] {}
            .getClass();

   public void testRebuildServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("rebuildServer", int.class,
               rebuildServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 3 });
      assertEquals("{\"rebuild\":{}}", httpMethod.getEntity());
      validateRebuildServer(method, httpMethod);
   }

   public void testRebuildServerWithImage() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("rebuildServer", int.class,
               rebuildServerOptionsVarargsClass);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 3,
               withImage(2) });
      assertEquals("{\"rebuild\":{\"imageId\":2}}", httpMethod.getEntity());
      validateRebuildServer(method, httpMethod);
   }

   private void validateRebuildServer(Method method, HttpRequest httpMethod) {
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/3/action");
      assertEquals(httpMethod.getEndpoint().getQuery(), "format=json");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
      assertNotNull(processor.getMapEntityBinderOrNull(method, new Object[] { "",
               new RebuildServerOptions[] { withImage(2) } }));
   }

   public void testReboot() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("rebootServer", int.class,
               RebootType.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2,
               RebootType.HARD });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"reboot\":{\"type\":\"HARD\"}}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("resizeServer", int.class, int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2, 3 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"resize\":{\"flavorId\":3}}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testConfirmResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("confirmResizeServer", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"confirmResize\":null}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   public void testRevertResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersConnection.class.getMethod("revertResizeServer", int.class);
      URI endpoint = URI.create("http://localhost");
      HttpRequest httpMethod = processor.createRequest(endpoint, method, new Object[] { 2 });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(httpMethod.getMethod(), HttpMethod.POST);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(httpMethod.getEntity().toString().getBytes().length + ""));
      assertEquals(httpMethod.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"revertResize\":null}", httpMethod.getEntity());
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method).getClass(), ReturnTrueIf2xx.class);
   }

   JaxrsAnnotationProcessor processor;

   @BeforeClass
   void setupFactory() {
      factory = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
         }

         @SuppressWarnings("unused")
         @Provides
         @Authentication
         public String getAuthToken() {
            return "testtoken";
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule()).getInstance(
               JaxrsAnnotationProcessor.Factory.class);
      processor = factory.create(CloudServersConnection.class);
   }

}
