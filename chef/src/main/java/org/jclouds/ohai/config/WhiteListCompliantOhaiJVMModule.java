/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ohai.config;

import java.util.Map;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.plugins.WhiteListCompliantJVM;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * Wires the components needed to parse ohai data without violating the GAE JVM
 * 
 * @author Adrian Cole
 */
@ConfiguresOhai
public class WhiteListCompliantOhaiJVMModule extends BaseOhaiJVMModule {

   @Override
   protected Iterable<Supplier<Map<String, JsonBall>>> suppliers(Injector injector) {
      return ImmutableList.<Supplier<Map<String, JsonBall>>> of(injector.getInstance(WhiteListCompliantJVM.class));
   }
}