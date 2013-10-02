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
package org.jclouds.vcloud;

import java.io.Closeable;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.features.CatalogApi;
import org.jclouds.vcloud.features.NetworkApi;
import org.jclouds.vcloud.features.OrgApi;
import org.jclouds.vcloud.features.TaskApi;
import org.jclouds.vcloud.features.VAppApi;
import org.jclouds.vcloud.features.VAppTemplateApi;
import org.jclouds.vcloud.features.VDCApi;
import org.jclouds.vcloud.features.VmApi;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VCloudApi extends Closeable {
   /**
    * Provides asynchronous access to VApp Template features.
    * 
    */
   @Delegate
   VAppTemplateApi getVAppTemplateApi();

   /**
    * Provides synchronous access to VApp features.
    */
   @Delegate
   VAppApi getVAppApi();

   /**
    * Provides synchronous access to Vm features.
    */
   @Delegate
   VmApi getVmApi();

   /**
    * Provides synchronous access to Catalog features.
    */
   @Delegate
   CatalogApi getCatalogApi();

   /**
    * Provides synchronous access to Task features.
    */
   @Delegate
   TaskApi getTaskApi();

   /**
    * Provides synchronous access to VDC features.
    */
   @Delegate
   VDCApi getVDCApi();

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkApi getNetworkApi();

   /**
    * Provides synchronous access to Org features.
    */
   @Delegate
   OrgApi getOrgApi();

}
