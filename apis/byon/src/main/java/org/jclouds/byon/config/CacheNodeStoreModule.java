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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.byon.Node;

import com.google.common.annotations.Beta;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresNodeStore
@Beta
public class CacheNodeStoreModule extends AbstractModule {
   private final LoadingCache<String, Node> backing;

   public CacheNodeStoreModule(LoadingCache<String, Node> backing) {
      this.backing = checkNotNull(backing, "backing");
   }

   public CacheNodeStoreModule(Map<String, Node> backing) {
      this(CacheBuilder.newBuilder().<String, Node>build(CacheLoader.from(Functions.forMap(backing))));
      for (String node : backing.keySet())
         this.backing.getUnchecked(node);
   }

   @Override
   protected void configure() {
      bind(new TypeLiteral<LoadingCache<String, Node>>() {
      }).toInstance(backing);
      bind(new TypeLiteral<Supplier<LoadingCache<String, Node>>>() {
      }).toInstance(Suppliers.<LoadingCache<String, Node>> ofInstance(backing));
   }

}
