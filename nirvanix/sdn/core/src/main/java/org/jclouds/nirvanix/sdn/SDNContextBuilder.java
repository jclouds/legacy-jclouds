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
package org.jclouds.nirvanix.sdn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.cloud.CloudContext;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.nirvanix.sdn.config.RestSDNAuthenticationModule;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public abstract class SDNContextBuilder<X extends CloudContext<?>> extends CloudContextBuilder<X> {

   public SDNContextBuilder(Properties props) {
      super(props);
      properties.setProperty(SDNConstants.PROPERTY_SDN_ENDPOINT, "http://services.nirvanix.com/ws");
   }

   public void authenticate(String id, String secret) {
      checkNotNull(properties.getProperty(SDNConstants.PROPERTY_SDN_APPKEY));
      properties.setProperty(SDNConstants.PROPERTY_SDN_USERNAME, checkNotNull(id, "user"));
      properties.setProperty(SDNConstants.PROPERTY_SDN_PASSWORD, checkNotNull(secret, "key"));
   }

   protected void addApiModule(List<Module> modules) {
      modules.add(new RestSDNAuthenticationModule());
   }

}
