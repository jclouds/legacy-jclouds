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
package org.jclouds.scriptbuilder.statements.git;

import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CloneGitRepoTest")
public class CloneGitRepoTest {

   public void testUNIX() {
      assertEquals(CloneGitRepo.builder().repository("https://github.com/joyent/node.git").build()
               .render(OsFamily.UNIX), "git clone https://github.com/joyent/node.git\ncd node\n");
   }

   public void testWithBranchUNIX() {
      assertEquals(CloneGitRepo.builder().repository("https://github.com/joyent/node.git").branch("v0.6").build()
               .render(OsFamily.UNIX), "git clone -b v0.6 https://github.com/joyent/node.git\ncd node\n");

   }

   public void testWithBranchAndTagUNIX() {
      assertEquals(CloneGitRepo.builder().repository("https://github.com/joyent/node.git").branch("v0.6")
               .tag("v0.6.10").build().render(OsFamily.UNIX),
               "git clone -b v0.6 https://github.com/joyent/node.git\ncd node\ngit checkout v0.6.10\n");
   }

   public void testWithDirectoryUNIX() {
      assertEquals(CloneGitRepo.builder().repository("https://github.com/joyent/node.git").directory("/tmp/node-local")
               .build().render(OsFamily.UNIX),
               "git clone https://github.com/joyent/node.git /tmp/node-local\ncd /tmp/node-local\n");
   }
}
