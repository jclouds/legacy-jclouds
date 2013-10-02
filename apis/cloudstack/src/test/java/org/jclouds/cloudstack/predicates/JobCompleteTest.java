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
package org.jclouds.cloudstack.predicates;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.jclouds.cloudstack.AsyncJobException;
import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJobError;
import org.jclouds.cloudstack.domain.AsyncJob.ResultCode;
import org.jclouds.cloudstack.domain.AsyncJob.Status;
import org.jclouds.cloudstack.domain.AsyncJobError.ErrorCode;
import org.jclouds.cloudstack.features.AsyncJobApi;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit", singleThreaded = true)
public class JobCompleteTest {

   CloudStackApi client;
   AsyncJobApi asyncJobClient;

   @BeforeMethod
   public void setUp() {
      client = createMock(CloudStackApi.class);
      asyncJobClient = createMock(AsyncJobApi.class);

      expect(client.getAsyncJobApi()).andReturn(asyncJobClient);
   }

   @Test
   public void testJobComplete() {
      AsyncJob<?> job = AsyncJob.builder().id("100")
         .status(Status.SUCCEEDED).resultCode(ResultCode.SUCCESS).build();
      expect((Object) asyncJobClient.getAsyncJob(job.getId())).andReturn(job);

      replay(client, asyncJobClient);
      assertTrue(new JobComplete(client).apply(job.getId()));
      verify(client, asyncJobClient);
   }

   @Test
   public void testFailedJobComplete() {
      AsyncJob<?> job = AsyncJob.builder().id("100")
         .status(Status.FAILED).resultCode(ResultCode.FAIL)
         .error(AsyncJobError.builder().errorCode(ErrorCode.INTERNAL_ERROR).errorText("Dummy test error").build()).build();
      expect((Object) asyncJobClient.getAsyncJob(job.getId())).andReturn(job);

      replay(client, asyncJobClient);
      try {
         new JobComplete(client).apply(job.getId());
         fail("No exception thrown");

      } catch (AsyncJobException e) {
         assertTrue(e.toString().contains("Dummy test error"));
      }
      verify(client, asyncJobClient);
   }
}
