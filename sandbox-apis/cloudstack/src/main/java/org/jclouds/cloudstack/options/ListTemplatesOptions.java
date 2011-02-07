/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.options;

import org.jclouds.cloudstack.domain.TemplateFilter;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what templates information is returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listTemplates.html" />
 * @author Adrian Cole
 */
public class ListTemplatesOptions extends BaseHttpRequestOptions {
   public ListTemplatesOptions() {
      filter(TemplateFilter.EXECUTABLE);
   }

   public static final ListTemplatesOptions NONE = new ListTemplatesOptions();

   /**
    * @param filter
    *           how to constrain the list
    */
   public ListTemplatesOptions filter(TemplateFilter filter) {
      this.queryParameters.replaceValues("templatefilter", ImmutableSet.of(filter.toString()));
      return this;
   }

   /**
    * @param id
    *           the template ID
    */
   public ListTemplatesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * 
    * @param account
    *           account id
    * @param domain
    *           domain id
    */
   public ListTemplatesOptions accountInDomain(String account, String domain) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return domainId(domain);
   }

   /**
    * @param name
    *           the template name
    */
   public ListTemplatesOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param domainId
    *           list all templates in specified domain. If used with the account parameter, lists
    *           all templates for an account in the specified domain.
    */
   public ListTemplatesOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId));
      return this;

   }

   /**
    * @param zoneId
    *           list templates by zoneId.
    */
   public ListTemplatesOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId));
      return this;

   }

   /**
    * @param hypervisor
    *           the hypervisor for which to restrict the search
    */
   public ListTemplatesOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor));
      return this;
   }

   public static class Builder {

      /**
       * @see ListTemplatesOptions#filter
       */
      public static ListTemplatesOptions filter(TemplateFilter filter) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.filter(filter);
      }

      /**
       * @see ListTemplatesOptions#domainId
       */
      public static ListTemplatesOptions domainId(String id) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListTemplatesOptions#accountInDomain
       */
      public static ListTemplatesOptions accountInDomain(String account, String domain) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListTemplatesOptions#id
       */
      public static ListTemplatesOptions id(String id) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.id(id);
      }

      /**
       * @see ListTemplatesOptions#name
       */
      public static ListTemplatesOptions name(String name) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.name(name);
      }

      /**
       * @see ListTemplatesOptions#zoneId
       */
      public static ListTemplatesOptions zoneId(String id) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.zoneId(id);
      }

      /**
       * @see ListTemplatesOptions#hypervisor
       */
      public static ListTemplatesOptions hypervisor(String hypervisor) {
         ListTemplatesOptions options = new ListTemplatesOptions();
         return options.hypervisor(hypervisor);
      }
   }

}
