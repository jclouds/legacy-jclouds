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

package org.jclouds.management.functions;


import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.management.internal.ManagedTypeModel;

import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.util.concurrent.ExecutionException;

/**
 * A {@link Function} that converts an {@link Iterable} to {@link TabularData}.
 * @param <T>
 */
public final class ToTabularData<T> implements Function<Iterable<? extends T>, TabularData> {

   private final ManagedTypeModel<T> model;
   private final ToCompositeData<T> compositeConverter;
   private static final LoadingCache<Class<?>, ToTabularData<?>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, ToTabularData<?>>() {

      @Override
      public ToTabularData<?> load(Class<?> key) throws Exception {
         return new ToTabularData(key);
      }
   });

   /**
    * Constructor.
    * Only available from the internal cache.
    * @param type
    */
   private ToTabularData(Class<T> type) {
      this.model = ManagedTypeModel.of(type);
      this.compositeConverter = ToCompositeData.from(type);
   }

   /**
    * Factory method.
    * @param type
    * @param <T>
    * @return
    */
   public static <T> ToTabularData<T> from(Class<T> type) {
      try {
         return (ToTabularData<T>) CACHE.get(type);
      } catch (ExecutionException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Conversion function.
    * @param input
    * @return
    */
   @Override
   public TabularData apply(@Nullable Iterable<? extends T> input) {
      TabularDataSupport table = new TabularDataSupport(model.getTabularType());
      for (T obj : input) {
         try {
            table.put(compositeConverter.apply(obj));
         } catch (Exception ex) {
            ex.printStackTrace(System.out);
         }
      }
      return table;
   }
}
