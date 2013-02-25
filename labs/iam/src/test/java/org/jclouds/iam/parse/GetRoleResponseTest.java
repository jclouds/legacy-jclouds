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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.xml.RoleHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetRoleResponseTest")
public class GetRoleResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_role.xml");

      Role expected = expected();

      RoleHandler handler = injector.getInstance(RoleHandler.class);
      Role result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getPath(), expected.getPath());
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getCreateDate(), expected.getCreateDate());
      assertEquals(result.getAssumeRolePolicy(), expected.getAssumeRolePolicy());
   }

   public Role expected() {
      return Role.builder()
                 .arn("arn:aws:iam::993194456877:role/foobie")
                 .id("AROAIBFDQ5TQHEMPBEUE4")
                 .name("foobie")
                 .path("/")
                 .assumeRolePolicy("{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}")
                 .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2013-02-25T01:51:35Z")).build();
   }

}
