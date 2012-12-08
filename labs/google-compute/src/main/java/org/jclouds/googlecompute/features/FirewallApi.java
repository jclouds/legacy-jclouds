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
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Firewalls via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see FirewallAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
 *
 * TODO support PATCH
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface FirewallApi {

   /**
    * Returns the specified image resource.
    *
    * @param projectName  name of the project scoping this request.
    * @param firewallName name of the firewall resource to return.
    * @return an Firewall resource
    */
   public Firewall get(String projectName, String firewallName);

   /**
    * Creates a firewall resource in the specified project using the data included in the request.
    *
    * @param projectName name of the project scoping this request.
    * @param firewall    the firewall to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation insert(String projectName, Firewall firewall);

   /**
    * Updates the specified firewall resource with the data included in the request.
    *
    * @param projectName  name of the project scoping this request.
    * @param firewallName the name firewall to be patched.
    * @param firewall     the firewall patch.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation update(String projectName, String firewallName, Firewall firewall);

//   /**
//    * Updates the specified firewall resource with the data included in the request. This method supports patch
//    * semantics.
//    *
//    * @param projectName  name of the project scoping this request.
//    * @param firewallName the name firewall to be patched.
//    * @param firewall     the firewall patch.
//    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
//    *         you, and look for the status field.
//    */
//   public Operation patch(String projectName, String firewallName, Firewall firewall);

   /**
    * Deletes the specified image resource.
    *
    * @param projectName name of the project scoping this request.
    * @param imageName   name of the firewall resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   public Operation delete(String projectName, String imageName);

   /**
    * Retrieves the list of firewall resources available to the specified project.
    *
    * @param projectName name of the project scoping this request.
    * @param listOptions listing options @see ListOptions for details on how to build filters and use pagination
    * @return a collection that might be paginated
    * @see org.jclouds.googlecompute.options.ListOptions , org.jclouds.googlecompute.domain.PagedList ,PagedIterable
    */
   public PagedIterable<Firewall> list(String projectName, @Nullable ListOptions listOptions);

}
