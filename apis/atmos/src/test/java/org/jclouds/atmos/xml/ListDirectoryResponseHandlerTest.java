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
package org.jclouds.atmos.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.atmos.domain.DirectoryEntry;
import org.jclouds.atmos.domain.FileType;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ListDirectoryResponseHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListDirectoryResponseHandlerTest")
public class ListDirectoryResponseHandlerTest extends BaseHandlerTest {

   ParseSax<Set<DirectoryEntry>> createParser() {
      ParseSax<Set<DirectoryEntry>> parser = factory
               .create(injector.getInstance(ListDirectoryResponseHandler.class));
      return parser;
   }

   public void testApplyInputStreamBase() {
      InputStream is = getClass().getResourceAsStream("/list_basic.xml");
      ParseSax<Set<DirectoryEntry>> parser = createParser();
      Set<DirectoryEntry> expected = Sets.newTreeSet();
      expected.add(new DirectoryEntry("4980cdb2a411106a04a4538c92a1b204ad92077de6e3",
               FileType.DIRECTORY, "adriancole-blobstore-2096685753"));
      expected.add(new DirectoryEntry("4980cdb2a410105404980d99e53a0504ad93939e7dc3",
               FileType.DIRECTORY, "adriancole-blobstore247496608"));
      Set<DirectoryEntry> result = parser.parse(is);
      assertEquals(result, expected);
   }
}
