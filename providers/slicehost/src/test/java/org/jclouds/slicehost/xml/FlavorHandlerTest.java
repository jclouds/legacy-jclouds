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
package org.jclouds.slicehost.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.slicehost.domain.Flavor;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code FlavorHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class FlavorHandlerTest {

   static ParseSax<Flavor> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<Flavor> parser = (ParseSax<Flavor>) injector.getInstance(ParseSax.Factory.class).create(
            injector.getInstance(FlavorHandler.class));
      return parser;
   }

   public static Flavor parseFlavor() {
      InputStream is = FlavorHandlerTest.class.getResourceAsStream("/test_get_flavor.xml");
      return createParser().parse(is);
   }

   public void test() {
      Flavor expects = new Flavor(1, "256 slice", 2000, 256);

      assertEquals(parseFlavor(), expects);
   }

}
