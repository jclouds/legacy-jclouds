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
package org.jclouds.json.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Eliminates null values when deserializing Collections, Maps, and Multimaps
 * <p/>
 * Treats [null] as the empty set; [A, null] as [A]; etc.
 * 
 * @author Adrian Cole
 */
public class NullFilteringTypeAdapterFactories {
   private NullFilteringTypeAdapterFactories() {
   }

   static <T> TypeToken<?> resolve(TypeToken<T> ownerType, Type param) {
      return TypeToken.get(com.google.gson.internal.$Gson$Types.resolve(ownerType.getType(), ownerType.getRawType(),
            param));
   }

   public static final class IterableTypeAdapter<E> extends TypeAdapter<Iterable<E>> {

      private final TypeAdapter<E> elementAdapter;

      public IterableTypeAdapter(TypeAdapter<E> elementAdapter) {
         this.elementAdapter = elementAdapter;
         nullSafe();
      }

      public void write(JsonWriter out, Iterable<E> value) throws IOException {
         if (value == null) {
            out.nullValue();
            return;
         }
         out.beginArray();
         for (E element : value)
            elementAdapter.write(out, element);
         out.endArray();
      }

      public Iterable<E> read(JsonReader in) throws IOException {
         return readAndBuild(in, ImmutableList.<E> builder());
      }

      @SuppressWarnings("unchecked")
      protected <C extends Iterable<E>, B extends ImmutableCollection.Builder<E>> C readAndBuild(JsonReader in,
            B builder) throws IOException {
         in.beginArray();
         while (in.hasNext()) {
            E element = elementAdapter.read(in);
            if (element != null)
               builder.add(element);
         }
         in.endArray();
         return (C) builder.build();
      }

      @Override
      public int hashCode() {
         return elementAdapter.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         IterableTypeAdapter<?> that = IterableTypeAdapter.class.cast(obj);
         return equal(this.elementAdapter, that.elementAdapter);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("elementAdapter", elementAdapter).toString();
      }
   }

   public static class ImmutableListTypeAdapterFactory implements TypeAdapterFactory {

      protected final Class<?> declaring;

      public ImmutableListTypeAdapterFactory() {
         this(ImmutableList.class);
      }

      protected ImmutableListTypeAdapterFactory(Class<?> declaring) {
         this.declaring = declaring;
      }

      @SuppressWarnings("unchecked")
      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> ownerType) {
         Type type = ownerType.getType();
         if (ownerType.getRawType() != declaring || !(type instanceof ParameterizedType))
            return null;
         Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
         TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
         return (TypeAdapter<T>) newAdapter(elementAdapter);
      }

      @SuppressWarnings("unchecked")
      protected <E, I> TypeAdapter<I> newAdapter(TypeAdapter<E> elementAdapter) {
         return (TypeAdapter<I>) new IterableTypeAdapter<E>(elementAdapter);
      }

   }

   public static class IterableTypeAdapterFactory extends ImmutableListTypeAdapterFactory {
      public IterableTypeAdapterFactory() {
         super(Iterable.class);
      }
   }
   
   public static class CollectionTypeAdapterFactory extends ImmutableListTypeAdapterFactory {
      public CollectionTypeAdapterFactory() {
         super(Collection.class);
      }
   }

   public static class ListTypeAdapterFactory extends ImmutableListTypeAdapterFactory {
      public ListTypeAdapterFactory() {
         super(List.class);
      }
   }

   public static final class SetTypeAdapter<E> extends TypeAdapter<Set<E>> {

      private final IterableTypeAdapter<E> delegate;

      public SetTypeAdapter(TypeAdapter<E> elementAdapter) {
         this.delegate = new IterableTypeAdapter<E>(elementAdapter);
         nullSafe();
      }

      public void write(JsonWriter out, Set<E> value) throws IOException {
         this.delegate.write(out, value);
      }

      public Set<E> read(JsonReader in) throws IOException {
         return delegate.readAndBuild(in, ImmutableSet.<E> builder());
      }

      @Override
      public int hashCode() {
         return delegate.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         SetTypeAdapter<?> that = SetTypeAdapter.class.cast(obj);
         return equal(this.delegate, that.delegate);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("elementAdapter", delegate.elementAdapter).toString();
      }
   }

   public static class ImmutableSetTypeAdapterFactory extends ImmutableListTypeAdapterFactory {
      public ImmutableSetTypeAdapterFactory() {
         this(ImmutableSet.class);
      }

      protected ImmutableSetTypeAdapterFactory(Class<?> declaring) {
         super(declaring);
      }

      @SuppressWarnings("unchecked")
      protected <E, I> TypeAdapter<I> newAdapter(TypeAdapter<E> elementAdapter) {
         return (TypeAdapter<I>) new SetTypeAdapter<E>(elementAdapter);
      }
   }

   public static class SetTypeAdapterFactory extends ImmutableSetTypeAdapterFactory {
      public SetTypeAdapterFactory() {
         super(Set.class);
      }
   }

   private static final class FluentIterableTypeAdapter<E> extends TypeAdapter<FluentIterable<E>> {

      private final IterableTypeAdapter<E> delegate;

      public FluentIterableTypeAdapter(TypeAdapter<E> elementAdapter) {
         this.delegate = new IterableTypeAdapter<E>(elementAdapter);
         nullSafe();
      }

      public void write(JsonWriter out, FluentIterable<E> value) throws IOException {
         this.delegate.write(out, value.toList());
      }

      public FluentIterable<E> read(JsonReader in) throws IOException {
         return FluentIterable.from(delegate.read(in));
      }

      @Override
      public int hashCode() {
         return delegate.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         FluentIterableTypeAdapter<?> that = FluentIterableTypeAdapter.class.cast(obj);
         return equal(this.delegate, that.delegate);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("elementAdapter", delegate.elementAdapter).toString();
      }
   }

   public static class FluentIterableTypeAdapterFactory extends ImmutableListTypeAdapterFactory {
      public FluentIterableTypeAdapterFactory() {
         super(FluentIterable.class);
      }

      @SuppressWarnings("unchecked")
      protected <E, I> TypeAdapter<I> newAdapter(TypeAdapter<E> elementAdapter) {
         return (TypeAdapter<I>) new FluentIterableTypeAdapter<E>(elementAdapter);
      }
   }

   private static final class MapTypeAdapter<K, V> extends TypeAdapter<Map<K, V>> {

      protected final TypeAdapter<K> keyAdapter;
      protected final TypeAdapter<V> valueAdapter;

      protected MapTypeAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         this.keyAdapter = keyAdapter;
         this.valueAdapter = valueAdapter;
         nullSafe();
      }

      public void write(JsonWriter out, Map<K, V> value) throws IOException {
         if (value == null) {
            out.nullValue();
            return;
         }
         out.beginObject();
         for (Map.Entry<K, V> element : value.entrySet()) {
            out.name(String.valueOf(element.getKey()));
            valueAdapter.write(out, element.getValue());
         }
         out.endObject();
      }

      public Map<K, V> read(JsonReader in) throws IOException {
         ImmutableMap.Builder<K, V> result = ImmutableMap.builder();
         in.beginObject();
         while (in.hasNext()) {
            JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
            K name = keyAdapter.read(in);
            V value = valueAdapter.read(in);
            if (value != null)
               result.put(name, value);
         }
         in.endObject();
         return result.build();
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(keyAdapter, valueAdapter);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         MapTypeAdapter<?, ?> that = MapTypeAdapter.class.cast(obj);
         return equal(this.keyAdapter, that.keyAdapter) && equal(this.valueAdapter, that.valueAdapter);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("keyAdapter", keyAdapter).add("valueAdapter", valueAdapter).toString();
      }
   }

   public static class MapTypeAdapterFactory implements TypeAdapterFactory {

      protected final Class<?> declaring;

      public MapTypeAdapterFactory() {
         this(Map.class);
      }

      protected MapTypeAdapterFactory(Class<?> declaring) {
         this.declaring = declaring;
      }

      public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> ownerType) {
         Type type = ownerType.getType();
         if (ownerType.getRawType() != declaring || !(type instanceof ParameterizedType))
            return null;
         Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
         Type valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
         TypeAdapter<?> keyAdapter = gson.getAdapter(TypeToken.get(keyType));
         TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(valueType));
         return newAdapter(keyAdapter, valueAdapter);
      }

      @SuppressWarnings("unchecked")
      protected <K, V, T> TypeAdapter<T> newAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         return (TypeAdapter<T>) new MapTypeAdapter<K, V>(keyAdapter, valueAdapter);
      }
   }

   private static final class MultimapTypeAdapter<K, V> extends TypeAdapter<Multimap<K, V>> {

      private final MapTypeAdapter<K, Iterable<V>> delegate;

      public MultimapTypeAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         this.delegate = new MapTypeAdapter<K, Iterable<V>>(keyAdapter,
               new IterableTypeAdapter<V>(valueAdapter));
         nullSafe();
      }

      @SuppressWarnings("unchecked")
      public void write(JsonWriter out, Multimap<K, V> value) throws IOException {
         this.delegate.write(out, Map.class.cast(value.asMap()));
      }

      public Multimap<K, V> read(JsonReader in) throws IOException {
         ImmutableMultimap.Builder<K, V> builder = ImmutableMultimap.<K, V> builder();
         for (Entry<K, Iterable<V>> entry : delegate.read(in).entrySet())
            builder.putAll(entry.getKey(), entry.getValue());
         return builder.build();
      }

      @Override
      public int hashCode() {
         return delegate.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         MultimapTypeAdapter<?, ?> that = MultimapTypeAdapter.class.cast(obj);
         return equal(this.delegate, that.delegate);
      }

      @Override
      public String toString() {
         return toStringHelper(this).add("keyAdapter", delegate.keyAdapter)
               .add("valueAdapter", delegate.valueAdapter).toString();
      }
   }

   public static class MultimapTypeAdapterFactory extends MapTypeAdapterFactory {

      public MultimapTypeAdapterFactory() {
         super(Multimap.class);
      }

      @SuppressWarnings("unchecked")
      @Override
      protected <K, V, T> TypeAdapter<T> newAdapter(TypeAdapter<K> keyAdapter, TypeAdapter<V> valueAdapter) {
         return (TypeAdapter<T>) new MultimapTypeAdapter<K, V>(keyAdapter, valueAdapter);
      }

   }
}
