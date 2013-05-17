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
package org.jclouds.trmk.ecloud.compute.config;

import org.jclouds.trmk.ecloud.compute.functions.TerremarkECloudParseOsFromVAppTemplateName;
import org.jclouds.trmk.ecloud.suppliers.TerremarkECloudInternetServiceAndPublicIpAddressSupplier;
import org.jclouds.trmk.vcloud_0_8.compute.config.TerremarkVCloudComputeServiceContextModule;
import org.jclouds.trmk.vcloud_0_8.compute.functions.ParseOsFromVAppTemplateName;
import org.jclouds.trmk.vcloud_0_8.suppliers.InternetServiceAndPublicIpAddressSupplier;

/**
 * @author Adrian Cole
 */
public class TerremarkECloudComputeServiceContextModule extends TerremarkVCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      bind(InternetServiceAndPublicIpAddressSupplier.class).to(
               TerremarkECloudInternetServiceAndPublicIpAddressSupplier.class);
      bind(ParseOsFromVAppTemplateName.class).to(TerremarkECloudParseOsFromVAppTemplateName.class);
      super.configure();
   }

}
