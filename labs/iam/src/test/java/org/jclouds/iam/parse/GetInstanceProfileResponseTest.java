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

import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.xml.InstanceProfileHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetInstanceProfileResponseTest")
public class GetInstanceProfileResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/get_instance_profile.xml");

      InstanceProfile expected = expected();

      InstanceProfileHandler handler = injector.getInstance(InstanceProfileHandler.class);
      InstanceProfile result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getPath(), expected.getPath());
      assertEquals(result.getName(), expected.getName());
      assertEquals(result.getCreateDate(), expected.getCreateDate());
      assertEquals(result.getRoles(), expected.getRoles());
   }

   DateService date = new SimpleDateFormatDateService();

   public InstanceProfile expected() {
      return InstanceProfile.builder()
            .arn("arn:aws:iam::123456789012:instance-profile/application_abc/component_xyz/Webserver")
            .id("AIPAD5ARO2C5EXAMPLE3G")
            .name("Webserver")
            .path("/application_abc/component_xyz/")
            .role(Role.builder()
                      .arn("arn:aws:iam::123456789012:role/application_abc/component_xyz/S3Access")
                      .id("AROACVYKSVTSZFEXAMPLE")
                      .name("S3Access")
                      .path("/application_abc/component_xyz/")
                      .assumeRolePolicy("{\"Version\":\"2008-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":[\"ec2.amazonaws.com\"]},\"Action\":[\"sts:AssumeRole\"]}]}")
                      .createDate(date.iso8601SecondsDateParse("2012-05-09T15:45:35Z")).build())
            .createDate(date.iso8601SecondsDateParse("2012-05-09T16:11:10Z")).build();
   }

}
