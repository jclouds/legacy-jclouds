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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.fail;

import org.jclouds.lifecycle.Closer;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.Utils;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

/** 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BaseViewTest")
public class BaseViewTest {

   private static class Water extends ContextImpl {

      protected Water() {
         super("water", createMock(ProviderMetadata.class), "identity", createMock(Utils.class), createMock(Closer.class));
      }

      @Override
      public void close() {
      }
      
      @Override
      public boolean equals(Object in){
         return Objects.equal(in.getClass(), getClass());
      }
   }

   private static class PeanutButter extends ContextImpl {

      protected PeanutButter() {
         super("peanutbutter", createMock(ProviderMetadata.class), "identity", createMock(Utils.class), createMock(Closer.class));
      }

      @Override
      public void close() {
      }
      
      @Override
      public boolean equals(Object in){
         return Objects.equal(in.getClass(), getClass());
      }
   }
   
   private static class Wine extends BaseView {

      protected Wine() {
         super(new Water(), TypeToken.of(Water.class));
      }
   }

   public void testWaterTurnedIntoWine() {
      Wine wine = new Wine();
      assertEquals(wine.getBackendType(), TypeToken.of(Water.class));
      assertEquals(wine.unwrap(TypeToken.of(Water.class)), new Water());
      assertEquals(wine.unwrap(), new Water());
   }

   public void testPeanutButterDidntTurnIntoWine() {
      Wine wine = new Wine();
      assertNotEquals(wine.getBackendType(), TypeToken.of(PeanutButter.class));
      try {
         wine.unwrap(TypeToken.of(PeanutButter.class));
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals(e.getMessage(), "backend type: org.jclouds.internal.BaseViewTest$Water not assignable from org.jclouds.internal.BaseViewTest$PeanutButter");
      }
   }
   
}
