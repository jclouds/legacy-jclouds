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
package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.compute.util.VCloudComputeUtils.getCredentialsFrom;

import javax.inject.Singleton;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.VAppTemplate;

/**
 * @author Adrian Cole
 */
@Singleton
public class GetLoginCredentialsFromGuestConfiguration implements PopulateDefaultLoginCredentialsForImageStrategy {

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      checkNotNull(resourceToAuthenticate);
      checkArgument(resourceToAuthenticate instanceof VAppTemplate, "Resource must be an VAppTemplate");
      return getCredentialsFrom(VAppTemplate.class.cast(resourceToAuthenticate));
   }
}
