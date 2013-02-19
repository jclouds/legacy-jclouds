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
package org.jclouds.dynect.v3;

import java.io.IOException;

import org.jclouds.ContextBuilder;
import org.jclouds.rest.RestContext;
import org.testng.annotations.Test;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class DynectApiMockTest {

   static RestContext<DynECTApi, DynECTAsyncApi> getContext(String uri) {
      return ContextBuilder.newBuilder("dynect").credentials("jclouds:joe", "letmein").endpoint(uri).build();
   }

   String session = "{\"status\": \"success\", \"data\": {\"token\": \"FFFFFFFFFF\", \"version\": \"3.3.7\"}, \"job_id\": 254417252, \"msgs\": [{\"INFO\": \"login: Login successful\", \"SOURCE\": \"BLL\", \"ERR_CD\": null, \"LVL\": \"INFO\"}]}";
   String failure = "{\"status\": \"failure\", \"data\": {}, \"job_id\": 274509427, \"msgs\": [{\"INFO\": \"token: This session already has a job running\", \"SOURCE\": \"API-B\", \"ERR_CD\": \"OPERATION_FAILED\", \"LVL\": \"ERROR\"}]}";

   @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "This session already has a job running")
   public void test200OnFailureThrowsExceptionWithoutRetry() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(200).setBody(session));
      server.enqueue(new MockResponse().setResponseCode(200).setBody(failure));
      server.play();

      DynECTApi api = getContext(server.getUrl("/").toString()).getApi();

      try {
         api.getZoneApi().list();
      } finally {
         server.shutdown();
      }
   }
}
