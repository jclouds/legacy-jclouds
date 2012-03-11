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

   public static final String ANY = "*/*";

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
   
   public static final String V_APP = "application/vnd.vmware.vcloud.vApp+xml";
   
   public static final String V_APP_TEMPLATE = "application/vnd.vmware.vcloud.vAppTemplate+xml";
   
   public static final String CAPTURE_VAPP_PARAMS = 
         "application/vnd.vmware.vcloud.captureVAppParams+xml";
   
   public static final String CLONE_MEDIA_PARAMS = 
         "application/vnd.vmware.vcloud.cloneMediaParams+xml";
   
   public static final String CLONE_V_APP_PARAMS = 
         "application/vnd.vmware.vcloud.cloneVAppParams+xml";
   
   public static final String CLONE_V_APP_TEMPLATE_PARAMS = 
         "application/vnd.vmware.vcloud.cloneVAppTemplateParams+xml";
   
   public static final String COMPOSE_VAPP_PARAMS = 
         "application/vnd.vmware.vcloud.composeVAppParams+xml";
   
   public static final String INSTANTIATE_VAPP_TEMPLATE_PARAMS = 
         "application/vnd.vmware.vcloud.instantiateVAppTemplateParams+xml";
         
   public static final String UPLOAD_VAPP_TEMPLATE_PARAMS = 
         "application/vnd.vmware.vcloud.uploadVAppTemplateParams+xml";
         
   public static final String QUERY_RESULT_RECORDS = "application/vnd.vmware.vcloud.query.records+xml";
 
   public static final String QUERY_RESULT_REFERENCES = "application/vnd.vmware.vcloud.query.references+xml";

   public static final String QUERY_RESULT_ID_RECORDS = "application/vnd.vmware.vcloud.query.idrecords+xml";   
         
   public static final String CONTROL_ACCESS = "application/vnd.vmware.vcloud.controlAccess+xml";

   public static final String VAPP_TEMPLATE = "application/vnd.vmware.vcloud.vAppTemplate+xml";

   public static final String CUSTOMIZATION_SECTION = "application/vnd.vmware.vcloud.customizationSection+xml";

   public static final String GUEST_CUSTOMIZATION_SECTION = "application/vnd.vmware.vcloud.guestCustomizationSection+xml";

   public static final String NETWORK_SECTION = "application/vnd.vmware.vcloud.networkSection+xml";

   public static final String NETWORK_CONFIG_SECTION = "application/vnd.vmware.vcloud.networkConfigSection+xml";

   public static final String NETWORK_CONNECTION_SECTION = "application/vnd.vmware.vcloud.networkConnectionSection+xml";
   
   public static final String PRODUCT_SECTION_LIST = "application/vnd.vmware.vcloud.productSection+xml";

   public static final String RELOCATE_TEMPLATE = "application/vnd.vmware.vcloud.relocateTemplate+xml";

   public static final String LEASE_SETTINGS_SECTION =  "application/vnd.vmware.vcloud.leaseSettingsSection+xml";
   
   public static final String ENVELOPE =  "application/vnd.???";
   
   public static final String ADMIN_CATALOG = "application/vnd.vmware.admin.catalog+xml";
   
   public static final String ADMIN_ORGANIZATION = "application/vnd.vmware.admin.organization+xml";
   
   public static final String PUBLISH_CATALOG_PARAMS = "application/vnd.vmware.admin.publishCatalogParams+xml";
   
   public static final String GROUP = "application/vnd.vmware.admin.group+xml";
   
   public static final String ORG_VAPP_TEMPLATE_LEASE_SETTINGS = "application/vnd.vmware.admin.vAppTemplateLeaseSettings+xml";
   
   public static final String ORG_LEASE_SETTINGS = "application/vnd.vmware.admin.vAppLeaseSettings+xml";
   
   public static final String ORG_PASSWORD_POLICY_SETTINGS = "application/vnd.vmware.admin.organizationPasswordPolicySettings+xml";
   
   public static final String ORG_LDAP_SETTINGS = "application/vnd.vmware.admin.organizationLdapSettings+xml";
   
   public static final String ORG_GENERAL_SETTINGS = "application/vnd.vmware.admin.organizationGeneralSettings+xml";
   
   public static final String ORG_EMAIL_SETTINGS = "application/vnd.vmware.admin.organizationEmailSettings+xml";
   
   /**
    * 
    * All acceptable media types.
    *
    * This list must be updated whenever a new media type constant is added.
    */
   public static final List<String> ALL = ImmutableList.of(
            SESSION, ERROR, ORG_LIST, METADATA, METADATA_ENTRY,
            METADATA_VALUE, ORG, TASKS_LIST, TASK, NETWORK, ORG_NETWORK,
            CATALOG, CATALOG_ITEM, CATALOG_ITEMS, CATALOGS_LIST, PROPERTY,
            MEDIA, OWNER, VDC, ADMIN_USER, V_APP, V_APP_TEMPLATE, 
            CAPTURE_VAPP_PARAMS, CLONE_V_APP_PARAMS, CLONE_V_APP_TEMPLATE_PARAMS,
            COMPOSE_VAPP_PARAMS, INSTANTIATE_VAPP_TEMPLATE_PARAMS,
            UPLOAD_VAPP_TEMPLATE_PARAMS, ADMIN_CATALOG, ADMIN_ORGANIZATION,
            QUERY_RESULT_RECORDS, QUERY_RESULT_REFERENCES, QUERY_RESULT_ID_RECORDS,
            CONTROL_ACCESS, VAPP_TEMPLATE, CUSTOMIZATION_SECTION, GUEST_CUSTOMIZATION_SECTION, 
            NETWORK_SECTION, NETWORK_CONFIG_SECTION, NETWORK_CONNECTION_SECTION,   
            CLONE_MEDIA_PARAMS, LEASE_SETTINGS_SECTION, RELOCATE_TEMPLATE, ENVELOPE,
            PUBLISH_CATALOG_PARAMS, GROUP, ORG_VAPP_TEMPLATE_LEASE_SETTINGS,
            ORG_LEASE_SETTINGS, ORG_PASSWORD_POLICY_SETTINGS, ORG_LDAP_SETTINGS,
            ORG_GENERAL_SETTINGS, ORG_EMAIL_SETTINGS
      );
}
