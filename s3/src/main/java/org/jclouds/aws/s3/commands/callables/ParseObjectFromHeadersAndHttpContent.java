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

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Object.Metadata;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpHeaders;

import com.google.inject.Inject;

/**
 * Parses response headers and creates a new S3Object from them and the http
 * content.
 * 
 * @author Adrian Cole
 */
public class ParseObjectFromHeadersAndHttpContent extends
	HttpFutureCommand.ResponseCallable<S3Object> {
    private final ParseMetadataFromHeaders metaDataParser;

    @Inject
    public ParseObjectFromHeadersAndHttpContent(ParseMetadataFromHeaders metaDataParser) {
	this.metaDataParser = metaDataParser;
    }

    /**
     * First, calls {@link ParseMetadataFromHeaders}.
     * 
     * Then, sets the object size based on the Content-Length header and adds
     * the content to the {@link S3Object} result.
     * 
     * @throws org.jclouds.http.HttpException
     */
    public S3Object call() throws HttpException {
	checkCode();
	metaDataParser.setResponse(getResponse());
	S3Object.Metadata metaData = metaDataParser.call();

	parseContentLengthOrThrowException(metaData);
	return new S3Object(metaData, getResponse().getContent());
    }

    private void parseContentLengthOrThrowException(Metadata metaData)
	    throws HttpException {
	String contentLength = getResponse().getFirstHeaderOrNull(
		HttpHeaders.CONTENT_LENGTH);
	if (contentLength == null)
	    throw new HttpException(HttpHeaders.CONTENT_LENGTH
		    + " header not present in headers: "
		    + getResponse().getHeaders());
	metaData.setSize(Long.parseLong(contentLength));
    }

    public void setKey(String key) {
	this.metaDataParser.setKey(key);
    }

}