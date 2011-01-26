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

package org.jclouds.vi.compute;

import static org.jclouds.vi.reference.ViConstants.PROPERTY_VI_XML_NAMESPACE;
import static org.jclouds.vi.reference.ViConstants.PROPERTY_VI_IGNORE_CERTIFICATE;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

import com.vmware.vim25.mo.ServiceInstance;

/**
 * Builds properties used in vi Clients
 * 
 * @author Andrea Turli
 */
public class ViPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_VI_XML_NAMESPACE, ServiceInstance.VIM25_NAMESPACE);
      properties.setProperty(PROPERTY_VI_IGNORE_CERTIFICATE, "true");
      return properties;
   }

   public ViPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public ViPropertiesBuilder() {
      super();
   }
}
