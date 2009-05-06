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
package org.jclouds.aws.s3.xml;

import java.util.List;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Creates Parsers needed to interpret S3 Server responses
 * 
 * @author Adrian Cole
 */
public class S3ParserFactory {

    @Inject
    private GenericParseFactory<List<S3Bucket.MetaData>> parseListAllMyBucketsFactory;

    public static interface GenericParseFactory<T> {
	ParseSax<T> create(ParseSax.HandlerWithResult<T> handler);
    }

    @Inject
    Provider<ListAllMyBucketsHandler> ListAllMyBucketsHandlerprovider;

    @VisibleForTesting
    public ParseSax<List<S3Bucket.MetaData>> createListBucketsParser() {
	return parseListAllMyBucketsFactory
		.create(ListAllMyBucketsHandlerprovider.get());
    }

    @Inject
    private GenericParseFactory<S3Bucket> parseListBucketFactory;

    @Inject
    Provider<ListBucketHandler> ListBucketHandlerprovider;

    @VisibleForTesting
    public ParseSax<S3Bucket> createListBucketParser() {
	return parseListBucketFactory.create(ListBucketHandlerprovider.get());
    }

    @Inject
    private GenericParseFactory<S3Object.MetaData> parseCopyObjectFactory;

    @Inject
    Provider<CopyObjectHandler> copyObjectHandlerProvider;

    @VisibleForTesting
    public ParseSax<S3Object.MetaData> createCopyObjectParser() {
	return parseCopyObjectFactory.create(copyObjectHandlerProvider.get());
    }

    @Inject
    private GenericParseFactory<S3Error> parseErrorFactory;

    @Inject
    Provider<ErrorHandler> errorHandlerProvider;

    public ParseSax<S3Error> createErrorParser() {
	return parseErrorFactory.create(errorHandlerProvider.get());
    }

}