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

package org.jclouds.management;

import org.jclouds.management.internal.ManagedTypeModel;
import org.testng.annotations.Test;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;
import java.util.concurrent.ExecutionException;

import static org.jclouds.reflect.Reflection2.typeParameterOf;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


@Test
public class ManagedTypeModelTest {

   @Test
   void testDescription() throws ExecutionException {
      ManagedTypeModel info = ManagedTypeModel.of(TypeA.class);
      assertEquals("The id of type A", info.getDescription("id"));
      assertEquals("The name of type A", info.getDescription("name"));
      assertEquals("Some description", info.getDescription("description"));
      assertEquals("An Optional attribute", info.getDescription("note"));

      assertEquals(Long.class, info.getType("id"));
      assertEquals(String.class, info.getType("name"));
      assertEquals(String.class, info.getType("description"));
      assertEquals(String.class, typeParameterOf(info.getType("note")));
   }

   @Test
   void testType() throws ExecutionException {
      ManagedTypeModel info = ManagedTypeModel.of(TypeA.class);
      assertEquals(Long.class, info.getType("id"));
      assertEquals(String.class, info.getType("name"));
      assertEquals(String.class, info.getType("description"));
      assertEquals(String.class, typeParameterOf(info.getType("note")));
   }

   @Test
   void testOpenType() throws ExecutionException {
      ManagedTypeModel info = ManagedTypeModel.of(TypeA.class);
      assertEquals(SimpleType.LONG, info.getOpenType("id"));
      assertEquals(SimpleType.STRING, info.getOpenType("name"));
      assertEquals(SimpleType.STRING, info.getOpenType("description"));
      assertTrue(ArrayType.class.isAssignableFrom(info.getOpenType("stringSet").getClass()));
      assertTrue(TabularType.class.isAssignableFrom(info.getOpenType("typeBSet").getClass()));
   }


   @Test
   void testAttributeInheritance() throws ExecutionException {
      ManagedTypeModel info = ManagedTypeModel.of(TypeA.class);
      assertTrue(info.getNames().contains("parentProperty"));
      assertEquals(SimpleType.STRING, info.getOpenType("parentProperty"));
   }
}


