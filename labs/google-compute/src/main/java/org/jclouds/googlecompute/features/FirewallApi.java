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
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to Firewalls via their REST API.
 * <p/>
 * TODO support PATCH
 * (GCE uses PATCH as a Http method. Using this method is the only way to partially update a firewall.)
 *
 * @author David Alves
 * @see FirewallAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/firewalls"/>
 */
public interface FirewallApi {

   /**
    * Returns the specified image resource.
    *
    * @param firewallName name of the firewall resource to return.
    * @return an Firewall resource
    */
   Firewall get(String firewallName);

   /**
    * Creates a firewall resource in the specified project using the data included in the request.
    *
    *
    * @param firewall the firewall to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation create(Firewall firewall);

   /**
    * Updates the specified firewall resource with the data included in the request.
    *
    * @param firewallName the name firewall to be updated.
    * @param firewall     the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation update(String firewallName, Firewall firewall);

   /**
    * Updates the specified firewall resource, with patch semantics, with the data included in the request.
    *
    * @param firewallName the name firewall to be updated.
    * @param firewall     the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation patch(String firewallName, Firewall firewall);

   /**
    * Deletes the specified image resource.
    *
    * @param imageName name of the firewall resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   Operation delete(String imageName);

   /**
    * @see FirewallApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<Firewall> listFirstPage();

   /**
    * @see FirewallApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<Firewall> listAtMarker(@Nullable String marker);

   /**
    * Retrieves the list of firewall resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecompute.domain.ListPage
    */
   ListPage<Firewall> listAtMarker(@Nullable String marker, @Nullable ListOptions listOptions);

   /**
    * @see FirewallApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Firewall> list();

   /**
    * A paged version of FirewallApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see FirewallApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<Firewall> list(@Nullable ListOptions listOptions);


}
