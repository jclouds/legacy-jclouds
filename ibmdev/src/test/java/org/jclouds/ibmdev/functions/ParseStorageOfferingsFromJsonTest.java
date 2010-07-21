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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.domain.Price;
import org.jclouds.ibmdev.domain.StorageOffering;
import org.jclouds.ibmdev.domain.StorageOffering.Format;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseStorageOfferingsFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseStorageOfferingsFromJsonTest")
public class ParseStorageOfferingsFromJsonTest {
   private UnwrapOnlyJsonValue<Set<StorageOffering>> handler;
   private Injector injector;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new ParserModule() {
         @Override
         protected void configure() {
            bind(DateAdapter.class).to(LongDateAdapter.class);
            super.configure();
         }
      });
      handler = injector.getInstance(Key.get(new TypeLiteral<UnwrapOnlyJsonValue<Set<StorageOffering>>>() {
      }));
   }

   @Test
   public void testDeserialize() throws IOException {
      Set<StorageOffering> compare = ImmutableSet.of(//
            new StorageOffering("41", new Price(0.039, "UHR", "897", new Date(1279497600000l), "USD", 1), 256, "Small",
                  "20001208", ImmutableSet.of(new Format("ext3", "EXT3"))),//
            new StorageOffering("41", new Price(0.312, "UHR", "897", new Date(1279497600000l), "USD", 1), 2048,
                  "Large", "20001210", ImmutableSet.of(new Format("ext3", "EXT3"))),//
            new StorageOffering("41", new Price(0.078, "UHR", "897", new Date(1279497600000l), "USD", 1), 512,
                  "Medium", "20001209", ImmutableSet.of(new Format("ext3", "EXT3")))//
            );

      Set<StorageOffering> expected = handler.apply(new HttpResponse(200, "ok", Payloads
            .newPayload(ParseStorageOfferingsFromJsonTest.class.getResourceAsStream("/storage-offerings.json"))));

      assertEquals(compare, expected);
   }

}
