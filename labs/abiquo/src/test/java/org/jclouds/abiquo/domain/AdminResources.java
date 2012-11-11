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

package org.jclouds.abiquo.domain;

import static org.jclouds.abiquo.domain.DomainUtils.link;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.UserDto;

/**
 * Enterprise domain utilities.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class AdminResources {
   public static RoleDto rolePost() {
      RoleDto role = new RoleDto();
      role.addLink(new RESTLink("privileges", "http://localhost/api/admin/roles/1/action/privileges"));
      role.setName("HAWAIAN_ADMIN");
      return role;
   }

   public static RoleDto rolePut() {
      RoleDto role = rolePost();
      role.setId(1);
      role.addLink(new RESTLink("edit", "http://localhost/api/admin/roles/1"));

      return role;
   }

   public static String rolePostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<role>");
      buffer.append(link("/admin/roles/1/action/privileges", "privileges"));
      buffer.append("<blocked>false</blocked>");
      buffer.append("<name>HAWAIAN_ADMIN</name>");
      buffer.append("</role>");
      return buffer.toString();
   }

   public static String rolePutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<role>");
      buffer.append(link("/admin/roles/1/action/privileges", "privileges"));
      buffer.append(link("/admin/roles/1", "edit"));
      buffer.append("<blocked>false</blocked>");
      buffer.append("<id>1</id>");
      buffer.append("<name>HAWAIAN_ADMIN</name>");
      buffer.append("</role>");
      return buffer.toString();
   }

   public static DatacenterLimitsDto datacenterLimitsPost() {
      DatacenterLimitsDto limits = new DatacenterLimitsDto();
      limits.setCpuCountLimits(0, 0);
      limits.setHdLimitsInMb(0, 0);
      limits.setPublicIPLimits(0, 0);
      limits.setRamLimitsInMb(0, 0);
      limits.setStorageLimits(0, 0);
      limits.setVlansLimits(0, 0);
      limits.setRepositoryHardLimitsInMb(0);
      limits.setRepositorySoftLimitsInMb(0);
      return limits;
   }

   public static DatacenterLimitsDto datacenterLimitsPut(final EnterpriseDto enterprise) {
      DatacenterLimitsDto limits = datacenterLimitsPost();
      limits.setId(1);
      limits.addLink(new RESTLink("edit", "http://localhost/api/admin/enterprises/" + enterprise.getId() + "/limits/1"));
      return limits;
   }

   public static String datacenterLimitsPostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<limit>");
      buffer.append("<cpuHard>0</cpuHard>");
      buffer.append("<cpuSoft>0</cpuSoft>");
      buffer.append("<hdHard>0</hdHard>");
      buffer.append("<hdSoft>0</hdSoft>");
      buffer.append("<publicIpsHard>0</publicIpsHard>");
      buffer.append("<publicIpsSoft>0</publicIpsSoft>");
      buffer.append("<ramHard>0</ramHard>");
      buffer.append("<ramSoft>0</ramSoft>");
      buffer.append("<storageHard>0</storageHard>");
      buffer.append("<storageSoft>0</storageSoft>");
      buffer.append("<vlansHard>0</vlansHard>");
      buffer.append("<vlansSoft>0</vlansSoft>");
      buffer.append("<repositoryHard>0</repositoryHard>");
      buffer.append("<repositorySoft>0</repositorySoft>");
      buffer.append("</limit>");
      return buffer.toString();
   }

   public static String datacenterLimitsPutPayload(final EnterpriseDto enterprise) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<limit>");
      buffer.append(link("/admin/enterprises/" + enterprise.getId() + "/limits/1", "edit"));
      buffer.append("<cpuHard>0</cpuHard>");
      buffer.append("<cpuSoft>0</cpuSoft>");
      buffer.append("<hdHard>0</hdHard>");
      buffer.append("<hdSoft>0</hdSoft>");
      buffer.append("<publicIpsHard>0</publicIpsHard>");
      buffer.append("<publicIpsSoft>0</publicIpsSoft>");
      buffer.append("<ramHard>0</ramHard>");
      buffer.append("<ramSoft>0</ramSoft>");
      buffer.append("<storageHard>0</storageHard>");
      buffer.append("<storageSoft>0</storageSoft>");
      buffer.append("<vlansHard>0</vlansHard>");
      buffer.append("<vlansSoft>0</vlansSoft>");
      buffer.append("<id>1</id>");
      buffer.append("<repositoryHard>0</repositoryHard>");
      buffer.append("<repositorySoft>0</repositorySoft>");
      buffer.append("</limit>");
      return buffer.toString();
   }

   public static String userPostPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<user>");
      buffer.append(link("/admin/roles/1", "role"));
      buffer.append("<active>true</active>");
      buffer.append("<authType>ABIQUO</authType>");
      buffer.append("<description>A hawaian user</description>");
      buffer.append("<email>abe.joha@aloha.com</email>");
      buffer.append("<locale>en_US</locale>");
      buffer.append("<name>Aberahama</name>");
      buffer.append("<nick>abejo</nick>");
      buffer.append("<password>c69a39bd64ffb77ea7ee3369dce742f3</password>");
      buffer.append("<surname>Johanson</surname>");
      buffer.append("</user>");
      return buffer.toString();
   }

   public static UserDto userPost() {
      UserDto user = new UserDto();
      user.setName("Aberahama");
      user.setSurname("Johanson");
      user.setDescription("A hawaian user");
      user.setEmail("abe.joha@aloha.com");
      user.setNick("abejo");
      user.setAuthType("ABIQUO");
      user.setLocale("en_US");
      user.setActive(true);
      user.setPassword("c69a39bd64ffb77ea7ee3369dce742f3");
      user.addLink(new RESTLink("role", "http://localhost/api/admin/roles/1"));
      return user;
   }

   public static String userPutPayload() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("<user>");
      buffer.append(link("/admin/roles/1", "role"));
      buffer.append(link("/admin/enterprises/1/users/1", "edit"));
      buffer.append(link("/admin/enterprises/1", "enterprise"));
      buffer.append(link("/admin/enterprises/1/users/1/action/virtualmachines", "virtualmachines"));
      buffer.append("<active>true</active>");
      buffer.append("<authType>ABIQUO</authType>");
      buffer.append("<description>A hawaian user</description>");
      buffer.append("<email>abe.joha@aloha.com</email>");
      buffer.append("<id>1</id>");
      buffer.append("<locale>en_US</locale>");
      buffer.append("<name>Aberahama</name>");
      buffer.append("<nick>abejo</nick>");
      buffer.append("<password>c69a39bd64ffb77ea7ee3369dce742f3</password>");
      buffer.append("<surname>Johanson</surname>");
      buffer.append("</user>");
      return buffer.toString();
   }

   public static UserDto userPut() {
      UserDto user = userPost();
      user.setId(1);
      user.addLink(new RESTLink("edit", "http://localhost/api/admin/enterprises/1/users/1"));
      user.addLink(new RESTLink("enterprise", "http://localhost/api/admin/enterprises/1"));
      user.addLink(new RESTLink("virtualmachines",
            "http://localhost/api/admin/enterprises/1/users/1/action/virtualmachines"));
      return user;
   }
}
