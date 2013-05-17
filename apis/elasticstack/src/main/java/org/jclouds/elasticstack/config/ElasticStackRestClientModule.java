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
package org.jclouds.elasticstack.config;

import java.util.List;
import java.util.Map;

import org.jclouds.elasticstack.ElasticStackAsyncClient;
import org.jclouds.elasticstack.ElasticStackClient;
import org.jclouds.elasticstack.domain.Device;
import org.jclouds.elasticstack.domain.Drive;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveMetrics;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerMetrics;
import org.jclouds.elasticstack.functions.CreateDriveRequestToMap;
import org.jclouds.elasticstack.functions.DriveDataToMap;
import org.jclouds.elasticstack.functions.MapToDevices;
import org.jclouds.elasticstack.functions.MapToDriveMetrics;
import org.jclouds.elasticstack.functions.MapToNICs;
import org.jclouds.elasticstack.functions.MapToServerMetrics;
import org.jclouds.elasticstack.functions.ServerToMap;
import org.jclouds.elasticstack.functions.MapToDevices.DeviceToId;
import org.jclouds.elasticstack.handlers.ElasticStackErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

/**
 * Configures the elasticstack connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class ElasticStackRestClientModule extends RestClientModule<ElasticStackClient, ElasticStackAsyncClient> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Drive, Map<String, String>>>() {
      }).to(CreateDriveRequestToMap.class);
      bind(new TypeLiteral<Function<DriveData, Map<String, String>>>() {
      }).to(DriveDataToMap.class);
      bind(new TypeLiteral<Function<Map<String, String>, List<NIC>>>() {
      }).to(MapToNICs.class);
      bind(new TypeLiteral<Function<Map<String, String>, Map<String, ? extends Device>>>() {
      }).to(MapToDevices.class);
      bind(new TypeLiteral<Function<Map<String, String>, Map<String, ? extends DriveMetrics>>>() {
      }).to(MapToDriveMetrics.class);
      bind(new TypeLiteral<Function<Map<String, String>, ServerMetrics>>() {
      }).to(MapToServerMetrics.class);
      bind(new TypeLiteral<Function<Device, String>>() {
      }).to(DeviceToId.class);
      bind(new TypeLiteral<Function<Server, Map<String, String>>>() {
      }).to(ServerToMap.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ElasticStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ElasticStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ElasticStackErrorHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      // TODO
   }

}
