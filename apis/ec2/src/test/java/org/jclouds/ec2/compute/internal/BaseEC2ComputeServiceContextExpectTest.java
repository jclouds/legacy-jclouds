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
package org.jclouds.ec2.compute.internal;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.ec2.internal.BaseEC2ApiExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;

import com.google.common.base.Function;
import com.google.inject.Module;

public abstract class BaseEC2ComputeServiceContextExpectTest<T> extends BaseEC2ApiExpectTest<T> implements
         Function<ComputeServiceContext, T> {

   @Override
   public T createClient(Function<HttpRequest, HttpResponse> fn, Module module, Properties props) {
      return apply(createComputeServiceContext(fn, module, props));
   }

   private ComputeServiceContext createComputeServiceContext(Function<HttpRequest, HttpResponse> fn, Module module,
            Properties props) {
      return createInjector(fn, module, props).getInstance(ComputeServiceContext.class);
   }

}
