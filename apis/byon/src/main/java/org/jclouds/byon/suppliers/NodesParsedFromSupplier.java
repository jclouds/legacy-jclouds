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
package org.jclouds.byon.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class NodesParsedFromSupplier implements Supplier<LoadingCache<String, Node>> {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Supplier<InputStream> supplier;
   private final Function<InputStream, LoadingCache<String, Node>> parser;

   @Inject
   NodesParsedFromSupplier(@Provider Supplier<InputStream> supplier, Function<InputStream, LoadingCache<String, Node>> parser) {
      this.supplier = checkNotNull(supplier, "supplier");
      this.parser = checkNotNull(parser, "parser");
   }

   @Override
   public LoadingCache<String, Node> get() {
      LoadingCache<String, Node> nodes = parser.apply(supplier.get());
      checkState(nodes != null && nodes.size() > 0, "no nodes parsed from supplier: %s", supplier);
      return nodes;
   }

}
