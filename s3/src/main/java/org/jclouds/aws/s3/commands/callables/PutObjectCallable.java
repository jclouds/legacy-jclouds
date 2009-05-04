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
package org.jclouds.aws.s3.commands.callables;

import java.io.IOException;

import org.jclouds.aws.s3.S3Utils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class PutObjectCallable extends
	HttpFutureCommand.ResponseCallable<String> {

    public String call() throws HttpException {
	if (getResponse().getStatusCode() == 200) {
	    try {
		getResponse().getContent().close();
	    } catch (IOException e) {
		logger.error(e, "error consuming content");
	    }
	    return getResponse().getHeaders().get("ETag").iterator().next();
	} else {
	    try {
		String reason = S3Utils.toStringAndClose(getResponse()
			.getContent());
		throw new HttpException(getResponse().getStatusCode()
			+ ": Problem uploading content.\n" + reason);
	    } catch (IOException e) {
		throw new HttpException(getResponse().getStatusCode()
			+ ": Problem uploading content", e);
	    }
	}
    }
}