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
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.io.Closeable;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.ContextBuilder;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * The BaseApiMetadata class is an abstraction of {@link ApiMetadata} to be
 * extended by those implementing ApiMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader}
 * to work properly.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public abstract class BaseApiMetadata<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> implements
      ApiMetadata<S, A, C, M> {

   public static class Builder<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> implements
         ApiMetadata.Builder<S, A, C, M> {
      protected String id;
      protected String name;
      protected ApiType type;
      protected String endpointName = "https endpoint";
      protected String identityName;
      protected Optional<String> credentialName = Optional.absent();
      protected String version = "";
      protected Optional<String> buildVersion = Optional.of("");
      protected Optional<String> defaultEndpoint = Optional.absent();
      protected Optional<String> defaultIdentity = Optional.absent();
      protected Optional<String> defaultCredential = Optional.absent();
      protected Properties defaultProperties = defaultProperties();
      protected URI documentation;
      //
      protected Class<S> api;
      protected Class<A> asyncApi;
      protected TypeToken<C> context;
      protected TypeToken<? extends ContextBuilder<S, A, C, M>> contextBuilder = new TypeToken<ContextBuilder<S, A, C, M>>(getClass()) {
         private static final long serialVersionUID = 1L;
      };

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
         props.setProperty(PROPERTY_MAX_CONNECTION_REUSE, 75 + "");
         props.setProperty(PROPERTY_MAX_SESSION_FAILURES, 2 + "");
         props.setProperty(PROPERTY_SESSION_INTERVAL, 60 + "");
         props.setProperty(PROPERTY_PRETTY_PRINT_PAYLOADS, "true");
         return props;
      }

      public static Properties defaultPropertiesAnd(Map<String, String> overrides) {
         Properties props = defaultProperties();
         props.putAll(overrides);
         return props;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> id(String id) {
         this.id = checkNotNull(id, "id");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> type(ApiType type) {
         this.type = checkNotNull(type, "type");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> endpointName(String endpointName) {
         this.endpointName = checkNotNull(endpointName, "endpointName");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> identityName(String identityName) {
         this.identityName = checkNotNull(identityName, "identityName");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> credentialName(String credentialName) {
         this.credentialName = Optional.fromNullable(credentialName);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> version(String version) {
         this.version = checkNotNull(version, "version");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> buildVersion(String buildVersion) {
         this.buildVersion = Optional.fromNullable(buildVersion);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> defaultEndpoint(String defaultEndpoint) {
         this.defaultEndpoint = Optional.fromNullable(defaultEndpoint);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> defaultIdentity(String defaultIdentity) {
         this.defaultIdentity = Optional.fromNullable(defaultIdentity);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> defaultCredential(String defaultCredential) {
         this.defaultCredential = Optional.fromNullable(defaultCredential);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> defaultProperties(Properties defaultProperties) {
         this.defaultProperties = checkNotNull(defaultProperties, "defaultProperties");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<S, A, C, M> javaApi(Class<S> api, Class<A> asyncApi) {
         this.api = checkNotNull(api, "api");
         this.asyncApi = checkNotNull(asyncApi, "asyncApi");
         if (context == null)
            context(contextToken(TypeToken.of(checkNotNull(api, "api")), TypeToken.of(checkNotNull(asyncApi, "asyncApi"))));
         return this;
      }
      
      @SuppressWarnings("rawtypes")
      protected TypeToken contextToken(TypeToken<S> clientToken, TypeToken<A> asyncClientToken) {
         return new TypeToken<RestContext<S, A>>() {
            private static final long serialVersionUID = 1L;
         }.where(new TypeParameter<S>() {
         }, clientToken).where(new TypeParameter<A>() {
         }, asyncClientToken);
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> documentation(URI documentation) {
         this.documentation = checkNotNull(documentation, "documentation");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> context(TypeToken<C> context) {
         this.context = checkNotNull(context, "context");
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<S, A, C, M> contextBuilder(TypeToken<? extends ContextBuilder<S, A, C, M>> contextBuilder) {
         this.contextBuilder = checkNotNull(contextBuilder, "contextBuilder");
         return this;
      }

      public Builder<S, A, C, M> fromApiMetadata(M in) {
         return id(in.getId()).type(in.getType()).name(in.getName()).endpointName(in.getEndpointName())
               .identityName(in.getIdentityName()).credentialName(in.getCredentialName().orNull())
               .version(in.getVersion()).buildVersion(in.getBuildVersion().orNull())
               .defaultEndpoint(in.getDefaultEndpoint().orNull()).defaultIdentity(in.getDefaultIdentity().orNull())
               .defaultCredential(in.getDefaultCredential().orNull()).defaultProperties(in.getDefaultProperties())
               .documentation(in.getDocumentation()).javaApi(in.getApi(), in.getAsyncApi()).context(in.getContext())
               .contextBuilder(in.getContextBuilder());
      }

      @SuppressWarnings("unchecked")
      @Override
      public M build() {
         return (M) new BaseApiMetadata<S, A, C, M>(this) {
         };
      }

   }

   protected final String id;
   protected final String name;
   protected final ApiType type;
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
   protected final Class<S> api;
   protected final Class<A> asyncApi;
   protected final TypeToken<C> context;
   protected final TypeToken<? extends ContextBuilder<S, A, C, M>> contextBuilder;

   @SuppressWarnings("unchecked")
   protected BaseApiMetadata(Builder<?, ?, ?, ?> builder) {
      this(builder.id, builder.name, builder.type, builder.endpointName, builder.identityName, builder.credentialName,
            builder.version, builder.buildVersion, builder.defaultEndpoint, builder.defaultIdentity,
            builder.defaultCredential, builder.defaultProperties, builder.documentation, Class.class.cast(builder.api),
            Class.class.cast(builder.asyncApi), TypeToken.class.cast(builder.context), TypeToken.class
                  .cast(builder.contextBuilder));
   }


   public BaseApiMetadata(String id, String name, ApiType type, String endpointName, String identityName,
         Optional<String> credentialName, String version, Optional<String> buildVersion,
         Optional<String> defaultEndpoint, Optional<String> defaultIdentity, Optional<String> defaultCredential,
         Properties defaultProperties, URI documentation, Class<S> api, Class<A> asyncApi, TypeToken<C> context,
         TypeToken<ContextBuilder<S, A, C, M>> contextBuilder) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.type = checkNotNull(type, "type");
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
      this.api = checkNotNull(api, "api");
      this.asyncApi = checkNotNull(asyncApi, "asyncApi");
      this.context = checkNotNull(context, "context");
      this.contextBuilder = checkNotNull(contextBuilder, "contextBuilder");
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // subclass equivalence is ok, since we don't know the classloader
      // we'll get things from
      if (o == null || !(o instanceof ApiMetadata))
         return false;
      ApiMetadata<?, ?, ?, ?> that = ApiMetadata.class.cast(o);
      return equal(this.getId(), that.getId()) && equal(this.getName(), that.getName())
            && equal(this.getType(), that.getType());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getId(), getName(), getType());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("id", getId()).add("name", getName()).add("type", getType())
            .add("endpointName", getEndpointName()).add("identityName", getIdentityName())
            .add("credentialName", getCredentialName()).add("documentation", getDocumentation()).add("api", getApi())
            .add("asyncApi", getAsyncApi());
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
   public ApiType getType() {
      return type;
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
   public Class<S> getApi() {
      return api;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<A> getAsyncApi() {
      return asyncApi;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TypeToken<C> getContext() {
      return context;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public TypeToken<? extends ContextBuilder<S, A, C, M>> getContextBuilder() {
      return contextBuilder;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Builder<S, A, C, M> toBuilder() {
      return new Builder<S, A, C, M>().fromApiMetadata((M) this);
   }

}