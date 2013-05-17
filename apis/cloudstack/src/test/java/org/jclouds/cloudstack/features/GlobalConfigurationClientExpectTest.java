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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.net.URI;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.ConfigurationEntry;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack GlobalConfigurationClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "GlobalConfigurationClientExpectTest")
public class GlobalConfigurationClientExpectTest extends BaseCloudStackExpectTest<GlobalConfigurationClient> {

   @Test
   public void testListConfigurationEntriesWhenResponseIs2xx() {
      GlobalConfigurationClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listConfigurations&listAll=true&apiKey=identity&signature=%2BJ9mTuw%2BZXaumzMAJAXgZQaO2cc%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/listconfigurationsresponse.json"))
            .build());

      assertEquals(client.listConfigurationEntries(),
         ImmutableSet.of(
            ConfigurationEntry.builder().category("Advanced").name("account.cleanup.interval").value("86400")
               .description("The interval (in seconds) between cleanup for removed accounts").build(),
            ConfigurationEntry.builder().category("Advanced").name("agent.lb.enabled").value("true")
               .description("If agent load balancing enabled in cluster setup").build()
         ));
   }

   @Test
   public void testListConfigurationEntriesEmptyOn404() {
      GlobalConfigurationClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=listConfigurations&listAll=true&apiKey=identity&signature=%2BJ9mTuw%2BZXaumzMAJAXgZQaO2cc%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertEquals(client.listConfigurationEntries(), ImmutableSet.of());
   }

   @Test
   public void testUpdateConfigurationEntryWhenResponseIs2xx() {
      GlobalConfigurationClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=updateConfiguration&name=expunge.delay&value=11&" +
                  "apiKey=identity&signature=I2yG35EhfgIXYObeLfU3cvf%2BPeE%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/updateconfigurationsresponse.json"))
            .build());

      assertEquals(client.updateConfigurationEntry("expunge.delay", "11"),
         ConfigurationEntry.builder().category("Advanced").name("expunge.delay").value("11")
            .description("Determines how long (in seconds) to wait before actually expunging " +
               "destroyed vm. The default value = the default value of expunge.interval").build()
      );
   }

   @Test
   public void testUpdateConfigurationEntryNullOn404() {
      GlobalConfigurationClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=updateConfiguration&name=expunge.delay&value=11&" +
                  "apiKey=identity&signature=I2yG35EhfgIXYObeLfU3cvf%2BPeE%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(404)
            .build());

      assertNull(client.updateConfigurationEntry("expunge.delay", "11"));
   }

   @Override
   protected GlobalConfigurationClient clientFrom(CloudStackContext context) {
      return context.getGlobalContext().getApi().getConfigurationClient();
   }
}
