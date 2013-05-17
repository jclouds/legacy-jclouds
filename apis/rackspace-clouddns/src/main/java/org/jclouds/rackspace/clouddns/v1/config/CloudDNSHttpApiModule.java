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
package org.jclouds.rackspace.clouddns.v1.config;

import java.net.URI;

import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;

import com.google.common.base.Supplier;
import com.google.inject.TypeLiteral;

/**
 * Configures Rackspace Cloud DNS.
 * 
 * @author Everett Toews
 */
@ConfiguresHttpApi
public class CloudDNSHttpApiModule extends HttpApiModule<CloudDNSApi> {

   @Override
   protected void configure() {
      bind(new TypeLiteral<Supplier<URI>>() {}).annotatedWith(CloudDNS.class).to(new TypeLiteral<Supplier<URI>>() {});
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }
}
