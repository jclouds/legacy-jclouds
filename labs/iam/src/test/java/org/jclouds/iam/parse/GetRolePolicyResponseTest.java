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

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.domain.Policy;
import org.jclouds.iam.xml.PolicyHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetRolePolicyResponseTest")
public class GetRolePolicyResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_role_policy.xml");

      Policy expected = expected();

      PolicyHandler handler = injector.getInstance(PolicyHandler.class);
      Policy result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getOwner(), expected.getOwner());
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getDocument(), expected.getDocument());
   }

   public Policy expected() {
      return Policy.builder()
            .owner("S3Access")
            .name("S3AccessPolicy")
            .document(
                  "{\"Version\":\"2008-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:*\"],\"Resource\":[\"*\"]}]}")
            .build();
   }

}
