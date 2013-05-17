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
package org.jclouds.rackspace.cloudservers.uk.config;

import java.util.Map;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.openstack.nova.v2_0.compute.config.NovaComputeServiceContextModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
public class CloudServersUKComputeServiceContextModule extends NovaComputeServiceContextModule {

   /**
    * CloudServers images are accessible via the root user, not ubuntu
    */
   @Override
   protected Map<OsFamily, LoginCredentials> osFamilyToCredentials(Injector injector) {
      return ImmutableMap.of(OsFamily.WINDOWS, LoginCredentials.builder().user("Administrator").build(),
               OsFamily.UBUNTU, LoginCredentials.builder().user("root").build());
   }

}
