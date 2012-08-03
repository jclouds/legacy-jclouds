/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.greenqloud.compute.parse;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.jclouds.ec2.xml.DescribeRegionsResponseHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeRegionsResponseHandlerTest")
public class DescribeRegionsResponseHandlerTest extends BaseHandlerTest {

   public void test() {
      String text = new StringBuilder()
               .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
               .append("<DescribeRegionsResponse xmlns=\"http://ec2.amazonaws.com/doc/2010-06-15/\"><requestId>0a5a6b4d-c0d7-4531-9ba9-bbc0b94d2007</requestId><regionInfo><item><regionName>is-1</regionName><regionEndpoint>api.greenqloud.com</regionEndpoint></item></regionInfo></DescribeRegionsResponse>\n")
               .toString();

      Map<String, URI> expected = expected();

      DescribeRegionsResponseHandler handler = injector.getInstance(DescribeRegionsResponseHandler.class);
      Map<String, URI> result = factory.create(handler).parse(text);

      assertEquals(result.toString(), expected.toString());

   }

   public Map<String, URI> expected() {
      return ImmutableMap.of("is-1", URI.create("https://api.greenqloud.com"));
   }
}
