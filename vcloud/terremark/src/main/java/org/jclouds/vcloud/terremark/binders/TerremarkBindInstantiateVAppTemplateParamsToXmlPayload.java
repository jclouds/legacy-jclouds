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
package org.jclouds.vcloud.terremark.binders;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTCPUCOUNT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTMEMORY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULTNETWORK;
import static org.jclouds.vcloud.terremark.reference.TerremarkVCloudConstants.PROPERTY_TERREMARK_DEFAULTGROUP;
import static org.jclouds.vcloud.terremark.reference.TerremarkVCloudConstants.PROPERTY_TERREMARK_DEFAULTPASSWORD;
import static org.jclouds.vcloud.terremark.reference.TerremarkVCloudConstants.PROPERTY_TERREMARK_DEFAULTROW;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class TerremarkBindInstantiateVAppTemplateParamsToXmlPayload extends
         BindInstantiateVAppTemplateParamsToXmlPayload {

   @Inject
   public TerremarkBindInstantiateVAppTemplateParamsToXmlPayload(
            @Named("InstantiateVAppTemplateParams") String xmlTemplate,
            BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_DEFAULTNETWORK) String defaultNetwork,
            @Named(PROPERTY_VCLOUD_DEFAULTCPUCOUNT) String defaultCpuCount,
            @Named(PROPERTY_VCLOUD_DEFAULTMEMORY) String defaultMemory,
            @Named(PROPERTY_TERREMARK_DEFAULTGROUP) String defaultGroup,
            @Named(PROPERTY_TERREMARK_DEFAULTROW) String defaultRow,
            @Named(PROPERTY_TERREMARK_DEFAULTPASSWORD) String defaultPassword) {
      super(xmlTemplate, stringBinder, defaultNetwork, defaultCpuCount, defaultMemory);
      this.defaultParams.put("group", defaultGroup);
      this.defaultParams.put("row", defaultRow);
      this.defaultParams.put("password", defaultPassword);
   }

   @Override
   protected void addOptionsToMap(Map<String, String> postParams, GeneratedHttpRequest<?> gRequest) {
      super.addOptionsToMap(postParams, gRequest);
      for (Object arg : gRequest.getArgs()) {
         if (arg instanceof TerremarkInstantiateVAppTemplateOptions) {
            TerremarkInstantiateVAppTemplateOptions options = (TerremarkInstantiateVAppTemplateOptions) arg;
            if (options.getGroup() != null) {
               postParams.put("group", options.getGroup());
            }
            if (options.getRow() != null) {
               postParams.put("row", options.getRow());
            }
            if (options.getPassword() != null) {
               postParams.put("password", options.getPassword());
            }
         }
      }
   }

}
