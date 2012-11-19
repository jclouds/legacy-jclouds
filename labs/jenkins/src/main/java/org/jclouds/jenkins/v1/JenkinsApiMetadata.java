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
package org.jclouds.jenkins.v1;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.jenkins.v1.config.JenkinsRestClientModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for Jenkins 1.0 API
 * 
 * @author Adrian Cole
 */
public class JenkinsApiMetadata extends BaseRestApiMetadata {
   
   public static final String ANONYMOUS_IDENTITY = "ANONYMOUS";

   public static final TypeToken<RestContext<JenkinsApi, JenkinsAsyncApi>> CONTEXT_TOKEN = new TypeToken<RestContext<JenkinsApi, JenkinsAsyncApi>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public JenkinsApiMetadata() {
      this(new Builder());
   }

   protected JenkinsApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(JenkinsApi.class, JenkinsAsyncApi.class);
          id("jenkins")
         .name("Jenkins API")
         .identityName("Username (or " + ANONYMOUS_IDENTITY + " if anonymous)")
         .defaultIdentity(ANONYMOUS_IDENTITY)
         .credentialName("Password")
         .defaultCredential(ANONYMOUS_IDENTITY)
         .documentation(URI.create("https://wiki.jenkins-ci.org/display/JENKINS/Remote+access+API"))
         .version("1.0")
         .defaultEndpoint("http://localhost:8080")
         .defaultProperties(JenkinsApiMetadata.defaultProperties())
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(JenkinsRestClientModule.class));
      }
      
      @Override
      public JenkinsApiMetadata build() {
         return new JenkinsApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
