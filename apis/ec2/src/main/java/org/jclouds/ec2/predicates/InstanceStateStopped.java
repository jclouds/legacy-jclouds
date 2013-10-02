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
package org.jclouds.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.features.InstanceApi;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a task succeeds.
 * 
 * @author Adrian Cole
 */
@Singleton
public class InstanceStateStopped implements Predicate<RunningInstance> {

   private final InstanceApi client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public InstanceStateStopped(InstanceApi client) {
      this.client = client;
   }

   public boolean apply(RunningInstance instance) {
      logger.trace("looking for state on instance %s", instance);
      instance = refresh(instance);
      logger.trace("%s: looking for instance state %s: currently: %s", instance.getId(),
               InstanceState.STOPPED, instance.getInstanceState());
      return instance.getInstanceState() == InstanceState.STOPPED;
   }

   private RunningInstance refresh(RunningInstance instance) {
      return Iterables.getOnlyElement(Iterables.getOnlyElement(client.describeInstancesInRegion(
               instance.getRegion(), instance.getId())));
   }
}
