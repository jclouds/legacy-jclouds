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

import java.util.HashMap;
import java.util.Map;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.TemplateDefinitionListDto;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.UserDto;

/**
 * Enterprise domain utilities.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public class EnterpriseResources {
   public static EnterpriseDto enterprisePost() {
      EnterpriseDto enterprise = new EnterpriseDto();
      enterprise.setName("Kalakaua");
      return enterprise;
   }

   public static EnterpriseDto enterprisePut() {
      EnterpriseDto enterprise = enterprisePost();
      enterprise.setId(1);
      enterprise.addLink(new RESTLink("edit", "http://localhost/api/admin/enterprises/1"));
      enterprise.addLink(new RESTLink("limits", "http://localhost/api/admin/enterprises/1/limits"));
      enterprise.addLink(new RESTLink("users", "http://localhost/api/admin/enterprises/1/users"));
      enterprise.addLink(new RESTLink("properties", "http://localhost/api/admin/enterprises/1/properties"));
      enterprise.addLink(new RESTLink("reservedmachines", "http://localhost/api/admin/enterprises/1/reservedmachines"));
      enterprise.addLink(new RESTLink("datacenterrepositories",
            "http://localhost/api/admin/enterprises/1/datacenterrepositories"));
      enterprise.addLink(new RESTLink("externalnetworks",
            "http://localhost/api/admin/enterprises/1/action/externalnetworks"));
      enterprise.addLink(new RESTLink("virtualmachines",
            "http://localhost/api/admin/enterprises/1/action/virtualmachines"));
      enterprise.addLink(new RESTLink("cloud/virtualdatacenters",
            "http://localhost/api/admin/enterprises/1/action/virtualdatacenters"));
      enterprise.addLink(new RESTLink("virtualappliances",
            "http://localhost/api/admin/enterprises/1/action/virtualappliances"));
      enterprise.addLink(new RESTLink("appslib/templateDefinitionLists",
            "http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists"));

      return enterprise;
   }

   public static EnterprisePropertiesDto enterprisePropertiesPut() {
      EnterprisePropertiesDto enterpriseProp = new EnterprisePropertiesDto();
      enterpriseProp.setId(1);
      Map<String, String> props = new HashMap<String, String>();
      props.put("key", "value");
      enterpriseProp.setProperties(props);
      enterpriseProp.addLink(new RESTLink("edit", "http://localhost/api/admin/enterprises/1/properties"));
      enterpriseProp.addLink(new RESTLink("enterprise", "http://localhost/api/admin/enterprises/1"));

      return enterpriseProp;
   }

   public static String enterprisePostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<enterprise>");
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
      buffer.append("<isReservationRestricted>false</isReservationRestricted>");
      buffer.append("<name>Kalakaua</name>");
      buffer.append("<repositoryHard>0</repositoryHard>");
      buffer.append("<repositorySoft>0</repositorySoft>");
      buffer.append("</enterprise>");
      return buffer.toString();
   }

   public static String enterprisePutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<enterprise>");
      buffer.append(link("/admin/enterprises/1", "edit"));
      buffer.append(link("/admin/enterprises/1/limits", "limits"));
      buffer.append(link("/admin/enterprises/1/users", "users"));
      buffer.append(link("/admin/enterprises/1/properties", "properties"));
      buffer.append(link("/admin/enterprises/1/reservedmachines", "reservedmachines"));
      buffer.append(link("/admin/enterprises/1/datacenterrepositories", "datacenterrepositories"));
      buffer.append(link("/admin/enterprises/1/action/externalnetworks", "externalnetworks"));
      buffer.append(link("/admin/enterprises/1/action/virtualmachines", "virtualmachines"));
      buffer.append(link("/admin/enterprises/1/action/virtualdatacenters", "cloud/virtualdatacenters"));
      buffer.append(link("/admin/enterprises/1/action/virtualappliances", "virtualappliances"));
      buffer.append(link("/admin/enterprises/1/appslib/templateDefinitionLists", "appslib/templateDefinitionLists"));
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
      buffer.append("<isReservationRestricted>false</isReservationRestricted>");
      buffer.append("<name>Kalakaua</name>");
      buffer.append("<repositoryHard>0</repositoryHard>");
      buffer.append("<repositorySoft>0</repositorySoft>");
      buffer.append("</enterprise>");
      return buffer.toString();
   }

   public static String enterprisePropertiesPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<enterpriseProperties>");
      buffer.append(link("/admin/enterprises/1/properties", "edit"));
      buffer.append(link("/admin/enterprises/1", "enterprise"));
      buffer.append("<id>1</id>");
      buffer.append("<properties>");
      buffer.append("<entry>");
      buffer.append("<key>key</key>");
      buffer.append("<value>value</value>");
      buffer.append("</entry>");
      buffer.append("</properties>");
      buffer.append("</enterpriseProperties>");
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

   public static TemplateDefinitionListDto templateListPost() {
      TemplateDefinitionListDto templateList = new TemplateDefinitionListDto();
      templateList.setName("myList");
      templateList.setUrl("http://virtualapp-repository.com/vapp1.ovf");
      return templateList;
   }

   public static TemplateDefinitionListDto templateListPut() {
      TemplateDefinitionListDto templateList = templateListPost();
      templateList.setId(1);
      templateList.addLink(new RESTLink("edit",
            "http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1"));
      templateList.addLink(new RESTLink("repositoryStatus",
            "http://localhost/api/admin/enterprises/1/appslib/templateDefinitionLists/1/actions/repositoryStatus"));
      return templateList;
   }

   public static String datacenterLimitsPostPayload() {
      StringBuffer buffer = new StringBuffer();
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

   public static String templateListPostPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<templateDefinitionList>");
      buffer.append("<name>myList</name>");
      buffer.append("<url>http://virtualapp-repository.com/vapp1.ovf</url>");
      buffer.append("</templateDefinitionList>");
      return buffer.toString();
   }

   public static String templateListPutPayload() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("<templateDefinitionList>");
      buffer.append(link("/admin/enterprises/1/appslib/templateDefinitionLists/1", "edit"));
      buffer.append(link("/admin/enterprises/1/appslib/templateDefinitionLists/1/actions/repositoryStatus",
            "repositoryStatus"));
      buffer.append("<id>1</id>");
      buffer.append("<name>myList</name>");
      buffer.append("<url>http://virtualapp-repository.com/vapp1.ovf</url>");
      buffer.append("</templateDefinitionList>");
      return buffer.toString();
   }

   public static String datacenterLimitsPutPayload(final EnterpriseDto enterprise) {
      StringBuffer buffer = new StringBuffer();
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
      StringBuffer buffer = new StringBuffer();
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
      StringBuffer buffer = new StringBuffer();
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
