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
package org.jclouds.providers.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.testng.Assert.assertEquals;

import java.util.Set;
import java.util.logging.Logger;

import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
@Test(groups = "unit")
public abstract class BaseProviderMetadataTest {

   private final ProviderMetadata toTest;
   private final ApiMetadata expectedApi;
   private final Set<TypeToken<? extends View>> views;

   public BaseProviderMetadataTest(ProviderMetadata toTest, ApiMetadata expectedApi) {
      this.toTest = checkNotNull(toTest, "toTest must be defined");
      this.expectedApi = checkNotNull(expectedApi, "expectedApi must be defined");
      this.views = expectedApi.getViews();
   }

   @Test
   public void testWithId() {
      ProviderMetadata providerMetadata = Providers.withId(toTest.getId());

      assertEquals(toTest, providerMetadata);
      assert providerMetadata.getLinkedServices().contains(toTest.getId());
   }

   @Test
   public void testOfApiContains() {
      if (expectedApi == null)
         Logger.getAnonymousLogger().warning("please update your test class");
      ImmutableSet<ProviderMetadata> ofApi = ImmutableSet.copyOf(Providers.apiMetadataAssignableFrom(typeToken(expectedApi.getClass())));
      assert ofApi.contains(toTest) : String.format("%s not found in %s", toTest, ofApi);
   }

   @Test
   public void testTransformableToContains() {
      for (TypeToken<? extends View> view : views) {
         ImmutableSet<ProviderMetadata> ofType = ImmutableSet.copyOf(Providers.viewableAs(view));
         assert ofType.contains(toTest) : String.format("%s not found in %s for %s", toTest, ofType,
                  view);
      }
   }

   @Test
   public void testAllContains() {
      ImmutableSet<ProviderMetadata> all = ImmutableSet.copyOf(Providers.all());
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}
