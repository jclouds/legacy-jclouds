/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License")); you may not use this file except in compliance
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

package org.jclouds.management.functions;

import org.jclouds.management.TypeA;
import org.jclouds.management.TypeB;
import org.jclouds.management.TypeC;
import org.testng.annotations.Test;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

@Test
public class ToOpenTypeTest {

   @Test
   void testNativeTypes() {
      assertEquals(SimpleType.BOOLEAN, ToOpenType.FUNCTION.apply(boolean.class));
      assertEquals(SimpleType.BOOLEAN, ToOpenType.FUNCTION.apply(Boolean.class));
      assertEquals(SimpleType.BYTE, ToOpenType.FUNCTION.apply(byte.class));
      assertEquals(SimpleType.BYTE, ToOpenType.FUNCTION.apply(Byte.class));
      assertEquals(SimpleType.CHARACTER, ToOpenType.FUNCTION.apply(char.class));
      assertEquals(SimpleType.CHARACTER, ToOpenType.FUNCTION.apply(Character.class));
      assertEquals(SimpleType.SHORT, ToOpenType.FUNCTION.apply(short.class));
      assertEquals(SimpleType.SHORT, ToOpenType.FUNCTION.apply(Short.class));
      assertEquals(SimpleType.INTEGER, ToOpenType.FUNCTION.apply(int.class));
      assertEquals(SimpleType.INTEGER, ToOpenType.FUNCTION.apply(Integer.class));
      assertEquals(SimpleType.LONG, ToOpenType.FUNCTION.apply(long.class));
      assertEquals(SimpleType.LONG, ToOpenType.FUNCTION.apply(Long.class));
      assertEquals(SimpleType.DOUBLE, ToOpenType.FUNCTION.apply(double.class));
      assertEquals(SimpleType.DOUBLE, ToOpenType.FUNCTION.apply(Double.class));
      assertEquals(SimpleType.FLOAT, ToOpenType.FUNCTION.apply(float.class));
      assertEquals(SimpleType.FLOAT, ToOpenType.FUNCTION.apply(Float.class));
      assertEquals(SimpleType.BIGDECIMAL, ToOpenType.FUNCTION.apply(BigDecimal.class));
      assertEquals(SimpleType.BIGINTEGER, ToOpenType.FUNCTION.apply(BigInteger.class));
      assertEquals(SimpleType.DATE, ToOpenType.FUNCTION.apply(Date.class));
      assertEquals(SimpleType.STRING, ToOpenType.FUNCTION.apply(String.class));
   }

   @Test
   void testManagedAttributes() {
      assertTrue(ToOpenType.FUNCTION.apply(TypeA.class) instanceof CompositeType);
      assertTrue(ToOpenType.FUNCTION.apply(TypeB.class) instanceof CompositeType);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   void testIllegalTypes() {
      assertNull(ToOpenType.FUNCTION.apply(TypeC.class));
   }
}
