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
package org.jclouds;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;

/**
 * {@link View} allows access to the provider-specific, or library-driven api
 * behind an abstraction. One backend context can support multiple views.
 * <p/>
 * For example, the {@code CloudStackContext} can be backend by both
 * {@code ComputeServiceContext} and {@code LoadBalancerServiceContext}, as the
 * api covers these features.
 * 
 * @author Adrian Cole
 * 
 */
@Beta
public interface View {

   /**
    * 
    * @return type of the context powering the current one.
    */
   TypeToken<?> getBackendType();

   /**
    * Return an object of the specified type to allow access to the backend
    * context. If the backend context is not assignable from the supplied type,
    * an {@link IllegalArgumentException} is thrown.
    * 
    * ex.
    * <pre>
    * RestContext<NovaApi, NovaAsyncApi> backendApi = computeContext.unwrap(NovaApiMetadata.CONTEXT_TOKEN);
    * </pre>
    * @param type
    *           the type of the context to be returned. The backend context must
    *           be assignable from this type.
    * @return an instance of the specified type
    * @throws IllegalArgumentException
    *            if the backend context is not assignable from the specified
    *            class.
    * @see #getBackendType()
    */
   <C extends Context> C unwrap(TypeToken<C> type) throws IllegalArgumentException;

   /**
    * shortcut for {@code unwrap(getWrappedType())}
    * 
    * @throws ClassCastException
    *            if the user supplied {@code C} param is not assignableFrom
    *            {@link #getBackendType()}
    */
   <C extends Context> C unwrap() throws ClassCastException;

}
