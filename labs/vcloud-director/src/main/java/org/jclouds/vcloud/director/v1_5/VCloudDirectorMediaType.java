/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Resource Types used in VCloud.
 * 
 * The object type, specified as a MIME content type, of the object that the link references. This
 * attribute is present only for links to objects. It is not present for links to actions.
 * 
 * @see javax.ws.rs.core.MediaType;
 */
public class VCloudDirectorMediaType {

   // Wildcarded media types

   public static final String ANY = "*/*";

   public static final String ANY_IMAGE = "image/*";

   public static final String TEXT_XML = "text/xml";

   /** The default {@literal Accept} header for the vCloud API. */
   public static final String APPLICATION_XML_1_5 = "application/*+xml;version=1.5";
   
   // Specific media types

   public static final String SESSION = "application/vnd.vmware.vcloud.session+xml";

   public static final String ERROR = "application/vnd.vmware.vcloud.error+xml";

   public static final String ORG_LIST = "application/vnd.vmware.vcloud.orgList+xml";

   public static final String METADATA = "application/vnd.vmware.vcloud.metadata+xml";

   public static final String METADATA_ENTRY = "*/*"; // No media type (?) 

   public static final String METADATA_VALUE = "application/vnd.vmware.vcloud.metadata.value+xml";

   public static final String ORG = "application/vnd.vmware.vcloud.org+xml";

   public static final String TASKS_LIST = "application/vnd.vmware.vcloud.tasksList+xml";

   public static final String TASK = "application/vnd.vmware.vcloud.task+xml";
   
   public static final String NETWORK = "application/vnd.vmware.vcloud.network+xml";

   public static final String ORG_NETWORK = "application/vnd.vmware.vcloud.orgNetwork+xml";

   public static final String CATALOG = "application/vnd.vmware.vcloud.catalog+xml";

   public static final String CATALOG_ITEM = "application/vnd.vmware.vcloud.catalogItem+xml";

   public static final String CATALOG_ITEMS = "application/vnd.vmware.vcloud.catalogItems+xml";

   public static final String CATALOGS_LIST = "application/vnd.vmware.vcloud.catalogsList+xml";

   public static final String PROPERTY = "application/vnd.vmware.vcloud.property+xml";

   public static final String MEDIA = "application/vnd.vmware.vcloud.media+xml";

   public static final String OWNER = "application/vnd.vmware.vcloud.owner+xml";
   
   public static final String VDC = "application/vnd.vmware.vcloud.vdc+xml";

   public static final String ADMIN_USER = "application/vnd.vmware.admin.user+xml";
   
   public static final String VAPP = "application/vnd.vmware.vcloud.vApp+xml";
   
   public static final String VAPP_TEMPLATE = "application/vnd.vmware.vcloud.vAppTemplate+xml";
   
   public static final String VM = "application/vnd.vmware.vcloud.vm+xml";
   
   public static final String CAPTURE_VAPP_PARAMS = "application/vnd.vmware.vcloud.captureVAppParams+xml";
   
   public static final String CLONE_MEDIA_PARAMS = "application/vnd.vmware.vcloud.cloneMediaParams+xml";
   
   public static final String CLONE_VAPP_PARAMS = "application/vnd.vmware.vcloud.cloneVAppParams+xml";
   
   public static final String CLONE_VAPP_TEMPLATE_PARAMS = "application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml";
   
   public static final String COMPOSE_VAPP_PARAMS = "application/vnd.vmware.vcloud.composeVAppParams+xml";
   
   public static final String INSTANTIATE_VAPP_TEMPLATE_PARAMS = "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml";
         
   public static final String UPLOAD_VAPP_TEMPLATE_PARAMS = "application/vnd.vmware.vcloud.uploadVAppTemplateParams+xml";
         
   public static final String QUERY_RESULT_RECORDS = "application/vnd.vmware.vcloud.query.records+xml";
 
   public static final String QUERY_RESULT_REFERENCES = "application/vnd.vmware.vcloud.query.references+xml";

   public static final String QUERY_RESULT_ID_RECORDS = "application/vnd.vmware.vcloud.query.idrecords+xml";   
         
   public static final String CONTROL_ACCESS = "application/vnd.vmware.vcloud.controlAccess+xml";

   public static final String CUSTOMIZATION_SECTION = "application/vnd.vmware.vcloud.customizationSection+xml";

   public static final String GUEST_CUSTOMIZATION_SECTION = "application/vnd.vmware.vcloud.guestCustomizationSection+xml";

   public static final String LEASE_SETTINGS_SECTION =  "application/vnd.vmware.vcloud.leaseSettingsSection+xml";

   public static final String NETWORK_SECTION = "application/vnd.vmware.vcloud.networkSection+xml";

   public static final String NETWORK_CONFIG_SECTION = "application/vnd.vmware.vcloud.networkConfigSection+xml";

   public static final String NETWORK_CONNECTION_SECTION = "application/vnd.vmware.vcloud.networkConnectionSection+xml";
   
   public static final String OPERATING_SYSTEM_SECTION = "application/vnd.vmware.vcloud.operatingSystemSection+xml";
   
   public static final String PRODUCT_SECTION_LIST = "application/vnd.vmware.vcloud.productSections+xml";
   
   public static final String STARTUP_SECTION = "application/vnd.vmware.vcloud.startupSection+xml";

   public static final String VIRTUAL_HARDWARE_SECTION = "application/vnd.vmware.vcloud.virtualHardwareSection+xml";

   public static final String RELOCATE_TEMPLATE = "application/vnd.vmware.vcloud.relocateTemplate+xml";
   
   public static final String ENVELOPE =  "application/vnd.???";
   
   public static final String VM_PENDING_ANSWER = "application/vnd.vmware.vcloud.vmPendingAnswer+xml";
   
   public static final String VM_PENDING_QUESTION = "application/vnd.vmware.vcloud.vmPendingQuestion+xml";

   public static final String OVF_RASD_ITEM = "application/vnd.vmware.vcloud.rasdItem+xml";
   
   public static final String OVF_RASD_ITEMS_LIST = "application/vnd.vmware.vcloud.rasdItemsList+xml";
   
   public static final String ADMIN_CATALOG = "application/vnd.vmware.admin.catalog+xml";
   
   public static final String ADMIN_ORG = "application/vnd.vmware.admin.organization+xml";
   
   public static final String PUBLISH_CATALOG_PARAMS = "application/vnd.vmware.admin.publishCatalogParams+xml";
   
   public static final String GROUP = "application/vnd.vmware.admin.group+xml";
   
   public static final String ORG_VAPP_TEMPLATE_LEASE_SETTINGS = "application/vnd.vmware.admin.vAppTemplateLeaseSettings+xml";
   
   public static final String ORG_LEASE_SETTINGS = "application/vnd.vmware.admin.vAppLeaseSettings+xml";
   
   public static final String ORG_PASSWORD_POLICY_SETTINGS = "application/vnd.vmware.admin.organizationPasswordPolicySettings+xml";
   
   public static final String ORG_LDAP_SETTINGS = "application/vnd.vmware.admin.organizationLdapSettings+xml";
   
   public static final String ORG_GENERAL_SETTINGS = "application/vnd.vmware.admin.organizationGeneralSettings+xml";
   
   public static final String ORG_EMAIL_SETTINGS = "application/vnd.vmware.admin.organizationEmailSettings+xml";
   
   public static final String ORG_SETTINGS = "application/vnd.vmware.admin.orgSettings+xml";
   
   public static final String ADMIN_NETWORK = "application/vnd.vmware.admin.network+xml";
   
   public static final String ADMIN_ORG_NETWORK = "application/vnd.vmware.admin.orgNetwork+xml";
   
   public static final String USER = "application/vnd.vmware.admin.user+xml";
   
   public static final String ROLE = "application/vnd.vmware.admin.role+xml";
    
   public static final String DEPLOY_VAPP_PARAMS = "application/vnd.vmware.vcloud.deployVAppParams+xml";
   
   public static final String RECOMPOSE_VAPP_PARAMS = "application/vnd.vmware.vcloud.recomposeVAppParams+xml";
   
   public static final String RELOCATE_VM_PARAMS = "application/vnd.vmware.vcloud.relocateVmParams+xml";
   
   public static final String UNDEPLOY_VAPP_PARAMS = "application/vnd.vmware.vcloud.undeployVAppParams+xml";
   
   public static final String MEDIA_PARAMS = "application/vnd.vmware.vcloud.mediaInsertOrEjectParams+xml";
   
   public static final String RUNTIME_INFO_SECTION = "application/vnd.vmware.vcloud.runtimeInfoSection+xml";
   
   public static final String SCREEN_TICKET = "application/vnd.vmware.vcloud.screenTicket+xml";
   
   public static final String VAPP_NETWORK = "application/vnd.vmware.vcloud.vAppNetwork+xml";
   
   public static final String ADMIN_VDC = "application/vnd.vmware.admin.vdc+xml";

   public static final String NETWORK_POOL = "application/vnd.vmware.admin.networkPool+xml";

   public static final String ENTITY = "application/vnd.vmware.vcloud.entity+xml";

   public static final String ADMIN = "application/vnd.vmware.admin.vcloud+xml";
   
   /**
    * All acceptable media types.
    */
   public static final List<String> ALL = ImmutableList.of(
         SESSION, ERROR, ORG_LIST, METADATA, METADATA_ENTRY, METADATA_VALUE,
         ORG, TASKS_LIST, TASK, NETWORK, ORG_NETWORK, CATALOG, CATALOG_ITEM,
         CATALOG_ITEMS, CATALOGS_LIST, PROPERTY, MEDIA, OWNER, VDC, ADMIN_USER,
         VAPP, VAPP_TEMPLATE, VM, CAPTURE_VAPP_PARAMS, CLONE_MEDIA_PARAMS,
         CLONE_VAPP_PARAMS, CLONE_VAPP_TEMPLATE_PARAMS, COMPOSE_VAPP_PARAMS,
         INSTANTIATE_VAPP_TEMPLATE_PARAMS, UPLOAD_VAPP_TEMPLATE_PARAMS,
         QUERY_RESULT_RECORDS, QUERY_RESULT_REFERENCES, QUERY_RESULT_ID_RECORDS,
         CONTROL_ACCESS, CUSTOMIZATION_SECTION, GUEST_CUSTOMIZATION_SECTION,
         LEASE_SETTINGS_SECTION, NETWORK_SECTION, NETWORK_CONFIG_SECTION,
         NETWORK_CONNECTION_SECTION, OPERATING_SYSTEM_SECTION,
         PRODUCT_SECTION_LIST, STARTUP_SECTION, VIRTUAL_HARDWARE_SECTION,
         RELOCATE_TEMPLATE, ENVELOPE, VM_PENDING_ANSWER, VM_PENDING_QUESTION,
         OVF_RASD_ITEM, OVF_RASD_ITEMS_LIST, ADMIN_CATALOG, ADMIN_ORG,
         PUBLISH_CATALOG_PARAMS, GROUP, ORG_VAPP_TEMPLATE_LEASE_SETTINGS,
         ORG_LEASE_SETTINGS, ORG_PASSWORD_POLICY_SETTINGS, ORG_LDAP_SETTINGS,
         ORG_GENERAL_SETTINGS, ORG_EMAIL_SETTINGS, ORG_SETTINGS, ADMIN_NETWORK,
         ADMIN_ORG_NETWORK, USER, ROLE, DEPLOY_VAPP_PARAMS, RECOMPOSE_VAPP_PARAMS,
         RELOCATE_VM_PARAMS, UNDEPLOY_VAPP_PARAMS, ADMIN_VDC, MEDIA_PARAMS,
         RUNTIME_INFO_SECTION, SCREEN_TICKET, VAPP_NETWORK,
         TEXT_XML, ADMIN_VDC, NETWORK_POOL, ADMIN_ORG, ENTITY, ADMIN
      );

   // NOTE These lists must be edited whenever a new media type constant is added.

}
