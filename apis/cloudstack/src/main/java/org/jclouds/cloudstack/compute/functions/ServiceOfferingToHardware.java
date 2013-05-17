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
package org.jclouds.cloudstack.compute.functions;

import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.ServiceOffering;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 */
@Singleton
public class ServiceOfferingToHardware implements Function<ServiceOffering, Hardware> {

   @Override
   public Hardware apply(ServiceOffering offering) {
      return new HardwareBuilder()
            .ids(offering.getId() + "")
            .name(offering.getName())
            .tags(offering.getTags())
            .processors(ImmutableList.of(new Processor(offering.getCpuNumber(), offering.getCpuSpeed())))
            .ram(offering.getMemory())//
            // TODO: hypervisor probably from zone?
            // TODO .volumes()
            // displayText
            // created
            // haSupport
            // storageType
            // TODO where's the location of this?
            .build();
   }

}
