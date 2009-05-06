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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.jclouds.Utils;
import org.jclouds.aws.s3.internal.BaseS3Map;
import org.testng.annotations.Test;

/**
 * Tests to cover @{link LiveS3ObjectMap}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "s3.S3InputStreamMapTest")
public class S3InputStreamMapTest extends BaseS3MapTest<InputStream> {
    S3InputStreamMap map = null;

    @SuppressWarnings("unchecked")
    protected BaseS3Map<InputStream> createMap(S3Context context, String bucket) {
	map = context.createInputStreamMap(bucket);
	return (BaseS3Map<InputStream>) map;
    }

    @Override
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
    public void testRemove() throws IOException {
	putString("one", "two");
	InputStream old = map.remove("one");
	assertEquals(Utils.toStringAndClose(old), "two");
	old = map.remove("one");
	assert old == null;
	old = map.get("one");
	assert old == null;
	assertEquals(map.keySet().size(), 0);
    }

    @Override
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

    @Override
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

    void getOneReturnsAppleAndOldValueIsNull(InputStream old)
	    throws IOException {
	assert old == null;
	assertEquals(Utils.toStringAndClose(map.get("one")), "apple");
	assertEquals(map.size(), 1);
    }

    void getOneReturnsBearAndOldValueIsApple(InputStream oldValue)
	    throws IOException {
	assertEquals(Utils.toStringAndClose(map.get("one")), "bear");
	assertEquals(Utils.toStringAndClose(oldValue), "apple");
	assertEquals(map.size(), 1);
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

    @Override
    protected void putString(String key, String value) {
	map.putString(key, value);
    }

}
