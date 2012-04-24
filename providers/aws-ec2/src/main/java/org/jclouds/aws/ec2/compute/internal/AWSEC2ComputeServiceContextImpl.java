/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.ec2.compute.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeService;
import org.jclouds.aws.ec2.compute.AWSEC2ComputeServiceContext;
import org.jclouds.compute.Utils;
import org.jclouds.ec2.compute.internal.EC2ComputeServiceContextImpl;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2ComputeServiceContextImpl extends EC2ComputeServiceContextImpl implements
         AWSEC2ComputeServiceContext {
   @Inject
   public AWSEC2ComputeServiceContextImpl(@Provider Context backend,
            @Provider TypeToken<? extends Context> backendType, AWSEC2ComputeService computeService, Utils utils) {
      super(backend, backendType, computeService, utils);
   }

   @Override
   public AWSEC2ComputeService getComputeService() {
      return AWSEC2ComputeService.class.cast(super.getComputeService());
   }

}
