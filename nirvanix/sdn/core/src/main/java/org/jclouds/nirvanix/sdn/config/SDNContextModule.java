/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.nirvanix.sdn.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.nirvanix.sdn.SDN;
import org.jclouds.nirvanix.sdn.SDNClient;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@RequiresHttp
public class SDNContextModule extends AbstractModule {
   @Override
   protected void configure() {
      // for converters to work.
      install(new BlobStoreObjectModule());
   }

   @Provides
   @Singleton
   RestContext<SDNClient> provideContext(Closer closer, SDNClient defaultApi, @SDN URI endPoint,
            @Named(SDNConstants.PROPERTY_SDN_USERNAME) String account) {
      return new RestContextImpl<SDNClient>(closer, defaultApi, endPoint, account);
   }

}