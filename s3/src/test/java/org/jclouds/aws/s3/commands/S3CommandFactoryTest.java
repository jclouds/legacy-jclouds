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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import org.jclouds.aws.s3.commands.config.S3CommandsModule;
import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata.LocationConstraint;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class S3CommandFactoryTest {

    Injector injector = null;
    S3CommandFactory commandFactory = null;

    public static final String listAllMyBucketsResult = "<ListAllMyBucketsResult xmlns=\"http://s3.amazonaws.com/doc/callables/\"><Owner ><ID>e1a5f66a480ca99a4fdfe8e318c3020446c9989d7004e7778029fbcc5d990fa0</ID></Owner><Buckets><Bucket><Name>adrianjbosstest</Name><CreationDate>2009-03-12T02:00:07.000Z</CreationDate></Bucket><Bucket><Name>adrianjbosstest2</Name><CreationDate>2009-03-12T02:00:09.000Z</CreationDate></Bucket></Buckets></ListAllMyBucketsResult>";

    @BeforeMethod
    void setUpInjector() {
	injector = Guice.createInjector(new S3CommandsModule() {
	    @Override
	    protected void configure() {
		bindConstant().annotatedWith(
			Names.named("jclouds.http.address")).to("localhost");
		super.configure();
	    }
	});
	commandFactory = injector.getInstance(S3CommandFactory.class);
    }

    @AfterMethod
    void tearDownInjector() {
	commandFactory = null;
	injector = null;
    }

    @Test
    void testCreateCopyObject() {
	assert commandFactory.createCopyObject("sourceBucket", "sourceObject",
		"destBucket", "destObject", CopyObjectOptions.NONE) != null;
    }

    @Test
    void testCreateCopyObjectOptions() {
	assert commandFactory.createCopyObject("sourceBucket", "sourceObject",
		"destBucket", "destObject", new CopyObjectOptions()) != null;
    }

    @Test
    void testCreateDeleteBucket() {
	assert commandFactory.createDeleteBucket("test") != null;
    }

    @Test
    void testCreateDeleteObject() {
	assert commandFactory.createDeleteObject("test", "blah") != null;
    }

    @Test
    void testCreateHeadBucket() {
	assert commandFactory.createHeadBucket("test") != null;
    }

    @Test
    void testCreatePutBucket() {
	assert commandFactory.createPutBucket("test", PutBucketOptions.NONE) != null;
    }

    @Test
    void testCreatePutBucketOptions() {
	assert commandFactory.createPutBucket("test", PutBucketOptions.Builder
		.createIn(LocationConstraint.EU)) != null;
    }

    @Test
    void testCreatePutObject() {
	S3Object.Metadata metaData = createMock(S3Object.Metadata.class);
	S3Object object = new S3Object(metaData);
	expect(metaData.getSize()).andReturn(4L).atLeastOnce();
	expect(metaData.getKey()).andReturn("rawr");
	expect(metaData.getContentType()).andReturn("text/xml").atLeastOnce();
	expect(metaData.getCacheControl()).andReturn("no-cache").atLeastOnce();
	expect(metaData.getContentDisposition()).andReturn("disposition")
		.atLeastOnce();
	expect(metaData.getContentEncoding()).andReturn("encoding")
		.atLeastOnce();
	expect(metaData.getMd5()).andReturn("encoding".getBytes())
		.atLeastOnce();
	Multimap<String, String> userMdata = HashMultimap.create();
	expect(metaData.getUserMetadata()).andReturn(userMdata).atLeastOnce();

	replay(metaData);
	object.setData("<a></a>");

	assert commandFactory.createPutObject("test", object,
		PutObjectOptions.NONE) != null;
    }

    @Test
    void testCreateGetObject() {
	assert commandFactory.createGetObject("test", "blah",
		GetObjectOptions.NONE) != null;
    }

    @Test
    void testCreateHeadMetaData() {
	assert commandFactory.createHeadMetaData("test", "blah") != null;
    }

    @Test
    void testCreateListAllMyBuckets() {
	assert commandFactory.createGetMetaDataForOwnedBuckets() != null;
    }

    @Test
    void testCreateListBucket() {
	assert commandFactory.createListBucket("test", ListBucketOptions.NONE) != null;
    }

}