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
package org.jclouds.vcloud.terremark.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Map;
import java.util.SortedMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jclouds.rest.binders.BindToStringPayload;
import org.jclouds.vcloud.binders.BindInstantiateVAppTemplateParamsToXmlPayload;
import org.jclouds.vcloud.domain.ResourceType;

import com.jamesmurty.utils.XMLBuilder;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class TerremarkBindInstantiateVAppTemplateParamsToXmlPayload extends
         BindInstantiateVAppTemplateParamsToXmlPayload {

   @Inject
   public TerremarkBindInstantiateVAppTemplateParamsToXmlPayload(BindToStringPayload stringBinder,
            @Named(PROPERTY_VCLOUD_XML_NAMESPACE) String ns,
            @Named(PROPERTY_VCLOUD_XML_SCHEMA) String schema,
            @Named(PROPERTY_VCLOUD_DEFAULT_NETWORK) String network,
            OptionalConstantsHolder optionalDefaults) {
      super(stringBinder, ns, schema, network, optionalDefaults);
   }

   @Override
   protected String generateXml(String name, String template, Map<String, String> properties,
            SortedMap<ResourceType, String> virtualHardwareQuantity, String network)
            throws ParserConfigurationException, FactoryConfigurationError, TransformerException {
      checkNotNull(virtualHardwareQuantity.get(ResourceType.PROCESSOR),
               "cpuCount must be present in instantiateVapp on terremark");
      checkNotNull(virtualHardwareQuantity.get(ResourceType.MEMORY),
               "memorySizeMegabytes must be present in instantiateVapp on terremark");
      checkArgument(virtualHardwareQuantity.get(ResourceType.DISK_DRIVE) == null,
               "diskSizeKilobytes no settable on instantiateVapp on terremark");
      return super.generateXml(name, template, properties, virtualHardwareQuantity, network);
   }

   @Override
   protected void addPropertiesifPresent(XMLBuilder instantiationParamsBuilder,
            Map<String, String> properties) {
      if (properties.size() == 0) { // terremark requires the product section.
         instantiationParamsBuilder.e("ProductSection").a("xmlns:q1", ns).a("xmlns:ovf",
                  "http://schemas.dmtf.org/ovf/envelope/1");
      } else {
         super.addPropertiesifPresent(instantiationParamsBuilder, properties);
      }
   }

}
