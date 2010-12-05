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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.vi.ViConstants.PROPERTY_LIBVIRT_DOMAIN_DIR;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_SUSPENDED;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in Libvirt Clients
 * 
 * @author Andrea Turli
 */
public class ViPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_LIBVIRT_DOMAIN_DIR, "/etc/libvirt/qemu");

      properties.setProperty(PROPERTY_TIMEOUT_NODE_SUSPENDED, 120 * 1000 + "");
      // auth fail sometimes happens in EC2, as the rc.local script that injects the
      // authorized key executes after ssh has started
      properties.setProperty("jclouds.ssh.max_retries", "7");
      properties.setProperty("jclouds.ssh.retryable_messages",
            "Auth fail,invalid data,End of IO Stream Read,Connection reset,socket is not established");
      return properties;
   }

   public ViPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public ViPropertiesBuilder() {
      super();
   }
}
