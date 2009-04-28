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
package org.jclouds.aws.s3.commands.config;

import java.util.List;

import org.jclouds.aws.s3.commands.CopyObject;
import org.jclouds.aws.s3.commands.DeleteBucket;
import org.jclouds.aws.s3.commands.DeleteObject;
import org.jclouds.aws.s3.commands.HeadBucket;
import org.jclouds.aws.s3.commands.PutBucket;
import org.jclouds.aws.s3.commands.PutObject;
import org.jclouds.aws.s3.commands.RetrieveObject;
import org.jclouds.aws.s3.commands.S3CommandFactory;
import org.jclouds.aws.s3.commands.callables.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.commands.callables.xml.ListBucketHandler;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.jclouds.http.commands.callables.xml.config.SaxModule;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryProvider;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class S3CommandsModule extends AbstractModule {
    @Override
    protected void configure() {
	install(new SaxModule());

	bind(S3CommandFactory.CopyObjectFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.CopyObjectFactory.class,
			CopyObject.class));

	bind(S3CommandFactory.DeleteBucketFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.DeleteBucketFactory.class,
			DeleteBucket.class));

	bind(S3CommandFactory.DeleteObjectFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.DeleteObjectFactory.class,
			DeleteObject.class));

	bind(S3CommandFactory.HeadBucketFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.HeadBucketFactory.class,
			HeadBucket.class));

	final TypeLiteral<S3CommandFactory.GenericParseFactory<List<S3Bucket>>> listBucketsTypeLiteral = new TypeLiteral<S3CommandFactory.GenericParseFactory<List<S3Bucket>>>() {
	};
	final TypeLiteral<S3CommandFactory.GenericParseFactory<S3Bucket>> bucketTypeLiteral = new TypeLiteral<S3CommandFactory.GenericParseFactory<S3Bucket>>() {
	};

	bind(listBucketsTypeLiteral).toProvider(
		FactoryProvider.newFactory(listBucketsTypeLiteral,
			new TypeLiteral<ParseSax<List<S3Bucket>>>() {
			}));

	bind(bucketTypeLiteral).toProvider(
		FactoryProvider.newFactory(bucketTypeLiteral,
			new TypeLiteral<ParseSax<S3Bucket>>() {
			}));

	bind(new TypeLiteral<ParseSax.HandlerWithResult<List<S3Bucket>>>() {
	}).to(ListAllMyBucketsHandler.class);

	bind(new TypeLiteral<ParseSax.HandlerWithResult<S3Bucket>>() {
	}).to(ListBucketHandler.class);

	bind(S3CommandFactory.PutBucketFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.PutBucketFactory.class,
			PutBucket.class));

	bind(S3CommandFactory.PutObjectFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.PutObjectFactory.class,
			PutObject.class));

	bind(S3CommandFactory.RetrieveObjectFactory.class).toProvider(
		FactoryProvider.newFactory(
			S3CommandFactory.RetrieveObjectFactory.class,
			RetrieveObject.class));

    }

}