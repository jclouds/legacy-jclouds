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
package org.jclouds.apis.internal;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseApiMetadataTest {

   protected final ApiMetadata toTest;
   protected final Set<TypeToken<? extends View>> views;

   public BaseApiMetadataTest(ApiMetadata toTest, Set<TypeToken<? extends View>> views) {
      this.toTest = toTest;
      this.views = views;
   }

   @Test
   public void testWithId() {
      ApiMetadata apiMetadata = Apis.withId(toTest.getId());

      assertEquals(toTest, apiMetadata);
   }

   // it is ok to have multiple services in the same classpath (ex. ec2 vs elb)
   @Test
   public void testTransformableToContains() {
      for (TypeToken<? extends View> view : views) {
         ImmutableSet<ApiMetadata> ofType = ImmutableSet.copyOf(Apis.viewableAs(view));
         assert ofType.contains(toTest) : String.format("%s not found in %s for %s", toTest, ofType,
                  view);
      }
   }

   @Test
   public void testAllContains() {
      ImmutableSet<ApiMetadata> all = ImmutableSet.copyOf(Apis.all());
      assert all.contains(toTest) : String.format("%s not found in %s", toTest, all);
   }

}
