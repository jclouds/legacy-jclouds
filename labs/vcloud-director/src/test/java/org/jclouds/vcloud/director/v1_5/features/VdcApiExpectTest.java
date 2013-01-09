/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.net.URI;
import java.text.ParseException;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorException;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Capabilities;
import org.jclouds.vcloud.director.v1_5.domain.CapacityWithUsage;
import org.jclouds.vcloud.director.v1_5.domain.ComputeCapacity;
import org.jclouds.vcloud.director.v1_5.domain.Error;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Media.ImageType;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.params.CaptureVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneMediaParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.CloneVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.ComposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.InstantiateVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UploadVAppTemplateParams;
import org.jclouds.vcloud.director.v1_5.internal.VCloudDirectorAdminApiExpectTest;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Allows us to test a api via its side effects.
 * 
 * @author danikov
 */
@Test(groups = { "unit", "user" }, singleThreaded = true, testName = "VdcApiExpectTest")
public class VdcApiExpectTest extends VCloudDirectorAdminApiExpectTest {
   
   private URI vdcURI;
   
   @BeforeClass
   public void before() {
      String vdcId = "e9cd3387-ac57-4d27-a481-9bee75e0690f";
      vdcURI = URI.create(endpoint+"/vdc/"+vdcId);
   }
   
   @Test
   public void testGetVdc() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/vdc.xml", VCloudDirectorMediaType.VDC)
               .httpResponseBuilder().build());
      
      Vdc expected = getVdc();

      assertEquals(api.getVdcApi().get(vdcURI), expected);
   }

   @Test
   public void testResponse400ForInvalidVdcId() {
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/vdc/NOTAUUID")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/error400.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(400).build());

      Error expected = Error.builder()
            .message("validation error : EntityRef has incorrect type, expected type is com.vmware.vcloud.entity.vdc.")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();

      try {
         api.getVdcApi().get(URI.create(endpoint + "/vdc/NOTAUUID"));
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      }
   }

   @Test
   public void testResponse403ForFakeVdcId() {
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/vdc/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/error403-fake.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(403).build());

      assertNull(api.getVdcApi().get(URI.create(endpoint + "/vdc/aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
   }
   
   @Test(enabled = false)
   public void testCaptureVApp() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/captureVApp")
               .xmlFilePayload("/vdc/params/captureVApp.xml", VCloudDirectorMediaType.CAPTURE_VAPP_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/vdc.xml", VCloudDirectorMediaType.VDC)
               .httpResponseBuilder().build());
      
      VAppTemplate expected = captureVApp();

      // TODO: configure params
      CaptureVAppParams params = CaptureVAppParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().captureVApp(vdcURI, params), expected);
   }
   
   @Test(enabled = false)
   public void testResponse4xxForCaptureVAppNoParams() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/captureVApp")
               .xmlFilePayload("/vdc/params/captureVApp.xml", VCloudDirectorMediaType.CAPTURE_VAPP_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/param/error400.xml", VCloudDirectorMediaType.ERROR)
               .httpResponseBuilder().statusCode(400).build());

      Error expected = Error.builder()
            .message("?")
            .majorErrorCode(400)
            .minorErrorCode("BAD_REQUEST")
            .build();

      try {
         api.getVdcApi().captureVApp(vdcURI, null);
         fail("Should give HTTP 400 error");
      } catch (VCloudDirectorException vde) {
         assertEquals(vde.getError(), expected);
      }
   }
   
   @Test(enabled = false)
   public void testCloneVApp() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneVAppTemplate")
               .xmlFilePayload("/vdc/params/cloneVApp.xml", VCloudDirectorMediaType.CLONE_VAPP_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/cloneVApp.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());
      
      VApp expected = cloneVApp();

      // TODO: configure params
      CloneVAppParams params = CloneVAppParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().cloneVApp(vdcURI, params), expected);
   }
   
   @Test(enabled = false)
   public void testCloneVAppTemplate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneVAppTemplate")
               .xmlFilePayload("/vdc/params/cloneVAppTemplate.xml", VCloudDirectorMediaType.CLONE_VAPP_TEMPLATE_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/cloneVAppTemplate.xml", VCloudDirectorMediaType.VAPP_TEMPLATE)
               .httpResponseBuilder().build());
      
      VAppTemplate expected = cloneVAppTemplate();

      // TODO: configure params
      CloneVAppTemplateParams params = CloneVAppTemplateParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().cloneVAppTemplate(vdcURI, params), expected);
   }

   @Test(enabled = false)
   public void testComposeVApp() throws ParseException {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/composeVApp")
               .xmlFilePayload("/vdc/composeVAppParams.xml", VCloudDirectorMediaType.COMPOSE_VAPP_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/composeVApp.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());
      
      VApp expected = composeVApp();

      // TODO: configure params
      ComposeVAppParams params = ComposeVAppParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().composeVApp(vdcURI, params), expected);
   }

   @Test(enabled = false)
   public void testInstantiateVAppTemplate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/instantiateVAppTemplate")
               .xmlFilePayload("/vdc/params/instantiateVAppTemplate.xml", VCloudDirectorMediaType.CAPTURE_VAPP_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/instantiateVAppTemplate.xml", VCloudDirectorMediaType.VAPP)
               .httpResponseBuilder().build());
      
      VApp expected = instantiateVAppTemplate();

      // TODO: configure params
      InstantiateVAppTemplateParams params = InstantiateVAppTemplateParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().instantiateVApp(vdcURI, params), expected);
   }

   @Test(enabled = false)
   public void testUploadVAppTemplate() {
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/uploadVAppTemplate")
               .xmlFilePayload("/vdc/params/uploadVAppTemplate.xml", VCloudDirectorMediaType.UPLOAD_VAPP_TEMPLATE_PARAMS)
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/uploadVAppTemplate.xml", VCloudDirectorMediaType.VAPP_TEMPLATE)
               .httpResponseBuilder().build());
      
      VAppTemplate expected = uploadVAppTemplate();

      // TODO: configure params
      UploadVAppTemplateParams params = UploadVAppTemplateParams.builder()
         
         .build();
      
      assertEquals(api.getVdcApi().uploadVAppTemplate(vdcURI, params), expected);
   }

   @Test
   public void testAddMedia() {
      URI vdcUri = URI.create(endpoint + "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/media")
               .acceptMedia(VCloudDirectorMediaType.MEDIA)
               .xmlFilePayload("/media/addMediaSource.xml", VCloudDirectorMediaType.MEDIA)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/addMedia.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().statusCode(201).build());
      
      Media source = Media.builder()
         .size(0)
         .imageType(ImageType.ISO)
         .name("Test media 1")
         .type("application/vnd.vmware.vcloud.media+xml")
         .description("Test media generated by testCreateMedia()")
         .build();
      Media expected = MediaApiExpectTest.addMedia();
      
      assertEquals(api.getVdcApi().addMedia(vdcUri, source), expected);
   }
   
   @Test
   public void testCloneMedia() {
      URI vdcUri = URI.create(endpoint + "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f");

      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("POST", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneMedia")
               .acceptMedia(VCloudDirectorMediaType.MEDIA)
               .xmlFilePayload("/media/cloneMediaParams.xml", VCloudDirectorMediaType.CLONE_MEDIA_PARAMS)
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/media/cloneMedia.xml", VCloudDirectorMediaType.MEDIA)
               .httpResponseBuilder().statusCode(201).build());
      
      CloneMediaParams params = CloneMediaParams.builder()
         .name("moved test media")
         .description("moved by testCloneMedia()")
         .source(Reference.builder()
               .type("application/vnd.vmware.vcloud.media+xml")
               .name("copied test media")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/da8361af-cccd-4103-a71c-493513c49094"))
               .build())
         .isSourceDelete(false)
         .build();
      Media expected = MediaApiExpectTest.cloneMedia();
      
      assertEquals(api.getVdcApi().cloneMedia(vdcUri, params), expected);
   }
   
   @Test(enabled = false)
   public void testGetMetadata() {
      URI vdcUri = URI.create(endpoint + "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f");
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse,
            new VcloudHttpRequestPrimer()
                  .apiCommand("GET", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/metadata")
                  .acceptAnyMedia()
                  .httpRequestBuilder().build(),
            new VcloudHttpResponsePrimer()
                  .xmlFilePayload("/vdc/metadata.xml", VCloudDirectorMediaType.METADATA)
                  .httpResponseBuilder().build());
            
      Metadata expected = metadata();

      assertEquals(api.getMetadataApi(vdcUri).get(), expected);
   }
   
   @Test(enabled = false)
   public void testGetMetadataValue() {
      URI vdcUri = URI.create("https://vcloudbeta.bluelock.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f");
      
      VCloudDirectorApi api = requestsSendResponses(loginRequest, sessionResponse, 
            new VcloudHttpRequestPrimer()
               .apiCommand("GET", "/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/metadata/key")
               .acceptAnyMedia()
               .httpRequestBuilder().build(), 
            new VcloudHttpResponsePrimer()
               .xmlFilePayload("/vdc/metadataValue.xml", VCloudDirectorMediaType.METADATA_VALUE)
               .httpResponseBuilder().build());
      
      assertEquals(api.getMetadataApi(vdcUri).get("key"), "");
   }

   public static Vdc getVdc() {
      return Vdc.builder()
         .status(1)
         .name("orgVDC-cloudsoft-Tier1-PAYG")
         .id("urn:vcloud:vdc:e9cd3387-ac57-4d27-a481-9bee75e0690f")
         .type("application/vnd.vmware.vcloud.vdc+xml")
         .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
         .link(Link.builder()
            .rel("up")
            .type("application/vnd.vmware.vcloud.org+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/org/c076f90a-397a-49fa-89b8-b294c1599cd0"))
            .build())
         .link(Link.builder()
            .rel("down")
            .type("application/vnd.vmware.vcloud.metadata+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/metadata"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.uploadVAppTemplateParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/uploadVAppTemplate"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.media+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/media"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/instantiateVAppTemplate"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.cloneVAppParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneVApp"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneVAppTemplate"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.cloneMediaParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/cloneMedia"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.captureVAppParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/captureVApp"))
            .build())
         .link(Link.builder()
            .rel("add")
            .type("application/vnd.vmware.vcloud.composeVAppParams+xml")
            .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/action/composeVApp"))
            .build())
         .description("Pay As You go resources for organization cloudsoft ")
         .allocationModel("AllocationVApp")
         .storageCapacity(CapacityWithUsage.builder()
            .units("MB")
            .allocated(0l)
            .limit(0l)
            .used(4519l)
            .overhead(0l)
            .build())
         .computeCapacity(ComputeCapacity.builder()
            .cpu(CapacityWithUsage.builder()
               .units("MHz")
               .allocated(0l)
               .limit(0l)
               .used(0l)
               .overhead(0l)
               .build())
            .memory(CapacityWithUsage.builder()
               .units("MB")
               .allocated(0l)
               .limit(0l)
               .used(0l)
               .overhead(0l)
               .build())
            .build())
        .resourceEntity(Reference.builder()
               .type("application/vnd.vmware.vcloud.vApp+xml")
               .name("vcdcap-db9")
               .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-e2a4ab74-ea62-4afa-8bb7-0c11259044fb"))
               .build())
        .resourceEntity(Reference.builder()
               .type("application/vnd.vmware.vcloud.vAppTemplate+xml")
               .name("adriancolecap")
               .href(URI.create("https://mycloud.greenhousedata.com/api/vAppTemplate/vappTemplate-5571eb21-f532-4506-9737-01a4635a04cb"))
               .build())
        .resourceEntity(Reference.builder()
               .type("application/vnd.vmware.vcloud.media+xml")
               .name("DansTestMedia")
               .href(URI.create("https://mycloud.greenhousedata.com/api/media/794eb334-754e-4917-b5a0-5df85cbd61d1"))
               .build())
        .network(Reference.builder()
               .type("application/vnd.vmware.vcloud.network+xml")
               .name("orgNet-cloudsoft-Isolated")
               .href(URI.create("https://mycloud.greenhousedata.com/api/network/a604f3c2-0343-453e-ae1f-cddac5b7bd94"))
               .build())
        .network(Reference.builder()
               .type("application/vnd.vmware.vcloud.network+xml")
               .name("orgNet-cloudsoft-External")
               .href(URI.create("https://mycloud.greenhousedata.com/api/network/b466c0c5-8a5c-4335-b703-a2e2e6b5f3e1"))
               .build())
        .network(Reference.builder()
               .type("application/vnd.vmware.vcloud.network+xml")
               .name("orgNet-cloudsoft-Internal-Routed")
               .href(URI.create("https://mycloud.greenhousedata.com/api/network/6d7392e2-c816-43fb-99be-f9ebcd70abf6"))
               .build())
         .capabilities(Capabilities.builder()
               .supportedHardwareVersion("vmx-04")
               .supportedHardwareVersion("vmx-07")
               .supportedHardwareVersion("vmx-08")
               .build())
         .nicQuota(0)
         .networkQuota(10)
         .vmQuota(10)
         .isEnabled(true)
         .build();
   }
   
   private VAppTemplate captureVApp() {
      // TODO Auto-generated method stub
      return null;
   }
   
   private VApp cloneVApp() {
      // TODO Auto-generated method stub
      return null;
   }
   
   private VAppTemplate cloneVAppTemplate() {
      // TODO Auto-generated method stub
      return null;
   }
   
   private VApp composeVApp() throws ParseException {

      return VApp
               .builder()
               .status(0)
               .name("test-vapp")
               .id("urn:vcloud:vapp:d0e2b6b9-4381-4ddc-9572-cdfae54059be")
               .type("application/vnd.vmware.vcloud.vApp+xml")
               .description("Test VApp")
               .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be"))
               .link(Link
                        .builder()
                        .name("orgNet-cloudsoft-External")
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.vAppNetwork+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/network/2a2e2da4-446a-4ebc-a086-06df7c9570f0"))
                        .build())
               .link(Link
                        .builder()
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.controlAccess+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/controlAccess/"))
                        .build())
               .link(Link
                        .builder()
                        .rel("up")
                        .type("application/vnd.vmware.vcloud.vdc+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f"))
                        .build())
               .link(Link
                        .builder()
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.owner+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/owner"))
                        .build())
               .link(Link
                        .builder()
                        .rel("down")
                        .type("application/vnd.vmware.vcloud.metadata+xml")
                        .href(URI.create("https://mycloud.greenhousedata.com/api/vApp/vapp-d0e2b6b9-4381-4ddc-9572-cdfae54059be/metadata"))
                        .build())
               .owner(Owner
                        .builder()
                        .type("application/vnd.vmware.vcloud.owner+xml")
                        .user(User
                                 .builder()
                                 .type("application/vnd.vmware.admin.user+xml")
                                 .name("acole")
                                 .href(URI.create("https://mycloud.greenhousedata.com/api/admin/user/c090335b-708c-4c1c-9e3d-89560d002120"))
                                 .build().getRole()).build()).build();
   }

   private VApp instantiateVAppTemplate() {
      // TODO Auto-generated method stub
      return null;
   }
   
   private VAppTemplate uploadVAppTemplate() {
      // TODO Auto-generated method stub
      return null;
   }
   
   private Metadata metadata() {
      // TODO Auto-generated method stub
      return null;
   }
   
}
