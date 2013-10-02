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
package org.jclouds.rest;

import org.jclouds.apis.ApiMetadata;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 * 
 * @deprecated please use {@link HttpApiMetadata} as
 *             async interface will be removed in jclouds 1.7.
 */
@Deprecated
@Beta
public interface RestApiMetadata extends ApiMetadata {

   public static interface Builder<T extends Builder<T>> extends ApiMetadata.Builder<T> {

      /**
       * @see ApiMetadata#getApi()
       * @see ApiMetadata#getAsyncApi()
       */
      T javaApi(Class<?> api, Class<?> asyncApi);
   }

   /**
    * 
    * @return the type of the api which blocks on all requests
    */
   Class<?> getApi();

   /**
    * 
    * @return the type of the api, which is the same as {@link #getApi}, except
    *         all methods return {@link ListenableFuture}
    */
   Class<?> getAsyncApi();
}
