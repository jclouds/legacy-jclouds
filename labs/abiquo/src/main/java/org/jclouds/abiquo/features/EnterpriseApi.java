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

import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.concurrent.Timeout;

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

/**
 * Provides synchronous access to Abiquo Enterprise API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see EnterpriseAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface EnterpriseApi {

   /*********************** Enterprise ********************** */

   /**
    * List all enterprises.
    * 
    * @return The list of Enterprises.
    */
   EnterprisesDto listEnterprises();

   /**
    * List enterprises with options.
    * 
    * @param options
    *           Filtering options.
    * @return The list of Enterprises.
    */
   EnterprisesDto listEnterprises(EnterpriseOptions options);

   /**
    * List filtered enterprises by datacenter.
    * 
    * @param datacenter
    *           The given datacenter.
    * @param options
    *           Filtering options.
    * @return The list of Enterprises.
    */
   EnterprisesDto listEnterprises(DatacenterDto datacenter, EnterpriseOptions options);

   /**
    * Create a new enterprise.
    * 
    * @param enterprise
    *           The enterprise to be created.
    * @return The created enterprise.
    */
   EnterpriseDto createEnterprise(EnterpriseDto enterprise);

   /**
    * Get the given enterprise.
    * 
    * @param enterpriseId
    *           The id of the enterprise.
    * @return The enterprise or <code>null</code> if it does not exist.
    */
   EnterpriseDto getEnterprise(Integer enterpriseId);

   /**
    * Updates an existing enterprise.
    * 
    * @param enterprise
    *           The new attributes for the enterprise.
    * @return The updated enterprise.
    */
   EnterpriseDto updateEnterprise(EnterpriseDto enterprise);

   /**
    * Deletes an existing enterprise.
    * 
    * @param enterprise
    *           The enterprise to delete.
    */
   void deleteEnterprise(EnterpriseDto enterprise);

   /**
    * List the allowed datacenters to the given enterprise.
    * 
    * @param enterpriseId
    *           The id of the enterprise.
    * @return The allowed datacenters to the given enterprise.
    */
   DatacentersDto listAllowedDatacenters(Integer enterpriseId);

   /**
    * List all virtual datacenters of an enterprise.
    * 
    * @param enterprise
    *           The given enterprise.
    * @return The list of Datacenters.
    */
   VirtualDatacentersDto listVirtualDatacenters(EnterpriseDto enterprise);

   /*********************** Enterprise Properties ***********************/

   /**
    * Get defined properties of the given enterprise.
    * 
    * @param enterpriseId
    *           The enterprise id.
    * @return Set of enterprise properties.
    */
   @EnterpriseEdition
   EnterprisePropertiesDto getEnterpriseProperties(EnterpriseDto enterprise);

   /**
    * Updates the given enterprise properties set.
    * 
    * @param properties
    *           The properties set.
    * @return The updated properties.
    */
   @EnterpriseEdition
   EnterprisePropertiesDto updateEnterpriseProperties(EnterprisePropertiesDto properties);

   /*********************** Enterprise Limits ***********************/

   /**
    * Allows the given enterprise to use the given datacenter with the given
    * limits.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenter
    *           The datacenter to allow to the given enterprise.
    * @param limits
    *           The usage limits for the enterprise in the given datacenter.
    * @return The usage limits for the enterprise in the given datacenter.
    */
   DatacenterLimitsDto createLimits(final EnterpriseDto enterprise, final DatacenterDto datacenter,
         final DatacenterLimitsDto limits);

   /**
    * Retrieves the limits for the given enterprise and datacenter.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenter
    *           The datacenter.
    * @return The usage limits for the enterprise in the given datacenter.
    */
   DatacentersLimitsDto getLimits(EnterpriseDto enterprise, DatacenterDto datacenter);

   /**
    * Retrieves limits for the given enterprise and any datacenter.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The usage limits for the enterprise on any datacenter.
    */
   DatacentersLimitsDto listLimits(EnterpriseDto enterprise);

   /**
    * Updates an existing enterprise-datacenter limits.
    * 
    * @param limits
    *           The new set of limits.
    * @return The updated limits.
    */
   DatacenterLimitsDto updateLimits(DatacenterLimitsDto limits);

   /**
    * Deletes existing limits for a pair enterprise-datacenter.
    * 
    * @param limits
    *           The limits to delete.
    */
   void deleteLimits(DatacenterLimitsDto limits);

   /*********************** User ********************** */

   /**
    * Retrieves users of the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The users of the enterprise.
    */
   UsersDto listUsers(final EnterpriseDto enterprise);

   /**
    * Create a new user in the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param user
    *           The user to be created.
    * @return The created user.
    */
   UserDto createUser(EnterpriseDto enterprise, UserDto user);

   /**
    * Get the given user from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param userId
    *           The id of the user.
    * @return The user or <code>null</code> if it does not exist.
    */
   UserDto getUser(final EnterpriseDto enterprise, final Integer idUser);

   /**
    * Updates an existing user.
    * 
    * @param enterprise
    *           The new attributes for the user.
    * @return The updated user.
    */
   UserDto updateUser(UserDto user);

   /**
    * Deletes existing user.
    * 
    * @param user
    *           The user to delete.
    */
   void deleteUser(UserDto user);

   /**
    * Retrieves list of virtual machines by user.
    * 
    * @param user
    *           The user.
    * @return The list of virtual machines of the user.
    */
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(final UserDto user);

   /*********************** Datacenter Repository ***********************/

   /**
    * Get the given datacenter repository from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param datacenterRepositoryId
    *           The id of the datacenter repository.
    * @return The datacenter repository or <code>null</code> if it does not
    *         exist.
    */
   DatacenterRepositoryDto getDatacenterRepository(final EnterpriseDto enterprise, final Integer datacenterRepositoryId);

   /**
    * Refreshes database with virtual machine templates existing in the
    * repository filesystem.
    * 
    * @param enterpriseId
    *           Id of the enterprise which information will be refreshed.
    * @param datacenterRepositoryId
    *           Id of the datacenter repository containing the templates.
    */
   @Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
   void refreshTemplateRepository(Integer enterpriseId, Integer datacenterRepositoryId);

   /*********************** Network ***********************/

   /**
    * List external networks of the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of external networks created and assigned.
    */
   @EnterpriseEdition
   VLANNetworksDto listExternalNetworks(EnterpriseDto enterprise);

   /*********************** Cloud ***********************/

   /**
    * Retrieves list of virtual appliances by the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of virtual appliances of the enterprise.
    */
   VirtualAppliancesDto listVirtualAppliances(EnterpriseDto enterprise);

   /**
    * List virtual machines for the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of virtual machines by the enterprise.
    */
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(EnterpriseDto enterprise);

   /**
    * List reserved machines for the enterprise
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of reserved machines by the enterprise.
    */
   MachinesDto listReservedMachines(EnterpriseDto enterprise);

   /**
    * List all template definitions in apps library.
    * 
    * @param enterprise
    *           The enterprise.
    * @return The list of template definitions by the enterprise.
    */
   TemplateDefinitionListsDto listTemplateDefinitionLists(EnterpriseDto enterprise);

   /**
    * Create a new template definition list in apps library in the given
    * enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param template
    *           The template to be created.
    * @return The created template.
    */
   TemplateDefinitionListDto createTemplateDefinitionList(EnterpriseDto enterprise,
         TemplateDefinitionListDto templateList);

   /**
    * Update an existing template definition list in apps library.
    * 
    * @param template
    *           The template to be update.
    * @return The updated template.
    */
   TemplateDefinitionListDto updateTemplateDefinitionList(TemplateDefinitionListDto templateList);

   /**
    * Deletes existing user.
    * 
    * @param user
    *           The user to delete.
    */
   void deleteTemplateDefinitionList(TemplateDefinitionListDto templateList);

   /**
    * Get the given template definition list from the given enterprise.
    * 
    * @param enterprise
    *           The enterprise.
    * @param templateListId
    *           The id of the template definition list.
    * @return The list or <code>null</code> if it does not exist.
    */
   TemplateDefinitionListDto getTemplateDefinitionList(final EnterpriseDto enterprise, final Integer templateListId);

   /**
    * Get the list of status of a template definition list in a datacenter.
    * 
    * @param templateList
    *           The template definition list.
    * @param datacenter
    *           The given datacenter.
    * @return The list of states.
    */
   TemplatesStateDto listTemplateListStatus(TemplateDefinitionListDto templateList, DatacenterDto datacenter);
}
