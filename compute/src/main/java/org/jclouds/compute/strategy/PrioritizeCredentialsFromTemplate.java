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
package org.jclouds.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.LoginCredentials.Builder;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class PrioritizeCredentialsFromTemplate {
   private final Function<Template, LoginCredentials> credentialsFromImageOrTemplateOptions;

   @Inject
   public PrioritizeCredentialsFromTemplate(Function<Template, LoginCredentials> credentialsFromImageOrTemplateOptions) {
      this.credentialsFromImageOrTemplateOptions = checkNotNull(credentialsFromImageOrTemplateOptions,
            "credentialsFromImageOrTemplateOptions");
   }

   public LoginCredentials apply(Template template, LoginCredentials fromNode) {
      LoginCredentials creds = fromNode;
      LoginCredentials credsFromParameters = credentialsFromImageOrTemplateOptions.apply(template);
      if (credsFromParameters != null) {
         Builder builder = LoginCredentials.builder(creds);
         if (credsFromParameters.getUser() != null)
            builder.user(credsFromParameters.getUser());
         if (credsFromParameters.getPassword() != null)
            builder.password(credsFromParameters.getPassword());
         if (credsFromParameters.getPrivateKey() != null)
            builder.privateKey(credsFromParameters.getPrivateKey());
         if (credsFromParameters.shouldAuthenticateSudo())
            builder.authenticateSudo(true);
         creds = builder.build();
      }
      return creds;
   }

}
