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

import org.jclouds.Utils;
import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;

/**
 * This parses @{link {@link S3Object.MetaData} from http headers or returns
 * {@link S3Object.MetaData#NOT_FOUND} on 404.
 * 
 * @author Adrian Cole
 */
public class HeadMetaDataCallable extends
	HttpFutureCommand.ResponseCallable<S3Object.MetaData> {
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
    public S3Object.MetaData call() throws HttpException {
	if (getResponse().getStatusCode() == 200) {
	    S3Object.MetaData metaData = new S3Object.MetaData(key);
	    String md5Header = getResponse().getFirstHeaderOrNull(
		    "x-amz-meta-object-md5");
	    if (md5Header != null) {
		metaData.setMd5(S3Utils.fromHexString(md5Header));
	    }

	    metaData.setLastModified(dateParser
		    .dateTimeFromHeaderFormat(getResponse()
			    .getFirstHeaderOrNull("Last-Modified")));
	    String eTag = getResponse().getFirstHeaderOrNull("ETag");
	    if (eTag != null) {
		metaData.setMd5(S3Utils
			.fromHexString(eTag.replaceAll("\"", "")));
	    }
	    metaData.setContentType(getResponse().getFirstHeaderOrNull(
		    "Content-Type"));
	    metaData.setSize(Long.parseLong(getResponse().getFirstHeaderOrNull(
		    "Content-Length")));
	    metaData.setServer(getResponse().getFirstHeaderOrNull("Server"));
	    return metaData;
	} else if (getResponse().getStatusCode() == 404) {
	    return S3Object.MetaData.NOT_FOUND;
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

}