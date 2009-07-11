/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http;

import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.inject.internal.Nullable;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>.
 * 
 * @author Adrian Cole
 */
public interface TransformingHttpCommandExecutorService {
   /**
    * 
    * Submits the command and transforms the result before requested via {@link Future#get()}.
    * 
    * @param <T>
    *           type that is required from the value.
    * @param command
    *           what to execute
    * @param responseTransformer
    *           how to transform the response from the above command
    * @param exceptionTransformer
    *           maps any non-critical exceptions to the return type {@code <T>}
    * @return value of the intended response.
    */
   public <T> Future<T> submit(HttpCommand command, Function<HttpResponse, T> responseTransformer,
            @Nullable Function<Exception, T> exceptionTransformer);

}
