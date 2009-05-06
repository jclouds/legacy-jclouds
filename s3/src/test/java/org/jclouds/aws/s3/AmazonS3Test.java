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
package org.jclouds.aws.s3;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = "unit", sequential = true, testName = "s3.AmazonS3Test")
public class AmazonS3Test extends S3IntegrationTest {

    private String returnedString;

    List<S3Bucket.MetaData> listAllMyBuckets() throws Exception {
	return client.getMetaDataOfOwnedBuckets().get(10, TimeUnit.SECONDS);
    }

    S3Object getObject() throws Exception {
	return client.getObject(bucketPrefix + "adrianjbosstest", "3366").get(
		10, TimeUnit.SECONDS);
    }

    String putFileObject() throws Exception {
	S3Object object = new S3Object("meat");
	object.getMetaData().setContentType("text/xml");
	object.setData(new File("pom.xml"));
	return client.addObject(bucketPrefix + "adrianjbosstest", object).get(
		10, TimeUnit.SECONDS);
    }

    @DataProvider(name = "putTests")
    public Object[][] createData1() throws IOException {

	String realObject = IOUtils.toString(new FileInputStream("pom.xml"));

	return new Object[][] {
		{ "file", "text/xml", new File("pom.xml"), realObject },
		{ "string", "text/xml", realObject, realObject },
		{ "bytes", "application/octet-stream", realObject.getBytes(),
			realObject } };
    }

    @Test(dataProvider = "putTests")
    void testPutObject(String key, String type, Object content,
	    Object realObject) throws Exception {
	String s3Bucket = bucketPrefix + "filetestsforadrian";
	client.createBucketIfNotExists(s3Bucket).get(10, TimeUnit.SECONDS);
	context.createS3ObjectMap(s3Bucket).clear();
	assertEquals(client.getBucket(s3Bucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 0);
	S3Object object = new S3Object(key);
	object.getMetaData().setContentType(type);
	object.setData(content);
	assertNotNull(client.addObject(s3Bucket, object).get(10,
		TimeUnit.SECONDS));
	object = client.getObject(s3Bucket, object.getKey()).get(10,
		TimeUnit.SECONDS);
	returnedString = S3Utils.getContentAsStringAndClose(object);
	assertEquals(returnedString, realObject);
	assertEquals(client.getBucket(s3Bucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 1);
    }

    @Test
    void testCopyObject() throws Exception {
	String realObject = IOUtils.toString(new FileInputStream("pom.xml"));

	String sourceBucket = bucketPrefix + "copysource";
	client.createBucketIfNotExists(sourceBucket).get(10, TimeUnit.SECONDS);
	assertEquals(client.getBucket(sourceBucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 0);

	S3Object sourceObject = new S3Object("file");
	sourceObject.getMetaData().setContentType("text/xml");
	sourceObject.setData(new File("pom.xml"));

	client.addObject(sourceBucket, sourceObject).get(10, TimeUnit.SECONDS);
	assertEquals(client.getBucket(sourceBucket).get(10, TimeUnit.SECONDS)
		.getContents().size(), 1);

	sourceObject = client.getObject(sourceBucket, sourceObject.getKey())
		.get(10, TimeUnit.SECONDS);
	assertEquals(S3Utils.getContentAsStringAndClose(sourceObject),
		realObject);

	String destinationBucket = bucketPrefix + "copydestination";
	client.createBucketIfNotExists(destinationBucket).get(10,
		TimeUnit.SECONDS);
	assertEquals(client.getBucket(destinationBucket).get(10,
		TimeUnit.SECONDS).getContents().size(), 0);

	client.copyObject(sourceBucket, sourceObject.getKey(),
		destinationBucket, sourceObject.getKey()).get(10,
		TimeUnit.SECONDS);
	assertEquals(client.getBucket(destinationBucket).get(10,
		TimeUnit.SECONDS).getContents().size(), 1);
	// todo compare etag

	S3Object destinationObject = client.getObject(destinationBucket,
		sourceObject.getKey()).get(10, TimeUnit.SECONDS);
	assertEquals(S3Utils.getContentAsStringAndClose(destinationObject),
		realObject);

    }

    S3Object.MetaData headObject() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.getObjectMetaData(s3Bucket, "3366").get(10,
		TimeUnit.SECONDS);
    }

    Boolean bucketExists() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.bucketExists(s3Bucket).get(10, TimeUnit.SECONDS);
    }

    Boolean deleteBucket() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.deleteBucketIfNotEmpty(s3Bucket)
		.get(10, TimeUnit.SECONDS);
    }

    Boolean deleteObject() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.deleteObject(s3Bucket, "3366").get(10, TimeUnit.SECONDS);
    }

    Boolean createBucketIfNotExists() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.createBucketIfNotExists(s3Bucket).get(10,
		TimeUnit.SECONDS);
    }

    S3Bucket getBucket() throws Exception {
	String s3Bucket = bucketPrefix + "adrianjbosstest";
	return client.getBucket(s3Bucket).get(10, TimeUnit.SECONDS);
    }

}