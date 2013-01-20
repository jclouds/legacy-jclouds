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
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.domain.EnterpriseResources;
import org.jclouds.abiquo.domain.InfrastructureResources;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.domain.options.search.reference.OrderBy;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.am.model.TemplatesStateDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListsDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.enterprise.UsersDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests annotation parsing of {@code EnterpriseAsyncApi}
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "EnterpriseAsyncApiTest")
public class EnterpriseAsyncApiTest extends BaseAbiquoAsyncApiTest<EnterpriseAsyncApi> {
   /*********************** Enterprise ********************** */

   public void testListEnterprises() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listEnterprises");
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterprisesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListEnterprisesWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseOptions options = EnterpriseOptions.builder().has("abi").orderBy(OrderBy.NAME).ascendant(true).build();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listEnterprises", EnterpriseOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(options));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises?has=abi&by=name&asc=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterprisesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListEnterprisesByDatacenter() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseOptions options = EnterpriseOptions.builder().startWith(0).limit(25).network(true).build();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listEnterprises", DatacenterDto.class,
            EnterpriseOptions.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(InfrastructureResources.datacenterPut(), options));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/datacenters/1/action/enterprises?network=true&startwith=0&limit=25 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterprisesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateEnterprise() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "createEnterprise", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePost()));

      assertRequestLineEquals(request, "POST http://localhost/api/admin/enterprises HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterpriseDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.enterprisePostPayload()), EnterpriseDto.class,
            EnterpriseDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetEnterprise() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getEnterprise", Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(1));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterpriseDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateEnterprise() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "updateEnterprise", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/enterprises/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterpriseDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.enterprisePutPayload()), EnterpriseDto.class,
            EnterpriseDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteEnterprise() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "deleteEnterprise", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/enterprises/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListAllowedDatacenters() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listAllowedDatacenters", Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(1));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/datacenters?idEnterprise=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListVirtualDatacentersFromEnterprise() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listVirtualDatacenters", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/action/virtualdatacenters HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualDatacentersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Enterprise Properties ********************** */

   public void testGetEnterpriseProperties() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getEnterpriseProperties", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/properties HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterprisePropertiesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(request);
   }

   public void testUpdateEnterpriseProperties() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "updateEnterpriseProperties", EnterprisePropertiesDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePropertiesPut()));

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/enterprises/1/properties HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EnterprisePropertiesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.enterprisePropertiesPutPayload()),
            EnterprisePropertiesDto.class, EnterprisePropertiesDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Enterprise Limits ********************** */

   public void testCreateLimits() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();
      DatacenterDto datacenter = InfrastructureResources.datacenterPut();
      DatacenterLimitsDto limits = EnterpriseResources.datacenterLimitsPost();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "createLimits", EnterpriseDto.class, DatacenterDto.class,
            DatacenterLimitsDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(enterprise, datacenter, limits));

      String limitsUri = enterprise.searchLink("limits").getHref();
      String requestURI = String.format("POST %s?datacenter=%d HTTP/1.1", limitsUri, datacenter.getId());

      assertRequestLineEquals(request, requestURI);
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterLimitsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.datacenterLimitsPostPayload()),
            DatacenterLimitsDto.class, DatacenterLimitsDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetLimits() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();
      DatacenterDto datacenter = InfrastructureResources.datacenterPut();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getLimits", EnterpriseDto.class, DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(enterprise, datacenter));

      String limitsUri = enterprise.searchLink("limits").getHref();
      String requestURI = String.format("GET %s?datacenter=%d HTTP/1.1", limitsUri, datacenter.getId());

      assertRequestLineEquals(request, requestURI);
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersLimitsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testUpdateLimits() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "updateLimits", DatacenterLimitsDto.class);
      GeneratedHttpRequest request = processor.createRequest(method,
            ImmutableList.<Object> of(EnterpriseResources.datacenterLimitsPut(enterprise)));

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/enterprises/1/limits/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterLimitsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.datacenterLimitsPutPayload(enterprise)),
            DatacenterLimitsDto.class, DatacenterLimitsDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteLimits() throws SecurityException, NoSuchMethodException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "deleteLimits", DatacenterLimitsDto.class);
      GeneratedHttpRequest request = processor.createRequest(method,
            ImmutableList.<Object> of(EnterpriseResources.datacenterLimitsPut(enterprise)));

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/enterprises/1/limits/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListLimitsEnterprise() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listLimits", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/limits HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersLimitsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** User ***********************/

   public void testGetUser() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getUser", EnterpriseDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut(), 1));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/users/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UserDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListUsers() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listUsers", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/users HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UsersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateUser() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();
      UserDto user = EnterpriseResources.userPost();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "createUser", EnterpriseDto.class, UserDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(enterprise, user));

      assertRequestLineEquals(request, "POST http://localhost/api/admin/enterprises/1/users HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UserDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.userPostPayload()), UserDto.class,
            UserDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateUser() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "updateUser", UserDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.userPut()));

      assertRequestLineEquals(request, "PUT http://localhost/api/admin/enterprises/1/users/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + UserDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.userPutPayload()), UserDto.class,
            UserDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteUser() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "deleteUser", UserDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.userPut()));

      assertRequestLineEquals(request, "DELETE http://localhost/api/admin/enterprises/1/users/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListVirtualMachinesByUser() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listVirtualMachines", UserDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.userPut()));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/users/1/action/virtualmachines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Datacenter Repository ********************** */

   public void testGetDatacenterRepository() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getDatacenterRepository", EnterpriseDto.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut(),
            InfrastructureResources.datacenterPut().getId()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/datacenterrepositories/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacenterRepositoryDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRefreshTemplateRepository() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "refreshTemplateRepository", Integer.class, Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut().getId(),
            InfrastructureResources.datacenterPut().getId()));

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/enterprises/1/datacenterrepositories/1/actions/refresh HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** External Network ********************** */

   public void testListExternalNetworks() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listExternalNetworks", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/action/externalnetworks HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VLANNetworksDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Cloud ********************** */

   public void testListVirtualMachines() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listVirtualMachines", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/action/virtualmachines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualMachinesWithNodeExtendedDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListVirtualAppliances() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listVirtualAppliances", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/action/virtualappliances HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + VirtualAppliancesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Machine ********************** */

   public void testListReservedMachines() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listReservedMachines", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request, "GET http://localhost/api/admin/enterprises/1/reservedmachines HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + MachinesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Template definition list ***********************/

   public void testListTemplateDefinitionLists() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listTemplateDefinitionLists", EnterpriseDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut()));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TemplateDefinitionListsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateTemplateDefinitionList() throws SecurityException, NoSuchMethodException, IOException {
      EnterpriseDto enterprise = EnterpriseResources.enterprisePut();
      TemplateDefinitionListDto template = EnterpriseResources.templateListPost();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "createTemplateDefinitionList", EnterpriseDto.class,
            TemplateDefinitionListDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(enterprise, template));

      assertRequestLineEquals(request,
            "POST http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TemplateDefinitionListDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.templateListPostPayload()),
            TemplateDefinitionListDto.class, TemplateDefinitionListDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateTemplateDefinitionList() throws SecurityException, NoSuchMethodException, IOException {
      TemplateDefinitionListDto template = EnterpriseResources.templateListPut();

      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "updateTemplateDefinitionList",
            TemplateDefinitionListDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(template));

      assertRequestLineEquals(request,
            "PUT http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TemplateDefinitionListDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(EnterpriseResources.templateListPutPayload()),
            TemplateDefinitionListDto.class, TemplateDefinitionListDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteTemplateDefinitionList() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "deleteTemplateDefinitionList",
            TemplateDefinitionListDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.templateListPut()));

      assertRequestLineEquals(request,
            "DELETE http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetTemplateDefinitionList() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "getTemplateDefinitionList", EnterpriseDto.class,
            Integer.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.enterprisePut(), 1));

      assertRequestLineEquals(request,
            "GET http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TemplateDefinitionListDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testListTemplateListStatus() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EnterpriseAsyncApi.class, "listTemplateListStatus", TemplateDefinitionListDto.class,
            DatacenterDto.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(EnterpriseResources.templateListPut(),
            InfrastructureResources.datacenterPut()));

      assertRequestLineEquals(
            request,
            "GET http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1/actions/repositoryStatus?datacenterId=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + TemplatesStateDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
