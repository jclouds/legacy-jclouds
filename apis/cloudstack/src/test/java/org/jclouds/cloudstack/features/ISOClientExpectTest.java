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

import static org.jclouds.cloudstack.options.ListISOsOptions.Builder.accountInDomain;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack ISOClient
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ISOClientExpectTest")
public class ISOClientExpectTest extends BaseCloudStackExpectTest<ISOClient> {
   
   private static final ISO iso1 = ISO.builder()
                                      .id("018e0928-8205-4d8e-9329-f731a9ccd488")
                                      .name("xs-tools.iso")
                                      .displayText("xen-pv-drv-iso")
                                      .isPublic(true)
                                      .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-08-21T15:45:01+0530"))
                                      .isReady(true)
                                      .passwordEnabled(false)
                                      .bootable(false)
                                      .isFeatured(true)
                                      .crossZones(false)
                                      .account("system")
                                      .domain("ROOT")
                                      .domainid("9d189ea2-097e-4b2b-9bae-d885f5430d69")
                                      .isExtractable(false).build();
   
   private static final ISO iso2 = ISO.builder()
                                      .id("1e29244b-9cf0-4ff2-9978-677eb83f6bfb")
                                      .name("vmware-tools.iso")
                                      .displayText("VMware Tools Installer ISO")
                                      .isPublic(true)
                                      .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-08-21T15:45:01+0530"))
                                      .isReady(true)
                                      .passwordEnabled(false)
                                      .bootable(false)
                                      .isFeatured(true)
                                      .crossZones(false)
                                      .account("system")
                                      .domain("ROOT")
                                      .domainid("9d189ea2-097e-4b2b-9bae-d885f5430d69")
                                      .isExtractable(false).build();

   HttpRequest listIsos = HttpRequest.builder().method("GET")
                                     .endpoint("http://localhost:8080/client/api")
                                     .addQueryParam("response", "json")
                                     .addQueryParam("command", "listIsos")
                                     .addQueryParam("listAll", "true")
                                     .addQueryParam("apiKey", "identity")
                                     .addQueryParam("signature", "qUUF6hCDc57Bc/nHriS9umbZBKA%3D")
                                     .addHeader("Accept", "application/json")
                                     .build();
   
   public void testListISOsWhenResponseIs2xx() {
      ISOClient client = requestSendsResponse(listIsos,
         HttpResponse.builder()
                     .statusCode(200)
                     .payload(payloadFromResource("/listisosresponse.json"))
                     .build());

      assertEquals(client.listISOs().toString(), ImmutableSet.of(iso1, iso2).toString());
   }

   public void testListISOsWhenResponseIs404() {
      ISOClient client = requestSendsResponse(listIsos,
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listISOs(), ImmutableSet.of());
   }
   
   HttpRequest listIsosOptions = HttpRequest.builder().method("GET")
                                            .endpoint("http://localhost:8080/client/api")
                                            .addQueryParam("response", "json")
                                            .addQueryParam("command", "listIsos")
                                            .addQueryParam("listAll", "true")
                                            .addQueryParam("account", "fred")
                                            .addQueryParam("domainid", "5")
                                            .addQueryParam("bootable", "true")
                                            .addQueryParam("hypervisor", "xen")
                                            .addQueryParam("id", "3")
                                            .addQueryParam("isofilter", "featured")
                                            .addQueryParam("ispublic", "true")
                                            .addQueryParam("isready", "true")
                                            .addQueryParam("keyword", "bob")
                                            .addQueryParam("name", "bob%27s%20iso")
                                            .addQueryParam("zoneid", "7")
                                            .addQueryParam("apiKey", "identity")
                                            .addQueryParam("signature", "4S5ustbaBErEnpymWLSj1rEJ/nk%3D")
                                            .addHeader("Accept", "application/json")
                                            .build();
   
   public void testListISOsOptionsWhenResponseIs2xx() {
      ISOClient client = requestSendsResponse(listIsosOptions,
         HttpResponse.builder()
                     .statusCode(200)
                     .payload(payloadFromResource("/listisosresponse.json"))
                     .build());

      assertEquals(client.listISOs(accountInDomain("fred", "5").bootable().hypervisor("xen").id("3").isoFilter(ISO.ISOFilter.featured).isPublic().isReady().keyword("bob").name("bob's iso").zoneId("7")).toString(), ImmutableSet.of(iso1, iso2).toString());
   }

   HttpRequest getIso = HttpRequest.builder().method("GET")
                                     .endpoint("http://localhost:8080/client/api")
                                     .addQueryParam("response", "json")
                                     .addQueryParam("command", "listIsos")
                                     .addQueryParam("listAll", "true")
                                     .addQueryParam("id", "018e0928-8205-4d8e-9329-f731a9ccd488")
                                     .addQueryParam("apiKey", "identity")
                                     .addQueryParam("signature", "uZyPUJt6ThMDcQSDa%2BEv5LMs%2B2U%3D")
                                     .addHeader("Accept", "application/json")
                                     .build();
   
   public void testGetISOWhenResponseIs2xx() {
      ISOClient client = requestSendsResponse(getIso,
         HttpResponse.builder()
                     .statusCode(200)
                     .payload(payloadFromResource("/getisoresponse.json"))
                     .build());

      assertEquals(client.getISO("018e0928-8205-4d8e-9329-f731a9ccd488").toString(), iso1.toString());
   }

   public void testGetISOWhenResponseIs404() {
      ISOClient client = requestSendsResponse(getIso,
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.getISO("018e0928-8205-4d8e-9329-f731a9ccd488"));
   }

   HttpRequest registerIso = HttpRequest.builder().method("GET")
                                        .endpoint("http://localhost:8080/client/api")
                                        .addQueryParam("response", "json")
                                        .addQueryParam("command", "registerIso")
                                        .addQueryParam("name", "ubuntu10.10")
                                        .addQueryParam("displaytext", "ubuntu 10.10 (32 bit)")
                                        .addQueryParam("url", "http://ubuntu/ubuntu-10.10.iso")
                                        .addQueryParam("zoneid", "1e0335d9-b6cc-4805-bddf-0828e66a0d01")
                                        .addQueryParam("account", "root")
                                        .addQueryParam("domainid", "99f4159b-c698-4bd9-b8c5-5ac462f101eb")
                                        .addQueryParam("bootable", "true")
                                        .addQueryParam("isextractable", "true")
                                        .addQueryParam("isfeatured", "true")
                                        .addQueryParam("ispublic", "true")
                                        .addQueryParam("ostypeid", "1234-abcd")
                                        .addQueryParam("apiKey", "identity")
                                        .addQueryParam("signature", "YpFMYUUu0daLgwxNFubVfkV0Nw8%3D")
                                        .addHeader("Accept", "application/json") 
                                        .build();
   
   RegisterISOOptions registerISOOptions = RegisterISOOptions.Builder
           .accountInDomain("root", "99f4159b-c698-4bd9-b8c5-5ac462f101eb")
           .bootable(true).isExtractable(true).isFeatured(true).isPublic(true).osTypeId("1234-abcd");
   
   @Test
   public void testRegisterISOsWhenResponseIs2xx() {
      ISOClient client = requestSendsResponse(
         registerIso,
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/registerisoresponse.json"))
            .build());
      
      assertEquals(client.registerISO("ubuntu10.10", "ubuntu 10.10 (32 bit)", "http://ubuntu/ubuntu-10.10.iso", "1e0335d9-b6cc-4805-bddf-0828e66a0d01", 
              registerISOOptions),
            ISO.builder().id("b52c509d-c6e2-452c-b6ec-aa00720ed6cd").name("ubuntu10.10").displayText("ubuntu 10.10 (32 bit)").isPublic(true)
                .isReady(false).bootable(true).isFeatured(false).crossZones(false).osTypeId("0e0335d9-b6cc-4808-bddf-0828e66a0d03")
                .created(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-08-21T15:45:01+0530"))
                .osTypeName("Ubuntu 10.10 (32-bit)").account("root").domain("ROOT").domainid("99f4159b-c698-4bd9-b8c5-5ac462f101eb").status("")
                .account("admin").zoneId("6f9a2921-b22a-4149-8b71-6ffc275a2177").zoneName("Basic1")
                .isExtractable(false).build());
   }
   
   @Test(expectedExceptions=ResourceNotFoundException.class)
   public void testRegisterISOWhenResponseIs404() {
       ISOClient client = requestSendsResponse(registerIso,
          HttpResponse.builder()
             .statusCode(404)
             .build());

       assertNull(client.registerISO("ubuntu10.10", "ubuntu 10.10 (32 bit)", "http://ubuntu/ubuntu-10.10.iso", "1e0335d9-b6cc-4805-bddf-0828e66a0d01", 
               registerISOOptions));
    }
   
   @Override
   protected ISOClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getISOClient();
   }
}
