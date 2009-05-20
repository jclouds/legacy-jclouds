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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.internal.BaseS3Map;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public abstract class BaseS3MapTest<T> extends S3IntegrationTest {

    public abstract void testPutAll();

    public abstract void testEntrySet() throws IOException;

    public abstract void testValues() throws IOException;

    protected BaseS3Map<T> map;
    protected Map<String, String> fiveStrings = ImmutableMap.of("one", "apple",
	    "two", "bear", "three", "candy", "four", "dogma", "five", "emma");
    protected Map<String, byte[]> fiveBytes = ImmutableMap.of("one", "apple"
	    .getBytes(), "two", "bear".getBytes(), "three", "candy".getBytes(),
	    "four", "dogma".getBytes(), "five", "emma".getBytes());
    protected Map<String, InputStream> fiveInputs;
    protected Map<String, File> fiveFiles;
    String tmpDirectory;
    private String bucket;

    @BeforeMethod
    @Parameters( { "basedir" })
    protected void setUpTempDir(String basedir) throws InterruptedException,
	    ExecutionException, FileNotFoundException, IOException,
	    TimeoutException {
	tmpDirectory = basedir + File.separator + "target" + File.separator
		+ "testFiles" + File.separator + getClass().getSimpleName();
	new File(tmpDirectory).mkdirs();

	fiveFiles = ImmutableMap.of("one", new File(tmpDirectory, "apple"),
		"two", new File(tmpDirectory, "bear"), "three", new File(
			tmpDirectory, "candy"), "four", new File(tmpDirectory,
			"dogma"), "five", new File(tmpDirectory, "emma"));

	for (File file : fiveFiles.values()) {
	    IOUtils.write(file.getName(), new FileOutputStream(file));
	}

	fiveInputs = ImmutableMap.of("one", IOUtils.toInputStream("apple"),
		"two", IOUtils.toInputStream("bear"), "three", IOUtils
			.toInputStream("candy"), "four", IOUtils
			.toInputStream("dogma"), "five", IOUtils
			.toInputStream("emma"));
	bucket = (bucketPrefix + ".mimi").toLowerCase();
	client.putBucketIfNotExists(bucket).get(10, TimeUnit.SECONDS);
	map = createMap(context, bucket);
	map.clear();
    }

    protected abstract BaseS3Map<T> createMap(S3Context context, String bucket);

    @AfterMethod
    public void tearDown() {
	map.clear();
	map = null;
    }

    @Test
    public void testClear() {
	map.clear();
	assertEquals(map.size(), 0);
	putString("one", "apple");
	assertEquals(map.size(), 1);
	map.clear();
	assertEquals(map.size(), 0);
    }

    @Test()
    public abstract void testRemove() throws IOException;

    @Test()
    public void testKeySet() {
	assertEquals(map.keySet().size(), 0);
	putString("one", "two");
	assertEquals(map.keySet(), ImmutableSet.of("one"));
    }

    @Test()
    public void testContainsKey() {
	assert !map.containsKey("one");
	putString("one", "apple");
	assert map.containsKey("one");
    }

    @Test()
    public void testIsEmpty() {
	assert map.isEmpty();
	putString("one", "apple");
	assert !map.isEmpty();
    }

    abstract protected void putString(String key, String value);

    protected void fourLeftRemovingOne() {
	map.remove("one");
	assertEquals(map.size(), 4);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		ImmutableSet.of("two", "three", "four", "five")));
    }

    @Test
    public abstract void testPut() throws IOException;

    @Test()
    public void testGetBucket() {
	assertEquals(map.getBucket().getName(), bucket);
    }

}