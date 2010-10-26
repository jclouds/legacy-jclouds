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
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.slicehost.domain.Slice;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code SliceHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.SliceHandler")
public class SliceHandlerTest extends BaseHandlerTest {

   static ParseSax<Slice> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<Slice> parser = (ParseSax<Slice>) injector.getInstance(ParseSax.Factory.class).create(
            injector.getInstance(SliceHandler.class));
      return parser;
   }

   public static Slice parseSlice() {
      return parseSlice("/test_get_slice.xml");
   }

   public static Slice parseSlice(String resource) {
      InputStream is = SliceHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }

   public void test() {
      Slice expects = new Slice(1, "jclouds-foo", 1, 2, null, Slice.Status.BUILD, 0, 0, 0, ImmutableSet.<String> of(
            "174.143.212.229", "10.176.164.199"), null);

      assertEquals(parseSlice("/test_get_slice.xml"), expects);
   }

   public void testNew() {
      Slice expects = new Slice(71907, "slicetest", 1, 11, null, Slice.Status.BUILD, 0, 0, 0, ImmutableSet.<String> of(
            "10.176.168.15", "67.23.20.114"), "fooadfa1231");

      assertEquals(parseSlice("/test_new_slice.xml"), expects);

   }

}
