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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Singleton;

import org.jclouds.ec2.domain.Image;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class EC2PopulateDefaultLoginCredentialsForImageStrategy implements
      PopulateDefaultLoginCredentialsForImageStrategy {

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      Credentials credentials = new Credentials("root", null);
      if (resourceToAuthenticate != null) {
         String owner = null;
         if (resourceToAuthenticate instanceof Image) {
            owner = Image.class.cast(resourceToAuthenticate).getImageOwnerId();
         } else if (resourceToAuthenticate instanceof org.jclouds.compute.domain.Image) {
            owner = org.jclouds.compute.domain.Image.class.cast(resourceToAuthenticate).getUserMetadata().get("owner");
         }
         checkArgument(owner != null, "Resource must be an image (for EC2)");
         // canonical/alestic images use the ubuntu user to login
         if (owner.matches("063491364108|099720109477")) {
            credentials = new Credentials("ubuntu", null);
            // http://typepad.com/2010/09/introducing-amazon-linux-ami.html
         } else if (owner.equals("137112412989")) {
            credentials = new Credentials("ec2-user", null);
         }
      }
      return credentials;
   }
}
