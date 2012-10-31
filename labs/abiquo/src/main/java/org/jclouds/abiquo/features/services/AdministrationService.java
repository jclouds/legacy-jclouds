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

package org.jclouds.abiquo.features.services;

import org.jclouds.abiquo.domain.config.Category;
import org.jclouds.abiquo.domain.config.License;
import org.jclouds.abiquo.domain.config.Privilege;
import org.jclouds.abiquo.domain.config.SystemProperty;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.EnterpriseProperties;
import org.jclouds.abiquo.domain.enterprise.Role;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.internal.BaseAdministrationService;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Provides high level Abiquo administration operations.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@ImplementedBy(BaseAdministrationService.class)
public interface AdministrationService {
   /*********************** Datacenter ***********************/

   /**
    * Get the list of all datacenters.
    */
   Iterable<Datacenter> listDatacenters();

   /**
    * Get the list of datacenters matching the given filter.
    */
   Iterable<Datacenter> listDatacenters(final Predicate<Datacenter> filter);

   /**
    * Get the first datacenter that matches the given filter or
    * <code>null</code> if none is found.
    */
   Datacenter findDatacenter(final Predicate<Datacenter> filter);

   /**
    * Get the datacenter with the given id.
    */
   Datacenter getDatacenter(final Integer datacenterId);

   /*********************** Machine ***********************/

   /**
    * Get the list of all machines in the infrastructure.
    */
   public Iterable<Machine> listMachines();

   /**
    * Get the list of all machines in the infrastructure matching the given
    * filter.
    */
   public Iterable<Machine> listMachines(Predicate<Machine> filter);

   /**
    * Get the first machine in the infrastructure that matches the given filter.
    */
   public Machine findMachine(Predicate<Machine> filter);

   /*********************** Enterprise ***********************/

   /**
    * Get the list of all enterprises.
    */
   Iterable<Enterprise> listEnterprises();

   /**
    * Get the list of enterprises matching the given filter.
    */
   Iterable<Enterprise> listEnterprises(final Predicate<Enterprise> filter);

   /**
    * Get the first enterprises that matches the given filter or
    * <code>null</code> if none is found.
    */
   Enterprise findEnterprise(final Predicate<Enterprise> filter);

   /**
    * Get the enterprise with the given id.
    */
   Enterprise getEnterprise(final Integer enterpriseId);

   /*********************** Enterprise Properties ***********************/
   /**
    * Get the properties of an enterprise.
    */
   EnterpriseProperties getEnterpriseProperties(final Enterprise enterprise);

   /*********************** Role ***********************/

   /**
    * Get the list of global roles.
    */
   Iterable<Role> listRoles();

   /**
    * Get the list of roles matching the given filter.
    */
   Iterable<Role> listRoles(final Predicate<Role> filter);

   /**
    * Get the first role that matches the given filter or <code>null</code> if
    * none is found.
    */
   Role findRole(final Predicate<Role> filter);

   /**
    * Get the role with the given id.
    */
   Role getRole(final Integer roleId);

   /*********************** Privilege ***********************/

   /**
    * Get the list of global privileges.
    */
   Iterable<Privilege> listPrivileges();

   /**
    * Get the list of privileges matching the given filter.
    */
   Iterable<Privilege> listPrivileges(final Predicate<Privilege> filter);

   /**
    * Get the first privilege that matches the given filter or <code>null</code>
    * if none is found.
    */
   Privilege findPrivilege(final Predicate<Privilege> filter);

   /**
    * Get a privilege given its id.
    * 
    * @param privilegeId
    *           The id of the privilege.
    * @return The privilege.
    */
   Privilege getPrivilege(Integer privilegeId);

   /*********************** User ***********************/

   /**
    * Get the current user.
    */
   User getCurrentUser();

   /**
    * Get the enterprise of the current user.
    */
   Enterprise getCurrentEnterprise();

   /*********************** License ***********************/

   /**
    * Get the list of all licenses.
    */
   Iterable<License> listLicenses();

   /**
    * Get the list of all active/inactive licenses.
    * 
    * @param active
    *           Defines if searching for active (<code>true</code>) or inactive
    *           ( <code>false</code>) licenses.
    */
   Iterable<License> listLicenses(boolean active);

   /**
    * Get the list of licenses matching the given filter.
    */
   Iterable<License> listLicenses(final Predicate<License> filter);

   /**
    * Get the first license that matches the given filter or <code>null</code>
    * if none is found.
    */
   License findLicense(final Predicate<License> filter);

   /*********************** System Properties ***********************/

   /**
    * Get the list of system properties.
    */
   Iterable<SystemProperty> listSystemProperties();

   /**
    * Get the list of system properties matching the given filter.
    */
   Iterable<SystemProperty> listSystemProperties(final Predicate<SystemProperty> filter);

   /**
    * Get the first system property that matches the given filter or
    * <code>null</code> if none is found.
    */
   SystemProperty findSystemProperty(final Predicate<SystemProperty> filter);

   /**
    * Get the system property with the give name or <code>null</code> if none is
    * found.
    */
   SystemProperty getSystemProperty(String name);

   /**
    * Get the list of system properties with options.
    */
   Iterable<SystemProperty> listSystemProperties(String component);

   /*********************** Category ***********************/

   /**
    * Get the list of categories.
    */
   Iterable<Category> listCategories();

   /**
    * Get the list of categories matching the given filter.
    */
   Iterable<Category> listCategories(final Predicate<Category> filter);

   /**
    * Get the first categories that matches the given filter or
    * <code>null</code> if none is found.
    */
   Category findCategory(final Predicate<Category> filter);

   /**
    * Get the category identified by the given id.
    * 
    * @param categoryId
    *           The id of the category.
    * @return The requested category.
    */
   Category getCategory(Integer categoryId);
}
