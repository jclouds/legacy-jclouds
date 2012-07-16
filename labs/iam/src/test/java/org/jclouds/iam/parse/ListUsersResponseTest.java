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
import org.jclouds.iam.domain.User;
import org.jclouds.iam.xml.ListUsersResultHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListUsersResponseTest")
public class ListUsersResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/list_users.xml");

      IterableWithMarker<User> expected = expected();

      ListUsersResultHandler handler = injector.getInstance(ListUsersResultHandler.class);
      IterableWithMarker<User> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public IterableWithMarker<User> expected() {
      return IterableWithMarkers.from(ImmutableSet.of(
               User.builder()
                   .path("/division_abc/subdivision_xyz/engineering/")
                   .name("Andrew")
                   .id("AID2MAB8DPLSRHEXAMPLE")
                   .arn("arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/engineering/Andrew")
                   .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2009-03-06T21:47:48Z")).build(),
               User.builder()
                   .path("/division_abc/subdivision_xyz/engineering/")
                   .name("Jackie")
                   .id("AIDIODR4TAW7CSEXAMPLE")
                   .arn("arn:aws:iam::123456789012:user/division_abc/subdivision_xyz/engineering/Jackie")
                   .createDate(new SimpleDateFormatDateService().iso8601SecondsDateParse("2009-03-06T21:47:48Z")).build()));
   }

}
