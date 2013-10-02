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
package org.jclouds.compute.internal;

import static org.jclouds.reflect.Reflection2.typeToken;

import org.jclouds.compute.ComputeServiceContext;

import com.google.common.reflect.TypeToken;

/**
 * @author Jason King, Adrian Cole
 */
public abstract class BaseComputeServiceContextLiveTest extends BaseGenericComputeServiceContextLiveTest<ComputeServiceContext> {

   @Override
   protected TypeToken<ComputeServiceContext> viewType() {
      return typeToken(ComputeServiceContext.class);
   }
   
}
