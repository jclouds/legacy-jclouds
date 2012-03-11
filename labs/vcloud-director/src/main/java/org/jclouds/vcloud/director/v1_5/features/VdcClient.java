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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.InstantiateVAppParamsType;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;

/**
 * Provides synchronous access to a vDC.
 * <p/>
 * 
 * @see VdcAsyncClient
 * @see <a href= "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID=" />
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VdcClient {

   /**
    * Retrieves a vdc.
    * 
    * @return the vdc or null if not found
    */
   Vdc getVdc(URI vdcUri);
   
   /**
    * Captures a vApp into vApp template. 
    * The status of vApp template will be in UNRESOLVED(0) until the capture task is finished.
    * 
    * @return a VApp resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the vApp.
    */
   VAppTemplate captureVApp(URI vdcUri, CaptureVAppParams params);
   
   /**
    * Clones a media into new one. 
    * The status of the returned media is UNRESOLVED(0) until the task for cloning finish.
    * 
    * @return a Media resource which will contain a task. 
    * The user should monitor the contained task status in order to check when it is completed.
    */
   Media cloneMedia(URI vdcUri, CloneMediaParams params);
   
   /**
    * Clones a vApp into new one. The status of vApp will be in UNRESOLVED(0) until the clone task is finished.
    * 
    * @return a VApp resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the vApp.
    */
   VApp cloneVApp(URI vdcUri, CloneVAppParams params);
   
   /**
    * Clones a vApp template into new one. 
    * The status of vApp template will be in UNRESOLVED(0) until the clone task is finished.
    * 
    * @return a VAppTemplate resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the VAppTemplate.
    */
   VAppTemplate cloneVAppTemplate(URI vdcUri, CloneVAppTemplateParams params);
   
   /**
    * Composes a new vApp using VMs from other vApps or vApp templates. The vCloud API supports 
    * composing a vApp from any combination of vApp templates, vApps, or virtual machines. 
    * When you compose a vApp, all children of each composition source become peers in the 
    * Children collection of the composed vApp. To compose a vApp, a client makes a compose 
    * vApp request whose body is a ComposeVAppParams element, includes the following information: 
    * - An InstantiationParams element that applies to the composed vApp itself and any vApp 
    * templates referenced in Item elements. - A SourcedItem element for each virtual machine, 
    * vApp, or vAppTemplate to include in the composition. Each SourcedItem can contain the 
    * following elements: - A required Source element whose href attribute value is a reference 
    * to a vApp template, vApp, or VM to include in the composition. If the Source element 
    * references a VM, the Item must also include an InstantiationParams element specific to 
    * that VM. - An optional NetworkAssignment element that specifies how the network connections 
    * of child VM elements are mapped to vApp networks in the parent. - If any of the composition 
    * items is subject to a EULA, the ComposeVAppParams element must include an AllEULAsAccepted 
    * element that has a value of true, indicating that you accept the EULA. Otherwise, composition 
    * fails. The composed vApp must be deployed and powered on before it can be used. The status 
    * of vApp will be UNRESOLVED(0) until the compose task is finished.
    * 
    * @return a VApp resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the vApp.
    */
   VApp composeVApp(URI vdcUri, ComposeVAppParams params);
   
   /**
    * Instantiate a vApp template into a new vApp. 
    * The status of vApp will be in UNRESOLVED(0) until the instantiate task is finished.
    * 
    * @return a VApp resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the vApp.
    */
   VApp instantiateVApp(URI vdcUri, InstantiateVAppParamsType<?> params);
   
   /**
    * Uploading vApp template to a vDC. The operation is separate on several steps: 
    * 1. creating empty vApp template entity 
    * 2. uploading an OVF of vApp template 
    * 3. uploading disks described from the OVF 
    * 4. finishing task for uploading 
    * The status of vApp template will be NOT_READY(0) until the ovf and all disks are uploaded 
    * to the transfer site. After this a task will run on the vApp template uploading.
    * 
    * @return a VAppTemplate resource which will contain a task. 
    * The user should should wait for this task to finish to be able to use the VAppTemplate.
    */
   VAppTemplate uploadVAppTemplate(URI vdcUri, UploadVAppTemplateParams params);
   
   /**
    * Creates a media (and present upload link for the floppy/iso file).
    * 
    * @return The response will return a link to transfer site to be able to continue with uploading the media.
    */
   Media createMedia(URI vdcUri, Media media);
   
   /**
    * @return synchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataClient.Readable getMetadataClient();

}
