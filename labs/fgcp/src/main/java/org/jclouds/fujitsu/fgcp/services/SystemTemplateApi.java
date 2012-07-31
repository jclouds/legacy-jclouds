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
package org.jclouds.fujitsu.fgcp.services;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.fujitsu.fgcp.domain.VSystemDescriptor;

/**
 * API relating to system templates, also referred to as virtual system
 * descriptors.
 * 
 * @author Dies Koper
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface SystemTemplateApi {

    VSystemDescriptor get(String id);

    void update(String id);

    void deregisterSystem(String id);

    void deregisterPrivateSystem(String id);

    /*
     * GetVSYSDescriptorAttributes GetVSYSDescriptorConfiguration
     * 
     * UnregisterPrivateVSYSDescriptor UnregisterVSYSDescriptor
     * UpdateVSYSDescriptorAttribute
     */
}
