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

import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.Project;

import java.util.Map;
/**
 * Provides synchronous access to Projects via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see ProjectAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/projects"/>
 */
public interface ProjectApi {

   /**
    * Returns the specified project resource.
    *
    * @param projectName name of the project to return
    * @return if successful, this method returns a Project resource
    */
   Project get(String projectName);

   /**
    * Sets metadata common to all instances within the specified project using the data included in the request.
    * <p/>
    * NOTE: This *sets* metadata items on the project (vs *adding* items to metadata),
    * if there are pre-existing metadata items that must be kept these must be fetched first and then re-set on the
    * new Metadata, e.g.
    * <pre><tt>
    *    Metadata.Builder current = projectApi.get("myProject").getCommonInstanceMetadata().toBuilder();
    *    current.addItem("newItem","newItemValue");
    *    projectApi.setCommonInstanceMetadata(current.build());
    * </tt></pre>
    *
    * @param projectName            name of the project to return
    * @param commonInstanceMetadata the metadata to set
    * @return an Operations resource. To check on the status of an operation, poll the Operations resource returned
    *         to you, and look for the status field.
    */
   Operation setCommonInstanceMetadata(String projectName, Map<String, String> commonInstanceMetadata);

}
