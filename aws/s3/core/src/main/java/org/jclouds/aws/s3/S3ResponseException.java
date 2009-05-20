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
package org.jclouds.aws.s3;

import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * Encapsulates an S3 Error from Amazon.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/UsingRESTError.html" />
 * @see S3Error
 * @see org.jclouds.aws.s3.handlers.ParseS3ErrorFromXmlContent
 * @author Adrian Cole
 * 
 */
public class S3ResponseException extends HttpResponseException {

    private static final long serialVersionUID = 1L;

    private S3Error error = new S3Error();

    public S3ResponseException(HttpFutureCommand<?> command,
	    HttpResponse response, S3Error error) {
	super(error.toString(), command, response);
	this.setError(error);

    }

    public S3ResponseException(HttpFutureCommand<?> command,
	    HttpResponse response, S3Error error, Throwable cause) {
	super(error.toString(), command, response, cause);
	this.setError(error);

    }

    public S3ResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response, S3Error error) {
	super(message, command, response);
	this.setError(error);

    }

    public S3ResponseException(String message, HttpFutureCommand<?> command,
	    HttpResponse response, S3Error error, Throwable cause) {
	super(message, command, response, cause);
	this.setError(error);

    }

    public void setError(S3Error error) {
	this.error = error;
    }

    public S3Error getError() {
	return error;
    }

}
