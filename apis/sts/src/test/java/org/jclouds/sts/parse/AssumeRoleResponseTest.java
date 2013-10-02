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
package org.jclouds.sts.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.sts.domain.UserAndSessionCredentials;
import org.jclouds.sts.domain.User;
import org.jclouds.sts.xml.UserAndSessionCredentialsHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AssumeRoleResponseTest")
public class AssumeRoleResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/assume_role.xml");

      UserAndSessionCredentials expected = expected();

      UserAndSessionCredentialsHandler handler = injector.getInstance(UserAndSessionCredentialsHandler.class);
      UserAndSessionCredentials result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getUser(), expected.getUser());
      assertEquals(result.getPackedPolicySize(), expected.getPackedPolicySize());
   }

   public UserAndSessionCredentials expected() {
      return UserAndSessionCredentials.builder()
                        .credentials(new GetSessionTokenResponseTest().expected())
                        .user(User.fromIdAndArn("ARO123EXAMPLE123:Bob", "arn:aws:sts::123456789012:assumed-role/demo/Bob"))
                        .packedPolicySize(6).build();
   }
}
