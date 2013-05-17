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
package org.jclouds.internal;

import static org.easymock.EasyMock.createMock;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.fail;

import org.jclouds.domain.Credentials;
import org.jclouds.lifecycle.Closer;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.Utils;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

/** 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BaseViewTest")
public class BaseViewTest {

   static Supplier<Credentials> creds = Suppliers.ofInstance(new Credentials("identity", null));

   private static class Water extends ContextImpl {

      protected Water() {
         super("water", createMock(ProviderMetadata.class), creds, createMock(Utils.class), createMock(Closer.class));
      }

      public void close() {
      }
   }

   private static class PeanutButter extends ContextImpl {

      protected PeanutButter() {
         super("peanutbutter", createMock(ProviderMetadata.class), creds, createMock(Utils.class), createMock(Closer.class));
      }

      public void close() {
      }
   }
   
   private static class Wine extends BaseView {

      protected Wine() {
         super(new Water(), typeToken(Water.class));
      }
   }

   public void testWaterTurnedIntoWine() {
      Wine wine = new Wine();
      assertEquals(wine.getBackendType(), typeToken(Water.class));
      assertEquals(wine.unwrap(typeToken(Water.class)).getClass(), Water.class);
      assertEquals(wine.unwrap().getClass(), Water.class);
   }

   public void testPeanutButterDidntTurnIntoWine() {
      Wine wine = new Wine();
      assertNotEquals(wine.getBackendType(), typeToken(PeanutButter.class));
      try {
         wine.unwrap(typeToken(PeanutButter.class));
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals(e.getMessage(), "backend type: org.jclouds.internal.BaseViewTest$Water not assignable from org.jclouds.internal.BaseViewTest$PeanutButter");
      }
   }
   
}
