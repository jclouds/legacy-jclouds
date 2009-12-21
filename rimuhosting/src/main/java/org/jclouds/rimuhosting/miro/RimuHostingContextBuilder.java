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
package org.jclouds.rimuhosting.miro;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rimuhosting.miro.config.RimuHostingContextModule;
import org.jclouds.rimuhosting.miro.config.RimuHostingRestClientModule;
import org.jclouds.rimuhosting.miro.reference.RimuHostingConstants;

import java.util.List;
import java.util.Properties;

/**
 * @author Adrian Cole
 */
public class RimuHostingContextBuilder extends RestContextBuilder<RimuHostingAsyncClient, RimuHostingClient> {

   public RimuHostingContextBuilder(Properties props) {
      super(new TypeLiteral<RimuHostingAsyncClient>() {
      }, new TypeLiteral<RimuHostingClient>() {
      }, props);
      checkNotNull(properties.getProperty(RimuHostingConstants.PROPERTY_RIMUHOSTING_APIKEY));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new RimuHostingRestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new RimuHostingContextModule());
   }

}
