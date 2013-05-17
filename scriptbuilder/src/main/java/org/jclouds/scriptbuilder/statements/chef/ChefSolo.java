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
package org.jclouds.scriptbuilder.statements.chef;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.util.List;
import java.util.Map;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.domain.chef.DataBag;
import org.jclouds.scriptbuilder.domain.chef.Role;
import org.jclouds.scriptbuilder.domain.chef.RunList;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Bootstraps a node using Chef Solo.
 * 
 * @author Ignasi Barrera
 */
public class ChefSolo implements Statement {

   public static final String DEFAULT_SOLO_PATH = "/var/chef";

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String fileCachePath = DEFAULT_SOLO_PATH;
      private String rolePath;
      private String databagPath;
      private ImmutableList.Builder<String> cookbookPath = ImmutableList.builder();
      private String cookbooksArchiveLocation;
      private String jsonAttributes;
      private String group;
      private Integer interval;
      private String logLevel;
      private String logFile;
      private String nodeName;
      private Integer splay;
      private String user;
      private List<Role> roles = Lists.newArrayList();
      private List<DataBag> databags = Lists.newArrayList();
      private RunList runlist;
      private String chefVersion;

      /**
       * Directory where Chef Solo will store files.
       */
      public Builder fileCachePath(String fileCachePath) {
         this.fileCachePath = checkNotNull(fileCachePath, "fileCachePath");
         return this;
      }

      /**
       * Directory where Chef Solo will store roles.
       */
      public Builder rolePath(String rolePath) {
         this.rolePath = checkNotNull(rolePath, "rolePath");
         return this;
      }

      /**
       * Directory where Chef Solo will store data bags.
       */
      public Builder dataBagPath(String dataBagPath) {
         this.databagPath = checkNotNull(dataBagPath, "dataBagPath");
         return this;
      }

      /**
       * Directory where Chef Solo will look for cookbooks.
       */
      public Builder cookbookPath(String cookbookPath) {
         this.cookbookPath.add(checkNotNull(cookbookPath, "cookbookPath"));
         return this;
      }

      /**
       * Directories where Chef Solo will look for cookbooks.
       */
      public Builder cookbookPaths(Iterable<String> cookbookPaths) {
         this.cookbookPath.addAll(checkNotNull(cookbookPaths, "cookbookPath"));
         return this;
      }

      /**
       * Local file path or remote URL of a cookbook tar file. Chef solo will
       * download and unpack its contents in the {@link #fileCachePath}
       * directory.
       */
      public Builder cookbooksArchiveLocation(String cookbooksArchiveLocation) {
         this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation");
         return this;
      }

      /**
       * JSON attributes to customize cookbook values.
       */
      public Builder jsonAttributes(String jsonAttributes) {
         this.jsonAttributes = checkNotNull(jsonAttributes, "jsonAttributes");
         return this;
      }

      /**
       * The goup to set privilege to.
       */
      public Builder group(String group) {
         this.group = checkNotNull(group, "group");
         return this;
      }

      /**
       * Run chef-client periodically, in seconds.
       */
      public Builder interval(Integer interval) {
         this.interval = checkNotNull(interval, "interval");
         return this;
      }

      /**
       * Set he Log level (debug, info, warn, error, fatal).
       */
      public Builder logLevel(String logLevel) {
         this.logLevel = checkNotNull(logLevel, "logLevel");
         return this;
      }

      /**
       * Set the log file location, by default STDOUT.
       */
      public Builder logFile(String logFile) {
         this.logFile = checkNotNull(logFile, "logFile");
         return this;
      }

      /**
       * Set the name for the node. By default the hostname will be used.
       */
      public Builder nodeName(String nodeName) {
         this.nodeName = checkNotNull(nodeName, "nodeName");
         return this;
      }

      /**
       * The splay time for running at intervals, in seconds.
       */
      public Builder splay(Integer splay) {
         this.splay = checkNotNull(splay, "splay");
         return this;
      }

      /**
       * The user to set privilege to.
       */
      public Builder user(String user) {
         this.user = checkNotNull(user, "user");
         return this;
      }

      /**
       * Creates a role.
       */
      public Builder defineRole(Role role) {
         this.roles.add(checkNotNull(role, "role"));
         return this;
      }

      /**
       * Creates a set of roles.
       */
      public Builder defineRoles(Iterable<Role> roles) {
         this.roles = ImmutableList.<Role> copyOf(checkNotNull(roles, "roles"));
         return this;
      }

      /**
       * Creates a data bag.
       * 
       * @since Chef 0.10.4
       */
      public Builder defineDataBag(DataBag dataBag) {
         this.databags.add(checkNotNull(dataBag, "dataBag"));
         return this;
      }

      /**
       * Creates a set of data bags.
       * 
       * @since Chef 0.10.4
       */
      public Builder defineDataBags(Iterable<DataBag> databags) {
         this.databags = ImmutableList.<DataBag> copyOf(checkNotNull(databags, "databags"));
         return this;
      }

      /**
       * The run list to be executed in the Chef Solo run.
       */
      public Builder runlist(RunList runlist) {
         this.runlist = checkNotNull(runlist, "runlist");
         return this;
      }

      /**
       * The version of the Chef gem to install.
       */
      public Builder chefVersion(String chefVersion) {
         this.chefVersion = checkNotNull(chefVersion, "chefVersion");
         return this;
      }

      public ChefSolo build() {
         return new ChefSolo(Optional.of(fileCachePath), Optional.fromNullable(rolePath),
               Optional.fromNullable(databagPath), Optional.of(cookbookPath.build()),
               Optional.fromNullable(cookbooksArchiveLocation), Optional.fromNullable(jsonAttributes),
               Optional.fromNullable(group), Optional.fromNullable(interval), Optional.fromNullable(logLevel),
               Optional.fromNullable(logFile), Optional.fromNullable(nodeName), Optional.fromNullable(splay),
               Optional.fromNullable(user), Optional.of(roles), Optional.of(databags), Optional.fromNullable(runlist),
               Optional.fromNullable(chefVersion));
      }

   }

   private String fileCachePath;
   private String rolePath;
   private String databagPath;
   private List<String> cookbookPath;
   private Optional<String> cookbooksArchiveLocation;
   private Optional<String> jsonAttributes;
   private Optional<String> group;
   private Optional<Integer> interval;
   private Optional<String> logLevel;
   private Optional<String> logFile;
   private Optional<String> nodeName;
   private Optional<Integer> splay;
   private Optional<String> user;
   private Optional<List<Role>> roles;
   private Optional<List<DataBag>> databags;
   private RunList runlist;
   private final InstallChefGems installChefGems;

   protected ChefSolo(Optional<String> fileCachePath, Optional<String> rolePath, Optional<String> databagPath,
         Optional<ImmutableList<String>> cookbookPath, Optional<String> cookbooksArchiveLocation,
         Optional<String> jsonAttributes, Optional<String> group, Optional<Integer> interval,
         Optional<String> logLevel, Optional<String> logFile, Optional<String> nodeName, Optional<Integer> splay,
         Optional<String> user, Optional<List<Role>> roles, Optional<List<DataBag>> databags,
         Optional<RunList> runlist, Optional<String> chefVersion) {
      this.fileCachePath = checkNotNull(fileCachePath, "fileCachePath must be set").or(DEFAULT_SOLO_PATH);
      this.rolePath = checkNotNull(rolePath, "rolePath must be set").or(this.fileCachePath + "/roles");
      this.databagPath = checkNotNull(databagPath, "databagPath must be set").or(this.fileCachePath + "/data_bags");
      this.cookbooksArchiveLocation = checkNotNull(cookbooksArchiveLocation, "cookbooksArchiveLocation must be set");
      this.jsonAttributes = checkNotNull(jsonAttributes, "jsonAttributes must be set");
      this.group = checkNotNull(group, "group must be set");
      this.interval = checkNotNull(interval, "interval must be set");
      this.logLevel = checkNotNull(logLevel, "logLevel must be set");
      this.logFile = checkNotNull(logFile, "logFile must be set");
      this.nodeName = checkNotNull(nodeName, "nodeName must be set");
      this.splay = checkNotNull(splay, "splay must be set");
      this.user = checkNotNull(user, "user must be set");
      this.roles = checkNotNull(roles, "roles must be set");
      this.databags = checkNotNull(databags, "databags must be set");
      this.runlist = checkNotNull(runlist, "runlist must be set").or(RunList.builder().build());
      this.user = checkNotNull(user, "chefVersion must be set");
      if (!checkNotNull(cookbookPath, "cookbookPath must be set").isPresent() || cookbookPath.get().isEmpty()) {
         this.cookbookPath = ImmutableList.<String> of(this.fileCachePath + "/cookbooks");
      } else {
         this.cookbookPath = ImmutableList.<String> copyOf(cookbookPath.get());
      }
      this.installChefGems = InstallChefGems.builder().version(chefVersion.orNull()).build();
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }

      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      statements.add(installChefGems);

      createSoloConfiguration(statements);
      createRolesIfNecessary(statements);
      createDatabagsIfNecessary(statements);
      createNodeConfiguration(statements);

      ImmutableMap.Builder<String, String> options = ImmutableMap.builder();
      options.put("-c", fileCachePath + "/solo.rb");
      options.put("-j", fileCachePath + "/node.json");
      options.put("-N", nodeName.or("`hostname`"));

      if (group.isPresent()) {
         options.put("-g", group.get());
      }
      if (interval.isPresent()) {
         options.put("-i", interval.get().toString());
      }
      if (logLevel.isPresent()) {
         options.put("-l", logLevel.get());
      }
      if (logFile.isPresent()) {
         options.put("-L", logFile.get());
      }
      if (cookbooksArchiveLocation.isPresent()) {
         options.put("-r", cookbooksArchiveLocation.get());
      }
      if (splay.isPresent()) {
         options.put("-s", splay.get().toString());
      }
      if (user.isPresent()) {
         options.put("-u", user.get());
      }

      String strOptions = Joiner.on(' ').withKeyValueSeparator(" ").join(options.build());
      statements.add(Statements.exec(String.format("chef-solo %s", strOptions)));

      return new StatementList(statements.build()).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return installChefGems.functionDependencies(family);
   }

   @VisibleForTesting
   void createSoloConfiguration(ImmutableList.Builder<Statement> statements) {
      statements.add(exec("{md} " + fileCachePath));
      for (String path : cookbookPath) {
         statements.add(exec("{md} " + path));
      }
      String cookbookPathJoined = Joiner.on(',').join(transform(cookbookPath, quote()));
      statements.add(createOrOverwriteFile(
            fileCachePath + "/solo.rb",
            ImmutableSet.of("file_cache_path \"" + fileCachePath + "\"", //
                  "cookbook_path [" + cookbookPathJoined + "]", "role_path \"" + rolePath + "\"", "data_bag_path \""
                        + databagPath + "\"")));
   }

   @VisibleForTesting
   void createNodeConfiguration(ImmutableList.Builder<Statement> statements) {
      StringBuilder json = new StringBuilder();
      if (jsonAttributes.isPresent()) {
         // Start the node configuration with the attributes, but remove the
         // last bracket to append the run list to the json configuration
         json.append(jsonAttributes.get().substring(0, jsonAttributes.get().lastIndexOf('}')));
         json.append(",");
      } else {
         json.append("{");
      }
      json.append("\"run_list\":");
      json.append(runlist.toString());
      json.append("}");

      statements.add(createOrOverwriteFile(fileCachePath + "/node.json", ImmutableSet.of(json.toString())));
   }

   @VisibleForTesting
   void createRolesIfNecessary(ImmutableList.Builder<Statement> statements) {
      // The roles directory must contain one file for each role definition
      if (roles.isPresent() && !roles.get().isEmpty()) {
         statements.add(exec("{md} " + rolePath));
         for (Role role : roles.get()) {
            statements.add(createOrOverwriteFile(rolePath + "/" + role.getName() + ".json",
                  ImmutableSet.of(role.toJsonString())));
         }
      }
   }

   @VisibleForTesting
   void createDatabagsIfNecessary(ImmutableList.Builder<Statement> statements) {
      // Each data bag item must be defined in a file inside the data bag
      // directory, and each data bag item must have its own JSON file.
      if (databags.isPresent() && !databags.get().isEmpty()) {
         statements.add(exec("{md} " + databagPath));
         for (DataBag databag : databags.get()) {
            String databagFolder = databagPath + "/" + databag.getName();
            statements.add(exec("{md} " + databagFolder));
            for (Map.Entry<String, String> item : databag.getItems().entrySet()) {
               statements.add(createOrOverwriteFile(databagFolder + "/" + item.getKey() + ".json",
                     ImmutableSet.of(item.getValue())));
            }
         }
      }
   }

   private static Function<String, String> quote() {
      return new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      };
   }
}
