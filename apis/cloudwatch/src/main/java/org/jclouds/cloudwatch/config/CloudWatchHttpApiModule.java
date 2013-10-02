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
package org.jclouds.cloudwatch.config;

import com.google.common.collect.ImmutableMap;
import org.jclouds.aws.config.FormSigningHttpApiModule;
import org.jclouds.cloudwatch.CloudWatchApi;
import org.jclouds.cloudwatch.features.AlarmApi;
import org.jclouds.cloudwatch.features.MetricApi;
import org.jclouds.cloudwatch.handlers.CloudWatchErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresHttpApi;

import java.util.Map;

import static org.jclouds.reflect.Reflection2.typeToken;

/**
 * Configures the Monitoring connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public class CloudWatchHttpApiModule extends FormSigningHttpApiModule<CloudWatchApi> {
   public CloudWatchHttpApiModule() {
      super(CloudWatchApi.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudWatchErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudWatchErrorHandler.class);
   }

}
