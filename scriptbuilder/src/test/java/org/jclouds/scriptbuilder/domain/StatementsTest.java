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
package org.jclouds.scriptbuilder.domain;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "StatementsTest")
public class StatementsTest {

   public void testSaveHttpResponseToUNIX() {
      Statement save = Statements.saveHttpResponseTo(
            URI.create("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar"), "/opt/minecraft",
            "minecraft_server.jar");
      assertEquals(
            save.render(OsFamily.UNIX),
            "(mkdir -p /opt/minecraft && cd /opt/minecraft && [ ! -f minecraft_server.jar ] && curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -C - -X GET  https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar >minecraft_server.jar)\n");
   }

   public void testExtractTargzIntoDirectoryUNIX() {
      Statement save = Statements
            .extractTargzIntoDirectory(
                  URI.create("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.tar.gz"),
                  "/opt/minecraft");
      assertEquals(
            save.render(OsFamily.UNIX),
            "curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.tar.gz |(mkdir -p /opt/minecraft &&cd /opt/minecraft &&tar -xpzf -)\n");
   }

}
