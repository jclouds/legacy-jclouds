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
package org.jclouds.sqs.handlers;

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.testng.annotations.Test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code SQSErrorRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SQSErrorRetryHandlerTest")
public class SQSErrorRetryHandlerTest {

   String code = "AWS.SimpleQueueService.QueueDeletedRecently";
   AWSError error;
   HttpResponse response = HttpResponse.builder().statusCode(400)
         .payload(String.format("<Error><Code>%s</Code></Error>", code)).build();

   public SQSErrorRetryHandlerTest() {
      error = new AWSError();
      error.setCode(code);
   }
   
   public void testQueueDeletedRecentlyRetriesWhen59SleepsAndTries() {

      SQSErrorRetryHandler retry = new SQSErrorRetryHandler(createMock(AWSUtils.class),
            createMock(BackoffLimitedRetryHandler.class), ImmutableSet.<String> of(), 60, 100);

      HttpCommand command = createHttpCommandForFailureCount(59);

      Stopwatch watch = new Stopwatch().start();
      assertTrue(retry.shouldRetryRequestOnError(command, response, error));
      assertEquals(command.getFailureCount(), 60);
      // allow for slightly inaccurate system timers
      assertTrue(watch.stop().elapsedTime(TimeUnit.MILLISECONDS) >= 98);
   }
   

   
   public void testQueueDeletedRecentlyRetriesWhen60DoesntTry() {

      SQSErrorRetryHandler retry = new SQSErrorRetryHandler(createMock(AWSUtils.class),
            createMock(BackoffLimitedRetryHandler.class), ImmutableSet.<String> of(), 60, 100);

      HttpCommand command = createHttpCommandForFailureCount(60);

      Stopwatch watch = new Stopwatch().start();
      assertFalse(retry.shouldRetryRequestOnError(command, response, error));
      assertEquals(command.getFailureCount(), 61);
      assertTrue(watch.stop().elapsedTime(TimeUnit.MILLISECONDS) < 100);
   }
   
   HttpCommand createHttpCommandForFailureCount(final int failureCount) {
      HttpCommand command = new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
      while (command.getFailureCount() != failureCount)
         command.incrementFailureCount();
      return command;
   }
}
