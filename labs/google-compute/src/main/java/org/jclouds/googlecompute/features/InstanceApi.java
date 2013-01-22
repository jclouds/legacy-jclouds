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
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.domain.InstanceTemplate;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to Instances via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see InstanceAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances/get"/>
 */
public interface InstanceApi {

   /**
    * Returns the specified instance resource.
    *
    * @param instanceName name of the instance resource to return.
    * @return an Instance resource
    */
   public Instance get(String instanceName);

   /**
    * Creates a instance resource in the specified project using the data included in the request.
    *
    * @param instanceName this name of the instance to be created
    * @param template the instance template
    * @param zone the name of the zone where the instance will be created
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation createInZone(String instanceName, InstanceTemplate template, String zone);

   /**
    * Deletes the specified instance resource.
    *
    * @param instanceName name of the instance resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation delete(String instanceName);

   /**
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Instance> listFirstPage();

   /**
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Instance> listAtMarker(@Nullable String marker);

   /**
    * Retrieves the list of instance resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecompute.domain.ListPage
    */
   public ListPage<Instance> listAtMarker(@Nullable String marker, @Nullable ListOptions listOptions);

   /**
    * @see InstanceApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Instance> list();

   /**
    * A paged version of InstanceApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see InstanceApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Instance> list(@Nullable ListOptions listOptions);

   /**
    * Adds an access config to an instance's network interface.
    *
    * @param instanceName         the instance name.
    * @param accessConfig         the AccessConfig to add.
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation addAccessConfigToNic(String instanceName, Instance.NetworkInterface.AccessConfig accessConfig,
                                         String networkInterfaceName);

   /**
    * Deletes an access config from an instance's network interface.
    *
    * @param instanceName         the instance name.
    * @param accessConfigName     the name of the access config to delete
    * @param networkInterfaceName network interface name.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation deleteAccessConfigFromNic(String instanceName, String accessConfigName,
                                              String networkInterfaceName);

   /**
    * Returns the specified instance's serial port output.
    *
    * @param instanceName the instance name.
    * @return if successful, this method returns a SerialPortOutput containing the instance's serial output.
    */
   public Instance.SerialPortOutput getSerialPortOutput(String instanceName);
}
