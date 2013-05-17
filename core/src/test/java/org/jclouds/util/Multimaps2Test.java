/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.functions.ToLowerCase;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Everett Toews
 */
@Test(groups = "unit")
public class Multimaps2Test {
	public void testTransformKeysToLowerCase() {
		Multimap<String, String> map = ImmutableMultimap.of("oNe", "1", "TWO", "2", "three", "3", "Three", "3.0");
		Multimap<String, String> expected = ImmutableMultimap.of("one", "1", "two", "2", "three", "3", "three", "3.0");
		Multimap<String, String> transformed = Multimaps2.transformKeys(map, new ToLowerCase());
		
		assertEquals(transformed, expected);
	}
}
