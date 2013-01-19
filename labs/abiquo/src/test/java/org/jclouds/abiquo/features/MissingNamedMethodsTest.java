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

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Named;

import org.jclouds.abiquo.config.AbiquoRestClientModule;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

/**
 * Check that no method in the apis is missing the {@link Named} annotation.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "MissingNamedMethodsTest")
public class MissingNamedMethodsTest {

   public void testMissingNamedMethods() {
      Collection<Class<?>> apis = AbiquoRestClientModule.DELEGATE_MAP.values();

      Iterable<String> missing = transform(concat(transform(apis, new Function<Class<?>, Iterable<Method>>() {
         @Override
         public Iterable<Method> apply(Class<?> input) {
            return filter(Arrays.asList(input.getMethods()), new Predicate<Method>() {
               @Override
               public boolean apply(Method input) {
                  Named named = input.getAnnotation(Named.class);
                  return named == null || Strings.isNullOrEmpty(named.value()) || !named.value().matches(".+:.+");
               }
            });
         }
      })), new Function<Method, String>() {
         @Override
         public String apply(Method input) {
            return input.getDeclaringClass().getSimpleName() + "." + input.getName();
         }
      });

      if (!isEmpty(missing)) {
         fail(size(missing) + " methods missing @Named annotation:\n" + Joiner.on('\n').join(missing));
      }
   }
}