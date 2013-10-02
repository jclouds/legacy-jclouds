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
package org.jclouds.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.InputSupplier;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class InputSupplierMap<K, V> extends AbstractMap<K, V> {
   private final Map<K, InputSupplier<V>> toMap;
   private final Function<V, InputSupplier<V>> putFunction;

   public InputSupplierMap(Map<K, InputSupplier<V>> toMap, Function<V, InputSupplier<V>> putFunction) {
      this.toMap = checkNotNull(toMap);
      this.putFunction = checkNotNull(putFunction);
   }

   @Override
   public int size() {
      return toMap.size();
   }

   @Override
   public V put(K key, V value) {
      V old = get(key);
      toMap.put(key, value != null ? putFunction.apply(value) : null);
      return old;
   }

   @Override
   public boolean containsKey(Object key) {
      return toMap.containsKey(key);
   }

   @Override
   public V get(Object key) {
      InputSupplier<V> value = toMap.get(key);
      try {
         return value != null ? value.getInput() : null;
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public V remove(Object key) {
      try {
         return toMap.containsKey(key) ? toMap.remove(key).getInput() : null;
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public void clear() {
      toMap.clear();
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new EntrySet();
   }

   private class EntrySet extends AbstractSet<Entry<K, V>> {
      @Override
      public int size() {
         return InputSupplierMap.this.size();
      }

      @Override
      public Iterator<Entry<K, V>> iterator() {
         final Iterator<java.util.Map.Entry<K, InputSupplier<V>>> mapIterator = toMap.entrySet().iterator();

         return new Iterator<Entry<K, V>>() {
            public boolean hasNext() {
               return mapIterator.hasNext();
            }

            public Entry<K, V> next() {
               final java.util.Map.Entry<K, InputSupplier<V>> entry = mapIterator.next();
               return new AbstractMapEntry<K, V>() {
                  @Override
                  public K getKey() {
                     return entry.getKey();
                  }

                  @Override
                  public V getValue() {
                     try {
                        return entry.getValue().getInput();
                     } catch (IOException e) {
                        throw Throwables.propagate(e);
                     }
                  }
               };
            }

            public void remove() {
               mapIterator.remove();
            }
         };
      }

      @Override
      public void clear() {
         toMap.clear();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         }
         Entry<?, ?> entry = (Entry<?, ?>) o;
         Object entryKey = entry.getKey();
         Object entryValue = entry.getValue();
         V mapValue = InputSupplierMap.this.get(entryKey);
         if (mapValue != null) {
            return mapValue.equals(entryValue);
         }
         return entryValue == null && containsKey(entryKey);
      }

      @Override
      public boolean remove(Object o) {
         if (contains(o)) {
            Entry<?, ?> entry = (Entry<?, ?>) o;
            Object key = entry.getKey();
            toMap.remove(key);
            return true;
         }
         return false;
      }
   }
}
