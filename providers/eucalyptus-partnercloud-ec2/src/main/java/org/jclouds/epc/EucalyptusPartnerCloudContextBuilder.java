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

package org.jclouds.epc;

import java.util.List;
import java.util.Properties;

import org.jclouds.ec2.EC2ContextBuilder;
import org.jclouds.epc.config.EucalyptusPartnerCloudComputeServiceContextModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class EucalyptusPartnerCloudContextBuilder extends EC2ContextBuilder {

   public EucalyptusPartnerCloudContextBuilder(Properties props) {
      super(props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new EucalyptusPartnerCloudComputeServiceContextModule());
   }

}
