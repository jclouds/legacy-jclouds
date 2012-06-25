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
import org.jclouds.iam.IAMAsyncClient;
import org.jclouds.iam.IAMClient;
import org.jclouds.iam.features.UserAsyncClient;
import org.jclouds.iam.features.UserClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

/**
 * Configures the Monitoring connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class IAMRestClientModule extends FormSigningRestClientModule<IAMClient, IAMAsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(UserClient.class, UserAsyncClient.class)
         .build();
   
   public IAMRestClientModule() {
      super(TypeToken.of(IAMClient.class), TypeToken.of(IAMAsyncClient.class), DELEGATE_MAP);
   }

}
