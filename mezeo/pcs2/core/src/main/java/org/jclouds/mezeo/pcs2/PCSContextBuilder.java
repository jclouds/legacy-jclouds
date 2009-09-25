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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.cloud.CloudContext;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.mezeo.pcs2.config.RestPCSCloudModule;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public abstract class PCSContextBuilder<X extends CloudContext<?>> extends CloudContextBuilder<X> {

   public PCSContextBuilder(Properties props) {
      super(props);
   }

   public void authenticate(String id, String secret) {
      checkNotNull(properties.getProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT));
      properties.setProperty(PCSConstants.PROPERTY_PCS2_USER, checkNotNull(id, "user"));
      properties.setProperty(PCSConstants.PROPERTY_PCS2_PASSWORD, checkNotNull(secret, "key"));
   }

   protected void addApiModule(List<Module> modules) {
      modules.add(new RestPCSCloudModule());
   }

}
