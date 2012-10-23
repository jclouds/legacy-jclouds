/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.functions.enterprise;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link ParseEnterpriseId} function.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "ParseEnterpriseIdTest")
public class ParseEnterpriseIdTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidNullInput() {
      Function<Object, String> parser = new ParseEnterpriseId();
      parser.apply(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testInvalidInputType() {
      Function<Object, String> parser = new ParseEnterpriseId();
      parser.apply(new Object());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInvalidId() {
      Function<Object, String> parser = new ParseEnterpriseId();
      parser.apply(new EnterpriseDto());
   }

   public void testValidId() {
      Function<Object, String> parser = new ParseEnterpriseId();

      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.setId(5);
      assertEquals(parser.apply(enterprise), "5");
   }
}
