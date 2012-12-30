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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.options.CreatePodOptions;
import org.jclouds.cloudstack.options.ListPodsOptions;
import org.jclouds.cloudstack.options.UpdatePodOptions;

/**
 * Provides synchronous access to CloudStack Pod features available to Global
 * Admin users.
 * 
 * @author Richard Downer
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface GlobalPodClient {

   /**
    * Lists pods
    *
    * @param options
    *           if present, how to constrain the list.
    * @return pods matching query, or empty set, if no pods are found
    */
   Set<Pod> listPods(ListPodsOptions... options);

   /**
    * get a specific pod by id
    *
    * @param id
    *           pod to get
    * @return pod or null if not found
    */
   Pod getPod(String id);

   /**
    * Creates a new Pod.
    *
    * @param name the name of the Pod
    * @param zoneId the Zone ID in which the Pod will be created
    * @param startIp the starting IP address for the Pod
    * @param endIp the ending IP address for the Pod
    * @param gateway the gateway for the Pod
    * @param netmask the netmask for the Pod
    * @param createPodOptions optional arguments
    * @return the new Pod
    */
   Pod createPod(String name, String zoneId, String startIp, String endIp, String gateway, String netmask, CreatePodOptions... createPodOptions);

   /**
    * Creates a new Pod.
    *
    * @param name the name of the Pod
    * @param zoneId the Zone ID in which the Pod will be created
    * @param startIp the starting IP address for the Pod
    * @param gateway the gateway for the Pod
    * @param netmask the netmask for the Pod
    * @param createPodOptions optional arguments
    * @return the new Pod
    */
   Pod createPod(String name, String zoneId, String startIp, String gateway, String netmask, CreatePodOptions... createPodOptions);

   /**
    * Deletes a Pod.
    * @param id the ID of the Pod
    */
   void deletePod(String id);

   /**
    * Updates a Pod.
    * @param id the ID of the Pod
    * @param updatePodOptions optional arguments
    * @return the updated pod
    */
   Pod updatePod(String id, UpdatePodOptions... updatePodOptions);

}
