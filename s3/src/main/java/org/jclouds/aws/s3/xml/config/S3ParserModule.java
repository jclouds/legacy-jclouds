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
package org.jclouds.aws.s3.xml.config;

import java.util.List;

import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Error;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.aws.s3.xml.ErrorHandler;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.jclouds.http.commands.callables.xml.config.SaxModule;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * Creates the factories needed to interpret S3 responses
 * 
 * @author Adrian Cole
 */
public class S3ParserModule extends AbstractModule {
    @Override
    protected void configure() {
	install(new SaxModule());

	final TypeLiteral<S3ParserFactory.GenericParseFactory<List<S3Bucket.Metadata>>> listBucketsTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<List<S3Bucket.Metadata>>>() {
	};
	final TypeLiteral<S3ParserFactory.GenericParseFactory<S3Bucket>> bucketTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<S3Bucket>>() {
	};
	final TypeLiteral<S3ParserFactory.GenericParseFactory<S3Object.Metadata>> objectMetaDataTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<S3Object.Metadata>>() {
	};
	final TypeLiteral<S3ParserFactory.GenericParseFactory<S3Error>> errorTypeLiteral = new TypeLiteral<S3ParserFactory.GenericParseFactory<S3Error>>() {
	};

	bind(listBucketsTypeLiteral).toProvider(
		FactoryProvider.newFactory(listBucketsTypeLiteral,
			new TypeLiteral<ParseSax<List<S3Bucket.Metadata>>>() {
			}));
	bind(bucketTypeLiteral).toProvider(
		FactoryProvider.newFactory(bucketTypeLiteral,
			new TypeLiteral<ParseSax<S3Bucket>>() {
			}));
	bind(objectMetaDataTypeLiteral).toProvider(
		FactoryProvider.newFactory(objectMetaDataTypeLiteral,
			new TypeLiteral<ParseSax<S3Object.Metadata>>() {
			}));
	bind(errorTypeLiteral).toProvider(
		FactoryProvider.newFactory(errorTypeLiteral,
			new TypeLiteral<ParseSax<S3Error>>() {
			}));

	// Bind the implementations of handlers to the interfaces they implement
	bind(
		new TypeLiteral<ParseSax.HandlerWithResult<List<S3Bucket.Metadata>>>() {
		}).to(ListAllMyBucketsHandler.class);
	bind(new TypeLiteral<ParseSax.HandlerWithResult<S3Bucket>>() {
	}).to(ListBucketHandler.class);
	bind(new TypeLiteral<ParseSax.HandlerWithResult<S3Object.Metadata>>() {
	}).to(CopyObjectHandler.class);
	bind(new TypeLiteral<ParseSax.HandlerWithResult<S3Error>>() {
	}).to(ErrorHandler.class);
    }

}