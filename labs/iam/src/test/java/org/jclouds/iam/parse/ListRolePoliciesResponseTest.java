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
package org.jclouds.iam.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.xml.ListPoliciesResultHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListRolePoliciesResponseTest")
public class ListRolePoliciesResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/list_role_policies.xml");

      IterableWithMarker<String> expected = expected();

      ListPoliciesResultHandler handler = injector.getInstance(ListPoliciesResultHandler.class);
      IterableWithMarker<String> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public IterableWithMarker<String> expected() {
      return IterableWithMarkers.from(ImmutableSet.of("CloudwatchPutMetricPolicy", "S3AccessPolicy"));
   }

}
