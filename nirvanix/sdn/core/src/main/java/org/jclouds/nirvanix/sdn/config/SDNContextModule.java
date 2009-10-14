/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.cloud.internal.CloudContextImpl;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.nirvanix.sdn.SDN;
import org.jclouds.nirvanix.sdn.SDNConnection;
import org.jclouds.nirvanix.sdn.SDNContext;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

@RequiresHttp
public class SDNContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(SDNContext.class).to(SDNContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class SDNContextImpl extends CloudContextImpl<SDNConnection> implements SDNContext {
      @Inject
      public SDNContextImpl(Closer closer, SDNConnection defaultApi, @SDN URI endPoint,
               @Named(SDNConstants.PROPERTY_SDN_USERNAME) String account) {
         super(closer, defaultApi, endPoint, account);
      }
   }

}