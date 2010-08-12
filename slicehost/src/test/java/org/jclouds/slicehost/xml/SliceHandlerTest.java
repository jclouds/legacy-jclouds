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
import org.jclouds.slicehost.domain.Slice;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code SliceHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.SliceHandler")
public class SliceHandlerTest extends BaseHandlerTest {

   ParseSax<Slice> createParser() {
      ParseSax<Slice> parser = (ParseSax<Slice>) factory.create(injector.getInstance(SliceHandler.class));
      return parser;
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_get_slice.xml");
      Slice expects = new Slice(1, "jclouds-foo", 1, 10, null, Slice.Status.BUILD, 0, 0, 0, ImmutableSet.<String> of(
            "174.143.212.229", "10.176.164.199"), null);

      assertEquals(createParser().parse(is), expects);
   }

   public void testNew() {
      InputStream is = getClass().getResourceAsStream("/test_new_slice.xml");
      Slice expects = new Slice(71907, "slicetest", 1, 11, null, Slice.Status.BUILD, 0, 0, 0, ImmutableSet.<String> of(
            "10.176.168.15", "67.23.20.114"), "fooadfa1231");

      assertEquals(createParser().parse(is), expects);
      
   }
   
}
