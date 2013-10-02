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
package org.jclouds.atmos.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.atmos.domain.AtmosError;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ErrorHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ErrorHandlerTest")
public class ErrorHandlerTest extends BaseHandlerTest {

   ParseSax<AtmosError> createParser() {
      ParseSax<AtmosError> parser = factory.create(injector
               .getInstance(ErrorHandler.class));
      return parser;
   }

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/error.xml");
      ParseSax<AtmosError> parser = createParser();
      AtmosError result = parser.parse(is);
      assertEquals(result.getCode(), 1003);
   }
}
