/*
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

package org.jclouds.googlecompute.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.options.ListOptions;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to MachineTypes via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/machineTypes"/>
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface MachineTypeApi {

   /**
    * Returns the specified machine type resource
    *
    * @param projectName     name of the project scoping this request.
    * @param machineTypeName name of the machine type resource to return.
    * @return If successful, this method returns a MachineType resource
    */
   public MachineType get(String projectName, String machineTypeName);

   /**
    * Retrieves the list of machine type resources available to the specified project.
    *
    * @param projectName name of the project scoping this request.
    * @param listOptions listing options @see ListOptions for details on how to build filters and use pagination
    * @return a collection that might be paginated
    * @see ListOptions, org.jclouds.googlecompute.domain.PagedList ,PagedIterable
    */
   public PagedIterable<MachineType> list(String projectName, ListOptions listOptions);
}
