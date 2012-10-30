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
package org.jclouds.apis.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTION_REUSE;
import static org.jclouds.Constants.PROPERTY_MAX_SESSION_FAILURES;
import static org.jclouds.Constants.PROPERTY_PRETTY_PRINT_PAYLOADS;
import static org.jclouds.Constants.PROPERTY_SCHEDULER_THREADS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.jclouds.Context;
import org.jclouds.View;
import org.jclouds.apis.ApiMetadata;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * The BaseApiMetadata class is an abstraction of {@link ApiMetadata} to be extended by those
 * implementing ApiMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader} to work properly.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public abstract class BaseApiMetadata implements ApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -8080028444066498110L;

   public static Properties defaultProperties() {
      Properties props = new Properties();
      // TODO: move this to ApiMetadata
      props.setProperty(PROPERTY_ISO3166_CODES, "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      props.setProperty(PROPERTY_SO_TIMEOUT, 60000 + "");
      props.setProperty(PROPERTY_CONNECTION_TIMEOUT, 60000 + "");
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 20 + "");
      props.setProperty(PROPERTY_USER_THREADS, 0 + "");
      props.setProperty(PROPERTY_SCHEDULER_THREADS, 10 + "");
      props.setProperty(PROPERTY_MAX_CONNECTION_REUSE, 75 + "");
      props.setProperty(PROPERTY_MAX_SESSION_FAILURES, 2 + "");
      props.setProperty(PROPERTY_SESSION_INTERVAL, 60 + "");
      props.setProperty(PROPERTY_PRETTY_PRINT_PAYLOADS, "true");
      return props;
   }
   
   public static class Builder implements ApiMetadata.Builder {
      protected String id;
      protected String name;
      protected Set<TypeToken<? extends View>> views = ImmutableSet.of();
      protected String endpointName = "https endpoint";
      protected String identityName;
      protected Optional<String> credentialName = Optional.absent();
      protected String version = "";
      protected Optional<String> buildVersion = Optional.of("");
      protected Optional<String> defaultEndpoint = Optional.absent();
      protected Optional<String> defaultIdentity = Optional.absent();
      protected Optional<String> defaultCredential = Optional.absent();
      protected Properties defaultProperties = BaseApiMetadata.defaultProperties();
      protected URI documentation;
      //
      protected TypeToken<? extends Context> context = TypeToken.of(Context.class);
      protected Set<Class<? extends Module>> defaultModules = ImmutableSet.of();

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder view(Class<? extends View> view) {
         return view(TypeToken.of(checkNotNull(view, "view")));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder view(TypeToken<? extends View> view) {
         return views(ImmutableSet.<TypeToken<? extends View>>of(checkNotNull(view, "view")));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder views(Set<TypeToken<? extends View>> views) {
         this.views = ImmutableSet.copyOf(checkNotNull(views, "views"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder endpointName(String endpointName) {
         this.endpointName = checkNotNull(endpointName, "endpointName");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder identityName(String identityName) {
         this.identityName = checkNotNull(identityName, "identityName");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder credentialName(String credentialName) {
         this.credentialName = Optional.fromNullable(credentialName);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder version(String version) {
         this.version = checkNotNull(version, "version");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder buildVersion(String buildVersion) {
         this.buildVersion = Optional.fromNullable(buildVersion);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultEndpoint(String defaultEndpoint) {
         this.defaultEndpoint = Optional.fromNullable(defaultEndpoint);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultIdentity(String defaultIdentity) {
         this.defaultIdentity = Optional.fromNullable(defaultIdentity);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultCredential(String defaultCredential) {
         this.defaultCredential = Optional.fromNullable(defaultCredential);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultProperties(Properties defaultProperties) {
         this.defaultProperties = checkNotNull(defaultProperties, "defaultProperties");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder documentation(URI documentation) {
         this.documentation = checkNotNull(documentation, "documentation");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder context(TypeToken<? extends Context> context) {
         this.context = checkNotNull(context, "context");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultModule(Class<? extends Module> defaultModule) {
         return defaultModules(ImmutableSet.<Class<? extends Module>>of(checkNotNull(defaultModule, "defaultModule")));
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder defaultModules(Set<Class<? extends Module>> defaultModules) {
         this.defaultModules = ImmutableSet.copyOf(checkNotNull(defaultModules, "defaultModules"));
         return this;
      }

      public Builder fromApiMetadata(ApiMetadata in) {
         return id(in.getId()).views(in.getViews()).name(in.getName()).endpointName(in.getEndpointName()).identityName(
                  in.getIdentityName()).credentialName(in.getCredentialName().orNull()).version(in.getVersion())
                  .buildVersion(in.getBuildVersion().orNull()).defaultEndpoint(in.getDefaultEndpoint().orNull())
                  .defaultIdentity(in.getDefaultIdentity().orNull()).defaultCredential(
                           in.getDefaultCredential().orNull()).defaultProperties(in.getDefaultProperties())
                  .documentation(in.getDocumentation()).context(in.getContext()).defaultModules(in.getDefaultModules());
      }

      @Override
      public ApiMetadata build() {
         return new BaseApiMetadata(this) {
            /** The serialVersionUID */
            private static final long serialVersionUID = 3599854558038790678L;
         };
      }

   }

   protected final String id;
   protected final String name;
   protected final Set<TypeToken<? extends View>> views;
   protected final String endpointName;
   protected final String identityName;
   protected final Optional<String> credentialName;
   protected final String version;
   protected final Optional<String> buildVersion;
   protected final Optional<String> defaultEndpoint;
   protected final Optional<String> defaultIdentity;
   protected final Optional<String> defaultCredential;
   protected final Properties defaultProperties;
   protected final URI documentation;
   protected final TypeToken<? extends Context> context;
   protected final Set<Class<? extends Module>> defaultModules;

   protected BaseApiMetadata(Builder builder) {
      this(builder.id, builder.name, builder.views, builder.endpointName, builder.identityName, builder.credentialName,
               builder.version, builder.buildVersion, builder.defaultEndpoint, builder.defaultIdentity,
               builder.defaultCredential, builder.defaultProperties, builder.documentation, builder.context,
               builder.defaultModules);
   }

   public BaseApiMetadata(String id, String name, Set<TypeToken<? extends View>> views, String endpointName, String identityName,
            Optional<String> credentialName, String version, Optional<String> buildVersion,
            Optional<String> defaultEndpoint, Optional<String> defaultIdentity, Optional<String> defaultCredential,
            Properties defaultProperties, URI documentation, TypeToken<? extends Context> context,
            Set<Class<? extends Module>> defaultModules) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.views = ImmutableSet.copyOf(checkNotNull(views, "views"));
      this.endpointName = checkNotNull(endpointName, "endpointName");
      this.identityName = checkNotNull(identityName, "identityName");
      this.credentialName = checkNotNull(credentialName, "credentialName");
      this.version = checkNotNull(version, "version");
      this.buildVersion = checkNotNull(buildVersion, "buildVersion");
      this.defaultEndpoint = checkNotNull(defaultEndpoint, "defaultEndpoint");
      this.defaultIdentity = checkNotNull(defaultIdentity, "defaultIdentity");
      this.defaultCredential = checkNotNull(defaultCredential, "defaultCredential");
      this.defaultProperties = checkNotNull(defaultProperties, "defaultProperties");
      this.documentation = checkNotNull(documentation, "documentation");
      this.context = checkNotNull(context, "context");
      this.defaultModules = ImmutableSet.copyOf(checkNotNull(defaultModules, "defaultModules"));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // subclass equivalence is ok, since we don't know the classloader
      // we'll get things from
      if (o == null || !(o instanceof ApiMetadata))
         return false;
      ApiMetadata that = ApiMetadata.class.cast(o);
      return equal(this.getId(), that.getId()) && equal(this.getName(), that.getName())
               && equal(this.getViews(), that.getViews());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getId(), getName(), getViews());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("id", getId()).add("name", getName()).add("views", getViews()).add(
               "endpointName", getEndpointName()).add("identityName", getIdentityName()).add("credentialName",
               getCredentialName()).add("documentation", getDocumentation());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<TypeToken<? extends View>> getViews() {
      return views;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getEndpointName() {
      return endpointName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return identityName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<String> getCredentialName() {
      return credentialName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getVersion() {
      return version;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<String> getBuildVersion() {
      return buildVersion;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<String> getDefaultEndpoint() {
      return defaultEndpoint;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<String> getDefaultIdentity() {
      return defaultIdentity;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Optional<String> getDefaultCredential() {
      return defaultCredential;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Properties getDefaultProperties() {
      return defaultProperties;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getDocumentation() {
      return documentation;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TypeToken<? extends Context> getContext() {
      return context;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Class<? extends Module>> getDefaultModules() {
      return defaultModules;
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

}
