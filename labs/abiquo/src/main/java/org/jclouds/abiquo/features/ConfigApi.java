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

import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;

import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.LicensesDto;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;

/**
 * Provides synchronous access to Abiquo Admin API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see ConfigAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
public interface ConfigApi {
   /*********************** License ***********************/

   /**
    * List all licenses.
    * 
    * @return The list of licenses.
    */
   @EnterpriseEdition
   LicensesDto listLicenses();

   /**
    * List all active/inactive licenses.
    * 
    * @param options
    *           Optional query params.
    * @return The list of licenses.
    */
   @EnterpriseEdition
   LicensesDto listLicenses(LicenseOptions options);

   /**
    * Add a new license.
    * 
    * @param license
    *           The license to add.
    * @return The added license.
    */
   @EnterpriseEdition
   LicenseDto addLicense(LicenseDto license);

   /**
    * Removes an existing license.
    * 
    * @param license
    *           The license to delete.
    */
   @EnterpriseEdition
   void removeLicense(LicenseDto license);

   /*********************** Privilege ***********************/

   /**
    * List all privileges in the system.
    * 
    * @return The list of privileges.
    */
   PrivilegesDto listPrivileges();

   /**
    * Get the given privilege.
    * 
    * @param privilegeId
    *           The id of the privilege.
    * @return The privilege or <code>null</code> if it does not exist.
    */
   PrivilegeDto getPrivilege(Integer privilegeId);

   /*********************** System Properties ***********************/

   /**
    * List all system properties.
    * 
    * @return The list of properties.
    */
   SystemPropertiesDto listSystemProperties();

   /**
    * List properties with options.
    * 
    * @param options
    *           Optional query params.
    * @return The list of system properties.
    */
   SystemPropertiesDto listSystemProperties(PropertyOptions options);

   /**
    * Updates a system property.
    * 
    * @param property
    *           The new attributes for the system property.
    * @return The updated system property.
    */
   SystemPropertyDto updateSystemProperty(SystemPropertyDto property);

   /*********************** Category ***********************/

   /**
    * List all categories.
    * 
    * @return The list of categories.
    */
   CategoriesDto listCategories();

   /**
    * Get the given category.
    * 
    * @param categoryId
    *           The id of the category.
    * @return The category or <code>null</code> if it does not exist.
    */
   CategoryDto getCategory(Integer categoryId);

   /**
    * Create a new category.
    * 
    * @param icon
    *           The category to be created.
    * @return The created category.
    */
   CategoryDto createCategory(CategoryDto category);

   /**
    * Updates an existing category.
    * 
    * @param category
    *           The new attributes for the category.
    * @return The updated category.
    */
   CategoryDto updateCategory(CategoryDto category);

   /**
    * Deletes an existing category.
    * 
    * @param icon
    *           The category to delete.
    */
   void deleteCategory(CategoryDto category);
}
