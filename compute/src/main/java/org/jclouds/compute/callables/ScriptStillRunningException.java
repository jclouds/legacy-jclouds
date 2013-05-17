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
package org.jclouds.compute.callables;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.ExecResponse;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 */
public class ScriptStillRunningException extends TimeoutException implements Supplier<ListenableFuture<ExecResponse>> {

   private final ListenableFuture<ExecResponse> delegate;

   public ScriptStillRunningException(long timeout, TimeUnit unit, ListenableFuture<ExecResponse> delegate) {
      this(format("time up waiting %ds for %s to complete."
               + " call get() on this exception to get access to the task in progress", TimeUnit.SECONDS.convert(
               timeout, unit), delegate), delegate);
   }

   public ScriptStillRunningException(String message, ListenableFuture<ExecResponse> delegate) {
      super(checkNotNull(message, "message"));
      this.delegate = checkNotNull(delegate, "delegate");
   }

   @Override
   public ListenableFuture<ExecResponse> get() {
      return delegate;
   }

}
