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
import org.jclouds.tmrk.enterprisecloud.domain.service.Protocol;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetService;
import org.jclouds.tmrk.enterprisecloud.domain.service.internet.InternetServicePersistenceType;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code BindCreateInternetServiceToXmlPayload}
 * @author Jason King
 */
@Test(groups = "unit", testName = "BindCreateInternetServiceToXmlPayloadTest")
public class BindCreateInternetServiceToXmlPayloadTest {
   Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
      }
   });

   public void testPayloadMinimalXmlContent() throws IOException {
       String expected =
         "<CreateInternetService name='testName'>" +
            "<Protocol>HTTP</Protocol>" +
            "<Port>2020</Port>" +
            "<Enabled>true</Enabled>" +
            "<Persistence>" +
               "<Type>None</Type>" +
            "</Persistence>" +
         "</CreateInternetService>";

      HttpRequest request = new HttpRequest("GET", URI.create("http://test"));
      BindInternetServiceToXmlPayload binder = injector
               .getInstance(BindCreateInternetServiceToXmlPayload.class);

      InternetService.Builder builder = InternetService.builder()
                  .href(URI.create(""))
                  .name("testName")
                  .protocol(Protocol.HTTP)
                  .port(2020)
                  .enabled(true)
                  .persistence(InternetServicePersistenceType.builder().persistenceType(InternetServicePersistenceType.PersistenceType.NONE).build());

      binder.bindToRequest(request, builder.build());
      assertEquals(request.getPayload().getRawContent(), expected.replaceAll("'","\""));
   }
}
