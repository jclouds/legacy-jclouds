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
package org.jclouds.sts.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sts.domain.UserAndTemporaryCredentials;
import org.jclouds.sts.domain.User;
import org.jclouds.sts.xml.UserAndTemporaryCredentialsHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetFederationTokenResponseTest")
public class GetFederationTokenResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/federation_token.xml");

      UserAndTemporaryCredentials expected = expected();

      UserAndTemporaryCredentialsHandler handler = injector.getInstance(UserAndTemporaryCredentialsHandler.class);
      UserAndTemporaryCredentials result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getUser(), expected.getUser());
      assertEquals(result.getPackedPolicySize(), expected.getPackedPolicySize());
   }

   public UserAndTemporaryCredentials expected() {
      return UserAndTemporaryCredentials.builder()
                        .credentials(new GetSessionTokenResponseTest().expected())
                        .user(User.fromIdAndArn("123456789012:Bob", "arn:aws:sts::123456789012:federated-user/Bob"))
                        .packedPolicySize(6).build();
   }
}
