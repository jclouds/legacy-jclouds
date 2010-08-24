/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.internal.VCloudExpressVAppImpl;

import com.google.common.collect.ListMultimap;
import com.google.inject.ImplementedBy;

/**
 * A virtual application (vApp) is a software solution, packaged in OVF containing one or more
 * virtual machines. A vApp can be authored by Developers at ISVs and VARs or by IT Administrators
 * in Enterprises and Service Providers.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(VCloudExpressVAppImpl.class)
public interface VCloudExpressVApp extends ReferenceType {
   ReferenceType getVDC();

   Status getStatus();

   Long getSize();

   ListMultimap<String, String> getNetworkToAddresses();

   /**
    * 
    * @return CIM OSType of the image or null, if this information isn't available yet
    * @see <a href="http://dmtf.org/standards/cim/cim_schema_v2260">DMTF CIM model</a>
    */
   @Nullable
   Integer getOsType();

   /**
    * 
    * @return description or null, if this information isn't available yet
    */
   @Nullable
   String getOperatingSystemDescription();

   VirtualSystem getSystem();

   Set<ResourceAllocation> getResourceAllocations();

}