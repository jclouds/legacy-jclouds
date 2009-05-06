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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import org.jclouds.aws.s3.commands.config.S3CommandsModule;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
	assert commandFactory.createCopyObject(new S3Bucket("sourceBucket"),
		new S3Object("sourceObject"), new S3Bucket("destBucket"),
		new S3Object("destObject")) != null;
    }

    @Test
    void testCreateDeleteBucket() {
	assert commandFactory.createDeleteBucket(new S3Bucket("test")) != null;
    }

    @Test
    void testCreateDeleteObject() {
	assert commandFactory.createDeleteObject(new S3Bucket("test"), "blah") != null;
    }

    @Test
    void testCreateHeadBucket() {
	assert commandFactory.createHeadBucket(new S3Bucket("test")) != null;
    }

    @Test
    void testCreatePutBucket() {
	assert commandFactory.createPutBucket(new S3Bucket("test")) != null;
    }

    @Test
    void testCreatePutObject() {
	S3Object.MetaData metaData = createMock(S3Object.MetaData.class);
	S3Object object = new S3Object(metaData);
	object.setData("<a></a>");
	expect(metaData.getKey()).andReturn("rawr");
	expect(metaData.getContentType()).andReturn("text/xml").atLeastOnce();
	expect(metaData.getSize()).andReturn(4L);

	replay(metaData);

	assert commandFactory.createPutObject(new S3Bucket("test"), object) != null;
    }

    @Test
    void testCreateGetObject() {
	assert commandFactory.createGetObject(new S3Bucket("test"), "blah") != null;
    }

    @Test
    void testCreateHeadMetaData() {
	assert commandFactory.createHeadMetaData(new S3Bucket("test"), "blah") != null;
    }

    @Test
    void testCreateListBucketsParser() {
	assert commandFactory.createListBucketsParser() != null;
    }

    @Test
    void testCreateListAllMyBuckets() {
	assert commandFactory.createListAllMyBuckets() != null;
    }

    @Test
    void testCreateListBucketParser() {
	assert commandFactory.createListBucketParser() != null;
    }

    @Test
    void testCreateListBucket() {
	assert commandFactory.createListBucket(new S3Bucket("test")) != null;
    }

}