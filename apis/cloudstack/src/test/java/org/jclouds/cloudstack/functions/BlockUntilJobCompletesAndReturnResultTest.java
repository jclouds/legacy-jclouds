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
package org.jclouds.cloudstack.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJobError;
import org.jclouds.cloudstack.domain.AsyncJobError.ErrorCode;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BlockUntilJobCompletesAndReturnResultTest")
public class BlockUntilJobCompletesAndReturnResultTest {

   public void testApply() {
      String id = "1";
      String jobId = "2";

      CloudStackClient client = createMock(CloudStackClient.class);
      Predicate<String> jobComplete = Predicates.alwaysTrue();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(AsyncJob.builder().id(jobId).result("foo").build()).atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String>apply(AsyncCreateResponse.builder().id(id).jobId(
                  jobId).build()), "foo");

      verify(client);
      verify(jobClient);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testJobDoesntCompleteThrowsIllegalStateException() {
      String id = "1";
      String jobId = "2";

      CloudStackClient client = createMock(CloudStackClient.class);
      // the alwaysfalse predicate should blow up with IllegalStateException
      Predicate<String> jobComplete = Predicates.alwaysFalse();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(AsyncJob.builder().id(jobId).result("foo").build()).atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String>apply(
                  AsyncCreateResponse.builder().id(id).jobId(jobId).build()), "foo");

      verify(client);
      verify(jobClient);

   }

   @Test(expectedExceptions = UncheckedExecutionException.class)
   public void testJobWithErrorThrowsUncheckedExecutionException() {
      String id = "1";
      String jobId = "2";

      CloudStackClient client = createMock(CloudStackClient.class);
      Predicate<String> jobComplete = Predicates.alwaysTrue();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(
            AsyncJob.builder().id(jobId)
                  .error(AsyncJobError.builder().errorCode(ErrorCode.INTERNAL_ERROR).errorText("ERRROR").build())
                  .result("foo").build())
            .atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String>apply(
                  AsyncCreateResponse.builder().id(id).jobId(jobId).build()), "foo");

      verify(client);
      verify(jobClient);

   }
}
