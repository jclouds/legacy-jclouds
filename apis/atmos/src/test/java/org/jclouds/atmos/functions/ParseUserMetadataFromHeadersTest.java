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
package org.jclouds.atmos.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.atmos.domain.UserMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code ParseUserMetadataFromHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseUserMetadataFromHeadersTest {
   static final UserMetadata EXPECTED = new UserMetadata(ImmutableMap.<String, String> of("meta1", "foo1"),
         ImmutableMap.of("listablemeta1", "listablefoo1", "listablemeta2", "listablefoo2"), ImmutableSet.of("tag1",
               "tag2"), ImmutableSet.of("listabletag1", "listabletag2"));

   public void test() {
      ParseUserMetadataFromHeaders parser = Guice.createInjector().getInstance(ParseUserMetadataFromHeaders.class);
      UserMetadata data = parser.apply(ParseObjectFromHeadersAndHttpContentTest.RESPONSE);

      assertEquals(data, EXPECTED);
   }
}
