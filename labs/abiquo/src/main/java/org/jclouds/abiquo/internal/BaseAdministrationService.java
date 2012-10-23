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

package org.jclouds.abiquo.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.config.Category;
import org.jclouds.abiquo.domain.config.License;
import org.jclouds.abiquo.domain.config.Privilege;
import org.jclouds.abiquo.domain.config.SystemProperty;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.EnterpriseProperties;
import org.jclouds.abiquo.domain.enterprise.Role;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.strategy.admin.ListRoles;
import org.jclouds.abiquo.strategy.config.ListCategories;
import org.jclouds.abiquo.strategy.config.ListLicenses;
import org.jclouds.abiquo.strategy.config.ListPrivileges;
import org.jclouds.abiquo.strategy.config.ListProperties;
import org.jclouds.abiquo.strategy.enterprise.ListEnterprises;
import org.jclouds.abiquo.strategy.infrastructure.ListDatacenters;
import org.jclouds.abiquo.strategy.infrastructure.ListMachines;
import org.jclouds.collect.Memoized;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * Provides high level Abiquo administration operations.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Singleton
public class BaseAdministrationService implements AdministrationService {
   @VisibleForTesting
   protected RestContext<AbiquoApi, AbiquoAsyncApi> context;

   @VisibleForTesting
   protected final ListDatacenters listDatacenters;

   @VisibleForTesting
   protected final ListMachines listMachines;

   @VisibleForTesting
   protected final ListEnterprises listEnterprises;

   @VisibleForTesting
   protected final ListRoles listRoles;

   @VisibleForTesting
   protected final ListLicenses listLicenses;

   @VisibleForTesting
   protected final ListPrivileges listPrivileges;

   @VisibleForTesting
   protected final ListProperties listProperties;

   @VisibleForTesting
   protected final ListCategories listCategories;

   @VisibleForTesting
   protected final Supplier<User> currentUser;

   @VisibleForTesting
   protected final Supplier<Enterprise> currentEnterprise;

   @Inject
   protected BaseAdministrationService(final RestContext<AbiquoApi, AbiquoAsyncApi> context,
         final ListDatacenters listDatacenters, final ListMachines listMachines, final ListEnterprises listEnterprises,
         final ListRoles listRoles, final ListLicenses listLicenses, final ListPrivileges listPrivileges,
         final ListProperties listProperties, final ListCategories listCategories,
         @Memoized final Supplier<User> currentUser, @Memoized final Supplier<Enterprise> currentEnterprise) {
      this.context = checkNotNull(context, "context");
      this.listDatacenters = checkNotNull(listDatacenters, "listDatacenters");
      this.listMachines = checkNotNull(listMachines, "listMachines");
      this.listEnterprises = checkNotNull(listEnterprises, "listEnterprises");
      this.listRoles = checkNotNull(listRoles, "listRoles");
      this.listLicenses = checkNotNull(listLicenses, "listLicenses");
      this.listPrivileges = checkNotNull(listPrivileges, "listPrivileges");
      this.listProperties = checkNotNull(listProperties, "listProperties");
      this.listCategories = checkNotNull(listCategories, "listCategories");
      this.currentUser = checkNotNull(currentUser, "currentUser");
      this.currentEnterprise = checkNotNull(currentEnterprise, "currentEnterprise");
   }

   /*********************** Datacenter ********************** */

   @Override
   public Iterable<Datacenter> listDatacenters() {
      return listDatacenters.execute();
   }

   @Override
   public Iterable<Datacenter> listDatacenters(final Predicate<Datacenter> filter) {
      return listDatacenters.execute(filter);
   }

   @Override
   public Datacenter getDatacenter(final Integer datacenterId) {
      DatacenterDto datacenter = context.getApi().getInfrastructureApi().getDatacenter(datacenterId);
      return wrap(context, Datacenter.class, datacenter);
   }

   @Override
   public Datacenter findDatacenter(final Predicate<Datacenter> filter) {
      return Iterables.getFirst(listDatacenters(filter), null);
   }

   @Override
   public Iterable<Datacenter> getDatacenters(final List<Integer> datacenterIds) {
      return listDatacenters.execute(datacenterIds);
   }

   /*********************** Machine ***********************/

   @Override
   public Iterable<Machine> listMachines() {
      return listMachines.execute();
   }

   @Override
   public Iterable<Machine> listMachines(final Predicate<Machine> filter) {
      return listMachines.execute(filter);
   }

   @Override
   public Machine findMachine(final Predicate<Machine> filter) {
      return Iterables.getFirst(listMachines(filter), null);
   }

   /*********************** Enterprise ***********************/

   @Override
   public Iterable<Enterprise> listEnterprises() {
      return listEnterprises.execute();
   }

   @Override
   public Iterable<Enterprise> listEnterprises(final Predicate<Enterprise> filter) {
      return listEnterprises.execute(filter);
   }

   @Override
   public Enterprise findEnterprise(final Predicate<Enterprise> filter) {
      return Iterables.getFirst(listEnterprises(filter), null);
   }

   @Override
   public Enterprise getEnterprise(final Integer enterpriseId) {
      EnterpriseDto enterprise = context.getApi().getEnterpriseApi().getEnterprise(enterpriseId);
      return wrap(context, Enterprise.class, enterprise);
   }

   /*********************** Enterprise Properties ***********************/

   @Override
   public EnterpriseProperties getEnterpriseProperties(final Enterprise enterprise) {
      checkNotNull(enterprise.getId(), ValidationErrors.MISSING_REQUIRED_FIELD + " id in " + Enterprise.class);

      EnterprisePropertiesDto properties = context.getApi().getEnterpriseApi()
            .getEnterpriseProperties(enterprise.unwrap());
      return wrap(context, EnterpriseProperties.class, properties);
   }

   /*********************** Role ********************** */

   @Override
   public Iterable<Role> listRoles() {
      return listRoles.execute();
   }

   @Override
   public Iterable<Role> listRoles(final Predicate<Role> filter) {
      return listRoles.execute(filter);
   }

   @Override
   public Role findRole(final Predicate<Role> filter) {
      return Iterables.getFirst(listRoles(filter), null);
   }

   @Override
   public Role getRole(final Integer roleId) {
      RoleDto role = context.getApi().getAdminApi().getRole(roleId);
      return wrap(context, Role.class, role);
   }

   /*********************** Privilege ***********************/

   @Override
   public Privilege findPrivilege(final Predicate<Privilege> filter) {
      return Iterables.getFirst(listPrivileges(filter), null);
   }

   @Override
   public Iterable<Privilege> listPrivileges() {
      return listPrivileges.execute();
   }

   @Override
   public Iterable<Privilege> listPrivileges(final Predicate<Privilege> filter) {
      return listPrivileges.execute(filter);
   }

   /*********************** User ***********************/

   @Override
   public User getCurrentUser() {
      return currentUser.get();
   }

   @Override
   public Enterprise getCurrentEnterprise() {
      return currentEnterprise.get();
   }

   /*********************** License ***********************/

   @Override
   public Iterable<License> listLicenses() {
      return listLicenses.execute();
   }

   @Override
   public Iterable<License> listLicenses(final boolean active) {
      LicenseOptions options = LicenseOptions.builder().active(active).build();
      return listLicenses.execute(options);
   }

   @Override
   public Iterable<License> listLicenses(final Predicate<License> filter) {
      return listLicenses.execute(filter);
   }

   @Override
   public License findLicense(final Predicate<License> filter) {
      return Iterables.getFirst(listLicenses(filter), null);
   }

   /*********************** System Properties ***********************/

   @Override
   public Iterable<SystemProperty> listSystemProperties() {
      return listProperties.execute();
   }

   @Override
   public Iterable<SystemProperty> listSystemProperties(final Predicate<SystemProperty> filter) {
      return listProperties.execute(filter);
   }

   @Override
   public SystemProperty findSystemProperty(final Predicate<SystemProperty> filter) {
      return Iterables.getFirst(listSystemProperties(filter), null);
   }

   @Override
   public SystemProperty getSystemProperty(final String name) {
      PropertyOptions options = PropertyOptions.builder().name(name).build();
      return Iterables.getFirst(listProperties.execute(options), null);
   }

   @Override
   public Iterable<SystemProperty> listSystemProperties(final String component) {
      PropertyOptions options = PropertyOptions.builder().component(component).build();
      return listProperties.execute(options);
   }

   @Override
   public Category findCategory(final Predicate<Category> filter) {
      return Iterables.getFirst(listCategories(filter), null);
   }

   @Override
   public Iterable<Category> listCategories() {
      return listCategories.execute();
   }

   @Override
   public Iterable<Category> listCategories(final Predicate<Category> filter) {
      return listCategories.execute(filter);
   }
}
