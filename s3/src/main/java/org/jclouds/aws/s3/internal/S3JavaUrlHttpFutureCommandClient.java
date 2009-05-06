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
package org.jclouds.aws.s3.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3ResponseException;
import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.JavaUrlHttpFutureCommandClient;

import com.google.inject.Inject;

public class S3JavaUrlHttpFutureCommandClient extends
	JavaUrlHttpFutureCommandClient {

    private S3ParserFactory parserFactory;

    @Inject
    public S3JavaUrlHttpFutureCommandClient(S3ParserFactory parserFactory,
	    URL target) throws MalformedURLException {
	super(target);
	this.parserFactory = parserFactory;
    }

    @Override
    protected HttpResponse getResponse(HttpURLConnection connection)
	    throws IOException {
	HttpResponse response = super.getResponse(connection);
	int code = response.getStatusCode();
	if (code >= 300) {
	    InputStream errorStream = response.getContent();
	    if (errorStream != null) {
		try {
		    S3Error error = parserFactory.createErrorParser().parse(
			    errorStream);
		    logger.error("received the following error from s3: %1s",
			    error);
		    throw new S3ResponseException(error, response);
		} catch (HttpException he) {
		    logger.error(he, "error parsing response");
		} finally {
		    IOUtils.closeQuietly(errorStream);
		}
	    }
	}
	return response;
    }
}
