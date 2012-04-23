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
package org.jclouds.apis;

import java.util.Properties;

import org.jclouds.Context;
import org.jclouds.Wrapper;

import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseWrapperLiveTest<W extends Wrapper> extends BaseContextLiveTest<Context>{

   protected volatile W wrapper;

   @Override
   protected void initializeContext() {
      Closeables.closeQuietly(context);
      wrapper = createWrapper(setupProperties(), setupModules());
      context = wrapper.unwrap();
   }

   protected abstract TypeToken<W> wrapperType();
   
   @Override
   protected TypeToken<Context> contextType() {
     return TypeToken.of(Context.class);
   }
   
   protected W createWrapper(Properties props, Iterable<Module> modules) {
      return newBuilder().modules(modules).overrides(props).buildAndWrapWith(wrapperType());
   }

}
