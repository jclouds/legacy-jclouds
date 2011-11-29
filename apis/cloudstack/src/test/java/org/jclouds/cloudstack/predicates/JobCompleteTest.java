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
package org.jclouds.cloudstack.predicates;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncJob;
import org.jclouds.cloudstack.features.AsyncJobClient;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.cloudstack.domain.AsyncJob.ResultCode.SUCCESS;
import static org.jclouds.cloudstack.domain.AsyncJob.Status.SUCCEEDED;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit", singleThreaded = true)
public class JobCompleteTest {

   @Test
   public void testJobComplete() {
      CloudStackClient client = createMock(CloudStackClient.class);
      AsyncJobClient asyncJobClient = createMock(AsyncJobClient.class);

      expect(client.getAsyncJobClient()).andReturn(asyncJobClient);

      AsyncJob job = AsyncJob.builder().id(100L)
         .status(SUCCEEDED.code()).resultCode(SUCCESS.code()).build();
      expect(asyncJobClient.getAsyncJob(job.getId())).andReturn(job);

      replay(client, asyncJobClient);
      assertTrue(new JobComplete(client).apply(job.getId()));
      verify(client, asyncJobClient);
   }
}
