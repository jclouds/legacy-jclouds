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
package org.jclouds.cloudsigma.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;

/**
 * Tests behavior of {@code NewlineDelimitedStringHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SplitNewlinesTest {

   static Function<HttpResponse, Set<String>> createParser() {
      return Guice.createInjector().getInstance(SplitNewlines.class);
   }

   public void test() {
      InputStream is = SplitNewlinesTest.class.getResourceAsStream("/uuids.txt");
      Set<String> list = createParser().apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());
      assertEquals(list, ImmutableSortedSet.of("7e8ab721-81c9-4cb9-a651-4cafbfe1501c",
            "ea6a8fdb-dab3-4d06-86c2-41a5835e6ed9", "74744450-d338-4087-b3b8-59b505110a57"));
   }

}
