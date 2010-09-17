/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.chef.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.io.Payloads.newStringPayload;
import static org.jclouds.scriptbuilder.domain.Statements.createFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.newStatementList;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.domain.Client;
import org.jclouds.crypto.Pems;
import org.jclouds.io.Payload;
import org.jclouds.json.Json;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * 
 * Generates a bootstrap script relevant for a particular tag
 * 
 * @author Adrian Cole
 */
@Singleton
public class TagToBootScript implements Function<String, Payload> {
   @VisibleForTesting
   static final Type RUN_LIST_TYPE = new TypeLiteral<Map<String, List<String>>>() {
   }.getType();
   private final URI endpoint;
   private final Json json;
   private final Map<String, Client> tagToClient;
   private final Map<String, List<String>> runListForTag;
   private final Statement installChefGems;

   @Inject
   public TagToBootScript(@Provider URI endpoint, Json json, Map<String, Client> tagToClient,
            Map<String, List<String>> runListForTag, @Named("installChefGems") Statement installChefGems) {
      this.endpoint = checkNotNull(endpoint, "endpoint");
      this.json = checkNotNull(json, "json");
      this.tagToClient = checkNotNull(tagToClient, "tagToClient");
      this.runListForTag = checkNotNull(runListForTag, "runListForTag");
      this.installChefGems = checkNotNull(installChefGems, "installChefGems");
   }

   public Payload apply(String tag) {
      checkNotNull(tag, "tag");

      Client client = tagToClient.get(tag);
      checkState(client != null, "could not get a client for tag %s", tag);
      checkState(client.getClientname() != null, "clientname null for %s", client);
      checkState(client.getPrivateKey() != null, "privatekey null for %s", client);

      List<String> runList = runListForTag.get(tag);
      checkState(runList != null, "runList for %s was not found", tag);
      checkState(runList.size() > 0, "runList for %s was empty", tag);

      String chefConfigDir = "{root}etc{fs}chef";
      Statement createChefConfigDir = exec("{md} " + chefConfigDir);
      Statement createClientRb = createFile(chefConfigDir + "{fs}client.rb", ImmutableList.of("require 'rubygems'",
               "require 'ohai'", "o = Ohai::System.new", "o.all_plugins", String.format(
                        "node_name \"%s-\" + o[:ipaddress]", tag), "log_level :info", "log_location STDOUT", String
                        .format("validation_client_name \"%s\"", client.getClientname()), String.format(
                        "chef_server_url \"%s\"", endpoint)));

      Statement createValidationPem = createFile(chefConfigDir + "{fs}validation.pem", Splitter.on('\n').split(
               Pems.pem(client.getPrivateKey())));

      String chefBootFile = chefConfigDir + "{fs}first-boot.json";

      Statement createFirstBoot = createFile(chefBootFile, Collections.singleton(json.toJson(ImmutableMap
               .<String, List<String>> of("run_list", runList), RUN_LIST_TYPE)));

      Statement runChef = exec("chef-client -j " + chefBootFile);

      Statement bootstrapAndRunChef = newStatementList(installChefGems, createChefConfigDir, createClientRb,
               createValidationPem, createFirstBoot, runChef);

      String runScript = bootstrapAndRunChef.render(OsFamily.UNIX);
      return newStringPayload(runScript);
   }

}