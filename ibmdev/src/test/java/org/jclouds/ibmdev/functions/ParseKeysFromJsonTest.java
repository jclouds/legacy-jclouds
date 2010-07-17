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
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.domain.Key;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseKeysFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseKeysFromJsonTest")
public class ParseKeysFromJsonTest {

   private ParseKeysFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule() {
         @Override
         protected void configure() {
            bind(DateAdapter.class).to(LongDateAdapter.class);
            super.configure();
         }
      });
      handler = injector.getInstance(ParseKeysFromJson.class);
   }

   public void test() {
      Key key1 = new Key(true, ImmutableSet.<String> of("1"),
            "AAAB3NzaC1yc2EAAAADAQABAAABAQCqBw7a+...", "DEFAULT", new Date(
                  1260428507510l));
      Key key2 = new Key(false, ImmutableSet.<String> of(),
            "AAAB3NzaC1yc2EAAAADAQABAAABAQCqBw7a+", "BEAR", new Date(
                  1260428507511l));

      Set<? extends Key> compare = handler.apply(new HttpResponse(200, "ok",
            Payloads.newInputStreamPayload(ParseKeysFromJsonTest.class
                  .getResourceAsStream("/keys.json"))));
      assert (compare.contains(key1));
      assert (compare.contains(key2));
   }
}
