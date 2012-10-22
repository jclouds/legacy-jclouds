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

package org.jclouds.abiquo.features;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jclouds.abiquo.config.AbiquoRestClientModule;
import org.jclouds.abiquo.rest.internal.AbiquoHttpAsyncClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests that all features have a unit test.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "FeatureCoverageTest")
public class FeatureCoverageTest {
   /** A collection with all async api classes. */
   private Collection<Class<?>> featureClasses;

   @BeforeMethod
   public void setup() {
      featureClasses = new ArrayList<Class<?>>();
      featureClasses.addAll(AbiquoRestClientModule.DELEGATE_MAP.values());
      featureClasses.add(AbiquoHttpAsyncClient.class);
   }

   public void testAllFeaturesHaveTest() throws ClassNotFoundException {
      List<String> missingTests = new ArrayList<String>();

      for (Class<?> featureClass : featureClasses) {
         try {
            Class<?> testClass = loadTestClass(featureClass);
            Iterable<String> testMethodNames = methodNames(testClass);

            for (Method method : featureClass.getMethods()) {
               if (!hasTest(testMethodNames, method)) {
                  missingTests.add(method.getDeclaringClass().getSimpleName() + "." + method.getName());
               }
            }
         } catch (ClassNotFoundException ex) {
            fail("Missing tests for class: " + featureClass.getName());
         }
      }

      assertTrue(missingTests.isEmpty(), "Missing tests: " + Joiner.on(", ").join(missingTests));
   }

   private Class<?> loadTestClass(final Class<?> featureClass) throws ClassNotFoundException {
      String testClassName = featureClass.getName() + "Test";
      return Thread.currentThread().getContextClassLoader().loadClass(testClassName);
   }

   private static Iterable<String> methodNames(final Class<?> clazz) {
      return Iterables.transform(Arrays.asList(clazz.getMethods()), new Function<Method, String>() {
         @Override
         public String apply(final Method input) {
            return input.getName();
         }
      });
   }

   private static boolean hasTest(final Iterable<String> testMethodNames, final Method method) {
      String testMethod = Iterables.find(testMethodNames, new Predicate<String>() {
         @Override
         public boolean apply(final String input) {
            return input.toLowerCase().contains(method.getName().toLowerCase());
         }
      }, null);

      return testMethod != null;
   }
}
