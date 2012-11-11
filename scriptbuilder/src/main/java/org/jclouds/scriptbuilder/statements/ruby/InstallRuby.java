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
package org.jclouds.scriptbuilder.statements.ruby;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.extractTargzAndFlattenIntoDirectory;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

/**
 * Installs Ruby and Rubygems gems onto a host.
 * 
 * @author Ignasi Barrera
 */
public class InstallRuby extends StatementList {

   private static final URI RUBYGEMS_URI = URI.create("http://production.cf.rubygems.org/rubygems/rubygems-1.8.10.tgz");

   public static Statement installRubyGems() {
      return new StatementList(//
            exec("("), //
            extractTargzAndFlattenIntoDirectory(RUBYGEMS_URI, "/tmp/rubygems"), //
            exec("{cd} /tmp/rubygems"), //
            exec("ruby setup.rb --no-format-executable"), //
            exec("{rm} -fr /tmp/rubygems"), //
            exec(")"), //
            // Make sure RubyGems is up to date
            exec("gem update --system"), //
            exec("gem update --no-rdoc --no-ri"));
   }

   public InstallRuby() {
      super(call("setupPublicCurl"), call("installRuby"), installRubyGems());
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }
      return super.render(family);
   }

}
