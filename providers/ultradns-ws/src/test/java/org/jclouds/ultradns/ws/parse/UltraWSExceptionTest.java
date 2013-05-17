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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.UltraDNSWSError;
import org.jclouds.ultradns.ws.xml.UltraWSExceptionHandler;
import org.testng.annotations.Test;

import com.google.common.base.Optional;

/**
 * @author Adrian Cole
 */
@Test(testName = "UltraWSExceptionTest")
public class UltraWSExceptionTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/zone_doesnt_exist.xml");

      UltraDNSWSError expected = expected();

      UltraWSExceptionHandler handler = injector.getInstance(UltraWSExceptionHandler.class);
      UltraDNSWSError result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   public UltraDNSWSError expected() {
      return UltraDNSWSError.fromCodeAndDescription(1801, Optional.of("Zone does not exist in the system."));
   }

}
