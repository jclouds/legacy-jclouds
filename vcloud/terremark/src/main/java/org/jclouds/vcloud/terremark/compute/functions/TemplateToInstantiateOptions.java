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

package org.jclouds.vcloud.terremark.compute.functions;

import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.processorCount;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.vcloud.terremark.compute.options.TerremarkVCloudTemplateOptions;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;

import com.google.common.base.Function;

/**
 * @author Adrian Cole
 */
@Singleton
public class TemplateToInstantiateOptions implements Function<Template, TerremarkInstantiateVAppTemplateOptions> {

   @Override
   public TerremarkInstantiateVAppTemplateOptions apply(Template from) {
      TerremarkInstantiateVAppTemplateOptions options = processorCount(
               Double.valueOf(from.getSize().getCores()).intValue()).memory(from.getSize().getRam());
      if (!from.getOptions().shouldBlockUntilRunning())
         options.block(false);
      String sshKeyFingerprint = TerremarkVCloudTemplateOptions.class.cast(from.getOptions()).getSshKeyFingerprint();
      if (sshKeyFingerprint != null)
         options.sshKeyFingerprint(sshKeyFingerprint);
      return options;
   }
}