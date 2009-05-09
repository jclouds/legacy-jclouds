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

import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.withBucketAcl;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3Headers;
import org.jclouds.aws.s3.S3IntegrationTest;
import org.jclouds.aws.s3.S3Utils;
import static org.jclouds.aws.s3.commands.options.PutObjectOptions.Builder.*;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests integrated functionality of all PutObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "integration", testName = "s3.PutObjectIntegrationTest")
public class PutObjectIntegrationTest extends S3IntegrationTest {
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
	client.putBucketIfNotExists(bucketName).get(10, TimeUnit.SECONDS);
	context.createS3ObjectMap(bucketName).clear();
	assertEquals(client.listBucket(bucketName).get(10, TimeUnit.SECONDS)
		.getContents().size(), 0);
	S3Object object = new S3Object(key);
	object.getMetaData().setContentType(type);
	object.setData(content);
	if (content instanceof InputStream) {
	    object.generateMd5();
	}
	assertNotNull(client.putObject(bucketName, object).get(10,
		TimeUnit.SECONDS));
	object = client.getObject(bucketName, object.getKey()).get(10,
		TimeUnit.SECONDS);
	String returnedString = S3Utils.getContentAsStringAndClose(object);
	assertEquals(returnedString, realObject);
	assertEquals(client.listBucket(bucketName).get(10, TimeUnit.SECONDS)
		.getContents().size(), 1);
    }

    @Test
    void testMetaData() throws Exception {
	String bucketName = bucketPrefix + "header";
	createBucketAndEnsureEmpty(bucketName);
	String key = "hello";

	S3Object object = new S3Object(key, TEST_STRING);
	object.getMetaData().setCacheControl("no-cache");
	object.getMetaData().setContentType("text/plain");
	object.getMetaData().setContentEncoding("x-compress");
	object.getMetaData().setSize(TEST_STRING.length());
	object.getMetaData().setContentDisposition(
		"attachment; filename=hello.txt");
	object.getMetaData().getUserMetadata().put(
		S3Headers.USER_METADATA_PREFIX + "adrian", "powderpuff");
	object.getMetaData().setMd5(S3Utils.md5(TEST_STRING.getBytes()));

	addObjectToBucket(bucketName, object);
	S3Object newObject = validateContent(bucketName, key);

	// TODO.. why does this come back as binary/octetstring
	assertEquals(newObject.getMetaData().getContentType(),
		"binary/octet-stream");
	assertEquals(newObject.getMetaData().getContentEncoding(), "x-compress");
	assertEquals(newObject.getMetaData().getContentDisposition(),
		"attachment; filename=hello.txt");
	assertEquals(newObject.getMetaData().getCacheControl(), "no-cache");
	assertEquals(newObject.getMetaData().getSize(), TEST_STRING.length());
	assertEquals(newObject.getMetaData().getUserMetadata().values()
		.iterator().next(), "powderpuff");
	assertEquals(newObject.getMetaData().getMd5(), S3Utils.md5(TEST_STRING
		.getBytes()));
    }

    @Test()
    void testCannedAccessPolicyPublic() throws Exception {
	String bucketName = bucketPrefix + "aclpublic";
	createBucketAndEnsureEmpty(bucketName);
	String key = "hello";

	client.putBucketIfNotExists(bucketName,
		withBucketAcl(CannedAccessPolicy.PUBLIC_READ)).get(10,
		TimeUnit.SECONDS);
	client.putObject(bucketName, new S3Object(key, TEST_STRING),

	withAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

	URL url = new URL(String.format("http://%1s.s3.amazonaws.com/%1s",
		bucketName, key));
	S3Utils.toStringAndClose(url.openStream());

    }

}