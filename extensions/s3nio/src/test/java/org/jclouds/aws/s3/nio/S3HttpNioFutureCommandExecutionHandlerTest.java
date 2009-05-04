/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3.nio;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.nio.entity.NStringEntity;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandConnectionRetry;
import org.jclouds.http.httpnio.pool.HttpNioFutureCommandExecutionHandler;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test
public class S3HttpNioFutureCommandExecutionHandlerTest {
    S3HttpNioFutureCommandExecutionHandler handler = null;
    HttpResponse response = null;
    StatusLine statusline = null;

    @BeforeMethod
    public void createHandler() {
	handler = new S3HttpNioFutureCommandExecutionHandler(
		createMock(HttpNioFutureCommandExecutionHandler.ConsumingNHttpEntityFactory.class),
		createMock(ExecutorService.class),
		createMock(HttpNioFutureCommandConnectionRetry.class));
	response = createMock(HttpResponse.class);
	statusline = createMock(StatusLine.class);
	expect(response.getStatusLine()).andReturn(statusline).atLeastOnce();
    }

    @Test
    void test500isRetryable() throws IOException {
	isRetryable(500);
    }

    @Test
    void test503isRetryable() throws IOException {
	isRetryable(503);
    }

    @Test
    void test409isRetryable() throws IOException {
	isRetryable(409);
    }

    @Test
    void test404NotRetryable() throws IOException {
	expect(statusline.getStatusCode()).andReturn(404).atLeastOnce();

	replay(statusline);
	replay(response);
	assert !handler.isRetryable(response);
	verify(statusline);
	verify(response);
    }

    @Test
    void test400WithNoEnitityNotRetryable() throws IOException {
	expect(statusline.getStatusCode()).andReturn(400).atLeastOnce();
	expect(response.getEntity()).andReturn(null);
	replay(statusline);
	replay(response);
	assert !handler.isRetryable(response);
	verify(statusline);
	verify(response);
    }

    @Test
    void test400WithIrrelevantEnitityNotRetryable() throws IOException {
	expect(statusline.getStatusCode()).andReturn(400).atLeastOnce();
	HttpEntity entity = createMock(HttpEntity.class);
	expect(response.getEntity()).andReturn(entity).atLeastOnce();
	expect(entity.getContent()).andReturn(IOUtils.toInputStream("hello"));
	response.setEntity(isA(NStringEntity.class));
	replay(entity);
	replay(statusline);
	replay(response);
	assert !handler.isRetryable(response);
	verify(statusline);
	verify(response);
	verify(entity);
    }

    @Test
    void test400WithRequestTimeTooSkewedTimeEnitityRetryable()
	    throws IOException {
	expect(statusline.getStatusCode()).andReturn(400).atLeastOnce();
	HttpEntity entity = createMock(HttpEntity.class);
	expect(response.getEntity()).andReturn(entity).atLeastOnce();
	expect(entity.getContent()).andReturn(
		IOUtils.toInputStream("RequestTimeTooSkewed"));
	response.setEntity(isA(NStringEntity.class));
	replay(entity);
	replay(statusline);
	replay(response);
	assert handler.isRetryable(response);
	verify(statusline);
	verify(response);
	verify(entity);
    }

    private void isRetryable(int code) throws IOException {
	expect(statusline.getStatusCode()).andReturn(code).atLeastOnce();
	replay(statusline);
	replay(response);
	assert handler.isRetryable(response);
	verify(statusline);
	verify(response);
    }

}