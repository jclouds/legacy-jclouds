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
package org.jclouds.aws.s3.internal;

import java.io.IOException;

import org.jclouds.Logger;
import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3InputStreamMap;
import org.jclouds.aws.s3.S3ObjectMap;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.lifecycle.Closer;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * // TODO: Adrian: Document return getConnection!
 * 
 * @author Adrian Cole
 */
public class GuiceS3Context implements S3Context {
    public interface S3ObjectMapFactory {
	S3ObjectMap createMapView(S3Bucket bucket);
    }

    public interface S3InputStreamMapFactory {
	S3InputStreamMap createMapView(S3Bucket bucket);
    }

    private final Logger logger;
    private final Injector injector;
    private final S3InputStreamMapFactory s3InputStreamMapFactory;
    private final S3ObjectMapFactory s3ObjectMapFactory;
    private final Closer closer;

    @Inject
    private GuiceS3Context(java.util.logging.Logger logger, Injector injector,
	    Closer closer, S3ObjectMapFactory s3ObjectMapFactory,
	    S3InputStreamMapFactory s3InputStreamMapFactory) {
	this.logger = new Logger(logger);
	this.injector = injector;
	this.s3InputStreamMapFactory = s3InputStreamMapFactory;
	this.s3ObjectMapFactory = s3ObjectMapFactory;
	this.closer = closer;
    }

    /**
     * {@inheritDoc}
     */
    public S3Connection getConnection() {
	return injector.getInstance(S3Connection.class);
    }

    /**
     * {@inheritDoc}
     */
    public S3InputStreamMap createS3InputStreamMap(S3Bucket bucket) {
	getConnection().createBucketIfNotExists(bucket);
	return s3InputStreamMapFactory.createMapView(bucket);
    }

    /**
     * {@inheritDoc}
     */
    public S3ObjectMap createS3ObjectMap(S3Bucket bucket) {
	getConnection().createBucketIfNotExists(bucket);
	return s3ObjectMapFactory.createMapView(bucket);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
	try {
	    closer.close();
	} catch (IOException e) {
	    logger.error(e, "error closing content");
	}
    }

}
