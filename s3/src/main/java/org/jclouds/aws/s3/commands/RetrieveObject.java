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
package org.jclouds.aws.s3.commands;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.s3.commands.callables.RetrieveObjectCallable;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class RetrieveObject extends HttpFutureCommand<S3Object> {
    private String key;

    @Inject
    public RetrieveObject(@Named("jclouds.http.address") String amazonHost,
	    RetrieveObjectCallable callable, @Assisted S3Bucket s3Bucket,
	    @Assisted String key, @Assisted boolean getContent) {
	super(getContent ? "GET" : "HEAD", "/" + key, callable);
	this.key = key;
	getRequest().getHeaders().put("Host",
		s3Bucket.getName() + "." + amazonHost);
    }

    @Override
    public S3Object get() throws InterruptedException, ExecutionException {
	S3Object object = super.get();
	object.setKey(key);
	return object;
    }

    @Override
    public S3Object get(long l, TimeUnit timeUnit) throws InterruptedException,
	    ExecutionException, TimeoutException {
	S3Object object = super.get(l, timeUnit);
	object.setKey(key);
	return object;
    }
}