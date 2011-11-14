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
package org.jclouds.compute.functions;

import static org.jclouds.domain.Credentials.NO_CREDENTIALS;

import javax.inject.Singleton;

import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DefaultCredentialsFromImageOrOverridingCredentials implements Function<Template, Credentials> {

   @Override
   public Credentials apply(Template template) {
      TemplateOptions options = template.getOptions();
      Credentials creds = template.getImage().getDefaultCredentials();
      Credentials overridingCredentials = options.getOverridingCredentials();
      Credentials overrideCreds = (overridingCredentials != null) ? overridingCredentials : NO_CREDENTIALS;
      if (creds == null)
         creds = overrideCreds;
      if (overrideCreds.identity != null)
         creds = creds.toBuilder().identity(overrideCreds.identity).build();
      if (overrideCreds.credential != null)
         creds = creds.toBuilder().credential(overrideCreds.credential).build();
      return creds.equals(NO_CREDENTIALS) ? null : creds;
   }

}