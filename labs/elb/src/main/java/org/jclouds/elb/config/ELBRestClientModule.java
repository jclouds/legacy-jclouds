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
package org.jclouds.elb.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import org.jclouds.aws.config.FormSigningRestClientModule;
import org.jclouds.elb.ELBApi;
import org.jclouds.elb.ELBAsyncApi;
import org.jclouds.elb.features.AvailabilityZoneApi;
import org.jclouds.elb.features.AvailabilityZoneAsyncApi;
import org.jclouds.elb.features.InstanceApi;
import org.jclouds.elb.features.InstanceAsyncApi;
import org.jclouds.elb.features.LoadBalancerApi;
import org.jclouds.elb.features.LoadBalancerAsyncApi;
import org.jclouds.elb.features.PolicyApi;
import org.jclouds.elb.features.PolicyAsyncApi;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the ELB connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class ELBRestClientModule extends FormSigningRestClientModule<ELBApi, ELBAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(LoadBalancerApi.class, LoadBalancerAsyncApi.class)
            .put(PolicyApi.class, PolicyAsyncApi.class)
            .put(InstanceApi.class, InstanceAsyncApi.class)
            .put(AvailabilityZoneApi.class, AvailabilityZoneAsyncApi.class)
            .build();

   public ELBRestClientModule() {
      super(typeToken(ELBApi.class), typeToken(ELBAsyncApi.class), DELEGATE_MAP);
   }
}
