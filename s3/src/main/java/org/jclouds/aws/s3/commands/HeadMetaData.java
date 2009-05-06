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

import org.jclouds.aws.s3.commands.callables.HeadMetaDataCallable;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Retrieves the metadata associated with the Key or
 * {@link S3Object.MetaData#NOT_FOUND} if not available;
 * 
 * @author Adrian Cole
 * 
 */
public class HeadMetaData extends S3FutureCommand<S3Object.MetaData> {

    @Inject
    public HeadMetaData(@Named("jclouds.http.address") String amazonHost,
	    HeadMetaDataCallable callable,
	    @Assisted("bucketName") String bucket, @Assisted("key") String key) {
	super("HEAD", "/" + checkNotNull(key), callable, amazonHost, bucket);
	callable.setKey(key);
    }
}