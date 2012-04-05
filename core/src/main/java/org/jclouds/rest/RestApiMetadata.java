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
package org.jclouds.rest;

import org.jclouds.apis.ApiMetadata;

import com.google.common.annotations.Beta;

/**
 * 
 * @author Adrian Cole
 * @since 1.5
 */
@Beta
public interface RestApiMetadata<S, A, C extends RestContext<S, A>, M extends RestApiMetadata<S, A, C, M>> extends
      ApiMetadata<S, A, C, M> {

   public static interface Builder<S, A, C extends RestContext<S, A>, M extends RestApiMetadata<S, A, C, M>> extends
         ApiMetadata.Builder<S, A, C, M> {
   }

}