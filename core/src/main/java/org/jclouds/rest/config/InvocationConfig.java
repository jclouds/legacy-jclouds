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
package org.jclouds.rest.config;

import org.jclouds.Fallback;
import org.jclouds.reflect.Invocation;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.ImplementedBy;

/**
 * Provides the ability to decouple timeouts and fallbacks from what's built-in.
 * 
 * @author Adrian Cole
 */
@Beta
@ImplementedBy(ReadAnnotationsAndProperties.class)
public interface InvocationConfig {

   /**
    * If this is present, Sync method calls will block up to the specified nanos
    * and throw an {@linkplain UncheckedTimeoutException}. If this is not
    * present, Sync method calls will be invoked directly, typically through
    * {@linkplain HttpCommandExecutorService#invoke}.
    */
   Optional<Long> getTimeoutNanos(Invocation in);

   /**
    * command named used in logging and configuration keys.
    */
   String getCommandName(Invocation invocation);

   /**
    * fallback used for Sync or Async commands.
    */
   Fallback<?> getFallback(Invocation invocation);
}
