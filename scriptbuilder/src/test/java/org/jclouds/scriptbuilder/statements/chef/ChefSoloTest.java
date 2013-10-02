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

import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.chef.DataBag;
import org.jclouds.scriptbuilder.domain.chef.Role;
import org.jclouds.scriptbuilder.domain.chef.RunList;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Unit tests for the {@link ChefSoloTest} statement.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "ChefSoloTest")
public class ChefSoloTest {

   public void testCreateDefaultSoloConfiguration() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().build();

      solo.createSoloConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      assertEquals(statementList.size(), 3);
      assertEquals(statementList.get(0), exec("{md} " + ChefSolo.DEFAULT_SOLO_PATH));
      assertEquals(statementList.get(1), exec("{md} " + ChefSolo.DEFAULT_SOLO_PATH + "/cookbooks"));

      Statement expected = createOrOverwriteFile(
            ChefSolo.DEFAULT_SOLO_PATH + "/solo.rb",
            ImmutableSet.of("file_cache_path \"" + ChefSolo.DEFAULT_SOLO_PATH + "\"", //
                  "cookbook_path [\"" + ChefSolo.DEFAULT_SOLO_PATH + "/cookbooks\"]", "role_path \""
                        + ChefSolo.DEFAULT_SOLO_PATH + "/roles\"", "data_bag_path \"" + ChefSolo.DEFAULT_SOLO_PATH
                        + "/data_bags\""));

      assertEquals(statementList.get(2).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateCustomSoloConfiguration() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().fileCachePath("/tmp").cookbookPath("/tmp/foo").cookbookPath("/tmp/bar")
            .rolePath("/tmp/roles").dataBagPath("/tmp/databags").build();

      solo.createSoloConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      assertEquals(statementList.size(), 4);
      assertEquals(statementList.get(0), exec("{md} /tmp"));
      assertEquals(statementList.get(1), exec("{md} /tmp/foo"));
      assertEquals(statementList.get(2), exec("{md} /tmp/bar"));

      Statement expected = createOrOverwriteFile("/tmp/solo.rb", ImmutableSet.of("file_cache_path \"/tmp\"", //
            "cookbook_path [\"/tmp/foo\",\"/tmp/bar\"]", "role_path \"/tmp/roles\"", "data_bag_path \"/tmp/databags\""));

      assertEquals(statementList.get(3).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateDefaultNodeConfiguration() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().build();

      solo.createNodeConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/node.json",
            ImmutableSet.of("{\"run_list\":[]}"));

      assertEquals(statementList.size(), 1);
      assertEquals(statementList.get(0).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateNodeConfigurationWithJsonAttributes() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().jsonAttributes("{\"foo\":\"bar\"}").build();

      solo.createNodeConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/node.json",
            ImmutableSet.of("{\"foo\":\"bar\",\"run_list\":[]}"));

      assertEquals(statementList.size(), 1);
      assertEquals(statementList.get(0).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateNodeConfigurationWithRunList() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      RunList runlist = RunList.builder().recipe("foo").role("bar").build();
      ChefSolo solo = ChefSolo.builder().runlist(runlist).build();

      solo.createNodeConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/node.json",
            ImmutableSet.of("{\"run_list\":[\"recipe[foo]\",\"role[bar]\"]}"));

      assertEquals(statementList.size(), 1);
      assertEquals(statementList.get(0).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateNodeConfigurationWithJsonAttributesAndRunList() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      RunList runlist = RunList.builder().recipe("foo").role("bar").build();
      ChefSolo solo = ChefSolo.builder().jsonAttributes("{\"foo\":\"bar\"}").runlist(runlist).build();

      solo.createNodeConfiguration(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/node.json",
            ImmutableSet.of("{\"foo\":\"bar\",\"run_list\":[\"recipe[foo]\",\"role[bar]\"]}"));

      assertEquals(statementList.size(), 1);
      assertEquals(statementList.get(0).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateRolesIfNecessaryWithDefaultValues() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().build();

      solo.createRolesIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      assertTrue(statementList.isEmpty());
   }

   public void testCreateRolesIfNecessaryWithOneRole() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      RunList runlist = RunList.builder().recipe("bar").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      ChefSolo solo = ChefSolo.builder().defineRole(role).build();

      solo.createRolesIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/roles/" + role.getName() + ".json",
            ImmutableSet.of(role.toJsonString()));

      assertEquals(statementList.size(), 2);
      assertEquals(statementList.get(0), exec("{md} " + ChefSolo.DEFAULT_SOLO_PATH + "/roles"));
      assertEquals(statementList.get(1).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateRolesIfNecessaryWithOneRoleAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      RunList runlist = RunList.builder().recipe("bar").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      ChefSolo solo = ChefSolo.builder().rolePath("/tmp/roles").defineRole(role).build();

      solo.createRolesIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile("/tmp/roles/" + role.getName() + ".json",
            ImmutableSet.of(role.toJsonString()));

      assertEquals(statementList.size(), 2);
      assertEquals(statementList.get(0), exec("{md} /tmp/roles"));
      assertEquals(statementList.get(1).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateRolesIfNecessaryWithMultipleRoleAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      Role roleFoo = Role.builder().name("foo").runlist(RunList.builder().recipe("foo").build()).build();
      Role roleBar = Role.builder().name("bar").runlist(RunList.builder().recipe("bar").build()).build();
      ChefSolo solo = ChefSolo.builder().rolePath("/tmp/roles").defineRole(roleFoo).defineRole(roleBar).build();

      solo.createRolesIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expectedFoo = createOrOverwriteFile("/tmp/roles/" + roleFoo.getName() + ".json",
            ImmutableSet.of(roleFoo.toJsonString()));
      Statement expectedBar = createOrOverwriteFile("/tmp/roles/" + roleBar.getName() + ".json",
            ImmutableSet.of(roleBar.toJsonString()));

      assertEquals(statementList.size(), 3);
      assertEquals(statementList.get(0), exec("{md} /tmp/roles"));
      assertEquals(statementList.get(1).render(OsFamily.UNIX), expectedFoo.render(OsFamily.UNIX));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expectedBar.render(OsFamily.UNIX));
   }

   public void testCreateDatabagsIfNecessaryWithDefaultValues() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      ChefSolo solo = ChefSolo.builder().build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      assertTrue(statementList.isEmpty());
   }

   public void testCreateDatabagsIfNecessaryWithOneDatabag() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      DataBag databag = DataBag.builder().name("foo").item("item", "{\"foo\":\"bar\"}").build();
      ChefSolo solo = ChefSolo.builder().defineDataBag(databag).build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile(ChefSolo.DEFAULT_SOLO_PATH + "/data_bags/foo/item.json",
            ImmutableSet.of("{\"foo\":\"bar\"}"));

      assertEquals(statementList.size(), 3);
      assertEquals(statementList.get(0), exec("{md} " + ChefSolo.DEFAULT_SOLO_PATH + "/data_bags"));
      assertEquals(statementList.get(1), exec("{md} " + ChefSolo.DEFAULT_SOLO_PATH + "/data_bags/" + databag.getName()));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateDatabagsIfNecessaryWithOneDatabagAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      DataBag databag = DataBag.builder().name("foo").item("item", "{\"foo\":\"bar\"}").build();
      ChefSolo solo = ChefSolo.builder().dataBagPath("/tmp/databags").defineDataBag(databag).build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expected = createOrOverwriteFile("/tmp/databags/foo/item.json", ImmutableSet.of("{\"foo\":\"bar\"}"));

      assertEquals(statementList.size(), 3);
      assertEquals(statementList.get(0), exec("{md} /tmp/databags"));
      assertEquals(statementList.get(1), exec("{md} /tmp/databags/" + databag.getName()));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expected.render(OsFamily.UNIX));
   }

   public void testCreateDatabagsIfNecessaryWithOneDatabagWithMultipleItemsAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      DataBag databag = DataBag.builder().name("foo").item("item1", "{\"foo\":\"bar\"}")
            .item("item2", "{\"bar\":\"foo\"}").build();
      ChefSolo solo = ChefSolo.builder().dataBagPath("/tmp/databags").defineDataBag(databag).build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expectedItem1 = createOrOverwriteFile("/tmp/databags/foo/item1.json",
            ImmutableSet.of("{\"foo\":\"bar\"}"));
      Statement expectedItem2 = createOrOverwriteFile("/tmp/databags/foo/item2.json",
            ImmutableSet.of("{\"bar\":\"foo\"}"));

      assertEquals(statementList.size(), 4);
      assertEquals(statementList.get(0), exec("{md} /tmp/databags"));
      assertEquals(statementList.get(1), exec("{md} /tmp/databags/" + databag.getName()));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expectedItem1.render(OsFamily.UNIX));
      assertEquals(statementList.get(3).render(OsFamily.UNIX), expectedItem2.render(OsFamily.UNIX));
   }

   public void testCreateDatabagsIfNecessaryWithMultipleDatabagsAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      DataBag databagFoo = DataBag.builder().name("foo").item("itemFoo", "{\"foo\":\"bar\"}").build();
      DataBag databagBar = DataBag.builder().name("bar").item("itemBar", "{\"bar\":\"foo\"}").build();
      ChefSolo solo = ChefSolo.builder().dataBagPath("/tmp/databags").defineDataBag(databagFoo)
            .defineDataBag(databagBar).build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expectedFoo = createOrOverwriteFile("/tmp/databags/foo/itemFoo.json",
            ImmutableSet.of("{\"foo\":\"bar\"}"));
      Statement expectedBar = createOrOverwriteFile("/tmp/databags/bar/itemBar.json",
            ImmutableSet.of("{\"bar\":\"foo\"}"));

      assertEquals(statementList.size(), 5);
      assertEquals(statementList.get(0), exec("{md} /tmp/databags"));
      assertEquals(statementList.get(1), exec("{md} /tmp/databags/" + databagFoo.getName()));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expectedFoo.render(OsFamily.UNIX));
      assertEquals(statementList.get(3), exec("{md} /tmp/databags/" + databagBar.getName()));
      assertEquals(statementList.get(4).render(OsFamily.UNIX), expectedBar.render(OsFamily.UNIX));
   }

   public void testCreateDatabagsIfNecessaryWithMultipleDatabagsAndMultipleItemsAndCustomPath() {
      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      DataBag databagFoo = DataBag.builder().name("foo").item("itemFoo", "{\"foo\":\"bar\"}").build();
      DataBag databagBar = DataBag.builder().name("bar").item("itemBar", "{\"bar\":\"foo\"}")
            .item("extra", "{\"extra\":[]}").build();
      ChefSolo solo = ChefSolo.builder().dataBagPath("/tmp/databags").defineDataBag(databagFoo)
            .defineDataBag(databagBar).build();

      solo.createDatabagsIfNecessary(statements);
      ImmutableList<Statement> statementList = statements.build();

      Statement expectedFoo = createOrOverwriteFile("/tmp/databags/foo/itemFoo.json",
            ImmutableSet.of("{\"foo\":\"bar\"}"));
      Statement expectedBar = createOrOverwriteFile("/tmp/databags/bar/itemBar.json",
            ImmutableSet.of("{\"bar\":\"foo\"}"));
      Statement expectedExtra = createOrOverwriteFile("/tmp/databags/bar/extra.json", ImmutableSet.of("{\"extra\":[]}"));

      assertEquals(statementList.size(), 6);
      assertEquals(statementList.get(0), exec("{md} /tmp/databags"));
      assertEquals(statementList.get(1), exec("{md} /tmp/databags/" + databagFoo.getName()));
      assertEquals(statementList.get(2).render(OsFamily.UNIX), expectedFoo.render(OsFamily.UNIX));
      assertEquals(statementList.get(3), exec("{md} /tmp/databags/" + databagBar.getName()));
      assertEquals(statementList.get(4).render(OsFamily.UNIX), expectedBar.render(OsFamily.UNIX));
      assertEquals(statementList.get(5).render(OsFamily.UNIX), expectedExtra.render(OsFamily.UNIX));
   }

   @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = "windows not yet implemented")
   public void testChefSoloInWindows() {
      ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").build().render(OsFamily.WINDOWS);
   }

   public void testChefWoloWithDefaultConfiguration() throws IOException {
      String script = ChefSolo.builder().build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname`\n");
   }

   public void testChefWoloWithNodeName() throws IOException {
      String script = ChefSolo.builder().nodeName("foo").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N foo\n");
   }

   public void testChefSoloWithGroup() throws IOException {
      String script = ChefSolo.builder().group("foo").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -g foo\n");
   }

   public void testChefSoloWithInterval() throws IOException {
      String script = ChefSolo.builder().interval(15).build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -i 15\n");
   }

   public void testChefSoloWithLogLevel() throws IOException {
      String script = ChefSolo.builder().logLevel("debug").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -l debug\n");
   }

   public void testChefSoloWithLogFile() throws IOException {
      String script = ChefSolo.builder().logFile("/var/log/solo.log").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -L /var/log/solo.log\n");
   }

   public void testChefSoloWithCookbooksLocation() throws IOException {
      String script = ChefSolo.builder().cookbooksArchiveLocation("/tmp/cookbooks").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -r /tmp/cookbooks\n");
   }

   public void testChefSoloWithSplay() throws IOException {
      String script = ChefSolo.builder().splay(15).build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -s 15\n");
   }

   public void testChefSoloWithUser() throws IOException {
      String script = ChefSolo.builder().user("foo").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems() + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname` -u foo\n");
   }

   public void testChefSoloWithChefGemVersion() throws IOException {
      String script = ChefSolo.builder().chefVersion(">= 0.10.8").build().render(OsFamily.UNIX);
      assertEquals(script, installChefGems(">= 0.10.8") + createConfigFile() + createNodeFile()
            + "chef-solo -c /var/chef/solo.rb -j /var/chef/node.json -N `hostname`\n");
   }

   private static String installChefGems() throws IOException {
      return "gem install chef --no-rdoc --no-ri\n";
   }

   private static String installChefGems(String version) throws IOException {
      return "gem install chef -v '" + version + "' --no-rdoc --no-ri\n";
   }

   private static String createConfigFile() {
      return "mkdir -p /var/chef\nmkdir -p /var/chef/cookbooks\ncat > /var/chef/solo.rb <<-'END_OF_JCLOUDS_FILE'\n"
            + "\tfile_cache_path \"/var/chef\"\n\tcookbook_path [\"/var/chef/cookbooks\"]\n"
            + "\trole_path \"/var/chef/roles\"\n\tdata_bag_path \"/var/chef/data_bags\"\nEND_OF_JCLOUDS_FILE\n";
   }

   private static String createNodeFile() {
      return "cat > /var/chef/node.json <<-'END_OF_JCLOUDS_FILE'\n\t{\"run_list\":[]}\nEND_OF_JCLOUDS_FILE\n";
   }

}
