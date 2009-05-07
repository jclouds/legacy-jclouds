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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import static org.jclouds.aws.s3.commands.options.GetBucketOptions.Builder.*;
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
	String bucketName = bucketPrefix + "filetestsforadrian";
	client.createBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
	context.createS3ObjectMap(bucketName).clear();
	assertEquals(client.getBucket(bucketName).get(10, TimeUnit.SECONDS)
		.getContents().size(), 0);
	S3Object object = new S3Object(key);
	object.getMetaData().setContentType(type);
	object.setData(content);
	assertNotNull(client.addObject(bucketName, object).get(10,
		TimeUnit.SECONDS));
	object = client.getObject(bucketName, object.getKey()).get(10,
		TimeUnit.SECONDS);
	returnedString = S3Utils.getContentAsStringAndClose(object);
	assertEquals(returnedString, realObject);
	assertEquals(client.getBucket(bucketName).get(10, TimeUnit.SECONDS)
		.getContents().size(), 1);
    }

    @Test()
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
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.getObjectMetaData(bucketName, "3366").get(10,
		TimeUnit.SECONDS);
    }

    @Test()
    void testGetBucketDelimiter() throws InterruptedException,
	    ExecutionException, TimeoutException, UnsupportedEncodingException {
	String bucketName = bucketPrefix + "delimiter";
	assert client.createBucketIfNotExists(bucketName).get(10,
		TimeUnit.SECONDS);
	String prefix = "apps";
	addTenObjectsUnderPrefix(bucketName, prefix);
	add15UnderRoot(bucketName);
	S3Bucket bucket = client.getBucket(bucketName, delimiter("/")).get(10,
		TimeUnit.SECONDS);
	assertEquals(bucket.getDelimiter(), "/");
	assertEquals(bucket.getContents().size(), 15);
	assertEquals(bucket.getCommonPrefixes().size(), 1);
    }

    private void addAlphabetUnderRoot(String bucketName)
	    throws InterruptedException, ExecutionException, TimeoutException {
	for (char letter = 'a'; letter <= 'z'; letter++) {
	    client.addObject(bucketName,
		    new S3Object(letter + "", letter + "content")).get(10,
		    TimeUnit.SECONDS);
	}
    }

    @Test
    void testGetBucketMarker() throws InterruptedException, ExecutionException,
	    TimeoutException, UnsupportedEncodingException {
	String bucketName = bucketPrefix + "marker";
	assert client.createBucketIfNotExists(bucketName).get(10,
		TimeUnit.SECONDS);
	addAlphabetUnderRoot(bucketName);
	S3Bucket bucket = client.getBucket(bucketName, marker("y")).get(10,
		TimeUnit.SECONDS);
	assertEquals(bucket.getMarker(), "y");
	assertEquals(bucket.getContents().size(), 1);
    }

    @Test()
    void testGetBucketPrefix() throws InterruptedException, ExecutionException,
	    TimeoutException, UnsupportedEncodingException {
	String bucketName = bucketPrefix + "prefix";
	assert client.createBucketIfNotExists(bucketName).get(10,
		TimeUnit.SECONDS);
	String prefix = "apps";
	addTenObjectsUnderPrefix(bucketName, prefix);
	add15UnderRoot(bucketName);

	S3Bucket bucket = client.getBucket(bucketName, prefix("apps/")).get(10,
		TimeUnit.SECONDS);
	assertEquals(bucket.getContents().size(), 10);
	assertEquals(bucket.getPrefix(), "apps/");

    }

    private void add15UnderRoot(String bucketName) throws InterruptedException,
	    ExecutionException, TimeoutException {
	for (int i = 0; i < 15; i++)
	    client.addObject(bucketName, new S3Object(i + "", i + "content"))
		    .get(10, TimeUnit.SECONDS);
    }

    private void addTenObjectsUnderPrefix(String bucketName, String prefix)
	    throws InterruptedException, ExecutionException, TimeoutException {
	for (int i = 0; i < 10; i++)
	    client.addObject(bucketName,
		    new S3Object(prefix + "/" + i, i + "content")).get(10,
		    TimeUnit.SECONDS);
    }

    @Test()
    void bucketExists() throws Exception {
	String bucketName = bucketPrefix + "needstoexist";
	assert !client.bucketExists(bucketName).get(10, TimeUnit.SECONDS);
	assert client.createBucketIfNotExists(bucketName).get(10,
		TimeUnit.SECONDS);
	assert client.bucketExists(bucketName).get(10, TimeUnit.SECONDS);

    }

    Boolean deleteBucket() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.deleteBucketIfEmpty(bucketName).get(10, TimeUnit.SECONDS);
    }

    Boolean deleteObject() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.deleteObject(bucketName, "3366")
		.get(10, TimeUnit.SECONDS);
    }

    Boolean createBucketIfNotExists() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.createBucketIfNotExists(bucketName).get(10,
		TimeUnit.SECONDS);
    }

    S3Bucket getBucket() throws Exception {
	String bucketName = bucketPrefix + "adrianjbosstest";
	return client.getBucket(bucketName).get(10, TimeUnit.SECONDS);
    }

}