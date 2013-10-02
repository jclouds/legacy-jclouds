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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

/**
 * A map that transforms values on the way in and out. Inspired by the guava method
 * {@code Maps.transformValues}.
 * 
 * @author Adrian Cole
 * 
 */
public class TransformingMap<K, V1, V2> extends AbstractMap<K, V2> {
   private final Map<K, V1> fromMap;
   private final Function<? super V1, V2> getFunction;
   private final Function<? super V2, V1> putFunction;

   public TransformingMap(Map<K, V1> fromMap, Function<? super V1, V2> getFunction, Function<? super V2, V1> putFunction) {
      this.fromMap = checkNotNull(fromMap);
      this.getFunction = checkNotNull(getFunction);
      this.putFunction = checkNotNull(putFunction);
   }

   @Override
   public int size() {
      return fromMap.size();
   }

   @Override
   public V2 put(K key, V2 value) {
      V2 old = get(key);
      fromMap.put(key, value != null ? putFunction.apply(value) : null);
      return old;
   }

   @Override
   public boolean containsKey(Object key) {
      return fromMap.containsKey(key);
   }

   @Override
   public V2 get(Object key) {
      V1 value = fromMap.get(key);
      return (value != null || fromMap.containsKey(key)) ? getFunction.apply(value) : null;
   }

   @Override
   public V2 remove(Object key) {
      return fromMap.containsKey(key) ? getFunction.apply(fromMap.remove(key)) : null;
   }

   @Override
   public void clear() {
      fromMap.clear();
   }

   @Override
   public Set<Entry<K, V2>> entrySet() {
      return new EntrySet();

   }

   private class EntrySet extends AbstractSet<Entry<K, V2>> {
      @Override
      public int size() {
         return TransformingMap.this.size();
      }

      @Override
      public Iterator<Entry<K, V2>> iterator() {
         final Iterator<Entry<K, V1>> mapIterator = fromMap.entrySet().iterator();

         return new Iterator<Entry<K, V2>>() {
            public boolean hasNext() {
               return mapIterator.hasNext();
            }

            public Entry<K, V2> next() {
               final Entry<K, V1> entry = mapIterator.next();
               return new AbstractMapEntry<K, V2>() {
                  @Override
                  public K getKey() {
                     return entry.getKey();
                  }

                  @Override
                  public V2 getValue() {
                     return getFunction.apply(entry.getValue());
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
         fromMap.clear();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         }
         Entry<?, ?> entry = (Entry<?, ?>) o;
         Object entryKey = entry.getKey();
         Object entryValue = entry.getValue();
         V2 mapValue = TransformingMap.this.get(entryKey);
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
            fromMap.remove(key);
            return true;
         }
         return false;
      }
   }
}
