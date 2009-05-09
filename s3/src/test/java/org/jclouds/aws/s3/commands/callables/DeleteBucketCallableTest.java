/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.commands.callables;

import org.testng.annotations.Test;

@Test
public class DeleteBucketCallableTest {

    // private HttpFutureCommand.ResponseCallable<Boolean> callable = null;
    //
    // @BeforeMethod
    // void setUp() {
    // callable = new DeleteBucketCallable();
    // }
    //
    // @AfterMethod
    // void tearDown() {
    // callable = null;
    // }
    //
    // @Test(expectedExceptions = HttpException.class)
    // public void testExceptionWhenNoContentOn409() throws Exception {
    // HttpResponse response = createMock(HttpResponse.class);
    // expect(response.getStatusCode()).andReturn(409).atLeastOnce();
    // expect(response.getContent()).andReturn(null);
    // replay(response);
    // callable.setResponse(response);
    // callable.call();
    // }
    //
    // @Test
    // public void testExceptionWhenIOExceptionOn409() throws
    // ExecutionException,
    // InterruptedException, TimeoutException, IOException {
    // HttpResponse response = createMock(HttpResponse.class);
    // expect(response.getStatusCode()).andReturn(409).atLeastOnce();
    // RuntimeException exception = new RuntimeException("bad");
    // expect(response.getContent()).andThrow(exception);
    // replay(response);
    // callable.setResponse(response);
    // try {
    // callable.call();
    // } catch (Exception e) {
    // assert e.equals(exception);
    // }
    // verify(response);
    // }
    //
    // @Test
    // public void testFalseWhenBucketNotEmptyOn409() throws Exception {
    // HttpResponse response = createMock(HttpResponse.class);
    // expect(response.getStatusCode()).andReturn(409).atLeastOnce();
    // expect(response.getContent()).andReturn(
    // IOUtils.toInputStream("BucketNotEmpty")).atLeastOnce();
    // replay(response);
    // callable.setResponse(response);
    // assert !callable.call().booleanValue();
    // verify(response);
    // }
    //
    // @Test
    // public void testResponseOk() throws Exception {
    // HttpResponse response = createMock(HttpResponse.class);
    // expect(response.getStatusCode()).andReturn(204).atLeastOnce();
    // replay(response);
    // callable.setResponse(response);
    // assertEquals(callable.call(), new Boolean(true));
    // verify(response);
    // }
}