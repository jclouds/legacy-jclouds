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
package org.jclouds.rackspace.cloudservers;

import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withMetadata;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withSharedIpGroup;
import static org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.changesSince;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.withDetails;
import static org.jclouds.rackspace.cloudservers.options.RebuildServerOptions.Builder.withImage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rackspace.cloudservers.config.CloudServersRestClientModule;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.DailyBackup;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.jclouds.rackspace.cloudservers.domain.WeeklyBackup;
import org.jclouds.rackspace.cloudservers.functions.ParseAddressesFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseBackupScheduleFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseInetAddressListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.cloudservers.options.RebuildServerOptions;
import org.jclouds.rackspace.filters.AddTimestampQuery;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code CloudServersClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.CloudServersClientTest")
public class CloudServersAsyncClientTest extends RestClientTest<CloudServersAsyncClient> {
   private static final Class<? extends ListOptions[]> listOptionsVarargsClass = new ListOptions[] {}
            .getClass();
   private static final Class<? extends CreateServerOptions[]> createServerOptionsVarargsClass = new CreateServerOptions[] {}
            .getClass();

   public void testCreateServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2, 1 });
      assertEquals("{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1}}", request
               .getPayload().getRawContent());
      validateCreateServer(method, request, null);
   }

   public void testCreateServerWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2, 1,
               withSharedIpGroup(2) });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2}}",
               request.getPayload().getRawContent());
      validateCreateServer(method, request, null);
   }

   public void testCreateServerWithFile() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2, 1,
               new CreateServerOptions[] { withFile("/etc/jclouds", "foo".getBytes()) } });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"personality\":[{\"path\":\"/etc/jclouds\",\"contents\":\"Zm9v\"}]}}",
               request.getPayload().getRawContent());
      validateCreateServer(method, request, null);
   }

   public void testCreateServerWithMetadata() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2, 1,
               withMetadata(ImmutableMap.of("foo", "bar")) });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"metadata\":{\"foo\":\"bar\"}}}",
               request.getPayload().getRawContent());
      validateCreateServer(method, request, null);
   }

   public void testCreateServerWithIpGroupAndSharedIp() throws SecurityException,
            NoSuchMethodException, UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("createServer", String.class,
               int.class, int.class, createServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2, 1,
               withSharedIpGroup(2).withSharedIp("127.0.0.1") });
      assertEquals(
               "{\"server\":{\"name\":\"ralphie\",\"imageId\":2,\"flavorId\":1,\"sharedIpGroupId\":2,\"addresses\":{\"public\":[\"127.0.0.1\"]}}}",
               request.getPayload().getRawContent());
      validateCreateServer(method, request, null);
   }

   private void validateCreateServer(Method method, HttpRequest request, Object[] args) {
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseServerFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertNotNull(processor.getMapPayloadBinderOrNull(method, new Object[] { "", 1, 2,
               new CreateServerOptions[] { CreateServerOptions.Builder.withSharedIpGroup(1) } }));
   }

   public void testDeleteImage() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteImage", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images/2");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseOnNotFoundOr404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
   }

   public void testListServers() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseServerListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   Date now = new Date();

   public void testListServersOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { changesSince(now)
               .maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseServerListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListServersDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listServers",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails() });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseServerListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testGetServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getServer", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseServerFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnNotFoundOr404.class);
   }

   public void testListFlavors() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/flavors");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseFlavorListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListFlavorsOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { changesSince(now)
               .maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/flavors");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseFlavorListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListFlavorsDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails() });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/flavors/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseFlavorListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListFlavorsDetailOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listFlavors",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails()
               .changesSince(now).maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/flavors/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseFlavorListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testGetFlavor() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getFlavor", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/flavors/2");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseFlavorFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnNotFoundOr404.class);
   }

   public void testListImages() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class
               .getMethod("listImages", listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListImagesDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class
               .getMethod("listImages", listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails() });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListImagesOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class
               .getMethod("listImages", listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { changesSince(now)
               .maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListImagesDetailOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class
               .getMethod("listImages", listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails()
               .changesSince(now).maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testGetImage() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getImage", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images/2");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnNotFoundOr404.class);
   }

   public void testDeleteServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteServer", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseOnNotFoundOr404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
   }

   public void testShareIpNoConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("shareIp", String.class, int.class,
               int.class, boolean.class);

      HttpRequest request = processor.createRequest(method,
               new Object[] { "127.0.0.1", 2, 3, false });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"shareIp\":{\"sharedIpGroupId\":3}}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testShareIpConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("shareIp", String.class, int.class,
               int.class, boolean.class);

      HttpRequest request = processor.createRequest(method,
               new Object[] { "127.0.0.1", 2, 3, true });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"shareIp\":{\"sharedIpGroupId\":3,\"configureServer\":true}}", request
               .getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testUnshareIpNoConfig() throws SecurityException, NoSuchMethodException,
            UnknownHostException {
      Method method = CloudServersAsyncClient.class.getMethod("unshareIp", String.class, int.class);

      HttpRequest request = processor.createRequest(method,
               new Object[] { "127.0.0.1", 2, 3, false });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips/public/127.0.0.1");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testReplaceBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("replaceBackupSchedule", int.class,
               BackupSchedule.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2,
               new BackupSchedule(WeeklyBackup.MONDAY, DailyBackup.H_0800_1000, true) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(request.getPayload().getRawContent(),
               "{\"backupSchedule\":{\"daily\":\"H_0800_1000\",\"enabled\":true,\"weekly\":\"MONDAY\"}}");
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseOn404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testDeleteBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteBackupSchedule", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseOnNotFoundOr404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
   }

   public void testChangeAdminPass() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("changeAdminPass", int.class,
               String.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2, "foo" });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"server\":{\"adminPass\":\"foo\"}}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testChangeServerName() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("renameServer", int.class,
               String.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2, "foo" });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"server\":{\"name\":\"foo\"}}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testListSharedIpGroups() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListSharedIpGroupsOptions() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { changesSince(now)
               .maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListSharedIpGroupsDetail() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails() });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListSharedIpGroupsDetailOptions() throws SecurityException,
            NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listSharedIpGroups",
               listOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { withDetails()
               .changesSince(now).maxResults(1).startAt(2) });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups/detail");
      assertEquals(request.getEndpoint().getQuery(), "format=json&changes-since=" + now.getTime()
               / 1000 + "&limit=1&offset=2");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupListFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testGetSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getSharedIpGroup", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups/2");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnNotFoundOr404.class);
   }

   private static final Class<? extends CreateSharedIpGroupOptions[]> createSharedIpGroupOptionsVarargsClass = new CreateSharedIpGroupOptions[] {}
            .getClass();

   public void testCreateSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createSharedIpGroup", String.class,
               createSharedIpGroupOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie" });
      assertEquals("{\"sharedIpGroup\":{\"name\":\"ralphie\"}}", request.getPayload()
               .getRawContent());
      validateCreateSharedIpGroup(method, request);
   }

   public void testCreateSharedIpGroupWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createSharedIpGroup", String.class,
               createSharedIpGroupOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method,
               new Object[] { "ralphie", withServer(2) });
      assertEquals("{\"sharedIpGroup\":{\"name\":\"ralphie\",\"server\":2}}", request.getPayload()
               .getRawContent());
      validateCreateSharedIpGroup(method, request);
   }

   private void validateCreateSharedIpGroup(Method method, HttpRequest request) {
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseSharedIpGroupFromJsonResponse.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertNotNull(processor.getMapPayloadBinderOrNull(method, new Object[] { "",
               new CreateSharedIpGroupOptions[] { withServer(2) } }));
   }

   public void testDeleteSharedIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("deleteSharedIpGroup", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/shared_ip_groups/2");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseOnNotFoundOr404.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
   }

   public void testListAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getAddresses", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseAddressesFromJsonResponse.class);
   }

   public void testListPublicAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listPublicAddresses", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips/public");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseInetAddressListFromJsonResponse.class);
   }

   public void testListPrivateAddresses() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("listPrivateAddresses", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/ips/private");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseInetAddressListFromJsonResponse.class);
   }

   public void testListBackupSchedule() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("getBackupSchedule", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/backup_schedule");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseBackupScheduleFromJsonResponse.class);
   }

   public void testCreateImageWithIpGroup() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("createImageFromServer",
               String.class, int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { "ralphie", 2 });
      assertEquals("{\"image\":{\"serverId\":2,\"name\":\"ralphie\"}}", request.getPayload()
               .getRawContent());
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/images");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseImageFromJsonResponse.class);
      assertNotNull(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method));
      assertNotNull(processor.getMapPayloadBinderOrNull(method, new Object[] { "", 2 }));
   }

   private static final Class<? extends RebuildServerOptions[]> rebuildServerOptionsVarargsClass = new RebuildServerOptions[] {}
            .getClass();

   public void testRebuildServer() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebuildServer", int.class,
               rebuildServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { 3 });
      assertEquals("{\"rebuild\":{}}", request.getPayload().getRawContent());
      validateRebuildServer(method, request);
   }

   public void testRebuildServerWithImage() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebuildServer", int.class,
               rebuildServerOptionsVarargsClass);

      HttpRequest request = processor.createRequest(method, new Object[] { 3, withImage(2) });
      assertEquals("{\"rebuild\":{\"imageId\":2}}", request.getPayload().getRawContent());
      validateRebuildServer(method, request);
   }

   private void validateRebuildServer(Method method, HttpRequest request) {
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/3/action");
      assertEquals(request.getEndpoint().getQuery(), "format=json");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
      assertNotNull(processor.getMapPayloadBinderOrNull(method, new Object[] { "",
               new RebuildServerOptions[] { withImage(2) } }));
   }

   public void testReboot() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("rebootServer", int.class,
               RebootType.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2, RebootType.HARD });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"reboot\":{\"type\":\"HARD\"}}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("resizeServer", int.class, int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2, 3 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"resize\":{\"flavorId\":3}}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testConfirmResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("confirmResizeServer", int.class);

      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"confirmResize\":null}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   public void testRevertResize() throws SecurityException, NoSuchMethodException {
      Method method = CloudServersAsyncClient.class.getMethod("revertResizeServer", int.class);
      HttpRequest request = processor.createRequest(method, new Object[] { 2 });
      assertEquals(request.getEndpoint().getHost(), "serverManagementUrl");
      assertEquals(request.getEndpoint().getPath(), "/servers/2/action");
      assertEquals(request.getMethod(), HttpMethod.POST);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList(request.getPayload().getRawContent().toString().getBytes().length
                        + ""));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_TYPE), Collections
               .singletonList(MediaType.APPLICATION_JSON));
      assertEquals("{\"revertResize\":null}", request.getPayload().getRawContent());
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudServersAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudServersAsyncClient>>() {
      };
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 2);
      assertEquals(request.getFilters().get(0).getClass(), AuthenticateRequest.class);
      assertEquals(request.getFilters().get(1).getClass(), AddTimestampQuery.class);

   }

   protected Module createModule() {
      return new CloudServersRestClientModule(new TestRackspaceAuthenticationRestClientModule());
   }

   @Override
   public ContextSpec<CloudServersClient, CloudServersAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("cloudservers", "user", "password",
               new Properties());
   }

}
