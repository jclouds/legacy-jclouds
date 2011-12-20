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
package org.jclouds.tmrk.enterprisecloud.binders;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.http.HttpRequest;
import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code BindInternetServiceToXmlPayload}
 * @author Jason King
 */
@Test(groups = "unit", testName = "BindInternetServiceToXmlPayloadTest")
public class BindInternetServiceToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }
   });

   public void testPayloadMinimalXmlContent() throws IOException {
       String expected =
         "<InternetService name='testName'>" +
            "<Enabled>true</Enabled>" +
            "<Persistence>" +
               "<Type>None</Type>" +
            "</Persistence>" +
         "</InternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
               .getInstance(BindInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder();
      builder.href(URI.create("/cloudapi/ecloud/internetservices/797"));
      builder.name("testName");
      builder.enabled(true);
      builder.persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build());

      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }

   public void testPayloadWithSourceIPXmlContent() throws IOException {
      String expected =
            "<InternetService name='testName'>" +
               "<Enabled>true</Enabled>" +
               "<Persistence>" +
                  "<Type>SourceIp</Type>" +
               "</Persistence>" +
            "</InternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
            .getInstance(BindInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder();
      builder.href(URI.create("/cloudapi/ecloud/internetservices/797"));
      builder.name("testName");
      builder.enabled(true);
      builder.persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.SOURCE_IP).build());

      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }

   public void testPayloadWithSourceIPAndTimeoutXmlContent() throws IOException {
      String expected =
            "<InternetService name='testName'>" +
               "<Enabled>true</Enabled>" +
               "<Persistence>" +
                  "<Type>SourceIp</Type>" +
                  "<Timeout>5</Timeout>" +
               "</Persistence>" +
            "</InternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
            .getInstance(BindInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder();
      builder.href(URI.create("/cloudapi/ecloud/internetservices/797"));
      builder.name("testName");
      builder.enabled(true);
      builder.persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.SOURCE_IP).timeout(5).build());

      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }

   public void testPayloadRedirectURLContent() throws IOException {
      String expected =
            "<InternetService name='testName'>" +
               "<Enabled>true</Enabled>" +
               "<Persistence>" +
                  "<Type>None</Type>" +
               "</Persistence>" +
               "<RedirectUrl>/dev/null</RedirectUrl>" +
            "</InternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
            .getInstance(BindInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder();
      builder.href(URI.create("/cloudapi/ecloud/internetservices/797"));
      builder.name("testName");
      builder.enabled(true);
      builder.persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build());
      builder.redirectUrl("/dev/null");

      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }

   public void testPayloadTrustedNetworkGroupAndBackupServiceXmlContent() throws IOException {
      String expected =
            "<InternetService name='testName'>" +
               "<Enabled>true</Enabled>" +
               "<Persistence>" +
                  "<Type>None</Type>" +
               "</Persistence>" +
               "<TrustedNetworkGroup href='/dev/null' name='groupName' type='groupType'/>"+
               "<BackupInternetService href='/foo/bar' name='backupName' type='backupType'/>"+
            "</InternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
            .getInstance(BindInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder();
      builder.href(URI.create("/cloudapi/ecloud/internetservices/797"));
      builder.name("testName");
      builder.enabled(true);
      builder.persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build());
      builder.trustedNetworkGroup(NamedResource.builder().href(URI.create("/dev/null")).name("groupName").type("groupType").build());
      builder.backupInternetService(NamedResource.builder().href(URI.create("/foo/bar")).name("backupName").type("backupType").build());
      
      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }
}
