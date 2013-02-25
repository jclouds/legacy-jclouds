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
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.xml.ListRolesResultHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListRolesResponseTest")
public class ListRolesResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/list_roles.xml");

      IterableWithMarker<Role> expected = expected();

      ListRolesResultHandler handler = injector.getInstance(ListRolesResultHandler.class);
      IterableWithMarker<Role> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public IterableWithMarker<Role> expected() {
      return IterableWithMarkers.from(ImmutableSet.of(
               Role.builder()
                   .arn("arn:aws:iam::993194456877:role/foobie")
                   .id("AROAIBFDQ5TQHEMPBEUE4")
                   .name("foobie")
                   .path("/")
                   .assumeRolePolicy("{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}")
                   .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2013-02-25T01:51:35Z")).build(),
               Role.builder()
                   .arn("arn:aws:iam::993194456877:role/s3-read-only")
                   .id("AROAJZ7NAM67BRSDAJ6PA")
                   .name("s3-read-only")
                   .path("/")
                   .assumeRolePolicy("{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}")
                   .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2013-02-25T01:48:59Z")).build()));
   }

}
