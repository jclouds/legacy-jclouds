/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.ws.rs.core.MediaType;

import org.jclouds.abiquo.domain.EnterpriseResources;
import org.jclouds.abiquo.domain.InfrastructureResources;
import org.jclouds.abiquo.domain.NetworkResources;
import org.jclouds.abiquo.domain.infrastructure.options.DatacenterOptions;
import org.jclouds.abiquo.domain.infrastructure.options.IpmiOptions;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.domain.network.options.NetworkOptions;
import org.jclouds.abiquo.domain.options.search.FilterOptions;
import org.jclouds.abiquo.functions.ReturnAbiquoExceptionOnNotFoundOr4xx;
import org.jclouds.abiquo.functions.ReturnFalseIfNotAvailable;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnStringIf2xx;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.BladeLocatorLedDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.FsmsDto;
import com.abiquo.server.core.infrastructure.LogicServerDto;
import com.abiquo.server.core.infrastructure.LogicServersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.OrganizationDto;
import com.abiquo.server.core.infrastructure.OrganizationsDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.abiquo.server.core.infrastructure.UcsRacksDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VlanTagAvailabilityDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesMetadataDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolsDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code InfrastructureAsyncApi}
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "InfrastructureAsyncApiTest")
public class InfrastructureAsyncApiTest extends BaseAbiquoAsyncApiTest<InfrastructureAsyncApi> {
   /*********************** Datacenter ***********************/

   public void testListDatacenters() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listDatacenters");
      GeneratedHttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createDatacenter", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.datacenterPostPayload()), DatacenterDto.class,
            DatacenterDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getDatacenter", Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateDatacenter", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.datacenterPutPayload()), DatacenterDto.class,
            DatacenterDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteDatacenter() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteDatacenter", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/datacenters/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListLimitsDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listLimits", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/action/getLimits HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersLimitsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Hypervisor ***********************/

   public void testGetHypervisorTypeFromMachine() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getHypervisorTypeFromMachine", DatacenterDto.class,
            DatacenterOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            DatacenterOptions.builder().ip("10.60.1.120").build());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/action/hypervisor?ip=10.60.1.120 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MediaType.TEXT_PLAIN + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnStringIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetHypervisorTypesFromDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getHypervisorTypes", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/hypervisors HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + HypervisorTypesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Managed Rack ***********************/

   public void testListRacks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listRacks", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RacksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createRack", DatacenterDto.class, RackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            InfrastructureResources.rackPost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/racks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.rackPostPayload()), RackDto.class,
            RackDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getRack", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateRack", RackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.rackPut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/racks/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.rackPutPayload()), RackDto.class,
            RackDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteRack() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteRack", RackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.rackPut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/datacenters/1/racks/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Managed Rack ***********************/

   public void testListManagedRacks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listManagedRacks", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UcsRacksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateManagedRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class
            .getMethod("createManagedRack", DatacenterDto.class, UcsRackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            InfrastructureResources.managedRackPost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/racks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UcsRackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.managedRackPostPayload()), UcsRackDto.class,
            UcsRackDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetManagedRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getManagedRack", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UcsRackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateManagedRack() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateManagedRack", UcsRackDto.class);

      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/racks/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UcsRackDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.managedRackPutPayload()), UcsRackDto.class,
            UcsRackDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListServiceProfiles() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listServiceProfiles", UcsRackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/logicservers HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LogicServersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListServiceProfilesWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      FilterOptions options = FilterOptions.builder().startWith(1).limit(2).build();

      Method method = InfrastructureAsyncApi.class.getMethod("listServiceProfiles", UcsRackDto.class,
            FilterOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/logicservers?startwith=1&limit=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LogicServersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListOrganizations() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listOrganizations", UcsRackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/organizations HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + OrganizationsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListOrganizationsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      FilterOptions options = FilterOptions.builder().has("org").build();

      Method method = InfrastructureAsyncApi.class
            .getMethod("listOrganizations", UcsRackDto.class, FilterOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/organizations?has=org HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + OrganizationsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListServiceProfileTemplates() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listServiceProfileTemplates", UcsRackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/lstemplates HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LogicServersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListServiceProfileTemplatesWithOptions() throws SecurityException, NoSuchMethodException,
         IOException {
      FilterOptions options = FilterOptions.builder().ascendant(true).build();

      Method method = InfrastructureAsyncApi.class.getMethod("listServiceProfileTemplates", UcsRackDto.class,
            FilterOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/lstemplates?asc=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LogicServersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAssociateLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("associateLogicServer", UcsRackDto.class,
            LogicServerDto.class, OrganizationDto.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut(), InfrastructureResources.organizationPut(), "blade");

      assertRequestLineEquals(
            request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/associate?bladeDn=blade&org=org-root%2Forg-Finance&lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAssociateTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("associateTemplate", UcsRackDto.class,
            LogicServerDto.class, OrganizationDto.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut(), InfrastructureResources.organizationPut(), "newname", "blade");

      assertRequestLineEquals(
            request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/associatetemplate?newName=newname&bladeDn=blade&org=org-root%2Forg-Finance&lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCloneAndAssociateLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("cloneAndAssociateLogicServer", UcsRackDto.class,
            LogicServerDto.class, OrganizationDto.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut(), InfrastructureResources.organizationPut(), "newname", "blade");

      assertRequestLineEquals(
            request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/assocclone?newName=newname&bladeDn=blade&org=org-root%2Forg-Finance&lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDissociateLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("dissociateLogicServer", UcsRackDto.class,
            LogicServerDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut(), InfrastructureResources.organizationPut());

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/dissociate?lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCloneLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("cloneLogicServer", UcsRackDto.class,
            LogicServerDto.class, OrganizationDto.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut(), InfrastructureResources.organizationPut(), "name");

      assertRequestLineEquals(
            request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/clone?newName=name&org=org-root%2Forg-Finance&lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteLogicServer", UcsRackDto.class,
            LogicServerDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(),
            InfrastructureResources.logicServerPut());

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/logicservers/delete?lsName=server HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListFsms() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listFsms", UcsRackDto.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.managedRackPut(), "dn");

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/fsm?dn=dn HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + FsmsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Remote Service **********************/

   public void testListRemoteServices() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listRemoteServices", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/remoteservices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RemoteServicesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateRemoteService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createRemoteService", DatacenterDto.class,
            RemoteServiceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            InfrastructureResources.remoteServicePost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/remoteservices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RemoteServiceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.remoteServicePostPayload()),
            RemoteServiceDto.class, RemoteServiceDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRemoteService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getRemoteService", DatacenterDto.class,
            RemoteServiceType.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            RemoteServiceType.STORAGE_SYSTEM_MONITOR);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/remoteservices/storagesystemmonitor HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RemoteServiceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateRemoteService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateRemoteService", RemoteServiceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.remoteServicePut());

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/datacenters/1/remoteservices/nodecollector HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + RemoteServiceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.remoteServicePutPayload()),
            RemoteServiceDto.class, RemoteServiceDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteRemoteService() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteRemoteService", RemoteServiceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.remoteServicePut());

      assertRequestLineEquals(request,
            "DELETE http://localhost/api/admin/datacenters/1/remoteservices/nodecollector HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testIsAvailableRemoteService() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("isAvailable", RemoteServiceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.remoteServicePut());

      String checkUri = InfrastructureResources.remoteServicePut().searchLink("check").getHref();
      assertRequestLineEquals(request, String.format("GET %s HTTP/1.1", checkUri));
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfNotAvailable.class);

      checkFilters(request);
   }

   /*********************** Machine ***********************/

   public void testDiscoverSingleMachineWithoutOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("discoverSingleMachine", DatacenterDto.class,
            String.class, HypervisorType.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", HypervisorType.XENSERVER, "user", "pass");

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/discoversingle";
      String query = "hypervisor=XENSERVER&ip=10.60.1.222&user=user&password=pass";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testDiscoverSingleMachineAllParams() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("discoverSingleMachine", DatacenterDto.class,
            String.class, HypervisorType.class, String.class, String.class, MachineOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "80.80.80.80", HypervisorType.KVM, "user", "pass", MachineOptions.builder().port(8889).build());

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/discoversingle";
      String query = "hypervisor=KVM&ip=80.80.80.80&user=user&password=pass&port=8889";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testDiscoverSingleMachineDefaultValues() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("discoverSingleMachine", DatacenterDto.class,
            String.class, HypervisorType.class, String.class, String.class, MachineOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "80.80.80.80", HypervisorType.KVM, "user", "pass", MachineOptions.builder().build());

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/discoversingle";
      String query = "hypervisor=KVM&ip=80.80.80.80&user=user&password=pass";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testDiscoverMultipleMachinesWithoutOptions() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("discoverMultipleMachines", DatacenterDto.class,
            String.class, String.class, HypervisorType.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", "10.60.1.250", HypervisorType.XENSERVER, "user", "pass");

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/discovermultiple";
      String query = "password=pass&ipTo=10.60.1.250&ipFrom=10.60.1.222&hypervisor=XENSERVER&user=user";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachinesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testDiscoverMultipleMachinesAllParams() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("discoverMultipleMachines", DatacenterDto.class,
            String.class, String.class, HypervisorType.class, String.class, String.class, MachineOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "80.80.80.80", "80.80.80.86", HypervisorType.KVM, "user", "pass", MachineOptions.builder().port(8889)
                  .build());

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/discovermultiple";
      String query = "password=pass&ipTo=80.80.80.86&ipFrom=80.80.80.80&hypervisor=KVM&user=user&port=8889";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachinesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testCheckMachineStateWithoutOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineState", DatacenterDto.class, String.class,
            HypervisorType.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", HypervisorType.XENSERVER, "user", "pass");

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/checkmachinestate";
      String query = "hypervisor=XENSERVER&ip=10.60.1.222&user=user&password=pass";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testCheckMachineStateAllParams() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineState", DatacenterDto.class, String.class,
            HypervisorType.class, String.class, String.class, MachineOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", HypervisorType.XENSERVER, "user", "pass", MachineOptions.builder().port(8889).build());

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/checkmachinestate";
      String query = "hypervisor=XENSERVER&ip=10.60.1.222&user=user&password=pass&port=8889";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testCheckMachineIpmiStateWithoutOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineIpmiState", DatacenterDto.class,
            String.class, String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", "user", "pass");

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/checkmachineipmistate";
      String query = "user=user&ip=10.60.1.222&password=pass";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineIpmiStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testCheckMachineIpmiStateWithALLOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineIpmiState", DatacenterDto.class,
            String.class, String.class, String.class, IpmiOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            "10.60.1.222", "user", "pass", IpmiOptions.builder().port(8889).build());

      String baseUrl = "http://localhost/api/admin/datacenters/1/action/checkmachineipmistate";
      String query = "user=user&ip=10.60.1.222&password=pass&port=8889";
      String expectedRequest = String.format("GET %s?%s HTTP/1.1", baseUrl, query);

      assertRequestLineEquals(request, expectedRequest);
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineIpmiStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnAbiquoExceptionOnNotFoundOr4xx.class);

      checkFilters(request);
   }

   public void testListMachines() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listMachines", RackDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.rackPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/machines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachinesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetMachine() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getMachine", RackDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.rackPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCheckMachineState() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineState", MachineDto.class, boolean.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut(), true);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/checkstate?sync=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCheckMachineIpmiState() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("checkMachineIpmiState", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/checkipmistate HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineIpmiStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateMachine() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createMachine", RackDto.class, MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.rackPut(),
            InfrastructureResources.machinePost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/racks/1/machines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.machinePostPayload()), MachineDto.class,
            MachineDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateMachine() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateMachine", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/racks/1/machines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.machinePutPayload()), MachineDto.class,
            MachineDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteMachine() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteMachine", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/datacenters/1/racks/1/machines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testReserveMachine() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("reserveMachine", EnterpriseDto.class, MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, EnterpriseResources.enterprisePut(),
            InfrastructureResources.machinePut());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/enterprises/1/reservedmachines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachineDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.machinePutPayload()), MachineDto.class,
            MachineDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCancelReservation() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class
            .getMethod("cancelReservation", EnterpriseDto.class, MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, EnterpriseResources.enterprisePut(),
            InfrastructureResources.machinePut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/enterprises/1/reservedmachines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListVirtualMachinesByMachine() throws SecurityException, NoSuchMethodException, IOException {
      MachineOptions options = MachineOptions.builder().sync(true).build();

      Method method = InfrastructureAsyncApi.class.getMethod("listVirtualMachinesByMachine", MachineDto.class,
            MachineOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/virtualmachines?sync=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetVirtualMachineByMachine() throws SecurityException, NoSuchMethodException, IOException {

      Method method = InfrastructureAsyncApi.class.getMethod("getVirtualMachine", MachineDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut(), 1);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/virtualmachines/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachineWithNodeExtendedDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /*********************** Blade ***********************/

   public void testPowerOff() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("powerOff", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/poweroff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testPowerOn() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("powerOn", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/poweron HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetLogicServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getLogicServer", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/logicserver HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LogicServerDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testLedOn() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("ledOn", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/ledon HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testLedOff() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("ledOff", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/datacenters/1/racks/1/machines/1/action/ledoff HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetLocatorLed() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getLocatorLed", MachineDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.machinePut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/racks/1/machines/1/led HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + BladeLocatorLedDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Storage Device ***********************/

   public void testListStorageDevices() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listStorageDevices", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/devices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StorageDevicesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListSupportedStorageDevices() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listSupportedStorageDevices", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/storage/devices/action/supported HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StorageDevicesMetadataDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateStorageDevice() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createStorageDevice", DatacenterDto.class,
            StorageDeviceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            InfrastructureResources.storageDevicePost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/storage/devices HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StorageDeviceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.storageDevicePostPayload()),
            StorageDeviceDto.class, StorageDeviceDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteStorageDevice() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteStorageDevice", StorageDeviceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/datacenters/1/storage/devices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateStorageDevice() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateStorageDevice", StorageDeviceDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/storage/devices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StorageDeviceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.storageDevicePutPayload()),
            StorageDeviceDto.class, StorageDeviceDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetStorageDevice() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getStorageDevice", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/devices/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StorageDeviceDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /*********************** Tier ***********************/

   public void testListTiers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listTiers", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/tiers HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TiersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateTier() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateTier", TierDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.tierPut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/storage/tiers/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TierDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.tierPutPayload()), TierDto.class,
            TierDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetTier() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getTier", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/tiers/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TierDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /*********************** StoragePool ***********************/

   public void testListSyncStoragePools() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listStoragePools", StorageDeviceDto.class,
            StoragePoolOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut(),
            StoragePoolOptions.builder().sync(true).build());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/storage/devices/1/pools?sync=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListStoragePoolsFromTier() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listStoragePools", TierDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.tierPut(),
            StoragePoolOptions.builder().sync(true).build());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/tiers/1/pools HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListStoragePoolsNoParams() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listStoragePools", StorageDeviceDto.class,
            StoragePoolOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut(),
            StoragePoolOptions.builder().build());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/storage/devices/1/pools HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateStoragePool() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("createStoragePool", StorageDeviceDto.class,
            StoragePoolDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut(),
            InfrastructureResources.storagePoolPost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/storage/devices/1/pools HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.storagePoolPostPayload()), StoragePoolDto.class,
            StoragePoolDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateStoragePool() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateStoragePool", StoragePoolDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storagePoolPut());

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/datacenters/1/storage/devices/1/pools/tururututu HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(InfrastructureResources.storagePoolPutPayload()), StoragePoolDto.class,
            StoragePoolDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteStoragePool() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteStoragePool", StoragePoolDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storagePoolPut());

      assertRequestLineEquals(request,
            "DELETE http://localhost/api/admin/datacenters/1/storage/devices/1/pools/tururututu HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetStoragePool() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getStoragePool", StorageDeviceDto.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storageDevicePut(),
            InfrastructureResources.storagePoolPut().getIdStorage());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/storage/devices/1/pools/tururututu HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRefreshStoragePool() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("refreshStoragePool", StoragePoolDto.class,
            StoragePoolOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.storagePoolPut(),
            StoragePoolOptions.builder().sync(true).build());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/storage/devices/1/pools/tururututu?sync=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + StoragePoolDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   /*********************** Network ***********************/

   public void testListNetworks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listNetworks", DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/network HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListNetworksWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      NetworkOptions options = NetworkOptions.builder().type(NetworkType.PUBLIC).build();

      Method method = InfrastructureAsyncApi.class.getMethod("listNetworks", DatacenterDto.class, NetworkOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), options);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/network?type=PUBLIC HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetNetworks() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getNetwork", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/network/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworkDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class
            .getMethod("createNetwork", DatacenterDto.class, VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(),
            NetworkResources.vlanPost());

      assertRequestLineEquals(request, "POST http://localhost/api/admin/datacenters/1/network HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworkDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(NetworkResources.vlanNetworkPostPayload()), VLANNetworkDto.class,
            VLANNetworkDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateNetwork() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("updateNetwork", VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.publicNetworkPut());

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/datacenters/1/network/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworkDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(NetworkResources.publicNetworkPutPayload()), VLANNetworkDto.class,
            VLANNetworkDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteNetwork() throws SecurityException, NoSuchMethodException {
      Method method = InfrastructureAsyncApi.class.getMethod("deleteNetwork", VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.publicNetworkPut());

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/datacenters/1/network/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCheckTagAvailability() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class
            .getMethod("checkTagAvailability", DatacenterDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, InfrastructureResources.datacenterPut(), 2);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/network/action/checkavailability?tag=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VlanTagAvailabilityDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   /*********************** Network IPs ***********************/

   public void testListPublicIps() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listPublicIps", VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.publicNetworkPut());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/network/1/ips HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PublicIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListPublicIpsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      IpOptions options = IpOptions.builder().startWith(10).build();
      Method method = InfrastructureAsyncApi.class.getMethod("listPublicIps", VLANNetworkDto.class, IpOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.publicNetworkPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/network/1/ips?startwith=10 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PublicIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetPublicIp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getPublicIp", VLANNetworkDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.publicNetworkPut(), 1);

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters/1/network/1/ips/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PublicIpDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListExternalIps() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listExternalIps", VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.externalNetworkPut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ExternalIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListExternalIpsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      IpOptions options = IpOptions.builder().startWith(10).build();
      Method method = InfrastructureAsyncApi.class.getMethod("listExternalIps", VLANNetworkDto.class, IpOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.externalNetworkPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips?startwith=10 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ExternalIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetExternalIp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getExternalIp", VLANNetworkDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.externalNetworkPut(), 1);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + ExternalIpDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListUnmanagedIps() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("listUnmanagedIps", VLANNetworkDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.unmanagedNetworkPut());

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UnmanagedIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testListUnmanagedIpsWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      IpOptions options = IpOptions.builder().startWith(10).build();
      Method method = InfrastructureAsyncApi.class.getMethod("listUnmanagedIps", VLANNetworkDto.class, IpOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.unmanagedNetworkPut(), options);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips?startwith=10 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UnmanagedIpsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetUnmanagedIp() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InfrastructureAsyncApi.class.getMethod("getUnmanagedIp", VLANNetworkDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, NetworkResources.externalNetworkPut(), 1);

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/limits/1/externalnetworks/1/ips/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UnmanagedIpDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<InfrastructureAsyncApi>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InfrastructureAsyncApi>>() {
      };
   }
}
