/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.byon.config;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.domain.YamlNode;
import org.jclouds.collect.TransformingMap;
import org.jclouds.io.CopyInputStreamInputSupplierMap;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.io.InputSupplier;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

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
         }).toInstance(backing);
      } else {
         bind(new TypeLiteral<Map<String, InputSupplier<InputStream>>>() {
         }).toInstance(BACKING);
         bind(new TypeLiteral<Map<String, InputStream>>() {
         }).to(new TypeLiteral<CopyInputStreamInputSupplierMap>() {
         });
      }
   }

   @Provides
   @Singleton
   protected Map<String, Node> provideNodeStore(Map<String, YamlNode> backing,
            Function<Node, YamlNode> yamlSerializer, Function<YamlNode, Node> yamlDeserializer) {
      return new TransformingMap<String, YamlNode, Node>(backing, yamlDeserializer, yamlSerializer);
   }

   @Provides
   @Singleton
   protected Map<String, YamlNode> provideYamlStore(Map<String, InputStream> backing,
            Function<YamlNode, InputStream> yamlSerializer, Function<InputStream, YamlNode> yamlDeserializer) {
      return new TransformingMap<String, InputStream, YamlNode>(backing, yamlDeserializer, yamlSerializer);
   }
}