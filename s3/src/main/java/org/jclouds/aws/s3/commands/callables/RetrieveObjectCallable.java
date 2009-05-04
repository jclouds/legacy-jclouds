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

import org.jclouds.Utils;
import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class RetrieveObjectCallable extends
	HttpFutureCommand.ResponseCallable<S3Object> {
    private final DateService dateParser;
    private String key;

    @Inject
    public RetrieveObjectCallable(DateService dateParser) {
	this.dateParser = dateParser;
    }

    /**
     * @return S3Content.NOT_FOUND, if not found.
     * @throws org.jclouds.http.HttpException
     */
    public S3Object call() throws HttpException {
	if (getResponse().getStatusCode() == 200) {
	    S3Object object = new S3Object(key);
	    String md5Header = getResponse().getFirstHeaderOrNull(
		    "x-amz-meta-object-md5");
	    if (md5Header != null)
		object.setContentMD5(md5Header);
	    object.setLastModified(dateParser
		    .dateTimeFromHeaderFormat(getResponse()
			    .getFirstHeaderOrNull("Last-Modified")));
	    String eTag = getResponse().getFirstHeaderOrNull("ETag");
	    if (eTag != null) {
		object.setETag(eTag.replaceAll("\"", ""));
	    }
	    object.setContentType(getResponse().getFirstHeaderOrNull(
		    "Content-Type"));
	    object.setSize(Long.parseLong(getResponse().getFirstHeaderOrNull(
		    "Content-Length")));
	    object.setServer(getResponse().getFirstHeaderOrNull("Server"));
	    if (getResponse().getContent() != null) {
		object.setContent(getResponse().getContent());
	    }
	    return object;
	} else if (getResponse().getStatusCode() == 404) {
	    return S3Object.NOT_FOUND;
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

    public void setKey(String key) {
	this.key = key;
    }

    public String getKey() {
	return key;
    }
}