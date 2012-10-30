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
package org.jclouds.vcloud.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.ovf.Envelope;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.options.CaptureVAppOptions;
import org.jclouds.vcloud.options.CloneVAppTemplateOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VApp Template functionality in vCloud
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VAppTemplateClient {
   /**
    * returns the vapp template corresponding to a catalog item in the catalog associated with the
    * specified name. Note that the org and catalog parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that isn't present
    */
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName, String itemName);

   /**
    */
   VApp createVAppInVDCByInstantiatingTemplate(String appName, URI vDC, URI template,
            InstantiateVAppTemplateOptions... options);

   Task copyVAppTemplateToVDCAndName(URI sourceVAppTemplate, URI vDC, String newName,
            CloneVAppTemplateOptions... options);

   Task moveVAppTemplateToVDCAndRename(URI sourceVAppTemplate, URI vDC, String newName,
            CloneVAppTemplateOptions... options);

   /**
    * The captureVApp request creates a vApp template from an instantiated vApp. <h4>Note</h4>
    * Before it can be captured, a vApp must be undeployed
    * 
    * @param targetVdcHref
    * @param sourceVAppHref
    * @param newTemplateName
    * @param options
    * @return template in progress
    */
   VAppTemplate captureVAppAsTemplateInVDC(URI sourceVAppHref, String newTemplateName, URI targetVdcHref,
            CaptureVAppOptions... options);

   VAppTemplate getVAppTemplate(URI vApp);

   Envelope getOvfEnvelopeForVAppTemplate(URI vAppTemplate);

   /**
    * delete a vAppTemplate, vApp, or media image. You cannot delete an object if it is in use. Any
    * object that is being copied or moved is in use. Other criteria that determine whether an
    * object is in use depend on the object type.
    * <ul>
    * <li>A vApptemplate is in use if it is being instantiated. After instantiation is complete, the
    * template is no longer in use.</li>
    * <li>A vApp is in use if it is deployed.</li>
    * <li>A media image is in use if it is inserted in a Vm.</li>
    * </ul>
    * 
    * @param id
    *           href of the vApp
    * @return task of the operation in progress
    */
   Task deleteVAppTemplate(URI id);
}
