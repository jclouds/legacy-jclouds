/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.slicehost.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.slicehost.domain.Flavor;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code FlavorHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.FlavorHandler")
public class FlavorHandlerTest extends BaseHandlerTest {

   ParseSax<Flavor> createParser() {
      ParseSax<Flavor> parser = (ParseSax<Flavor>) factory.create(injector.getInstance(FlavorHandler.class));
      return parser;
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_get_flavor.xml");
      Flavor expects = new Flavor(1, "256 slice", 2000, 256);

      assertEquals(createParser().parse(is), expects);
   }

}
