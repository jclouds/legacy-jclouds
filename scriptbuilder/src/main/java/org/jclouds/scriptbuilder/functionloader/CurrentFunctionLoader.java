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
package org.jclouds.scriptbuilder.functionloader;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.scriptbuilder.functionloader.filters.LicenseHeaderFilter;

import com.google.common.util.concurrent.Atomics;

/**
 * Means to access the current {@link FunctionLoader} instance;
 */
public class CurrentFunctionLoader {

   private static final AtomicReference<FunctionLoader> ref = Atomics.<FunctionLoader>newReference(
            BasicFunctionLoader.INSTANCE);

   public static FunctionLoader get() {
      // Filter out license headers in function scripts
      return new LicenseHeaderFilter(ref.get());
   }

   public static FunctionLoader set(FunctionLoader loader) {
      return ref.getAndSet(loader);
   }

   public static FunctionLoader reset() {
      return ref.getAndSet(BasicFunctionLoader.INSTANCE);
   }

}
