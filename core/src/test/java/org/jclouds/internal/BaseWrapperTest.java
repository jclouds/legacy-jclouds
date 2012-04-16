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
package org.jclouds.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;

import java.io.Closeable;

import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

/** 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BaseWrapperTest")
public class BaseWrapperTest {
   private static class Water implements Closeable {

      @Override
      public void close() {
      }
      @Override
      public boolean equals(Object in){
         return Objects.equal(in.getClass(), getClass());
      }
   }

   private static interface PeanutButter extends Closeable {

      @Override
      void close();
   }

   private static class Wine extends BaseWrapper {

      protected Wine() {
         super(new Water(), TypeToken.of(Water.class));
      }
   }

   public void testWaterTurnedIntoWine() {
      Wine wine = new Wine();
      assertEquals(wine.getWrappedType(), TypeToken.of(Water.class));
      assertEquals(wine.unwrap(TypeToken.of(Water.class)), new Water());
      assertEquals(wine.unwrap(), new Water());
   }

   public void testPeanutButterDidntTurnIntoWine() {
      Wine wine = new Wine();
      assertNotEquals(wine.getWrappedType(), TypeToken.of(PeanutButter.class));
      try {
         wine.unwrap(TypeToken.of(PeanutButter.class));
         assertFalse(true);
      } catch (IllegalArgumentException e) {
         assertEquals(e.getMessage(), "wrapped type: org.jclouds.internal.BaseWrapperTest$Water not assignable from org.jclouds.internal.BaseWrapperTest$PeanutButter");
      }
   }
   
}
