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
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Operations via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/operations"/>
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface OperationApi {

   /**
    * Retrieves the specified operation resource.
    *
    * @param projectName   name of the project scoping this request.
    * @param operationName name of the operation resource to return.
    * @return If successful, this method returns an Operation resource
    */
   public Operation get(String projectName, String operationName);

   /**
    * Deletes the specified operation resource.
    *
    * @param projectName   name of the project scoping this request.
    * @param operationName name of the operation resource to delete.
    */
   public void delete(String projectName, String operationName);

   /**
    * Retrieves the list of operation resources contained within the specified project.
    *
    * @param projectName name of the project scoping this request.
    * @param listOptions listing options @see ListOptions for details on how to build filters and use pagination
    * @return a collection that might be paginated
    * @see ListOptions, org.jclouds.googlecompute.domain.PagedList ,PagedIterable
    */
   public PagedIterable<Operation> list(String projectName, ListOptions listOptions);
}
