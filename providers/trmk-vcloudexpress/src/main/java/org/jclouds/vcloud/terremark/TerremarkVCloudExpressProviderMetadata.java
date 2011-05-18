/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Terremark's vCloud Express.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudExpressProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "trmk-vcloudexpress";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.COMPUTE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "email";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "password";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Terremark vCloud Express";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("https://vcloudexpress.terremark.com/");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://my.vcloudexpress.terremark.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("https://community.vcloudexpress.terremark.com/en-us/product_docs/m/vcefiles/2342.aspx");
   }

}