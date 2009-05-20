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
package org.jclouds.aws.s3.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.aws.s3.S3Connection;
import org.jclouds.aws.s3.S3Context;
import org.jclouds.aws.s3.S3InputStreamMap;
import org.jclouds.aws.s3.S3ObjectMap;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by S3Context methods.
 * 
 * @see Injector
 * @author Adrian Cole
 */
public class GuiceS3Context implements S3Context {
    public interface S3ObjectMapFactory {
	S3ObjectMap createMapView(String bucket);
    }

    public interface S3InputStreamMapFactory {
	S3InputStreamMap createMapView(String bucket);
    }

    @Resource
    private Logger logger = Logger.NULL;
    private final Injector injector;
    private final S3InputStreamMapFactory s3InputStreamMapFactory;
    private final S3ObjectMapFactory s3ObjectMapFactory;
    private final Closer closer;

    @Inject
    private GuiceS3Context(Injector injector, Closer closer,
	    S3ObjectMapFactory s3ObjectMapFactory,
	    S3InputStreamMapFactory s3InputStreamMapFactory) {
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
    public S3InputStreamMap createInputStreamMap(String bucket) {
	getConnection().putBucketIfNotExists(bucket);
	return s3InputStreamMapFactory.createMapView(bucket);
    }

    /**
     * {@inheritDoc}
     */
    public S3ObjectMap createS3ObjectMap(String bucket) {
	getConnection().putBucketIfNotExists(bucket);
	return s3ObjectMapFactory.createMapView(bucket);
    }

    /**
     * {@inheritDoc}
     * 
     * @see Closer
     */
    public void close() {
	try {
	    closer.close();
	} catch (IOException e) {
	    logger.error(e, "error closing content");
	}
    }

}
