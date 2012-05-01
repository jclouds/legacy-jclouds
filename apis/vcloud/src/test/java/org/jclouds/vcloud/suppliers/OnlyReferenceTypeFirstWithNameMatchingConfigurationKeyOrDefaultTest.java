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
package org.jclouds.vcloud.suppliers;

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of
 * {@code OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefaultTest")
public class OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefaultTest {
   ValueOfConfigurationKeyOrNull valueOfConfigurationKeyOrNull = new ValueOfConfigurationKeyOrNull(
         Guice.createInjector());

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testIllegalArgumentWhenResourcesEmpty() {
      new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(valueOfConfigurationKeyOrNull, "foo",
            Predicates.<ReferenceType> alwaysTrue()).apply(ImmutableSet.<ReferenceType> of());
   }

   @Test
   public void testReturnsOnlyResource() {
      ReferenceType reference = createMock(ReferenceType.class);

      assertEquals(new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(valueOfConfigurationKeyOrNull,
            "foo", Predicates.<ReferenceType> alwaysTrue()).apply(ImmutableSet.<ReferenceType> of(reference)),
            reference);

   }

   @Test
   public void testReturnsFirstResourceWhenConfigurationUnspecified() {
      ReferenceType reference1 = createMock(ReferenceType.class);
      ReferenceType reference2 = createMock(ReferenceType.class);

      assertEquals(new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(valueOfConfigurationKeyOrNull,
            "foo", Predicates.<ReferenceType> alwaysTrue()).apply(ImmutableList.<ReferenceType> of(reference1,
            reference2)), reference1);

   }

   @Test
   public void testReturnsResourceMatchingDefaultPredicateWhenConfigurationUnspecified() {
      ReferenceType reference1 = createMock(ReferenceType.class);
      ReferenceType reference2 = createMock(ReferenceType.class);

      assertEquals(new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(valueOfConfigurationKeyOrNull,
            "foo", Predicates.equalTo(reference2)).apply(ImmutableList.<ReferenceType> of(reference1, reference2)),
            reference2);

   }

   @Test
   public void testReturnsResourceWithNameMatchingConfigurationKey() {
      ReferenceType reference1 = new ReferenceTypeImpl("travis tritt", null, null);
      ReferenceType reference2 = new ReferenceTypeImpl("hail mary", null, null);

      assertEquals(new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(
            new ValueOfConfigurationKeyOrNull(Guice.createInjector(new AbstractModule() {

               @Override
               protected void configure() {
                  bindConstant().annotatedWith(Names.named("foo")).to(".*mary.*");
               }

            })), "foo", Predicates.<ReferenceType> alwaysTrue()).apply(ImmutableList.<ReferenceType> of(reference1,
            reference2)), reference2);

   }

   @Test(expectedExceptions = NoSuchElementException.class)
   public void testThrowsNoSuchElementWhenNoneMatchConfigurationKey() {
      ReferenceType reference1 = new ReferenceTypeImpl("travis tritt", null, null);
      ReferenceType reference2 = new ReferenceTypeImpl("hail mary", null, null);

      new OnlyReferenceTypeFirstWithNameMatchingConfigurationKeyOrDefault(new ValueOfConfigurationKeyOrNull(
            Guice.createInjector(new AbstractModule() {

               @Override
               protected void configure() {
                  bindConstant().annotatedWith(Names.named("foo")).to(".*happy.*");
               }

            })), "foo", Predicates.<ReferenceType> alwaysTrue()).apply(ImmutableList.<ReferenceType> of(reference1,
            reference2));

   }
}
