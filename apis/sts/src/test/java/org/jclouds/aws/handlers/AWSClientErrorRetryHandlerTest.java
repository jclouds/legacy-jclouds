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
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
 * Tests behavior of {@code AWSClientErrorRetryHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSClientErrorRetryHandlerTest")
public class AWSClientErrorRetryHandlerTest {
   @Test
   public void test401DoesNotRetry() {

      AWSUtils utils = createMock(AWSUtils.class);
      BackoffLimitedRetryHandler backoffLimitedRetryHandler = createMock(BackoffLimitedRetryHandler.class);
      HttpCommand command = createMock(HttpCommand.class);

      replay(utils, backoffLimitedRetryHandler, command);

      AWSClientErrorRetryHandler retry = new AWSClientErrorRetryHandler(utils, backoffLimitedRetryHandler,
            ImmutableSet.<String> of());

      assert !retry.shouldRetryRequest(command, HttpResponse.builder().statusCode(UNAUTHORIZED.getStatusCode()).build());

      verify(utils, backoffLimitedRetryHandler, command);

   }

   @DataProvider(name = "codes")
   public Object[][] createData() {
      return new Object[][] { { "RequestTimeout" }, { "OperationAborted" }, { "SignatureDoesNotMatch" } };
   }

   @Test(dataProvider = "codes")
   public void test409DoesBackoffAndRetryForCode(String code) {

      AWSUtils utils = createMock(AWSUtils.class);
      BackoffLimitedRetryHandler backoffLimitedRetryHandler = createMock(BackoffLimitedRetryHandler.class);
      HttpCommand command = createMock(HttpCommand.class);

      HttpRequest putBucket = HttpRequest.builder().method(PUT)
            .endpoint("https://adriancole-blobstore113.s3.amazonaws.com/").build();

      HttpResponse operationAborted = HttpResponse.builder().statusCode(CONFLICT.getStatusCode())
            .payload(Payloads.newStringPayload(String.format("<Error><Code>%s</Code></Error>", code))).build();

      expect(command.getCurrentRequest()).andReturn(putBucket);

      AWSError error = new AWSError();
      error.setCode(code);

      expect(utils.parseAWSErrorFromContent(putBucket, operationAborted)).andReturn(error);

      expect(backoffLimitedRetryHandler.shouldRetryRequest(command, operationAborted)).andReturn(Boolean.TRUE);

      replay(utils, backoffLimitedRetryHandler, command);

      AWSClientErrorRetryHandler retry = new AWSClientErrorRetryHandler(utils, backoffLimitedRetryHandler,
            ImmutableSet.<String> of("RequestTimeout", "OperationAborted", "SignatureDoesNotMatch"));

      assert retry.shouldRetryRequest(command, operationAborted);

      verify(utils, backoffLimitedRetryHandler, command);

   }

}
