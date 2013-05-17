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
package org.jclouds.fallbacks;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.RetryAfterException;
import org.testng.annotations.Test;

import com.google.common.net.HttpHeaders;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapHttp4xxCodesToExceptionsTest {

   @Test(expectedExceptions = AuthorizationException.class)
   public void test401ToAuthorizationException() throws Exception {
      fn.create(new HttpResponseException(command, HttpResponse.builder().statusCode(401).build()));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void test403ToAuthorizationException() throws Exception {
      fn.create(new HttpResponseException(command, HttpResponse.builder().statusCode(403).build()));
   }
   
   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void test404ToResourceNotFoundException() throws Exception {
      fn.create(new HttpResponseException(command, HttpResponse.builder().statusCode(404).build()));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void test409ToIllegalStateException() throws Exception {
      fn.create(new HttpResponseException(command, HttpResponse.builder().statusCode(409).build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry now")
   public void testHttpResponseExceptionWithRetryAfterDate() throws Exception {
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "Fri, 31 Dec 1999 23:59:59 GMT").build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry in 700 seconds")
   public void testHttpResponseExceptionWithRetryAfterOffset() throws Exception {
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "700").build()));
   }
   
   @Test(expectedExceptions = RetryAfterException.class, expectedExceptionsMessageRegExp = "retry in 86400 seconds")
   public void testHttpResponseExceptionWithRetryAfterPastIsZero() throws Exception {
      fn.create(new HttpResponseException(command, 
            HttpResponse.builder()
                        .statusCode(503)
                        .addHeader(HttpHeaders.RETRY_AFTER, "Sun, 2 Jan 2000 00:00:00 GMT").build()));
   }
   
   MapHttp4xxCodesToExceptions fn = new MapHttp4xxCodesToExceptions(HeaderToRetryAfterExceptionTest.fn);
   
   HttpCommand command = HeaderToRetryAfterExceptionTest.command;
   
}
