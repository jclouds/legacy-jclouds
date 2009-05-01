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
package org.jclouds.aws.s3;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups = "unit", sequential = true, testName = "s3.AmazonS3Test")
public class AmazonS3Test extends S3IntegrationTest {

    private String returnedString;

    List<S3Bucket> listAllMyBuckets() throws Exception {
	return client.getBuckets().get();
    }

    S3Object getObject() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.getObject(s3Bucket, "3366").get();
    }

    String putFileObject() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	S3Object object = new S3Object("meat");
	object.setContentType("text/xml");
	object.setContent(new File("pom.xml"));
	return client.addObject(s3Bucket, object).get();
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
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "filetestsforadrian");
	client.createBucketIfNotExists(s3Bucket).get();
	context.createS3ObjectMap(s3Bucket).clear();
	assertEquals(client.getBucket(s3Bucket).get().getContents().size(), 0);
	S3Object object = new S3Object(key);
	object.setContentType(type);
	object.setContent(content);
	assertNotNull(client.addObject(s3Bucket, object).get());
	object = client.getObject(s3Bucket, object.getKey()).get();
	returnedString = S3Utils.getContentAsStringAndClose(object);
	assertEquals(returnedString, realObject);
	assertEquals(client.getBucket(s3Bucket).get().getContents().size(), 1);
    }

    @Test
    void testCopyObject() throws Exception {
	String realObject = IOUtils.toString(new FileInputStream("pom.xml"));

	S3Bucket sourceBucket = new S3Bucket(bucketPrefix + "copysource");
	client.createBucketIfNotExists(sourceBucket).get();
	assertEquals(client.getBucket(sourceBucket).get().getContents().size(),
		0);

	S3Object sourceObject = new S3Object("file");
	sourceObject.setContentType("text/xml");
	sourceObject.setContent(new File("pom.xml"));

	client.addObject(sourceBucket, sourceObject).get();
	assertEquals(client.getBucket(sourceBucket).get().getContents().size(),
		1);

	sourceObject = client.getObject(sourceBucket, sourceObject.getKey())
		.get();
	assertEquals(S3Utils.getContentAsStringAndClose(sourceObject),
		realObject);

	S3Bucket destinationBucket = new S3Bucket(bucketPrefix
		+ "copydestination");
	client.createBucketIfNotExists(destinationBucket).get();
	assertEquals(client.getBucket(destinationBucket).get().getContents()
		.size(), 0);

	S3Object destinationObject = new S3Object(sourceObject.getKey());

	client.copyObject(sourceBucket, sourceObject, destinationBucket,
		destinationObject).get();
	assertEquals(client.getBucket(destinationBucket).get().getContents()
		.size(), 1);

	destinationObject = client.getObject(destinationBucket,
		destinationObject.getKey()).get();

	assertEquals(S3Utils.getContentAsStringAndClose(destinationObject),
		realObject);

    }

    S3Object headObject() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.headObject(s3Bucket, "3366").get();
    }

    Boolean bucketExists() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.bucketExists(s3Bucket).get();
    }

    Boolean deleteBucket() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.deleteBucket(s3Bucket).get();
    }

    Boolean deleteObject() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.deleteObject(s3Bucket, "3366").get();
    }

    Boolean createBucketIfNotExists() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.createBucketIfNotExists(s3Bucket).get();
    }

    S3Bucket getBucket() throws Exception {
	S3Bucket s3Bucket = new S3Bucket(bucketPrefix + "adrianjbosstest");
	return client.getBucket(s3Bucket).get();
    }

}