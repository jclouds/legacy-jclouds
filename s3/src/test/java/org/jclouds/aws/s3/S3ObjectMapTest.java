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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.jclouds.Utils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "s3.S3ObjectMapTest")
public class S3ObjectMapTest extends S3IntegrationTest {

    private S3Bucket bucket;
    private S3ObjectMap map;

    private Map<String, String> fiveStrings = ImmutableMap.of("one", "apple",
	    "two", "bear", "three", "candy", "four", "dogma", "five", "emma");
    private Map<String, byte[]> fiveBytes = ImmutableMap.of("one", "apple"
	    .getBytes(), "two", "bear".getBytes(), "three", "candy".getBytes(),
	    "four", "dogma".getBytes(), "five", "emma".getBytes());
    private Map<String, InputStream> fiveInputs;
    private Map<String, File> fiveFiles;

    String tmpDirectory;

    @BeforeMethod
    @Parameters( { "basedir" })
    protected void setUpTempDir(String basedir) throws InterruptedException,
	    ExecutionException, FileNotFoundException, IOException {
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
	bucket = new S3Bucket();
	bucket.setName(bucketPrefix + ".mimi");
	client.createBucketIfNotExists(bucket).get();
	map = context.createMapView(bucket);
	map.clear();
    }

    @AfterMethod
    public void tearDown() {
	map.clear();
	map = null;
	bucket = null;
    }

    @Test
    public void testClear() {
	map.clear();
	assert map.size() == 0;
	map.putString("one", "apple");
	map.clear();
	assert map.size() == 0;
    }

    @Test()
    public void testRemove() throws IOException {
	map.putString("one", "two");
	InputStream old = map.remove("one");
	assertEquals(Utils.toStringAndClose(old), "two");
	old = map.remove("one");
	assert old == null;
	old = map.get("one");
	assert old == null;
	assertEquals(map.keySet().size(), 0);
    }

    @Test()
    public void testKeySet() {
	assertEquals(map.keySet().size(), 0);
	map.putString("one", "two");
	assertEquals(map.keySet(), ImmutableSet.of("one"));
    }

    @Test()
    public void testValues() throws IOException {
	map.putAll(this.fiveInputs);
	Collection<InputStream> values = map.values();
	assertEquals(values.size(), 5);
	Set<String> valuesAsString = new HashSet<String>();
	for (InputStream stream : values) {
	    valuesAsString.add(Utils.toStringAndClose(stream));
	}
	valuesAsString.removeAll(fiveStrings.values());
	assert valuesAsString.size() == 0;
    }

    @Test()
    public void testEntrySet() throws IOException {
	map.putAllStrings(this.fiveStrings);
	Set<Entry<String, InputStream>> entries = map.entrySet();
	assertEquals(entries.size(), 5);
	for (Entry<String, InputStream> entry : entries) {
	    assertEquals(IOUtils.toString(entry.getValue()), fiveStrings
		    .get(entry.getKey()));
	    entry.setValue(IOUtils.toInputStream(""));
	}
	assertEquals(map.size(), 5);
	for (InputStream value : map.values()) {
	    assertEquals(IOUtils.toString(value), "");
	}
    }

    @Test()
    public void testContainsKey() {
	assert !map.containsKey("one");
	map.putString("one", "apple");
	assert map.containsKey("one");
    }

    @Test()
    public void testContainsStringValue() {
	map.putString("one", "apple");
	assert map.containsValue(fiveStrings.get("one"));
    }

    @Test()
    public void testContainsFileValue() {
	map.putString("one", "apple");
	assert map.containsValue(fiveFiles.get("one"));
    }

    @Test()
    public void testContainsInputStreamValue() {
	map.putString("one", "apple");
	assert map.containsValue(this.fiveInputs.get("one"));
    }

    @Test()
    public void testContainsBytesValue() {
	map.putString("one", "apple");
	assert map.containsValue(this.fiveBytes.get("one"));
    }

    @Test()
    public void testIsEmpty() {
	assert map.isEmpty();
	map.putString("one", "apple");
	assert !map.isEmpty();
    }

    @Test()
    public void testPutAll() {
	map.putAll(this.fiveInputs);
	assertEquals(map.size(), 5);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		fiveInputs.keySet()));
	fourLeftRemovingOne();
    }

    @Test()
    public void testPutAllBytes() {
	map.putAllBytes(this.fiveBytes);
	assertEquals(map.size(), 5);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		fiveBytes.keySet()));
	fourLeftRemovingOne();
    }

    @Test
    public void testPutAllFiles() {
	map.putAllFiles(this.fiveFiles);
	assertEquals(map.size(), 5);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		fiveFiles.keySet()));
	fourLeftRemovingOne();
    }

    void fourLeftRemovingOne() {
	map.remove("one");
	assertEquals(map.size(), 4);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		ImmutableSet.of("two", "three", "four", "five")));
    }

    @Test()
    public void testPutAllStrings() {
	map.putAllStrings(this.fiveStrings);
	assertEquals(map.size(), 5);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		fiveStrings.keySet()));
	fourLeftRemovingOne();
    }

    @Test()
    public void testPutString() throws IOException {
	InputStream old = map.putString("one", "apple");
	getOneReturnsAppleAndOldValueIsNull(old);
	InputStream apple = map.putString("one", "bear");
	getOneReturnsBearAndOldValueIsApple(apple);
    }

    private void getOneReturnsAppleAndOldValueIsNull(InputStream old)
	    throws IOException {
	assert old == null;
	assertEquals(Utils.toStringAndClose(map.get("one")), "apple");
	assert map.size() == 1;
    }

    private void getOneReturnsBearAndOldValueIsApple(InputStream oldValue)
	    throws IOException {
	assertEquals(Utils.toStringAndClose(map.get("one")), "bear");
	assertEquals(Utils.toStringAndClose(oldValue), "apple");
	assert map.size() == 1;
    }

    @Test()
    public void testPutFile() throws IOException {
	InputStream old = map.putFile("one", fiveFiles.get("one"));
	getOneReturnsAppleAndOldValueIsNull(old);
	InputStream apple = map.putFile("one", fiveFiles.get("two"));
	getOneReturnsBearAndOldValueIsApple(apple);
    }

    @Test()
    public void testPutBytes() throws IOException {
	InputStream old = map.putBytes("one", "apple".getBytes());
	getOneReturnsAppleAndOldValueIsNull(old);
	InputStream apple = map.putBytes("one", "bear".getBytes());
	getOneReturnsBearAndOldValueIsApple(apple);
    }

    @Test()
    public void testPut() throws IOException {
	InputStream old = map.put("one", IOUtils.toInputStream("apple"));
	getOneReturnsAppleAndOldValueIsNull(old);
	InputStream apple = map.put("one", IOUtils.toInputStream("bear"));
	getOneReturnsBearAndOldValueIsApple(apple);
    }

    @Test()
    public void testGetBucket() {
	assertEquals(map.getBucket().getName(), bucket.getName());
    }

    @Test
    void testPutS3Object() throws IOException {
	S3Object object = new S3Object();
	object.setKey("one");
	object.setContent(IOUtils.toInputStream("apple"));
	object.setSize("apple".getBytes().length);
	InputStream old = map.put(object);
	getOneReturnsAppleAndOldValueIsNull(old);
	object.setContent(IOUtils.toInputStream("bear"));
	object.setSize("bear".getBytes().length);
	InputStream apple = map.put(object);
	getOneReturnsBearAndOldValueIsApple(apple);
    }

    @Test
    void testPutAllS3Objects() {
	Set<S3Object> set = new HashSet<S3Object>();
	for (String key : fiveInputs.keySet()) {
	    S3Object object = new S3Object();
	    object.setKey(key);
	    object.setContent(fiveInputs.get(key));
	    object.setSize(fiveBytes.get(key).length);
	    set.add(object);
	}
	map.putAll(set);
	assertEquals(map.size(), 5);
	assertEquals(new TreeSet<String>(map.keySet()), new TreeSet<String>(
		fiveInputs.keySet()));
	fourLeftRemovingOne();
    }
}
