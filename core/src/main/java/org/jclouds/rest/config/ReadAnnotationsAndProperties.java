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
package org.jclouds.rest.config;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Maps.transformValues;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.Constants.PROPERTY_TIMEOUTS_PREFIX;
import static org.jclouds.util.Maps2.transformKeys;
import static org.jclouds.util.Predicates2.startsWith;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.reflect.Invocation;
import org.jclouds.rest.annotations.Fallback;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.reflect.Invokable;
import com.google.inject.Injector;

@Beta
@Singleton
public class ReadAnnotationsAndProperties implements InvocationConfig {
   private final Injector injector;
   private final org.jclouds.Fallback<Object> defaultFallback;
   private final Map<String, Long> timeouts;

   @Inject
   ReadAnnotationsAndProperties(Injector injector,
         Function<Predicate<String>, Map<String, String>> filterStringsBoundByName,
         org.jclouds.Fallback<Object> defaultFallback) {
      this.injector = injector;
      this.defaultFallback = defaultFallback;
      this.timeouts = timeouts(filterStringsBoundByName);
   }

   @Override
   public Optional<Long> getTimeoutNanos(Invocation in) {
      String commandName = getCommandName(in);
      Optional<Long> defaultMillis = fromNullable(timeouts.get("default"));
      Optional<Long> timeoutMillis = fromNullable(timeouts.get(commandName));
      Invokable<?, ?> invoked = in.getInvokable();
      if (invoked.isAnnotationPresent(Named.class)) {
         timeoutMillis = timeoutMillis.or(defaultMillis);
      } else {
         // TODO: remove old logic once Named annotations are on all methods
         String className = invoked.getOwnerType().getRawType().getSimpleName().replace("AsyncClient", "Client")
               .replace("AsyncApi", "Api");
         timeoutMillis = timeoutMillis.or(fromNullable(timeouts.get(className))).or(defaultMillis);
      }
      if (timeoutMillis.isPresent())
         return Optional.of(MILLISECONDS.toNanos(timeoutMillis.get()));
      return Optional.absent();
   }

   @Override
   public String getCommandName(Invocation invocation) {
      Invokable<?, ?> invoked = invocation.getInvokable();
      if (invoked.isAnnotationPresent(Named.class)) {
         return invoked.getAnnotation(Named.class).value();
      } else {
         // TODO: remove old logic once Named annotations are on all methods
         String className = invoked.getOwnerType().getRawType().getSimpleName().replace("AsyncClient", "Client")
               .replace("AsyncApi", "Api");
         return className + "." + invoked.getName();
      }
   }

   @Override
   public org.jclouds.Fallback<?> getFallback(Invocation invocation) {
      Fallback fallback = invocation.getInvokable().getAnnotation(Fallback.class);
      if (fallback != null) {
         return injector.getInstance(fallback.value());
      }
      return defaultFallback;
   }

   /**
    * override timeout by values configured in properties(in ms)
    */
   static Map<String, Long> timeouts(Function<Predicate<String>, Map<String, String>> filterStringsBoundByName) {
      Map<String, String> stringBoundWithTimeoutPrefix = filterStringsBoundByName
            .apply(startsWith(PROPERTY_TIMEOUTS_PREFIX));
      Map<String, Long> longsByName = transformValues(stringBoundWithTimeoutPrefix, new Function<String, Long>() {
         public Long apply(String input) {
            return Long.valueOf(String.valueOf(input));
         }
      });
      return transformKeys(longsByName, new Function<String, String>() {
         public String apply(String input) {
            return input.replaceFirst(PROPERTY_TIMEOUTS_PREFIX, "");
         }
      });
   }
}
