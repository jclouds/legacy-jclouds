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

package org.jclouds.abiquo.domain.enterprise;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.util.List;
import java.util.StringTokenizer;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.abiquo.strategy.cloud.ListVirtualDatacenters;
import org.jclouds.rest.RestContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.UserDto;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Adds high level functionality to {@link UserDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/Users+Resource">
 *      http://community.abiquo.com/display/ABI20/Users+Resource</a>
 */
public class User extends DomainWrapper<UserDto> {
   /** Default active value of the user */
   private static final boolean DEFAULT_ACTIVE = true;

   /** The default authentication type. */
   private static final String DEFAULT_AUTH_TYPE = "ABIQUO";

   /** The default locale for the user. */
   private static final String DEFAULT_LOCALE = "en_US";

   /** The enterprise where the user belongs. */
   private Enterprise enterprise;

   /** Role of the user. */
   private Role role;

   /**
    * Constructor to be used only by the builder.
    */
   protected User(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final UserDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/User+resource#Userresource-Deleteanexistinguser"
    *      >
    *      http://community.abiquo.com/display/ABI20/User+resource#Userresource
    *      -Deleteanexistinguser </a>
    */
   public void delete() {
      context.getApi().getEnterpriseApi().deleteUser(target);
      target = null;
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/User+resource#Userresource-Createanewuser"
    *      >
    *      http://community.abiquo.com/display/ABI20/User+resource#Userresource
    *      -Createanewuser</a>
    */
   public void save() {
      // set role link
      target.addLink(new RESTLink("role", role.unwrap().getEditLink().getHref()));
      target = context.getApi().getEnterpriseApi().createUser(enterprise.unwrap(), target);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/User+resource#Userresource-Updatesanexistinguser"
    *      >
    *      http://community.abiquo.com/display/ABI20/User+resource#Userresource
    *      -Updatesanexistinguser </a>
    */
   public void update() {
      // update role link (if exists)
      if (role != null) {
         target.searchLink("role").setHref(role.unwrap().getEditLink().getHref());
      }

      target = context.getApi().getEnterpriseApi().updateUser(target);
   }

   public List<VirtualDatacenter> listPermittedVirtualDatacenters() {
      List<Integer> ids = extractAvailableDatacenters();

      // null value means all virtual datacenters all allowed
      if (ids.size() == 0) {
         return this.getEnterprise().listVirtualDatacenters();
      }

      ListVirtualDatacenters listVirtualDatacenters = context.getUtils().getInjector()
            .getInstance(ListVirtualDatacenters.class);
      return Lists.newArrayList(listVirtualDatacenters.execute(ids));
   }

   public List<VirtualDatacenter> listPermittedVirtualDatacenters(final Predicate<VirtualDatacenter> filter) {
      return Lists.newLinkedList(filter(listPermittedVirtualDatacenters(), filter));
   }

   public VirtualDatacenter findPermittedVirtualDatacenter(final Predicate<VirtualDatacenter> filter) {
      return Iterables.getFirst(filter(listPermittedVirtualDatacenters(), filter), null);
   }

   /**
    * Give access to all virtualdatacenters in the enterprise (requires update).
    */
   public void permitAllVirtualDatacenters() {
      setAvailableVirtualDatacenters(null);
   }

   /**
    * Limits user access ONLY to the virtual datacenters in the list. If the
    * list is empty, user will get access to all virtual datacenters.
    * 
    * @param vdc
    *           List of virtual datacenters from the user's enterprise.
    */
   public void setPermittedVirtualDatacenters(final List<VirtualDatacenter> vdcs) {
      List<Integer> ids = Lists.newArrayList();

      for (VirtualDatacenter vdc : vdcs) {
         checkNotNull(vdc.getId(), ValidationErrors.MISSING_REQUIRED_FIELD + " id in " + VirtualDatacenter.class);
         ids.add(vdc.getId());
      }

      setAvailableVirtualDatacenters(ids);
   }

   // Parent access
   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Enterprise+Resource#EnterpriseResource-RetrieveaEnterprise"
    *      > http://community.abiquo.com/display/ABI20/Enterprise+Resource#
    *      EnterpriseResource- RetrieveaEnterprise</a>
    */
   public Enterprise getEnterprise() {
      Integer enterpriseId = target.getIdFromLink(ParentLinkName.ENTERPRISE);
      return wrap(context, Enterprise.class, context.getApi().getEnterpriseApi().getEnterprise(enterpriseId));
   }

   // Children access

   public Role getRole() {
      RoleDto role = context.getApi().getAdminApi().getRole(target);
      return wrap(context, Role.class, role);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/User+resource#Userresource-Retrievethelistofvirtualmachinesbyuser"
    *      > http://community.abiquo.com/display/ABI20/User+resource#
    *      Userresource- Retrievethelistofvirtualmachinesbyuser</a>
    */
   public List<VirtualMachine> listMachines() {
      VirtualMachinesWithNodeExtendedDto machines = context.getApi().getEnterpriseApi().listVirtualMachines(target);
      return wrap(context, VirtualMachine.class, machines.getCollection());
   }

   public List<VirtualMachine> listMachines(final Predicate<VirtualMachine> filter) {
      return Lists.newLinkedList(filter(listMachines(), filter));
   }

   public VirtualMachine findMachine(final Predicate<VirtualMachine> filter) {
      return Iterables.getFirst(filter(listMachines(), filter), null);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Enterprise enterprise,
         final Role role) {
      return new Builder(context, enterprise, role);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Enterprise enterprise;

      private Role role;

      private String name;

      private String nick;

      private String locale = DEFAULT_LOCALE;

      private String password;

      private String surname;

      private boolean active = DEFAULT_ACTIVE;

      private String email;

      private String description;

      private String authType = DEFAULT_AUTH_TYPE;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Enterprise enterprise, final Role role) {
         super();
         checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
         checkNotNull(role, ValidationErrors.NULL_RESOURCE + Role.class);
         this.context = context;
         this.enterprise = enterprise;
         this.role = role;
      }

      public Builder enterprise(final Enterprise enterprise) {
         checkNotNull(enterprise, ValidationErrors.NULL_RESOURCE + Enterprise.class);
         this.enterprise = enterprise;
         return this;
      }

      public Builder role(final Role role) {
         this.role = role;
         return this;
      }

      public Builder name(final String name, final String surname) {
         this.name = name;
         this.surname = surname;
         return this;
      }

      public Builder nick(final String nick) {
         this.nick = nick;
         return this;
      }

      public Builder locale(final String locale) {
         this.locale = locale;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder active(final boolean active) {
         this.active = active;
         return this;
      }

      public Builder email(final String email) {
         this.email = email;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder authType(final String authType) {
         this.authType = authType;
         return this;
      }

      public User build() {
         UserDto dto = new UserDto();
         dto.setActive(active);
         dto.setAuthType(authType);
         dto.setDescription(description);
         dto.setEmail(email);
         dto.setLocale(locale);
         dto.setName(name);
         dto.setNick(nick);
         dto.setPassword(password);
         dto.setSurname(surname);
         User user = new User(context, dto);
         user.enterprise = enterprise;
         user.role = role;

         return user;
      }

      public static Builder fromUser(final User in) {
         return User.builder(in.context, in.enterprise, in.role).active(in.isActive()).authType(in.getAuthType())
               .description(in.getDescription()).email(in.getEmail()).locale(in.getLocale())
               .name(in.getName(), in.getSurname()).nick(in.getNick()).password(in.getPassword());
      }
   }

   // Delegate methods

   public String getAuthType() {
      return target.getAuthType();
   }

   public String getDescription() {
      return target.getDescription();
   }

   public String getEmail() {
      return target.getEmail();
   }

   public Integer getId() {
      return target.getId();
   }

   public String getLocale() {
      return target.getLocale();
   }

   public String getName() {
      return target.getName();
   }

   public String getNick() {
      return target.getNick();
   }

   public String getPassword() {
      return target.getPassword();
   }

   public String getSurname() {
      return target.getSurname();
   }

   public boolean isActive() {
      return target.isActive();
   }

   public void setActive(final boolean active) {
      target.setActive(active);
   }

   public void setAuthType(final String authType) {
      target.setAuthType(authType);
   }

   public void setDescription(final String description) {
      target.setDescription(description);
   }

   public void setEmail(final String email) {
      target.setEmail(email);
   }

   public void setLocale(final String locale) {
      target.setLocale(locale);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setNick(final String nick) {
      target.setNick(nick);
   }

   public void setPassword(final String password) {
      target.setPassword(password);
   }

   public void setSurname(final String surname) {
      target.setSurname(surname);
   }

   public void setRole(final Role role) {
      this.role = role;
   }

   // Aux operations

   /**
    * Converts the tokenized String of available virtual datacenters provided in
    * the userDto to a list of ids.
    */
   private List<Integer> extractAvailableDatacenters() {
      List<Integer> ids = Lists.newArrayList();

      if (target.getAvailableVirtualDatacenters() != null) {

         StringTokenizer st = new StringTokenizer(target.getAvailableVirtualDatacenters(), ",");

         while (st.hasMoreTokens()) {
            ids.add(Integer.parseInt(st.nextToken()));
         }
      }

      return ids;
   }

   private void setAvailableVirtualDatacenters(final List<Integer> ids) {
      if (ids == null || ids.size() == 0) {
         target.setAvailableVirtualDatacenters("");
      } else {
         Joiner joiner = Joiner.on(",").skipNulls();
         target.setAvailableVirtualDatacenters(joiner.join(ids));
      }
   }

   @Override
   public String toString() {
      return "User [id=" + getId() + ", role=" + getRole() + ", authType=" + getAuthType() + ", description="
            + getDescription() + ", email=" + getEmail() + ", locale=" + getLocale() + ", name=" + getName()
            + ", nick=" + getNick() + ", password=" + getPassword() + ", surname=" + getSurname() + ", active="
            + isActive() + "]";
   }

}
