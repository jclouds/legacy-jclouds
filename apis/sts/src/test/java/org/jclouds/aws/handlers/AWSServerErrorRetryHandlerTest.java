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
package org.jclouds.aws.handlers;
import static javax.ws.rs.HttpMethod.PUT;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;

import java.util.concurrent.atomic.AtomicInteger;

import org.easymock.IAnswer;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payloads;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AWSServerErrorRetryHandler}
 * 
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "AWSServerErrorRetryHandlerTest")
public class AWSServerErrorRetryHandlerTest {
   @Test
   public void test500DoesNotRetry() {

      AWSUtils utils = createMock(AWSUtils.class);
      HttpCommand command = createMock(HttpCommand.class);

      replay(utils, command);

      AWSServerErrorRetryHandler retry = new AWSServerErrorRetryHandler(utils,
            ImmutableSet.<String> of());

      assertFalse(retry.shouldRetryRequest(command, HttpResponse.builder().statusCode(INTERNAL_SERVER_ERROR.getStatusCode()).build()));

      verify(utils, command);

   }

   @DataProvider(name = "codes")
   public Object[][] createData() {
      return new Object[][] { { "RequestLimitExceeded" } };
   }

   @Test(dataProvider = "codes")
   public void test503DoesBackoffAndRetryForCode(String code) {

      AWSUtils utils = createMock(AWSUtils.class);
      HttpCommand command = createMock(HttpCommand.class);

      HttpRequest putBucket = HttpRequest.builder().method(PUT)
            .endpoint("https://adriancole-blobstore113.s3.amazonaws.com/").build();

      HttpResponse limitExceeded = HttpResponse.builder().statusCode(SERVICE_UNAVAILABLE.getStatusCode())
            .payload(Payloads.newStringPayload(String.format("<Error><Code>%s</Code></Error>", code))).build();

      expect(command.getCurrentRequest()).andReturn(putBucket);
      final AtomicInteger counter = new AtomicInteger();
      expect(command.incrementFailureCount()).andAnswer(new IAnswer<Integer>() {
         @Override
         public Integer answer() throws Throwable {
            return counter.incrementAndGet();
         }
      }).anyTimes();
      expect(command.isReplayable()).andReturn(true).anyTimes();
      expect(command.getFailureCount()).andAnswer(new IAnswer<Integer>() {
         @Override
         public Integer answer() throws Throwable {
            return counter.get();
         }
      }).anyTimes();

      AWSError error = new AWSError();
      error.setCode(code);

      expect(utils.parseAWSErrorFromContent(putBucket, limitExceeded)).andReturn(error);

      replay(utils, command);

      AWSServerErrorRetryHandler retry = new AWSServerErrorRetryHandler(utils,
            ImmutableSet.<String> of("RequestLimitExceeded"));

      assert retry.shouldRetryRequest(command, limitExceeded);

      verify(utils, command);

   }

}
