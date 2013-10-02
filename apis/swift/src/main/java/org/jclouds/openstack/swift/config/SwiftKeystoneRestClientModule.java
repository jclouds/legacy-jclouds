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
package org.jclouds.openstack.swift.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.SwiftKeystoneAsyncClient;
import org.jclouds.openstack.swift.SwiftKeystoneClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;

/**
 *
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class SwiftKeystoneRestClientModule extends SwiftRestClientModule<SwiftKeystoneClient, SwiftKeystoneAsyncClient> {

   public SwiftKeystoneRestClientModule() {
      super(typeToken(SwiftKeystoneClient.class), typeToken(SwiftKeystoneAsyncClient.class), ImmutableMap
               .<Class<?>, Class<?>> of());
   }

   protected void bindResolvedClientsToCommonSwift() {
      bind(CommonSwiftClient.class).to(SwiftKeystoneClient.class).in(Scopes.SINGLETON);
      bind(CommonSwiftAsyncClient.class).to(SwiftKeystoneAsyncClient.class).in(Scopes.SINGLETON);
  }
}
