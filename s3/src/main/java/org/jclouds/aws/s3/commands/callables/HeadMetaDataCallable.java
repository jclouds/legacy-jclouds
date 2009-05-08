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

import java.io.IOException;
import java.util.Map.Entry;

import org.jclouds.Utils;
import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Headers;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpHeaders;

import com.google.inject.Inject;

/**
 * This parses @{link {@link S3Object.Metadata} from http headers or returns
 * {@link S3Object.Metadata#NOT_FOUND} on 404.
 * 
 * @author Adrian Cole
 */
public class HeadMetaDataCallable extends
	HttpFutureCommand.ResponseCallable<S3Object.Metadata> {
    private final DateService dateParser;
    private String key;

    @Inject
    public HeadMetaDataCallable(DateService dateParser) {
	this.dateParser = dateParser;
    }

    /**
     * @return S3Content.NOT_FOUND, if not found.
     * @throws org.jclouds.http.HttpException
     */
    public S3Object.Metadata call() throws HttpException {
	if (getResponse().getStatusCode() == 200) {
	    S3Object.Metadata metaData = new S3Object.Metadata(key);

	    extractUserMetadata(metaData);
	    addMd5(metaData);

	    metaData.setLastModified(dateParser
		    .dateTimeFromHeaderFormat(getResponse()
			    .getFirstHeaderOrNull(HttpHeaders.LAST_MODIFIED)));
	    metaData.setContentType(getResponse().getFirstHeaderOrNull(
		    HttpHeaders.CONTENT_TYPE));
	    metaData.setSize(Long.parseLong(getResponse().getFirstHeaderOrNull(
		    HttpHeaders.CONTENT_LENGTH)));
	    metaData.setCacheControl(getResponse().getFirstHeaderOrNull(
		    HttpHeaders.CACHE_CONTROL));
	    metaData.setContentDisposition(getResponse().getFirstHeaderOrNull(
		    HttpHeaders.CONTENT_DISPOSITION));
	    metaData.setContentEncoding(getResponse().getFirstHeaderOrNull(
		    HttpHeaders.CONTENT_ENCODING));
	    return metaData;
	} else if (getResponse().getStatusCode() == 404) {
	    return S3Object.Metadata.NOT_FOUND;
	} else {
	    String reason = null;
	    try {
		reason = Utils.toStringAndClose(getResponse().getContent());
	    } catch (IOException e) {
		logger.error(e, "error parsing reason");
	    }
	    throw new HttpException("Error parsing object " + getResponse()
		    + " reason: " + reason);
	}
    }

    private void addMd5(S3Object.Metadata metaData) {
	String md5Header = getResponse()
		.getFirstHeaderOrNull(S3Headers.AMZ_MD5);
	if (md5Header != null) {
	    metaData.setMd5(S3Utils.fromHexString(md5Header));
	}
	String eTag = getResponse().getFirstHeaderOrNull(S3Headers.ETAG);
	if (metaData.getMd5() == null && eTag != null) {
	    metaData.setMd5(S3Utils.fromHexString(eTag.replaceAll("\"", "")));
	}
    }

    private void extractUserMetadata(S3Object.Metadata metaData) {
	for (Entry<String, String> header : getResponse().getHeaders()
		.entries()) {
	    if (header.getKey() != null
		    && header.getKey().startsWith(
			    S3Headers.USER_METADATA_PREFIX))
		metaData.getUserMetadata().put(header.getKey(),
			header.getValue());
	}
    }

    public void setKey(String key) {
	this.key = key;
    }

}