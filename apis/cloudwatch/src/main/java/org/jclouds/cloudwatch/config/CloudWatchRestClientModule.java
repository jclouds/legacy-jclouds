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
package org.jclouds.cloudwatch.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.Map;

import org.jclouds.aws.config.FormSigningRestClientModule;
import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.CloudWatchAsyncApi;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.features.MetricAsyncApi;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the Monitoring connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudWatchRestClientModule extends FormSigningRestClientModule<CloudWatchApi, CloudWatchAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(MetricApi.class, MetricAsyncApi.class)
         .build();
   
   public CloudWatchRestClientModule() {
      super(typeToken(CloudWatchApi.class), typeToken(CloudWatchAsyncApi.class), DELEGATE_MAP);
   }

}
