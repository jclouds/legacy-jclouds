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

package org.jclouds.management;

import com.google.common.base.Charsets;

import com.google.common.base.Strings;
import com.google.common.io.Closeables;
import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.management.functions.ToCompositeData;
import org.jclouds.management.functions.ToTabularData;
import org.jclouds.management.internal.BaseManagementContext;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Core Jclouds MBean for displaying available {@link ApiMetadata}, {@link ProviderMetadata} and {@link Context}s.
 * Also useful for creating contexts.
 */
public class JcloudsManagementCore implements JcloudsManagementCoreMBean, JcloudsManagedBean {

   private final ManagementContext managementContext;

   private final ToCompositeData<ApiMetadata> apiToComposite = ToCompositeData.from(ApiMetadata.class);
   private final ToTabularData<ApiMetadata> apiToTabular = ToTabularData.from(ApiMetadata.class);
   private final ToCompositeData<ProviderMetadata> providerToComposite = ToCompositeData.from(ProviderMetadata.class);
   private final ToTabularData<ProviderMetadata> providerToTabular = ToTabularData.from(ProviderMetadata.class);
   private final ToCompositeData<Context> contextToComposite =  ToCompositeData.from(Context.class);
   private final ToTabularData<Context> contextToTabular = ToTabularData.from(Context.class);

   public JcloudsManagementCore() {
      this(BaseManagementContext.INSTANCE);

   }

   public JcloudsManagementCore(ManagementContext managementContext) {
      this.managementContext = managementContext;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData getApis() throws OpenDataException {
      return apiToTabular.apply(Apis.all());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData findApiById(String id) throws OpenDataException {
      return apiToComposite.apply(Apis.withId(id));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData getProviders() throws OpenDataException {
      return providerToTabular.apply(Providers.all());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData findProviderById(String id) throws OpenDataException {
      return providerToComposite.apply(Providers.withId(id));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TabularData getContexts() throws OpenDataException {
      return contextToTabular.apply(managementContext.listContexts());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompositeData createContext(String id, String name, String identity, String credential, String endpoint, String overrides) throws IOException, OpenDataException {
      Properties props = new Properties();
      if (!Strings.isNullOrEmpty(overrides)) {
         ByteArrayInputStream bis = null;
         try {
            bis = new ByteArrayInputStream(overrides.getBytes(Charsets.UTF_8));
            props.load(bis);
         } finally {
            Closeables.close(bis, true);
         }
      }
      return contextToComposite.apply(ContextBuilder.newBuilder(id).name(name).credentials(identity, credential).endpoint(endpoint).overrides(props).build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return "management";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "core";
   }
}
