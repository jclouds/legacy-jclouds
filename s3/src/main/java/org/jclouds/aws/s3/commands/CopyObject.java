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
package org.jclouds.aws.s3.commands;

import org.jclouds.aws.s3.commands.callables.CopyObjectCallable;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.HttpFutureCommand;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class CopyObject extends HttpFutureCommand<Boolean> {

    @Inject
    public CopyObject(@Named("jclouds.http.address") String amazonHost, CopyObjectCallable callable, @Assisted("sourceBucket") S3Bucket sourceBucket, @Assisted("sourceObject") S3Object sourceObject, @Assisted("destinationBucket") S3Bucket destinationBucket, @Assisted("destinationObject")  S3Object destinationObject) {
        super("PUT", "/" + destinationObject.getKey(), callable);
        getRequest().getHeaders().put("Host",
		destinationBucket.getName() + "." + amazonHost);
        getRequest().getHeaders().put("x-amz-copy-source", String.format("/%1s/%2s", sourceBucket.getName(), sourceObject.getKey()));
    }

}