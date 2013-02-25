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
package org.jclouds.iam.config;

import java.util.Map;

import org.jclouds.aws.config.FormSigningRestClientModule;
import org.jclouds.iam.IAMAsyncApi;
import org.jclouds.iam.IAMApi;
import org.jclouds.iam.features.InstanceProfileApi;
import org.jclouds.iam.features.InstanceProfileAsyncApi;
import org.jclouds.iam.features.RoleApi;
import org.jclouds.iam.features.RoleAsyncApi;
import org.jclouds.iam.features.RolePolicyApi;
import org.jclouds.iam.features.RolePolicyAsyncApi;
import org.jclouds.iam.features.UserApi;
import org.jclouds.iam.features.UserAsyncApi;
import org.jclouds.iam.features.UserApi;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

/**
 * Configures the Monitoring connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class IAMRestClientModule extends FormSigningRestClientModule<IAMApi, IAMAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(UserApi.class, UserAsyncApi.class)
         .put(RoleApi.class, RoleAsyncApi.class)
         .put(RolePolicyApi.class, RolePolicyAsyncApi.class)
         .put(InstanceProfileApi.class, InstanceProfileAsyncApi.class)
         .build();
   
   public IAMRestClientModule() {
      super(TypeToken.of(IAMApi.class), TypeToken.of(IAMAsyncApi.class), DELEGATE_MAP);
   }
}
