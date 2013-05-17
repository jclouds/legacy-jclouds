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
package org.jclouds.rest.config;

import static com.google.common.base.Preconditions.checkState;
import static com.google.inject.name.Names.named;

import org.jclouds.reflect.Invocation;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;

/**
 * Allows the provider to supply a value set in a threadlocal.
 * 
 * @author Adrian Cole
 */
public class SetCaller {

   private final ThreadLocal<Invocation> caller = new ThreadLocal<Invocation>();

   public void enter(Invocation caller) {
      checkState(this.caller.get() == null, "A scoping block is already in progress");
      this.caller.set(caller);
   }

   public void exit() {
      checkState(caller.get() != null, "No scoping block in progress");
      caller.remove();
   }

   public static class Module extends AbstractModule {
      public void configure() {
         SetCaller delegateScope = new SetCaller();
         bind(CALLER_INVOCATION).toProvider(delegateScope.new CallerInvocationProvider());
         bind(SetCaller.class).toInstance(delegateScope);
      }
   }

   private static final Key<Invocation> CALLER_INVOCATION = Key.get(Invocation.class, named("caller"));

   class CallerInvocationProvider implements Provider<Invocation> {
      @Override
      public Invocation get() {
         return caller.get();
      }
   }

}
