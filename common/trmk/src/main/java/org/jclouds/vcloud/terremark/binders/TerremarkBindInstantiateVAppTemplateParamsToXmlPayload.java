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
package org.jclouds.vcloud.terremark.binders;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.binders.BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.ovf.ResourceType;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class TerremarkBindInstantiateVAppTemplateParamsToXmlPayload extends
         BindInstantiateVCloudExpressVAppTemplateParamsToXmlPayload {

   @Inject
   public TerremarkBindInstantiateVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_API_VERSION) String apiVersion, @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
            @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema, @Nullable @Network URI network,
            @Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
      super(stringBinder, apiVersion, ns, schema, network, fenceMode);
   }

   ThreadLocal<Map<String, String>> propLocal = new ThreadLocal<Map<String, String>>();

   @Override
   protected InstantiateVAppTemplateOptions findOptionsInArgsOrNull(GeneratedHttpRequest<?> gRequest) {
      InstantiateVAppTemplateOptions options = super.findOptionsInArgsOrNull(gRequest);
      if (options != null && options instanceof TerremarkInstantiateVAppTemplateOptions)
         propLocal.set(TerremarkInstantiateVAppTemplateOptions.class.cast(options).getProperties());
      return options;
   }

   @Override
   protected void addVirtualQuantityIfPresent(XMLBuilder instantiationParamsBuilder,
            SortedMap<ResourceType, String> virtualHardwareQuantity) {
      XMLBuilder productSectionBuilder = instantiationParamsBuilder.e("ProductSection").a("xmlns:q1", ns).a(
               "xmlns:ovf", "http://schemas.dmtf.org/ovf/envelope/1");
      if (propLocal.get() != null) {
         for (Entry<String, String> entry : propLocal.get().entrySet()) {
            productSectionBuilder.e("Property").a("xmlns", "http://schemas.dmtf.org/ovf/envelope/1").a("ovf:key",
                     entry.getKey()).a("ovf:value", entry.getValue());
         }
         propLocal.set(null);
      }
      super.addVirtualQuantityIfPresent(instantiationParamsBuilder, virtualHardwareQuantity);
   }

}
