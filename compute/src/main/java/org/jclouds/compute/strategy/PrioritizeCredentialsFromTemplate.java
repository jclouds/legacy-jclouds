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
package org.jclouds.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.domain.Credentials.NO_CREDENTIALS;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class PrioritizeCredentialsFromTemplate {
   private final Function<Template, Credentials> credentialsFromImageOrTemplateOptions;

   @Inject
   public PrioritizeCredentialsFromTemplate(Function<Template, Credentials> credentialsFromImageOrTemplateOptions) {
      this.credentialsFromImageOrTemplateOptions = checkNotNull(credentialsFromImageOrTemplateOptions,
            "credentialsFromImageOrTemplateOptions");
   }

   public Credentials apply(Template template, Credentials fromNode) {
      Credentials creds = (fromNode != null) ? fromNode : NO_CREDENTIALS;
      Credentials credsFromParameters = credentialsFromImageOrTemplateOptions.apply(template);
      if (credsFromParameters != null) {
         if (credsFromParameters.identity != null)
            creds = creds.toBuilder().identity(credsFromParameters.identity).build();
         if (credsFromParameters.credential != null)
            creds = creds.toBuilder().credential(credsFromParameters.credential).build();
      }
      if (creds.equals(NO_CREDENTIALS))
         creds = null;
      return creds;
   }

}