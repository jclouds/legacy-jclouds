/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.chef;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.rest.RestContextBuilder;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;

import org.jclouds.chef.config.ChefContextModule;
import org.jclouds.chef.config.ChefRestClientModule;
import org.jclouds.chef.reference.ChefConstants;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class ChefContextBuilder extends RestContextBuilder<ChefAsyncClient, ChefClient> {

   public ChefContextBuilder(String providerName, Properties props) {
      super(providerName, new TypeLiteral<ChefAsyncClient>() {
      }, new TypeLiteral<ChefClient>() {
      }, props);
      checkNotNull(properties.getProperty(ChefConstants.PROPERTY_CHEF_IDENTITY));
      checkNotNull(properties.getProperty(ChefConstants.PROPERTY_CHEF_RSA_KEY));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new ChefRestClientModule());
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new ChefContextModule(providerName));
   }

}
