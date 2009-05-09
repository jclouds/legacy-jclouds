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
package org.jclouds.aws.s3.commands;

import static com.google.common.base.Preconditions.*;

import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.commands.callables.ParseMd5FromETagHeader;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpHeaders;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class PutObject extends S3FutureCommand<byte[]> {

    @Inject
    public PutObject(@Named("jclouds.http.address") String amazonHost,
	    ParseMd5FromETagHeader callable, @Assisted String s3Bucket,
	    @Assisted S3Object object, @Assisted PutObjectOptions options) {
	super("PUT", "/" + checkNotNull(object.getKey()), callable, amazonHost,
		s3Bucket);
	checkArgument(object.getMetaData().getSize() >=0,"size must be set");
	
	getRequest().setPayload(
		checkNotNull(object.getData(), "object.getContent()"));

	getRequest().getHeaders().put(
		HttpHeaders.CONTENT_TYPE,
		checkNotNull(object.getMetaData().getContentType(),
			"object.metaData.contentType()"));

	getRequest().getHeaders().put(HttpHeaders.CONTENT_LENGTH,
		object.getMetaData().getSize() + "");

	if (object.getMetaData().getCacheControl() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CACHE_CONTROL,
		    object.getMetaData().getCacheControl());
	}
	if (object.getMetaData().getContentDisposition() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_DISPOSITION,
		    object.getMetaData().getContentDisposition());
	}
	if (object.getMetaData().getContentEncoding() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_ENCODING,
		    object.getMetaData().getContentEncoding());
	}

	if (object.getMetaData().getMd5() != null)
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_MD5,
		    S3Utils.toBase64String(object.getMetaData().getMd5()));

	getRequest().getHeaders()
		.putAll(object.getMetaData().getUserMetadata());
	getRequest().getHeaders().putAll(options.buildRequestHeaders());

    }
}