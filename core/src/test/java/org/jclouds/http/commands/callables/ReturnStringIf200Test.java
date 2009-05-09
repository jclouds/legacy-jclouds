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
package org.jclouds.http.commands.callables;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ReturnStringIf200Test {

    private HttpFutureCommand.ResponseCallable<String> callable = null;

    @BeforeMethod
    void setUp() {
	callable = new ReturnStringIf200();
    }

    @AfterMethod
    void tearDown() {
	callable = null;
    }

    @Test
    public void testExceptionWhenNoContentOn200() throws ExecutionException,
	    InterruptedException, TimeoutException, IOException {
	HttpResponse response = createMock(HttpResponse.class);
	expect(response.getStatusCode()).andReturn(200).atLeastOnce();
	expect(response.getContent()).andReturn(null);
	replay(response);
	callable.setResponse(response);
	try {
	    callable.call();
	} catch (Exception e) {
	    assert e.getMessage().equals("no content");
	}
	verify(response);
    }

    @Test
    public void testExceptionWhenIOExceptionOn200() throws ExecutionException,
	    InterruptedException, TimeoutException, IOException {
	HttpResponse response = createMock(HttpResponse.class);
	expect(response.getStatusCode()).andReturn(200).atLeastOnce();
	RuntimeException exception = new RuntimeException("bad");
	expect(response.getContent()).andThrow(exception);
	replay(response);
	callable.setResponse(response);
	try {
	    callable.call();
	} catch (Exception e) {
	    assert e.equals(exception);
	}
	verify(response);
    }

    @Test
    public void testResponseOk() throws Exception {
	HttpResponse response = createMock(HttpResponse.class);
	expect(response.getStatusCode()).andReturn(200).atLeastOnce();
	expect(response.getContent()).andReturn(IOUtils.toInputStream("hello"));
	replay(response);
	callable.setResponse(response);
	assert "hello".equals(callable.call());
	verify(response);
    }
}