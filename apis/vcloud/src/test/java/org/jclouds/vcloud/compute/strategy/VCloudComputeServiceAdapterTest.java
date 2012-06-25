package org.jclouds.vcloud.compute.strategy;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Collections;

import org.jclouds.compute.ComputeService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.vcloud.compute.BaseVCloudComputeServiceExpectTest;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class VCloudComputeServiceAdapterTest extends BaseVCloudComputeServiceExpectTest {

   @Test
   public void testListHardwareProfiles() throws Exception {
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(versionsRequest, versionsResponseFromVCD1_5)
               .put(version1_0LoginRequest, successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg)
               .put(version1_0GetOrgRequest, successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork)
               .put(version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate)
               .put(version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate)
               .put(version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork)
               .put(version1_0GetVAppTemplateRequest, successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithSingleVMAndVDCParent)
               .put(version1_0GetOVFForVAppTemplateRequest, successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithSingleVM)
               .build());

      VCloudComputeServiceAdapter adapter = compute.getContext()
               .utils().injector().getInstance(VCloudComputeServiceAdapter.class);

      Iterable<VAppTemplate> hardwareProfiles = adapter.listHardwareProfiles();
      
      Iterable<URI> hardwareProfileRefs = Iterables.transform(ImmutableList.copyOf(hardwareProfiles), new Function<VAppTemplate,URI>() {
         @Override public URI apply(VAppTemplate input) {
            return input.getHref();
         }
      });
      assertEquals(ImmutableSet.copyOf(hardwareProfileRefs), ImmutableSet.of(URI.create("https://zone.myvcloud.com/api/v1.0/vAppTemplate/vappTemplate-51891b97-c5dd-47dc-a687-aabae354f728")));
   }
   
   /**
    * For issue 994. In BaseEnvelopeHandler when it encounters VirtualSystemCollection, it throws IllegalArgumentException
    * (cannot currently create envelopes with multiple virtual systems).
    * Thus we do not include the VM in the supported set, but we do return without propagating the exception.
    */
   @Test
   public void testListHardwareProfilesWithUnsupportedTemplate() throws Exception {
      ComputeService compute = requestsSendResponses(ImmutableMap.<HttpRequest, HttpResponse> builder()
               .put(versionsRequest, versionsResponseFromVCD1_5)
               .put(version1_0LoginRequest, successfulVersion1_0LoginResponseFromVCD1_5WithSingleOrg)
               .put(version1_0GetOrgRequest, successfulVersion1_0GetOrgResponseFromVCD1_5WithSingleTasksListVDCAndNetwork)
               .put(version1_0GetCatalogRequest, successfulVersion1_0GetCatalogResponseFromVCD1_5WithSingleTemplate)
               .put(version1_0GetCatalogItemRequest, successfulVersion1_0GetCatalogItemResponseFromVCD1_5ForTemplate)
               .put(version1_0GetVDCRequest, successfulVersion1_0GetVDCResponseFromVCD1_5WithSingleTemplateAndNetwork)
               .put(version1_0GetVAppTemplateRequest, successfulVersion1_0GetVAppTemplateResponseFromVCD1_5WithMultipleVMsAndVDCParent)
               .put(version1_0GetOVFForVAppTemplateRequest, successfulVersion1_0GetOVFForVAppTemplateResponseFromVCD1_5WithMultipleVMs)
               .build());

      VCloudComputeServiceAdapter adapter = compute.getContext()
               .utils().injector().getInstance(VCloudComputeServiceAdapter.class);

      Iterable<VAppTemplate> hardwareProfiles = adapter.listHardwareProfiles();
      
      assertEquals(ImmutableSet.copyOf(hardwareProfiles), Collections.emptySet());
   }
}
