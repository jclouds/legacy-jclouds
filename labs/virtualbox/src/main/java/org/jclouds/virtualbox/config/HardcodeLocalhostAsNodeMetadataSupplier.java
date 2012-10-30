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
package org.jclouds.virtualbox.config;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * In particular, this binds {@code Supplier<NodeMetadata>} so that it can be used in ssh commands.
 * Ssh is necessary for operations that cannot be performed in the virtual box api, such as clearing
 * sessions.
 * 
 * ex. once this is loaded, use Guice to inject {@code Supplier<NodeMetadata>} as {@code host} and
 * {@link RunScriptOnNode#Factory} as factory, to start making commands like the following:
 * 
 * <pre>
 * import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
 * import static org.jclouds.scriptbuilder.domain.Statements.*;
 * 
 *    ...
 *  
 *  // direct execute a script as opposed to using sudo, or an init wrapper
 *  ListenableFuture<ExecResponse> fooInTheFuture = factory.submit(host.get(), 
 *          exec("echo foo"), runAsRoot(false).wrapInInitScript(false));
 * 
 *  ExecResponse foo = Futures.getUnchecked(fooInTheFuture);
 * 
 *  // call a set of commands that are defined in classpath/functions/function_name.sh
 *  ListenableFuture<ExecResponse> kill = factory.submit(host.get(), 
 *          call("killsession"), runAsRoot(false).wrapInInitScript(false));
 * 
 * ...
 * 
 * </pre>
 * 
 * <h3>Note</h3>
 * 
 * People often forget to call {@link Future#get} when using {@link RunScriptOnNode.Factory#submit}.
 * Don't forget!
 * 
 * @author Adrian Cole
 */
public class HardcodeLocalhostAsNodeMetadataSupplier extends AbstractModule {

   public static final String HOST_ID = "host";
   public static final String HOSTNAME = Strings.nullToEmpty(System.getenv("HOSTNAME"));

   /**
    * Lazy so that we don't hang up the injector reading a file
    */
   @Provides
   @Singleton
   protected Supplier<NodeMetadata> lazySupplyHostAsNodeMetadata() {
      return new Supplier<NodeMetadata>() {

         @Override
         public NodeMetadata get() {
            String privateKey = readRsaIdentity();
            return new NodeMetadataBuilder()
                                    .id(HOST_ID)
                                    .name("host installing virtualbox")
                                    .hostname(HOSTNAME)
                                    .operatingSystem(OperatingSystem.builder()
                                                                    .family(OsFamily.LINUX)
                                                                    .description(System.getProperty("os.name"))
                                                                    .arch(System.getProperty("os.arch"))
                                                                    .version(System.getProperty("os.version"))
                                                                    .build())
                                    .status(Status.RUNNING)
                                    .location(new LocationBuilder().id(HOST_ID)
                                                                   .scope(LocationScope.HOST)
                                                                   .description(HOSTNAME)
                                                                   .build())
                                    .credentials(LoginCredentials.builder()
                                    		.user(System.getProperty("user.name"))
                                    		.privateKey(privateKey)					 
                                    		.build())
                                    .build();
         }

      };
   }
   
   static String readRsaIdentity() {
      String privateKey;
      try {
         File keyFile = new File(System.getProperty("user.home") + "/.ssh/id_rsa");
         privateKey = Files.toString(keyFile, Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
      return privateKey;
   }
   
   @Override
   protected void configure() {

   }
}