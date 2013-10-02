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

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.route53.domain.NewHostedZone;
import org.jclouds.route53.xml.CreateHostedZoneResponseHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CreateHostedZoneResponseTest")
public class CreateHostedZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/new_zone.xml");

      NewHostedZone expected = expected();

      CreateHostedZoneResponseHandler handler = injector.getInstance(CreateHostedZoneResponseHandler.class);
      NewHostedZone result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   public NewHostedZone expected() {
      return NewHostedZone.create(new GetHostedZoneResponseTest().expected(), new GetChangeResponseTest().expected());
   }
}
