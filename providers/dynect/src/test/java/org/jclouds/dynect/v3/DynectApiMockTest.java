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
package org.jclouds.dynect.v3;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static javax.ws.rs.core.Response.Status.OK;
import static org.jclouds.Constants.PROPERTY_MAX_RETRIES;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.dynect.v3.DynECTExceptions.TargetExistsException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true)
public class DynectApiMockTest {
   
   private static final Set<Module> modules = ImmutableSet.<Module> of(
         new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()));

   static DynECTApi mockDynectApi(String uri) {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_MAX_RETRIES, "1");
      return ContextBuilder.newBuilder("dynect")
                           .credentials("jclouds:joe", "letmein")
                           .endpoint(uri)
                           .overrides(overrides)
                           .modules(modules)
                           .buildApi(DynECTApi.class);
   }

   String session = "{\"status\": \"success\", \"data\": {\"token\": \"FFFFFFFFFF\", \"version\": \"3.3.8\"}, \"job_id\": 254417252, \"msgs\": [{\"INFO\": \"login: Login successful\", \"SOURCE\": \"BLL\", \"ERR_CD\": null, \"LVL\": \"INFO\"}]}";

   String running = "{\"status\": \"running\", \"data\": {}, \"job_id\": 274509427, \"msgs\": [{\"INFO\": \"token: This session already has a job running\", \"SOURCE\": \"API-B\", \"ERR_CD\": \"OPERATION_FAILED\", \"LVL\": \"ERROR\"}]}";

   @Test(expectedExceptions = JobStillRunningException.class, expectedExceptionsMessageRegExp = "This session already has a job running")
   public void test200OnFailureThrowsExceptionWithoutRetryWhenJobRunning() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(session));
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(running));
      server.play();

      DynECTApi api = mockDynectApi(server.getUrl("/").toString());

      try {
         api.getZoneApi().list();
      } finally {
         server.shutdown();
      }
   }

   String taskBlocking = "[{\"status\": \"failure\", \"data\": {}, \"job_id\": 275545493, \"msgs\": [{\"INFO\": \"zone: Operation blocked by current task\", \"SOURCE\": \"BLL\", \"ERR_CD\": \"ILLEGAL_OPERATION\", \"LVL\": \"ERROR\"}, {\"INFO\": \"task_name: ProvisionZone\", \"SOURCE\": \"BLL\", \"ERR_CD\": null, \"LVL\": \"INFO\"}, {\"INFO\": \"task_id: 39120953\", \"SOURCE\": \"BLL\", \"ERR_CD\": null, \"LVL\": \"INFO\"}]}]";

   @Test(expectedExceptions = JobStillRunningException.class, expectedExceptionsMessageRegExp = "Operation blocked by current task")
   public void test200OnFailureThrowsExceptionWithoutRetryWhenOperationBlocked() throws IOException,
         InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(session));
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(taskBlocking));
      server.play();

      DynECTApi api = mockDynectApi(server.getUrl("/").toString());

      try {
         api.getZoneApi().list();
      } finally {
         server.shutdown();
      }
   }

   String targetExists = "[{\"status\": \"failure\", \"data\": {}, \"job_id\": 275533917, \"msgs\": [{\"INFO\": \"name: Name already exists\", \"SOURCE\": \"BLL\", \"ERR_CD\": \"TARGET_EXISTS\", \"LVL\": \"ERROR\"}, {\"INFO\": \"create: You already have this zone.\", \"SOURCE\": \"BLL\", \"ERR_CD\": null, \"LVL\": \"INFO\"}]}]";

   @Test(expectedExceptions = TargetExistsException.class, expectedExceptionsMessageRegExp = "Name already exists")
   public void test200OnFailureThrowsExceptionWithoutRetryOnNameExists() throws IOException, InterruptedException {
      MockWebServer server = new MockWebServer();
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(session));
      server.enqueue(new MockResponse().setResponseCode(OK.getStatusCode()).setBody(targetExists));
      server.play();

      DynECTApi api = mockDynectApi(server.getUrl("/").toString());

      try {
         api.getZoneApi().list();
      } finally {
         server.shutdown();
      }
   }
}
