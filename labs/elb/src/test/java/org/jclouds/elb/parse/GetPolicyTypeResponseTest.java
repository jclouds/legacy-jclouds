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
package org.jclouds.elb.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.elb.domain.AttributeMetadata;
import org.jclouds.elb.domain.AttributeMetadata.Cardinality;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.xml.PolicyTypeHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetPolicyTypeResponseTest")
public class GetPolicyTypeResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_policy_type.xml");

      PolicyType expected = expected();

      PolicyTypeHandler handler = injector.getInstance(PolicyTypeHandler.class);
      PolicyType result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   public PolicyType expected() {
      return PolicyType.builder()
                       .attributeMetadata(AttributeMetadata.<String>builder()
                                 .name("PublicKey")
                                 .type(String.class)
                                 .rawType("String")
                                 .cardinality(Cardinality.ONE)
                                 .build())
                       .name("PublicKeyPolicyType")
                       .description("Policy containing a list of public keys to accept when authenticating the back-end server(s). This policy cannot be applied directly to back-end servers or listeners but must be part of a BackendServerAuthenticationPolicyType.")
                       .build();
   }
}
