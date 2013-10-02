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
package org.jclouds.http.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payload;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;

@Test(groups = { "unit" })
public class ReturnStringIf200Test {

   @Test
   public void testNullWhenNoContentOn200() throws ExecutionException, InterruptedException,
            TimeoutException, IOException {
      Function<HttpResponse, String> function = new ReturnStringIf2xx();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getPayload()).andReturn(null);

      replay(payload);
      replay(response);
      assert function.apply(response) == null;

      verify(payload);
      verify(response);
   }

   @Test
   public void testExceptionWhenIOExceptionOn200() throws ExecutionException, InterruptedException,
            TimeoutException, IOException {
      Function<HttpResponse, String> function = new ReturnStringIf2xx();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      RuntimeException exception = new RuntimeException("bad");
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andThrow(exception);
      payload.release();

      replay(payload);
      replay(response);
      try {
         function.apply(response);
      } catch (Exception e) {
         assert e.equals(exception);
      }
      verify(payload);
      verify(response);
   }

   @Test
   public void testResponseOk() throws Exception {
      Function<HttpResponse, String> function = new ReturnStringIf2xx();
      HttpResponse response = createMock(HttpResponse.class);
      Payload payload = createMock(Payload.class);

      expect(response.getStatusCode()).andReturn(200).atLeastOnce();
      expect(response.getPayload()).andReturn(payload).atLeastOnce();
      expect(payload.getInput()).andReturn(Strings2.toInputStream("hello"));
      payload.release();

      replay(payload);
      replay(response);

      assertEquals(function.apply(response), "hello");

      verify(payload);
      verify(response);
   }
}
