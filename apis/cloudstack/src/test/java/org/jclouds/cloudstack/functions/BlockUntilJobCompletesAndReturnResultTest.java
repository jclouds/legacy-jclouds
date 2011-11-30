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
package org.jclouds.cloudstack.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.cloudstack.domain.AsyncJobError.ErrorCode;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.domain.AsyncJobError;
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
      long id = 1;
      long jobId = 2;

      CloudStackClient client = createMock(CloudStackClient.class);
      Predicate<Long> jobComplete = Predicates.alwaysTrue();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(AsyncJob.builder().id(jobId).result("foo").build()).atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String> apply(new AsyncCreateResponse(id,
                  jobId)), "foo");

      verify(client);
      verify(jobClient);

   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testJobDoesntCompleteThrowsIllegalStateException() {
      long id = 1;
      long jobId = 2;

      CloudStackClient client = createMock(CloudStackClient.class);
      // the alwaysfalse predicate should blow up with IllegalStateException
      Predicate<Long> jobComplete = Predicates.alwaysFalse();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(AsyncJob.builder().id(jobId).result("foo").build()).atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String> apply(new AsyncCreateResponse(id,
                  jobId)), "foo");

      verify(client);
      verify(jobClient);

   }

   @Test(expectedExceptions = UncheckedExecutionException.class)
   public void testJobWithErrorThrowsUncheckedExecutionException() {
      long id = 1;
      long jobId = 2;

      CloudStackClient client = createMock(CloudStackClient.class);
      Predicate<Long> jobComplete = Predicates.alwaysTrue();
      AsyncJobClient jobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(jobClient).atLeastOnce();
      expect(jobClient.getAsyncJob(jobId)).andReturn(
            AsyncJob.builder().id(jobId).error(
               new AsyncJobError(ErrorCode.INTERNAL_ERROR, "ERRROR")).result("foo").build()).atLeastOnce();

      replay(client);
      replay(jobClient);

      assertEquals(
            new BlockUntilJobCompletesAndReturnResult(client, jobComplete).<String> apply(new AsyncCreateResponse(id,
                  jobId)), "foo");

      verify(client);
      verify(jobClient);

   }
}
