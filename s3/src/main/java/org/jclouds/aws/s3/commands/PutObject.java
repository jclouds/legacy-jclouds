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

import org.jclouds.aws.s3.commands.callables.PutObjectCallable;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class PutObject extends HttpFutureCommand<String> {

    @Inject
    public PutObject(@Named("jclouds.http.address") String amazonHost,
	    PutObjectCallable callable, @Assisted S3Bucket s3Bucket,
	    @Assisted S3Object object) {
	super("PUT", "/" + object.getKey(), callable);
	getRequest().getHeaders().put("Host",
		s3Bucket.getName() + "." + amazonHost);
	if (object.getContentType() == null) {
	    throw new IllegalArgumentException(
		    "PUT requests need content type set");
	}
	Object o = object.getContent();
	if (o == null) {
	    throw new IllegalArgumentException("PUT requests need object");
	}
	getRequest().setContent(o);
	getRequest().setContentType(object.getContentType());
	getRequest().setContentLength(object.getSize());
    }

}