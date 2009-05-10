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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.s3.commands.callables.ParseMd5FromETagHeader;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.http.HttpHeaders;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Store data by creating or overwriting an object.
 * 
 * <p/>
 * This returns a byte[] of the md5 hash of what Amazon S3 received
 * <p />
 * <p/>
 * This command allows you to specify {@link PutObjectOptions} to control
 * delivery of content.
 * 
 * 
 * @see PutObjectOptions
 * @see http 
 *      ://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectPUT
 *      .html
 * @author Adrian Cole
 */
public class PutObject extends S3FutureCommand<byte[]> {

    @Inject
    public PutObject(@Named("jclouds.http.address") String amazonHost,
	    ParseMd5FromETagHeader callable, @Assisted String s3Bucket,
	    @Assisted S3Object object, @Assisted PutObjectOptions options) {
	super("PUT", "/" + checkNotNull(object.getKey()), callable, amazonHost,
		s3Bucket);
	checkArgument(object.getMetadata().getSize() >= 0, "size must be set");

	getRequest().setPayload(
		checkNotNull(object.getData(), "object.getContent()"));

	getRequest().getHeaders().put(
		HttpHeaders.CONTENT_TYPE,
		checkNotNull(object.getMetadata().getContentType(),
			"object.metadata.contentType()"));

	getRequest().getHeaders().put(HttpHeaders.CONTENT_LENGTH,
		object.getMetadata().getSize() + "");

	if (object.getMetadata().getCacheControl() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CACHE_CONTROL,
		    object.getMetadata().getCacheControl());
	}
	if (object.getMetadata().getContentDisposition() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_DISPOSITION,
		    object.getMetadata().getContentDisposition());
	}
	if (object.getMetadata().getContentEncoding() != null) {
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_ENCODING,
		    object.getMetadata().getContentEncoding());
	}

	if (object.getMetadata().getMd5() != null)
	    getRequest().getHeaders().put(HttpHeaders.CONTENT_MD5,
		    S3Utils.toBase64String(object.getMetadata().getMd5()));

	getRequest().getHeaders()
		.putAll(object.getMetadata().getUserMetadata());
	getRequest().getHeaders().putAll(options.buildRequestHeaders());

    }
}