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
package org.jclouds.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.strategy.impl.ReturnCredentialsBoundToImage;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;
import org.jclouds.ec2.domain.Image;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class EC2PopulateDefaultLoginCredentialsForImageStrategy extends ReturnCredentialsBoundToImage {
   public EC2PopulateDefaultLoginCredentialsForImageStrategy() {
      this(null, ImmutableMap.<String, Credentials> of(), ImmutableMap.<OsFamily, LoginCredentials>of());
   }

   @Inject
   public EC2PopulateDefaultLoginCredentialsForImageStrategy(@Nullable @Named("image") LoginCredentials creds,
            Map<String, Credentials> credentialStore, Map<OsFamily, LoginCredentials> osFamilyToCredentials) {
      super(creds, credentialStore, osFamilyToCredentials);
   }

   @Override
   public LoginCredentials apply(Object resourceToAuthenticate) {
      if (creds != null)
         return creds;
      Builder credentials = LoginCredentials.builder().user("root");
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
            credentials.user("ubuntu");
            // http://typepad.com/2010/09/introducing-amazon-linux-ami.html
         } else if (owner.equals("137112412989")) {
            credentials.user("ec2-user");
         }
      }
      return credentials.build();
   }
}
