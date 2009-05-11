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

import java.util.Map.Entry;

import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Object.Metadata;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.s3.util.DateService;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpHeaders;

import com.google.inject.Inject;

/**
 * This parses @{link {@link org.jclouds.aws.s3.domain.S3Object.Metadata} from HTTP headers.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/latest/RESTObjectGET.html" />
 * @author Adrian Cole
 */
public class ParseMetadataFromHeaders extends
	HttpFutureCommand.ResponseCallable<S3Object.Metadata> {
    private final DateService dateParser;
    private String key;

    @Inject
    public ParseMetadataFromHeaders(DateService dateParser) {
	this.dateParser = dateParser;
    }

    /**
     * parses the http response headers to create a new
     * {@link org.jclouds.aws.s3.domain.S3Object.Metadata} object.
     */
    public S3Object.Metadata call() throws HttpException {
	checkCode();

	S3Object.Metadata metadata = new S3Object.Metadata(key);
	addAllHeadersTo(metadata);

	addUserMetadataTo(metadata);
	addMd5To(metadata);

	parseLastModifiedOrThrowException(metadata);
	setContentTypeOrThrowException(metadata);
	setContentLengthOrThrowException(metadata);

	metadata.setCacheControl(getResponse().getFirstHeaderOrNull(
		HttpHeaders.CACHE_CONTROL));
	metadata.setContentDisposition(getResponse().getFirstHeaderOrNull(
		HttpHeaders.CONTENT_DISPOSITION));
	metadata.setContentEncoding(getResponse().getFirstHeaderOrNull(
		HttpHeaders.CONTENT_ENCODING));
	return metadata;

    }

    private void addAllHeadersTo(Metadata metadata) {
	metadata.getAllHeaders().putAll(getResponse().getHeaders());
    }

    private void setContentTypeOrThrowException(S3Object.Metadata metadata)
	    throws HttpException {
	String contentType = getResponse().getFirstHeaderOrNull(
		HttpHeaders.CONTENT_TYPE);
	if (contentType == null)
	    throw new HttpException(HttpHeaders.CONTENT_TYPE
		    + " not found in headers");
	else
	    metadata.setContentType(contentType);
    }

    private void setContentLengthOrThrowException(S3Object.Metadata metadata)
	    throws HttpException {
	String contentLength = getResponse().getFirstHeaderOrNull(
		HttpHeaders.CONTENT_LENGTH);
	if (contentLength == null)
	    throw new HttpException(HttpHeaders.CONTENT_LENGTH
		    + " not found in headers");
	else
	    metadata.setSize(Long.parseLong(contentLength));
    }

    private void parseLastModifiedOrThrowException(S3Object.Metadata metadata)
	    throws HttpException {
	String lastModified = getResponse().getFirstHeaderOrNull(
		HttpHeaders.LAST_MODIFIED);
	metadata.setLastModified(dateParser
		.dateTimeFromHeaderFormat(lastModified));
	if (metadata.getLastModified() == null)
	    throw new HttpException("could not parse: "
		    + HttpHeaders.LAST_MODIFIED + ": " + lastModified);
    }

    private void addMd5To(S3Object.Metadata metadata) {
	String md5Header = getResponse()
		.getFirstHeaderOrNull(S3Headers.AMZ_MD5);
	if (md5Header != null) {
	    metadata.setMd5(S3Utils.fromHexString(md5Header));
	}
	String eTag = getResponse().getFirstHeaderOrNull(S3Headers.ETAG);
	if (metadata.getMd5() == null && eTag != null) {
	    metadata.setMd5(S3Utils.fromHexString(eTag.replaceAll("\"", "")));
	}
    }

    private void addUserMetadataTo(S3Object.Metadata metadata) {
	for (Entry<String, String> header : getResponse().getHeaders()
		.entries()) {
	    if (header.getKey() != null
		    && header.getKey().startsWith(
			    S3Headers.USER_METADATA_PREFIX))
		metadata.getUserMetadata().put(header.getKey(),
			header.getValue());
	}
    }

    public void setKey(String key) {
	this.key = key;
    }

}