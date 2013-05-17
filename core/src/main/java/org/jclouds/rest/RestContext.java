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

import org.jclouds.Context;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.ImplementedBy;

/**
 * Represents an authenticated context to the cloud.
 * 
 * <h2>Note</h2> Please issue {@link #close()} when you are finished with this context in order to
 * release resources.
 * 
 * 
 * @author Adrian Cole
 * 
 * @deprecated please use {@link org.jclouds.ContextBuilder#buildApi()} as
 *             async interface will be removed in jclouds 1.7.
 */
@Deprecated
@ImplementedBy(RestContextImpl.class)
public interface RestContext<S, A> extends Context {

   /**
    * low-level api to the cloud. Threadsafe implementations will return a singleton.
    * 
    * @return a connection to the cloud where all methods return {@link Future}s
    * @deprecated please use {@link org.jclouds.ContextBuilder#buildApi()} as
    *             async interface will be removed in jclouds 1.7.
    */
   @Deprecated
   A getAsyncApi();

   /**
    * reflects a tuned connection to the cloud which calls {@link #getAsyncApi()} with predetermined
    * timeouts.
    * 
    * @return a connection to the cloud where all methods block
    */
   S getApi();

}
