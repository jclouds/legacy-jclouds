/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.impl.ReturnCredentialsBoundToImage;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseVAppTemplateDescriptionToGetDefaultLoginCredentials extends ReturnCredentialsBoundToImage {
   @Inject
   public ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(@Nullable @Named("image") LoginCredentials creds,
            Map<String, Credentials> credentialStore, Map<OsFamily, LoginCredentials> osFamilyToCredentials) {
      super(creds, credentialStore, osFamilyToCredentials);
   }

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   public static final Pattern USER_PASSWORD_PATTERN = Pattern
         .compile(".*[Uu]sername: ([a-z]+) ?.*\n[Pp]assword: ([^ \n\r]+) ?\r?\n.*");

   @Override
   public LoginCredentials apply(Object resourceToAuthenticate) {
      if (creds != null)
         return creds;
      checkNotNull(resourceToAuthenticate);
      checkArgument(resourceToAuthenticate instanceof VAppTemplate, "Resource must be an VAppTemplate (for Terremark)");
      VAppTemplate template = (VAppTemplate) resourceToAuthenticate;
      String search = template.getDescription() != null ? template.getDescription() : template.getName();
      if (search.indexOf("Windows") >= 0) {
         return LoginCredentials.builder().user("Administrator").build();
      } else {
         Matcher matcher = USER_PASSWORD_PATTERN.matcher(search);
         if (matcher.find()) {
            return LoginCredentials.builder().user(matcher.group(1)).password(matcher.group(2)).authenticateSudo(true).build();
         } else {
            logger.warn("could not parse username/password for image: " + template.getHref() + "\n" + search);
            return null;
         }
      }
   }
}
