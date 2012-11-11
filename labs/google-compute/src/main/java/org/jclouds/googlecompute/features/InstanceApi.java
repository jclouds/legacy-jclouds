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
import org.jclouds.googlecompute.domain.Instance;
import org.jclouds.googlecompute.domain.InstanceNetworkInterfaceAccessConfig;
import org.jclouds.googlecompute.domain.InstanceSerialPortOutput;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Instances via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see InstanceAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances/get"/>
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface InstanceApi {

   /**
    * Returns the specified instance resource.
    *
    * @param projectName  name of the project scoping this request.
    * @param instanceName name of the instance resource to return.
    * @return an Instance resource
    */
   public Instance get(String projectName, String instanceName);

   /**
    * Creates a instance resource in the specified project using the data included in the request.
    *
    * @param projectName name of the project scoping this request.
    * @param instance    the instance to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   public Operation insert(String projectName, Instance instance);

   /**
    * Deletes the specified instance resource.
    *
    * @param projectName  name of the project scoping this request.
    * @param instanceName name of the instance resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation delete(String projectName, String instanceName);

   /**
    * Retrieves the list of instance resources available to the specified project.
    *
    * @param projectName name of the project scoping this request.
    * @param listOptions listing options @see ListOptions for details on how to build filters and use pagination
    * @return a collection that might be paginated
    * @see org.jclouds.googlecompute.options.ListOptions , org.jclouds.googlecompute.domain.PagedList ,PagedIterable
    */
   public PagedIterable<Instance> list(String projectName, @Nullable ListOptions listOptions);

   /**
    * Adds an access config to an instance's network interface.
    *
    * @param projectName          name of the project scoping this request.
    * @param instanceName         the instance name.
    * @param networkInterfaceName network interface name.
    * @param accessConfig         the InstanceNetworkInterfaceAccessConfig to add.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation addAccessConfig(String projectName, String instanceName,
                                    String networkInterfaceName, InstanceNetworkInterfaceAccessConfig accessConfig);

   /**
    * Deletes an access config from an instance's network interface.
    *
    * @param projectName          name of the project scoping this request.
    * @param instanceName         the instance name.
    * @param networkInterfaceName network interface name.
    * @param accessConfigName     the name of the access config to delete
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the instance did not exist the result is null.
    */
   public Operation deleteAccessConfig(String projectName, String instanceName,
                                       String networkInterfaceName, String accessConfigName);

   /**
    * Returns the specified instance's serial port output.
    *
    * @param projectName  name of the project scoping this request.
    * @param instanceName the instance name.
    * @return if successful, this method returns a InstanceSerialPortOutput containing the instance's serial output.
    */
   public InstanceSerialPortOutput serialPort(String projectName, String instanceName);
}
