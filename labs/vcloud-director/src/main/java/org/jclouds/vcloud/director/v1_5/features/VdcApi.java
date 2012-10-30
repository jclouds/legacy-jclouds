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
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.params.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.functions.href.VdcURNToHref;

/**
 * Provides synchronous access to a vDC.
 * 
 * @see VdcAsyncApi
 * @author danikov, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VdcApi {

   /**
    * Retrieves a vdc.
    * 
    * @return the vdc or null if not found
    */
   Vdc get(String vdcUrn);
   
   Vdc get(URI vdcHref);
   
   /**
    * Captures a vApp into vApp template.
    *
    * The status of vApp template will be in
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} until the
    * capture task is finished.
    * 
    * @return a VApp resource which will contain a task. The user should should wait for this task to finish to be able
    *         to use the vApp.
    */
   VAppTemplate captureVApp(String vdcUrn, CaptureVAppParams params);
   
   VAppTemplate captureVApp(URI vdcHref, CaptureVAppParams params);

   /**
    * Clones a media into new one.
    *
    * The status of the returned media is
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} until the task
    * for cloning finish.
    * 
    * @return a Media resource which will contain a task. The user should monitor the contained task status in order to
    *         check when it is completed.
    */
   Media cloneMedia(String vdcUrn, CloneMediaParams params);
   
   Media cloneMedia(URI vdcHref, CloneMediaParams params);

   /**
    * Clones a vApp into new one.
    *
    * The status of vApp will be in {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} until the clone task is finished.
    * 
    * @return a VApp resource which will contain a task. The user should should wait for this task to finish to be able
    *         to use the vApp.
    */
   VApp cloneVApp(String vdcUrn, CloneVAppParams params);
   
   VApp cloneVApp(URI vdcHref, CloneVAppParams params);

   /**
    * Clones a vApp template into new one.
    *
    * The status of vApp template will be in
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} until the clone
    * task is finished.
    * 
    * @return a VAppTemplate resource which will contain a task. The user should should wait for this task to finish to
    *         be able to use the VAppTemplate.
    */
   VAppTemplate cloneVAppTemplate(String vdcUrn, CloneVAppTemplateParams params);
   
   VAppTemplate cloneVAppTemplate(URI vdcHref, CloneVAppTemplateParams params);

   /**
    * Composes a new vApp using VMs from other vApps or vApp templates.
    *
    * The vCloud API supports composing a vApp from any combination of vApp templates, vApps,
    * or virtual machines. When you compose a vApp, all children of each composition source
    * become peers in the Children collection of the composed vApp. To compose a vApp, a api
    * makes a compose vApp request whose body is a ComposeVAppParams element, includes the
    * following information:
    * <ul>
    * <li>An InstantiationParams element that applies to the composed vApp itself and any vApp templates referenced in
    *    Item elements.
    * <li>A SourcedItem element for each virtual machine, vApp, or vAppTemplate to include in the composition. Each
    *    SourcedItem can contain the following elements:
    *    <ul>
    *    <li>A required Source element whose href attribute value is a reference to a vApp template, vApp, or VM to include
    *       in the composition. If the Source element references a VM, the Item must also include an InstantiationParams
    *       element specific to that VM.
    *    <li>An optional NetworkAssignment element that specifies how the network connections of child VM elements are
    *       mapped to vApp networks in the parent.
    *    </ul>
    * </ul>
    * If any of the composition items is subject to a EULA, the ComposeVAppParams element must include an
    * AllEULAsAccepted element that has a value of true, indicating that you accept the EULA. Otherwise, composition
    * fails. The composed vApp must be deployed and powered on before it can be used. The status of vApp will be
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} until the
    * compose task is finished.
    * 
    * @return a VApp resource which will contain a task. The user should should wait for this task to finish to be able
    *         to use the vApp.
    */
   VApp composeVApp(String vdcUrn, ComposeVAppParams params);
   
   VApp composeVApp(URI vdcHref, ComposeVAppParams params);

   /**
    * Instantiate a vApp template into a new vApp.
    *
    * The status of vApp will be in {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} until the instantiate task is finished.
    * 
    * <pre>
    * POST /vdc/{id}/action/instantiateVAppTemplate
    * </pre>
    * 
    * @return a VApp resource which will contain a task. The user should should wait for this task to finish to be able
    *         to use the vApp.
    */
   VApp instantiateVApp(String vdcUrn, InstantiateVAppParams params);
   
   VApp instantiateVApp(URI vdcHref, InstantiateVAppParams params);

   /**
    * Uploading vApp template to a vDC.
    *
    * The operation is separate on several steps:
    * <ol>
    * <li>creating empty vApp template entity
    * <li>uploading an OVF of vApp template
    * <li>uploading disks described from the OVF
    * <li>finishing task for uploading
    * </ol>
    * The status of vApp template will be
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#NOT_READY NOT_READY(0)} until the ovf and
    * all disks are uploaded to the transfer site. After this a task will run on the vApp template uploading.
    * Note that the empty vApp template's getFiles() returns a file of size -1 after step one above,
    * because the descriptor.ovf does not yet exist.
    * 
    * @return a VAppTemplate resource which will contain a task. The user should should wait for this task to finish to
    *         be able to use the VAppTemplate.
    */
   VAppTemplate uploadVAppTemplate(String vdcUrn, UploadVAppTemplateParams params);
   
   VAppTemplate uploadVAppTemplate(URI vdcHref, UploadVAppTemplateParams params);

   /**
    * Creates a media (and present upload link for the floppy/iso file).
    * 
    * @return The response will return a link to transfer site to be able to continue with uploading the media.
    */
   Media addMedia(String vdcUrn, Media media);
   
   Media addMedia(URI vdcHref, Media media);

   /**
    * @return synchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam(parser = VdcURNToHref.class) String vdcUrn);

   @Delegate
   MetadataApi.Readable getMetadataApi(@EndpointParam URI vdcHref);

}
