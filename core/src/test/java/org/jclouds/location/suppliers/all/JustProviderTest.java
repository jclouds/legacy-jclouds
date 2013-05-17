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
package org.jclouds.location.suppliers.all;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code JustProvider}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "JustProviderTest")
public class JustProviderTest {

   @Test
   public void test() throws SecurityException, NoSuchMethodException {
      JustProvider fn = new JustProvider("servo", Suppliers.ofInstance(URI.create("http://servo")), ImmutableSet.of("US"));
      assertEquals(
            fn.get(),
            ImmutableSet.of(new LocationBuilder().scope(LocationScope.PROVIDER).id("servo").description("http://servo")
                  .iso3166Codes(ImmutableSet.of("US")).build()));
   }

}
