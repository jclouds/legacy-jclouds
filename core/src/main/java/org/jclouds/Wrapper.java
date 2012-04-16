/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds;

import java.io.Closeable;

import com.google.common.annotations.Beta;
import com.google.common.reflect.TypeToken;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
@Beta
public interface Wrapper {

   /**
    * 
    * @return type of the context powering the current one.
    */
   TypeToken<?> getWrappedType();

   /**
    * Return an object of the specified type to allow access to the wrapped context. If the wrapped
    * context is not assignable from the supplied type, an {@link IllegalArgumentException} is
    * thrown.
    * 
    * @param type
    *           the type of the context to be returned. The wrapped context must be assignable from
    *           this type.
    * @return an instance of the specified type
    * @throws IllegalArgumentException
    *            if the wrapped context is not assignable from the specified class.
    * @see #getWrappedType()
    */
   <C extends Closeable> C unwrap(TypeToken<C> type) throws IllegalArgumentException;
   
   /**
    * shortcut for {@code unwrap(TypeToken.of(clazz))}
    * @see #unwrap(TypeToken)
    */
   <C extends Closeable> C unwrap(Class<C> clazz) throws IllegalArgumentException;
   
   /**
    * shortcut for {@code unwrap(getWrappedType())}
    * 
    * @throws ClassCastException
    *            if the user supplied {@code C} param is not assignableFrom
    *            {@link #getWrappedType()}
    */
   <C extends Closeable> C unwrap() throws ClassCastException;

}