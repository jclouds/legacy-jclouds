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
package org.jclouds.gogrid.handlers;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.json.config.GsonModule;
import org.testng.TestException;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * Tests that the GoGridErrorHandler is correctly handling the exceptions.
 * 
 * @author Oleksiy Yarmula
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GoGridErrorHandlerTest")
public class GoGridErrorHandlerTest {

   @Test
   public void testHandler() {
      InputStream is = getClass().getResourceAsStream("/test_error_handler.json");

      GoGridErrorHandler handler = Guice.createInjector(new GsonModule()).getInstance(GoGridErrorHandler.class);

      HttpCommand command = createHttpCommand();
      handler.handleError(command, HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      Exception createdException = command.getException();

      assertNotNull(createdException, "There should've been an exception generated");
      String message = createdException.getMessage();
      assertTrue(message.contains("No object found that matches your input criteria."),
            "Didn't find the expected error cause in the exception message");
      assertTrue(message.contains("IllegalArgumentException"),
            "Didn't find the expected error code in the exception message");

      // make sure the InputStream is closed
      try {
         is.available();
         throw new TestException("Stream wasn't closed by the GoGridErrorHandler when it should've");
      } catch (IOException e) {
         // this is the excepted output
      }
   }

   HttpCommand createHttpCommand() {
      return new HttpCommand(HttpRequest.builder().method("GET").endpoint("http://localhost").build());
   }

   InputStream createInputStreamFromString(String s) {
      return new ByteArrayInputStream(s.getBytes());
   }

}
