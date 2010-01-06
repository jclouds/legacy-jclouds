/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.domain;

import java.net.InetAddress;
import java.util.Set;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

/**
 * A virtual application (vApp) is a software solution, packaged in OVF containing one or more
 * virtual machines. A vApp can be authored by Developers at ISVs and VARs or by IT Administrators
 * in Enterprises and Service Providers.
 * 
 * @author Adrian Cole
 */
public interface VApp extends NamedResource {

   VAppStatus getStatus();

   Long getSize();

   ListMultimap<String, InetAddress> getNetworkToAddresses();

   String getOperatingSystemDescription();

   VirtualSystem getSystem();

   Set<ResourceAllocation> getResourceAllocations();

   Multimap<ResourceType, ResourceAllocation> getResourceAllocationByType();

}