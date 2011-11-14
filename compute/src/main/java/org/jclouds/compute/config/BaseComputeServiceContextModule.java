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
package org.jclouds.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.callables.BlockUntilInitScriptStatusIsZeroThenReturnOutput;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNodeAsInitScriptUsingSsh;
import org.jclouds.compute.callables.RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete;
import org.jclouds.compute.callables.RunScriptOnNodeUsingSsh;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.functions.CreateSshClientOncePortIsListeningOnNode;
import org.jclouds.compute.functions.DefaultCredentialsFromImageOrOverridingCredentials;
import org.jclouds.compute.functions.TemplateOptionsToStatement;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;
import org.jclouds.location.config.LocationModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.SshClient;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new LocationModule(authException));
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<Function<NodeMetadata, SshClient>>() {
      }).to(CreateSshClientOncePortIsListeningOnNode.class);
      bind(new TypeLiteral<Function<TemplateOptions, Statement>>() {
      }).to(TemplateOptionsToStatement.class);
      bind(Credentials.class).annotatedWith(Names.named("image")).toProvider(
            GetLoginForProviderFromPropertiesAndStoreCredentialsOrReturnNull.class);
      bind(new TypeLiteral<Function<Template, Credentials>>() {
      }).to(DefaultCredentialsFromImageOrOverridingCredentials.class);
      
      install(new FactoryModuleBuilder()
            .implement(RunScriptOnNodeUsingSsh.class, Names.named("direct"), RunScriptOnNodeUsingSsh.class)
            .implement(RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete.class, Names.named("blocking"),
                  RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete.class)
            .implement(RunScriptOnNodeAsInitScriptUsingSsh.class, Names.named("nonblocking"),
                  RunScriptOnNodeAsInitScriptUsingSsh.class).build(RunScriptOnNodeFactoryImpl.Factory.class));

      install(new PersistNodeCredentialsModule());

      bind(RunScriptOnNode.Factory.class).to(RunScriptOnNodeFactoryImpl.class);

      install(new FactoryModuleBuilder().implement(new TypeLiteral<Callable<Void>>() {
      }, CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.class)
            .implement(new TypeLiteral<Function<NodeMetadata, Void>>() {
            }, CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.class)
            .build(CustomizeNodeAndAddToGoodMapOrPutExceptionIntoBadMap.Factory.class));

      install(new FactoryModuleBuilder().implement(new TypeLiteral<Callable<RunScriptOnNode>>() {
      }, InitializeRunScriptOnNodeOrPlaceInBadMap.class).build(InitializeRunScriptOnNodeOrPlaceInBadMap.Factory.class));

      install(new FactoryModuleBuilder().build(BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory.class));
   }

   @Singleton
   static class RunScriptOnNodeFactoryImpl implements RunScriptOnNode.Factory {

      static interface Factory {

         @Named("direct")
         RunScriptOnNodeUsingSsh exec(NodeMetadata node, Statement script, RunScriptOptions options);

         @Named("blocking")
         RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete backgroundAndBlockOnComplete(NodeMetadata node,
               Statement script, RunScriptOptions options);

         @Named("nonblocking")
         RunScriptOnNodeAsInitScriptUsingSsh background(NodeMetadata node, Statement script, RunScriptOptions options);
      }

      private final Factory factory;

      @Inject
      RunScriptOnNodeFactoryImpl(Factory factory) {
         this.factory = checkNotNull(factory, "factory");
      }

      @Override
      public RunScriptOnNode create(NodeMetadata node, Statement runScript, RunScriptOptions options) {
         checkNotNull(node, "node");
         checkNotNull(runScript, "runScript");
         checkNotNull(options, "options");
         return !options.shouldWrapInInitScript() ? factory.exec(node, runScript, options) : (options
               .shouldBlockOnComplete() ? factory.backgroundAndBlockOnComplete(node, runScript, options) : factory
               .background(node, runScript, options));
      }

      @Override
      public BlockUntilInitScriptStatusIsZeroThenReturnOutput submit(NodeMetadata node, Statement script,
            RunScriptOptions options) {
         checkNotNull(node, "node");
         checkNotNull(script, "script");
         checkNotNull(options, "options");
         options.shouldWrapInInitScript();
         return factory.backgroundAndBlockOnComplete(node, script, options).init().future();
      }
   }

   @Provides
   @Singleton
   public Map<OsFamily, Map<String, String>> provideOsVersionMap(ComputeServiceConstants.ReferenceData data, Json json) {
      return json.fromJson(data.osVersionMapJson, new TypeLiteral<Map<OsFamily, Map<String, String>>>() {
      }.getType());
   }

   /**
    * The default template if none is provided.
    */
   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplateOptionallyFromProperties(Injector injector, TemplateBuilder template,
         @Provider String provider, ValueOfConfigurationKeyOrNull config) {
      template = provideTemplate(injector, template);
      String imageId = config.apply(provider + ".image-id");
      if (imageId == null)
         imageId = config.apply("jclouds.image-id");
      if (imageId != null)
         template.imageId(imageId);
      return template;
   }

   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).osVersionMatches("1[012].[01][04]").os64Bit(true);
   }

   /**
    * The default options if none are provided.
    */
   @Provides
   @Named("DEFAULT")
   protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
      return options;
   }

   /**
    * supplies how the tag is encoded into the name. A string of hex characters
    * is the last argument and tag is the first
    */
   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   protected String provideNamingConvention() {
      return "%s-%s";
   }

   protected AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Image>> provideImageMap(@Memoized Supplier<Set<? extends Image>> images) {
      return Suppliers.compose(new Function<Set<? extends Image>, Map<String, ? extends Image>>() {

         @Override
         public Map<String, ? extends Image> apply(Set<? extends Image> from) {
            return Maps.uniqueIndex(from, new Function<Image, String>() {

               @Override
               public String apply(Image from) {
                  return from.getId();
               }

            });
         }

      }, images);
   }

   @Provides
   @Singleton
   @Memoized
   protected Supplier<Set<? extends Image>> supplyImageCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final Supplier<Set<? extends Image>> imageSupplier) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Image>>(authException,
            seconds, new Supplier<Set<? extends Image>>() {
               @Override
               public Set<? extends Image> get() {
                  return imageSupplier.get();
               }
            });
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Hardware>> provideSizeMap(@Memoized Supplier<Set<? extends Hardware>> sizes) {
      return Suppliers.compose(new Function<Set<? extends Hardware>, Map<String, ? extends Hardware>>() {

         @Override
         public Map<String, ? extends Hardware> apply(Set<? extends Hardware> from) {
            return Maps.uniqueIndex(from, new Function<Hardware, String>() {

               @Override
               public String apply(Hardware from) {
                  return from.getId();
               }

            });
         }

      }, sizes);
   }

   @Provides
   @Singleton
   @Memoized
   protected Supplier<Set<? extends Hardware>> supplySizeCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         final Supplier<Set<? extends Hardware>> hardwareSupplier) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Set<? extends Hardware>>(authException,
            seconds, new Supplier<Set<? extends Hardware>>() {
               @Override
               public Set<? extends Hardware> get() {
                  return hardwareSupplier.get();
               }
            });
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getProviderId();
         }
      };
   }

}