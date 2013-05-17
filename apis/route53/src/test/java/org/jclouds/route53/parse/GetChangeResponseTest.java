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
package org.jclouds.route53.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Date;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.Change.Status;
import org.jclouds.route53.xml.ChangeHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetChangeResponseTest")
public class GetChangeResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/change.xml");

      Change expected = expected();

      ChangeHandler handler = injector.getInstance(ChangeHandler.class);
      Change result = factory.create(handler).parse(is);

      assertEquals(result, expected);
      assertEquals(result.getStatus(), expected.getStatus());
      assertEquals(result.getSubmittedAt(), expected.getSubmittedAt());
   }

   public Change expected() {
      Date submittedAt = new SimpleDateFormatDateService().iso8601DateParse("2011-09-10T01:36:41.958Z");
      return Change.create("C2682N5HXP0BZ4", Status.INSYNC, submittedAt);
   }

}
