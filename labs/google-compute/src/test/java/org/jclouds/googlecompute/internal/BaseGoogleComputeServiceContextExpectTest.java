/*
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

package org.jclouds.googlecompute.internal;

import com.google.common.base.Function;
import com.google.inject.Module;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.googlecompute.GoogleComputeApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

import java.util.Properties;

/**
 * @author David Alves
 */
public abstract class BaseGoogleComputeServiceContextExpectTest<T> extends BaseGoogleComputeExpectTest<T> implements
        Function<ComputeServiceContext, T> {


   @Override
   public T createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return apply(createComputeServiceContext(fn, module, props));
   }

   private ComputeServiceContext createComputeServiceContext(Function<HttpRequest, HttpResponse> fn, Module module,
                                                             Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeServiceContext.class);
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new GoogleComputeApiMetadata();
   }

}
