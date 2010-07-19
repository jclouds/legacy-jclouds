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

package org.jclouds.ibmdev.functions;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.domain.Volume;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseVolumesFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseVolumesFromJsonTest")
public class ParseVolumesFromJsonTest {

   private ParseVolumesFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule() {
         @Override
         protected void configure() {
            bind(DateAdapter.class).to(LongDateAdapter.class);
            super.configure();
         }
      });
      handler = injector.getInstance(ParseVolumesFromJson.class);
   }

   public void test() {
      Volume volume1 = new Volume("2", 5, 50, "aadelucc@us.ibm.com", new Date(
            1260469075119l), "1", ImmutableSet.<String> of(), "ext3",
            "New Storage", "67");

      Volume volume2 = new Volume(null, 6, 51, "aadelucc@us.ibm.com", new Date(
            1260469075120l), "2", ImmutableSet.<String> of("abrad"), "ext3",
            "New Storage1", "68");

      Set<? extends Volume> compare = handler.apply(new HttpResponse(200, "ok",
            Payloads.newInputStreamPayload(ParseVolumesFromJsonTest.class
                  .getResourceAsStream("/volumes.json"))));
      assert (compare.contains(volume1));
      assert (compare.contains(volume2));
   }
}
