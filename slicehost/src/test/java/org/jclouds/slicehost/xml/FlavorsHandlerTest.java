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
import java.util.Set;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.slicehost.domain.Flavor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code FlavorsHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.FlavorsHandler")
public class FlavorsHandlerTest extends BaseHandlerTest {

   ParseSax<Set<? extends Flavor>> createParser() {
      ParseSax<Set<? extends Flavor>> parser = (ParseSax<Set<? extends Flavor>>) factory.create(injector
            .getInstance(FlavorsHandler.class));
      return parser;
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_list_flavors.xml");
      Set<? extends Flavor> expects = ImmutableSet.of(new Flavor(1, "256 slice", 2000, 256), new Flavor(2, "512 slice",
            3800, 512), new Flavor(3, "1GB slice", 7000, 1024), new Flavor(4, "2GB slice", 13000, 2048));
      assertEquals(createParser().parse(is), expects);
   }

}
