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
package org.jclouds.openstack.swift.internal;

import java.net.URI;
import java.util.List;

import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.base.Throwables;
import com.google.common.reflect.Invokable;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BasePayloadTest {
   protected Injector i = Guice.createInjector(Rocoto.expandVariables(new ConfigurationModule() {
      protected void bindConfigurations() {
         bindProperties(new SwiftApiMetadata().getDefaultProperties());
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      }
   }), new GsonModule());

   protected GeneratedHttpRequest<?> requestForArgs(List<Object> args) {
      try {
         Invocation invocation = Invocation.create(Invokable.from(String.class.getDeclaredMethod("toString")), args);
         return GeneratedHttpRequest.builder(String.class).method("POST").endpoint(URI.create("http://localhost/key"))
               .invocation(invocation).build();
      } catch (SecurityException e) {
         throw Throwables.propagate(e);
      } catch (NoSuchMethodException e) {
         throw Throwables.propagate(e);
      }
   }
}