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
package org.jclouds.concurrent;

import java.util.Map;
import java.util.concurrent.Future;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * A failure occurred while concurrently operating on an Iterable
 * 
 * @author Adrian Cole
 */
@SuppressWarnings("serial")
public final class TransformParallelException extends RuntimeException {

   private final Map<?, Future<?>> success;
   private final Map<?, Exception> exceptions;

   public TransformParallelException(Map<?, Future<?>> success, Map<?, Exception> exceptions, String messagePrefix) {
      super(String.format("error %s: %s", messagePrefix, exceptions));
      this.success = ImmutableMap.copyOf(success);
      this.exceptions = ImmutableMap.copyOf(exceptions);
      initCause(Iterables.get(exceptions.values(), 0));
   }

   /**
    * @return Elements that performed the transform without error
    */
   public Map<?, Future<?>> getSuccessfulToValue() {
      return success;
   }

   /**
    * @return Elements that failed during the transform
    */
   public Map<?, Exception> getFromToException() {
      return exceptions;
   }

}
