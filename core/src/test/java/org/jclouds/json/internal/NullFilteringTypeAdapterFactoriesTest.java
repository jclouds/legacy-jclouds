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
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.CollectionTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.FluentIterableTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ImmutableListTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ImmutableSetTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.IterableTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.ListTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.MapTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.MultimapTypeAdapterFactory;
import org.jclouds.json.internal.NullFilteringTypeAdapterFactories.SetTypeAdapterFactory;
import org.testng.annotations.Test;

import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "NullFilteringTypeAdapterFactoriesTest")
public class NullFilteringTypeAdapterFactoriesTest {
   private static class Resource {
      private final String id;
      private final String name;

      private Resource(String id, String name) {
         this.id = id;
         this.name = name;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(id, name);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         Resource that = Resource.class.cast(obj);
         return equal(this.id, that.id) && equal(this.name, that.name);
      }
   }

   private Gson fluentIterable = new GsonBuilder().registerTypeAdapterFactory(new FluentIterableTypeAdapterFactory())
         .create();
   private Type fluentIterableType = new TypeToken<FluentIterable<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type fluentIterableResourceType = new TypeToken<FluentIterable<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testFluentIterable() {
      FluentIterable<String> noNulls = fluentIterable.fromJson("[\"value\",\"a test string!\"]", fluentIterableType);
      assertEquals(noNulls.toList(), ImmutableList.of("value", "a test string!"));
      FluentIterable<String> withNull = fluentIterable.fromJson("[null,\"a test string!\"]", fluentIterableType);
      assertEquals(withNull.toList(), ImmutableList.of("a test string!"));
      FluentIterable<String> withDupes = fluentIterable.fromJson("[\"value\",\"value\"]", fluentIterableType);
      assertEquals(withDupes.toList(), ImmutableList.of("value", "value"));
      FluentIterable<Resource> resources = fluentIterable.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", fluentIterableResourceType);
      assertEquals(resources.toList(), ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson collection = new GsonBuilder().registerTypeAdapterFactory(new CollectionTypeAdapterFactory()).create();
   private Type collectionType = new TypeToken<Collection<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type collectionResourceType = new TypeToken<Collection<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testCollection() {
      Collection<String> noNulls = collection.fromJson("[\"value\",\"a test string!\"]", collectionType);
      assertEquals(noNulls, ImmutableList.of("value", "a test string!"));
      Collection<String> withNull = collection.fromJson("[null,\"a test string!\"]", collectionType);
      assertEquals(withNull, ImmutableList.of("a test string!"));
      Collection<String> withDupes = collection.fromJson("[\"value\",\"value\"]", collectionType);
      assertEquals(withDupes, ImmutableList.of("value", "value"));
      Collection<Resource> resources = collection.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", collectionResourceType);
      assertEquals(resources, ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson iterable = new GsonBuilder().registerTypeAdapterFactory(new IterableTypeAdapterFactory()).create();
   private Type iterableType = new TypeToken<Iterable<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type iterableResourceType = new TypeToken<Iterable<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testIterable() {
      Iterable<String> noNulls = iterable.fromJson("[\"value\",\"a test string!\"]", iterableType);
      assertEquals(noNulls, ImmutableList.of("value", "a test string!"));
      Iterable<String> withNull = iterable.fromJson("[null,\"a test string!\"]", iterableType);
      assertEquals(withNull, ImmutableList.of("a test string!"));
      Iterable<String> withDupes = iterable.fromJson("[\"value\",\"value\"]", iterableType);
      assertEquals(withDupes, ImmutableList.of("value", "value"));
      Iterable<Resource> resources = iterable.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", iterableResourceType);
      assertEquals(resources, ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Type iterableWildcardExtendsResourceType = new TypeToken<Iterable<? extends Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testWildcardExtends() {
      Iterable<? extends Resource> wildcardExtendsResources = iterable.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]",
            iterableWildcardExtendsResourceType);
      assertEquals(wildcardExtendsResources,
            ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson list = new GsonBuilder().registerTypeAdapterFactory(new ListTypeAdapterFactory()).create();
   private Type listType = new TypeToken<List<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type listResourceType = new TypeToken<List<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testList() {
      Iterable<String> noNulls = list.fromJson("[\"value\",\"a test string!\"]", listType);
      assertEquals(noNulls, ImmutableList.of("value", "a test string!"));
      Iterable<String> withNull = list.fromJson("[null,\"a test string!\"]", listType);
      assertEquals(withNull, ImmutableList.of("a test string!"));
      Iterable<String> withDupes = list.fromJson("[\"value\",\"value\"]", listType);
      assertEquals(withDupes, ImmutableList.of("value", "value"));
      Iterable<Resource> resources = list.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", listResourceType);
      assertEquals(resources, ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson immutableList = new GsonBuilder().registerTypeAdapterFactory(new ImmutableListTypeAdapterFactory()).create();
   private Type immutableListType = new TypeToken<ImmutableList<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type immutableListResourceType = new TypeToken<ImmutableList<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testImmutableList() {
      Iterable<String> noNulls = immutableList.fromJson("[\"value\",\"a test string!\"]", immutableListType);
      assertEquals(noNulls, ImmutableList.of("value", "a test string!"));
      Iterable<String> withNull = immutableList.fromJson("[null,\"a test string!\"]", immutableListType);
      assertEquals(withNull, ImmutableList.of("a test string!"));
      Iterable<String> withDupes = immutableList.fromJson("[\"value\",\"value\"]", immutableListType);
      assertEquals(withDupes, ImmutableList.of("value", "value"));
      Iterable<Resource> resources = immutableList.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", immutableListResourceType);
      assertEquals(resources, ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson set = new GsonBuilder().registerTypeAdapterFactory(new SetTypeAdapterFactory()).create();
   private Type setType = new TypeToken<Set<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type setResourceType = new TypeToken<Set<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testSet() {
      Set<String> noNulls = set.fromJson("[\"value\",\"a test string!\"]", setType);
      assertEquals(noNulls, ImmutableSet.of("value", "a test string!"));
      Set<String> withNull = set.fromJson("[null,\"a test string!\"]", setType);
      assertEquals(withNull, ImmutableSet.of("a test string!"));
      Set<String> withDupes = set.fromJson("[\"value\",\"value\"]", setType);
      assertEquals(withDupes, ImmutableSet.of("value"));
      Set<Resource> resources = set.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", setResourceType);
      assertEquals(resources, ImmutableSet.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson immutableSet = new GsonBuilder().registerTypeAdapterFactory(new ImmutableSetTypeAdapterFactory()).create();
   private Type immutableSetType = new TypeToken<ImmutableSet<String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type immutableSetResourceType = new TypeToken<ImmutableSet<Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testImmutableSet() {
      Iterable<String> noNulls = immutableSet.fromJson("[\"value\",\"a test string!\"]", immutableSetType);
      assertEquals(noNulls, ImmutableSet.of("value", "a test string!"));
      Iterable<String> withNull = immutableSet.fromJson("[null,\"a test string!\"]", immutableSetType);
      assertEquals(withNull, ImmutableSet.of("a test string!"));
      Iterable<String> withDupes = immutableSet.fromJson("[\"value\",\"value\"]", immutableSetType);
      assertEquals(withDupes, ImmutableSet.of("value", "value"));
      Iterable<Resource> resources = immutableSet.fromJson(
            "[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]", immutableSetResourceType);
      assertEquals(resources, ImmutableSet.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }

   private Gson map = new GsonBuilder().registerTypeAdapterFactory(new MapTypeAdapterFactory()).create();
   private Type mapType = new TypeToken<Map<String, String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type mapResourceType = new TypeToken<Map<String, Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testMap() {
      Map<String, String> noNulls = map.fromJson("{\"value\":\"a test string!\"}", mapType);
      assertEquals(noNulls, ImmutableMap.of("value", "a test string!"));
      Map<String, String> withNull = map.fromJson("{\"value\":null}", mapType);
      assertEquals(withNull, ImmutableMap.of());
      Map<String, String> withEmpty = map.fromJson("{\"value\":\"\"}", mapType);
      assertEquals(withEmpty, ImmutableMap.of("value", ""));
      Map<String, Resource> resources = map.fromJson(
            "{\"i-foo\":{\"id\":\"i-foo\",\"name\":\"foo\"},\"i-bar\":{\"id\":\"i-bar\",\"name\":\"bar\"}}",
            mapResourceType);
      assertEquals(resources,
            ImmutableMap.of("i-foo", new Resource("i-foo", "foo"), "i-bar", new Resource("i-bar", "bar")));
   }

   private Gson multimap = new GsonBuilder().registerTypeAdapterFactory(new MultimapTypeAdapterFactory()).create();
   private Type multimapType = new TypeToken<Multimap<String, String>>() {
      private static final long serialVersionUID = 1L;
   }.getType();
   private Type multimapResourceType = new TypeToken<Multimap<String, Resource>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   public void testMultimap() {
      Multimap<String, String> noNulls = multimap.fromJson("{\"value\":[\"a test string!\"]}", multimapType);
      assertEquals(noNulls, ImmutableMultimap.of("value", "a test string!"));
      Multimap<String, String> withNull = multimap.fromJson("{\"value\":[null]}", multimapType);
      assertEquals(withNull, ImmutableMultimap.of());
      Multimap<String, String> withEmpty = multimap.fromJson("{\"value\":[\"\"]}", multimapType);
      assertEquals(withEmpty, ImmutableMultimap.of("value", ""));
      Multimap<String, String> withDupes = multimap.fromJson("{\"key\":[\"value\",\"value\"]}", multimapType);
      assertEquals(withDupes.get("key"), ImmutableList.of("value", "value"));
      Multimap<String, Resource> resources = multimap.fromJson(
            "{\"i-foo\":[{\"id\":\"i-foo\",\"name\":\"foo\"}],\"i-bar\":[{\"id\":\"i-bar\",\"name\":\"bar\"}]}",
            multimapResourceType);
      assertEquals(resources,
            ImmutableMultimap.of("i-foo", new Resource("i-foo", "foo"), "i-bar", new Resource("i-bar", "bar")));
      Multimap<String, Resource> resourceDupes = multimap.fromJson(
            "{\"i-foo\":[{\"id\":\"i-foo\",\"name\":\"foo\"},{\"id\":\"i-bar\",\"name\":\"bar\"}]}",
            multimapResourceType);
      assertEquals(resourceDupes.get("i-foo"),
            ImmutableList.of(new Resource("i-foo", "foo"), new Resource("i-bar", "bar")));
   }
}
