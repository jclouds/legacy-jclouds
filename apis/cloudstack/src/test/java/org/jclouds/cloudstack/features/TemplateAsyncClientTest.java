/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.features;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateFilter;
import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.CreateTemplateOptions;
import org.jclouds.cloudstack.options.DeleteTemplateOptions;
import org.jclouds.cloudstack.options.ExtractTemplateOptions;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.options.RegisterTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplatePermissionsOptions;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code TemplateAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TemplateAsyncClientTest")
public class TemplateAsyncClientTest extends BaseCloudStackAsyncClientTest<TemplateAsyncClient> {

   public void testCreateTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "createTemplate", TemplateMetadata.class, CreateTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(TemplateMetadata.builder().name("thename").osTypeId("10").displayText("description").build()));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=createTemplate&name=thename&ostypeid=10&displaytext=description HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCreateTemplateOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "createTemplate", TemplateMetadata.class, CreateTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(TemplateMetadata.builder().name("thename").osTypeId("10").displayText("description").build(), CreateTemplateOptions.Builder.bits(32).isFeatured(true).isPublic(true).passwordEnabled(true).requiresHVM(true).snapshotId("11").volumeId("12")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=createTemplate&bits=32&isfeatured=true&ispublic=true&passwordenabled=true&requireshvm=true&snapshotid=11&volumeid=12&name=thename&ostypeid=10&displaytext=description HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   HttpRequest registerTemplate = HttpRequest.builder().method("GET")
                                             .endpoint("http://localhost:8080/client/api")
                                             .addQueryParam("response", "json")
                                             .addQueryParam("command", "registerTemplate")
                                             .addQueryParam("format", "QCOW2")
                                             .addQueryParam("hypervisor", "xen")
                                             .addQueryParam("url", "http%3A//example.com/")
                                             .addQueryParam("zoneid", "20")
                                             .addQueryParam("name", "thename")
                                             .addQueryParam("ostypeid", "10")
                                             .addQueryParam("displaytext", "description").build();

   public void testRegisterTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "registerTemplate", TemplateMetadata.class, String.class, String.class, String.class, String.class, RegisterTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(TemplateMetadata.builder().name("thename").osTypeId("10").displayText("description").build(), Template.Format.QCOW2, "xen", "http://example.com/", 20));

      assertRequestLineEquals(httpRequest, registerTemplate.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   HttpRequest registerTemplateOptions = HttpRequest.builder().method("GET")
                                                    .endpoint("http://localhost:8080/client/api")
                                                    .addQueryParam("response", "json")
                                                    .addQueryParam("command", "registerTemplate")
                                                    .addQueryParam("format", "QCOW2")
                                                    .addQueryParam("hypervisor", "xen")
                                                    .addQueryParam("url", "http%3A//example.com/")
                                                    .addQueryParam("zoneid", "20")
                                                    .addQueryParam("account", "mydomain")
                                                    .addQueryParam("domainid", "3")
                                                    .addQueryParam("bits", "32")
                                                    .addQueryParam("checksum", "ABC")
                                                    .addQueryParam("isextractable", "true")
                                                    .addQueryParam("isfeatured", "true")
                                                    .addQueryParam("ispublic", "true")
                                                    .addQueryParam("passwordenabled", "true")
                                                    .addQueryParam("requireshvm", "true")
                                                    .addQueryParam("name", "thename")
                                                    .addQueryParam("ostypeid", "10")
                                                    .addQueryParam("displaytext", "description").build();

   public void testRegisterTemplateOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "registerTemplate", TemplateMetadata.class, String.class, String.class, String.class, String.class, RegisterTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(TemplateMetadata.builder().name("thename").osTypeId("10").displayText("description").build(), Template.Format.QCOW2, "xen", "http://example.com/", 20,
         RegisterTemplateOptions.Builder.accountInDomain("mydomain", "3").bits(32).checksum("ABC").isExtractable(true).isFeatured(true).isPublic(true).passwordEnabled(true).requiresHVM(true)));

      assertRequestLineEquals(httpRequest, registerTemplateOptions.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "updateTemplate", String.class, UpdateTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=updateTemplate&id=17 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateTemplateOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "updateTemplate", String.class, UpdateTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17, UpdateTemplateOptions.Builder.bootable(true).displayText("description").format(Template.Format.VHD).name("thename").osTypeId("12").passwordEnabled(true)));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=updateTemplate&id=17&bootable=true&displaytext=description&format=VHD&name=thename&ostypeid=12&passwordenabled=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   HttpRequest copyTemplate = HttpRequest.builder().method("GET")
                                         .endpoint("http://localhost:8080/client/api")
                                         .addQueryParam("response", "json")
                                         .addQueryParam("command", "copyTemplate")
                                         .addQueryParam("id", "17")
                                         .addQueryParam("sourcezoneid", "18")
                                         .addQueryParam("destzoneid", "19").build();

   public void testCopyTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "copyTemplateToZone", String.class, String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17, 18, 19));

      assertRequestLineEquals(httpRequest,copyTemplate.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "deleteTemplate", String.class, DeleteTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=deleteTemplate&id=17 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteTemplateOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "deleteTemplate", String.class, DeleteTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17, DeleteTemplateOptions.Builder.zoneId("8")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=deleteTemplate&id=17&zoneid=8 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListTemplates() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "listTemplates");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listTemplates&listAll=true&templatefilter=executable HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListTemplatesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "listTemplates", ListTemplatesOptions.class);
      GeneratedHttpRequest httpRequest = processor
            .createRequest(
                  method, ImmutableList.<Object> of(
                  ListTemplatesOptions.Builder.accountInDomain("adrian", "6").hypervisor("xen")
                        .filter(TemplateFilter.FEATURED)));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listTemplates&listAll=true&account=adrian&domainid=6&hypervisor=xen&templatefilter=featured HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "getTemplateInZone", String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5, 1));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listTemplates&listAll=true&templatefilter=executable&id=5&zoneid=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testUpdateTemplatePermissions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "updateTemplatePermissions", String.class, UpdateTemplatePermissionsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=updateTemplatePermissions&id=17 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testUpdateTemplatePermissionsOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "updateTemplatePermissions", String.class, UpdateTemplatePermissionsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17, UpdateTemplatePermissionsOptions.Builder.accounts(ImmutableSet.of("5", "6")).isExtractable(true).isFeatured(true).isPublic(true).op(UpdateTemplatePermissionsOptions.Operation.add)));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=updateTemplatePermissions&id=17&accounts=5,6&isextractable=true&isfeatured=true&ispublic=true&op=add HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListTemplatePermissions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "listTemplatePermissions", String.class, AccountInDomainOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=listTemplatePermissions&listAll=true&id=17 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListTemplatePermissionsOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "listTemplatePermissions", String.class, AccountInDomainOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(17, AccountInDomainOptions.Builder.accountInDomain("fred", "8")));

      assertRequestLineEquals(httpRequest, "GET http://localhost:8080/client/api?response=json&command=listTemplatePermissions&listAll=true&id=17&account=fred&domainid=8 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   HttpRequest extractTemplate = HttpRequest.builder().method("GET")
                                            .endpoint("http://localhost:8080/client/api")
                                            .addQueryParam("response", "json")
                                            .addQueryParam("command", "extractTemplate")
                                            .addQueryParam("id", "3")
                                            .addQueryParam("mode", "HTTP_DOWNLOAD")
                                            .addQueryParam("zoneid", "5").build();

   public void testExtractTemplate() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "extractTemplate", String.class, ExtractMode.class, String.class, ExtractTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(3, ExtractMode.HTTP_DOWNLOAD, 5));

      assertRequestLineEquals(httpRequest, extractTemplate.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   HttpRequest extractTemplateOptions = HttpRequest.builder().method("GET")
                                                   .endpoint("http://localhost:8080/client/api")
                                                   .addQueryParam("response", "json")
                                                   .addQueryParam("command", "extractTemplate")
                                                   .addQueryParam("id", "3")
                                                   .addQueryParam("mode", "HTTP_DOWNLOAD")
                                                   .addQueryParam("zoneid", "5")
                                                   .addQueryParam("url", "http%3A//example.com/").build();

   public void testExtractTemplateOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(TemplateAsyncClient.class, "extractTemplate", String.class, ExtractMode.class, String.class, ExtractTemplateOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(3, ExtractMode.HTTP_DOWNLOAD, 5, ExtractTemplateOptions.Builder.url("http://example.com/")));

      assertRequestLineEquals(httpRequest, extractTemplateOptions.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }
}
