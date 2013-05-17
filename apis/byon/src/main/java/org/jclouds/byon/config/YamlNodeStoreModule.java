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
package org.jclouds.byon.config;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.domain.YamlNode;
import org.jclouds.byon.functions.NodesFromYamlStream;
import org.jclouds.byon.suppliers.NodesParsedFromSupplier;
import org.jclouds.collect.TransformingMap;
import org.jclouds.io.CopyInputStreamInputSupplierMap;
import org.jclouds.io.CopyInputStreamIntoSupplier;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.InputSupplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresNodeStore
@Beta
public class YamlNodeStoreModule extends AbstractModule {
   private static final Map<String, InputSupplier<InputStream>> BACKING = new ConcurrentHashMap<String, InputSupplier<InputStream>>();
   private final Map<String, InputStream> backing;

   public YamlNodeStoreModule(Map<String, InputStream> backing) {
      this.backing = backing;
   }

   public YamlNodeStoreModule() {
      this(null);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<LoadingCache<String, Node>>>() {
      }).to(NodesParsedFromSupplier.class);
      bind(new TypeLiteral<Function<InputStream, LoadingCache<String, Node>>>() {
      }).to(NodesFromYamlStream.class);
      bind(new TypeLiteral<Function<YamlNode, InputStream>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.yamlNodeToInputStream);
      bind(new TypeLiteral<Function<InputStream, YamlNode>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.inputStreamToYamlNode);
      bind(new TypeLiteral<Function<Node, YamlNode>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.nodeToYamlNode);
      bind(new TypeLiteral<Function<YamlNode, Node>>() {
      }).toInstance(org.jclouds.byon.domain.YamlNode.toNode);
      if (backing != null) {
         bind(new TypeLiteral<Map<String, InputStream>>() {
         }).annotatedWith(Names.named("yaml")).toInstance(backing);
      } else {
         bind(new TypeLiteral<Map<String, InputSupplier<InputStream>>>() {
         }).annotatedWith(Names.named("yaml")).toInstance(BACKING);
         bind(new TypeLiteral<Map<String, InputStream>>() {
         }).annotatedWith(Names.named("yaml")).to(new TypeLiteral<YAMLCopyInputStreamInputSupplierMap>() {
         });
      }

   }

   @Singleton
   public static class YAMLCopyInputStreamInputSupplierMap extends CopyInputStreamInputSupplierMap {
      @Inject
      public YAMLCopyInputStreamInputSupplierMap(@Named("yaml") Map<String, InputSupplier<InputStream>> toMap,
            CopyInputStreamIntoSupplier putFunction) {
         super(toMap, putFunction);
      }
   }

   @Provides
   @Singleton
   protected LoadingCache<String, Node> provideNodeStore(Map<String, YamlNode> backing, Function<Node, YamlNode> yamlSerializer,
         Function<YamlNode, Node> yamlDeserializer) {
      return CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(new TransformingMap<String, YamlNode, Node>(backing, yamlDeserializer, yamlSerializer))));
   }

   @Provides
   @Singleton
   protected Map<String, YamlNode> provideYamlStore(@Named("yaml") Map<String, InputStream> backing,
         Function<YamlNode, InputStream> yamlSerializer, Function<InputStream, YamlNode> yamlDeserializer) {
      return new TransformingMap<String, InputStream, YamlNode>(backing, yamlDeserializer, yamlSerializer);
   }
}
