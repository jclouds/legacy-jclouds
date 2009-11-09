/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;

import org.jclouds.rest.domain.Link;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Catalog;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVDCImpl;
import org.jclouds.vcloud.terremark.endpoints.InternetServices;
import org.jclouds.vcloud.terremark.endpoints.PublicIPs;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@org.jclouds.vcloud.endpoints.VDC
@ImplementedBy(TerremarkVDCImpl.class)
public interface TerremarkVDC extends VDC {

   @Catalog
   Link getCatalog();

   @PublicIPs
   Link getPublicIps();

   @InternetServices
   Link getInternetServices();

   // TODO getDescription() // what is the type?
}