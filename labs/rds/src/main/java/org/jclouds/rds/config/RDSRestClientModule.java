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
package org.jclouds.rds.config;


import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import org.jclouds.aws.config.FormSigningRestClientModule;
import org.jclouds.rds.RDSApi;
import org.jclouds.rds.RDSAsyncApi;
import org.jclouds.rds.features.InstanceApi;
import org.jclouds.rds.features.InstanceAsyncApi;
import org.jclouds.rds.features.SecurityGroupApi;
import org.jclouds.rds.features.SecurityGroupAsyncApi;
import org.jclouds.rds.features.SubnetGroupApi;
import org.jclouds.rds.features.SubnetGroupAsyncApi;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the RDS connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class RDSRestClientModule extends FormSigningRestClientModule<RDSApi, RDSAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(InstanceApi.class, InstanceAsyncApi.class)
            .put(SecurityGroupApi.class, SecurityGroupAsyncApi.class)
            .put(SubnetGroupApi.class, SubnetGroupAsyncApi.class)
            .build();

   public RDSRestClientModule() {
      super(typeToken(RDSApi.class), typeToken(RDSAsyncApi.class), DELEGATE_MAP);
   }
}
