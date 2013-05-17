/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.domain;

import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VDCImpl;
import org.jclouds.trmk.vcloud_0_8.endpoints.Catalog;
import org.jclouds.trmk.vcloud_0_8.endpoints.InternetServices;
import org.jclouds.trmk.vcloud_0_8.endpoints.PublicIPs;

import com.google.inject.ImplementedBy;

/**
 * A vDC is a deployment environment for vApps. A Vdc element provides a user
 * view of a vDC.
 * 
 * @author Adrian Cole
 */
@org.jclouds.trmk.vcloud_0_8.endpoints.VDC
@ImplementedBy(VDCImpl.class)
public interface VDC extends ReferenceType {
   @Nullable
   String getDescription();

   /**
    * container for ResourceEntity elements
    * 
    * @since vcloud api 0.8
    */
   Map<String, ReferenceType> getResourceEntities();

   /**
    * container for OrgNetwork elements that represent organization networks
    * contained by the vDC
    * 
    * @since vcloud api 0.8
    */
   Map<String, ReferenceType> getAvailableNetworks();

   @Catalog
   ReferenceType getCatalog();

   @PublicIPs
   ReferenceType getPublicIps();

   @InternetServices
   ReferenceType getInternetServices();

}
