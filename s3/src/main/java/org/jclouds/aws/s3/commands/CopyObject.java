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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * The copy operation creates a copy of an object that is already storedin
 * Amazon S3.
 * <p/>
 * When copying an object, you can preserve all metadata (default) or
 * {@link CopyObjectOptions#overrideMetadataWith(com.google.common.collect.Multimap)
 * specify new metadata}. However, the ACL is not preserved and is set to
 * private for the user making the request. To override the default ACL setting,
 * {@link CopyObjectOptions#overrideAcl(org.jclouds.aws.s3.domain.acl.CannedAccessPolicy)
 * specify a new ACL} when generating a copy request.
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectCOPY.html" />
 * @see CopyObjectOptions
 * @see org.jclouds.aws.s3.domain.acl.CannedAccessPolicy
 * @author Adrian Cole
 * 
 */
public class CopyObject extends S3FutureCommand<S3Object.Metadata> {

    @Inject
    public CopyObject(@Named("jclouds.http.address") String amazonHost,
	    ParseSax<S3Object.Metadata> callable,
	    @Assisted("sourceBucket") String sourceBucket,
	    @Assisted("sourceObject") String sourceObject,
	    @Assisted("destinationBucket") String destinationBucket,
	    @Assisted("destinationObject") String destinationObject,
	    @Assisted CopyObjectOptions options) {
	super("PUT",
		"/" + checkNotNull(destinationObject, "destinationObject"),
		callable, amazonHost, destinationBucket);
	CopyObjectHandler handler = (CopyObjectHandler) callable.getHandler();
	handler.setKey(destinationObject);
	getRequest().getHeaders().put(
		"x-amz-copy-source",
		String.format("/%1$s/%2$s", checkNotNull(sourceBucket,
			"sourceBucket"), checkNotNull(sourceObject,
			"sourceObject")));
	getRequest().getHeaders().putAll(options.buildRequestHeaders());
    }
}