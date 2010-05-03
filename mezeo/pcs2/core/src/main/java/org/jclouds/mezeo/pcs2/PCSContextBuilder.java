/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Properties;

import org.jclouds.mezeo.pcs2.config.PCSContextModule;
import org.jclouds.mezeo.pcs2.config.PCSRestClientModule;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class PCSContextBuilder extends RestContextBuilder<PCSAsyncClient, PCSClient> {

   public PCSContextBuilder(String providerName, Properties props) {
      super(providerName, new TypeLiteral<PCSAsyncClient>() {
      }, new TypeLiteral<PCSClient>() {
      }, props);
      checkNotNull(properties.getProperty(PCSConstants.PROPERTY_PCS2_USER));
      checkNotNull(properties.getProperty(PCSConstants.PROPERTY_PCS2_PASSWORD));
      checkNotNull(properties.getProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT));
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new PCSRestClientModule());
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new PCSContextModule(providerName));
   }

}
