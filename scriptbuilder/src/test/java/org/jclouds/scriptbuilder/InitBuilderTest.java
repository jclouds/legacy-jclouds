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
package org.jclouds.scriptbuilder;

import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

/**
 * Tests possible uses of InitBuilder
 * 
 * @author Adrian Cole
 */
@Deprecated
public class InitBuilderTest {

   InitBuilder testInitBuilder = new InitBuilder("mkebsboot", "/mnt/tmp", "/mnt/tmp", ImmutableMap.of("TMP_DIR",
         "/mnt/tmp"), ImmutableList.<Statement> of(
         appendFile("{tmp}{fs}{uid}{fs}scripttest{fs}temp.txt", ImmutableList.<String> of("hello world")),
         exec("find /")));

   @Test(expectedExceptions = UnsupportedOperationException.class)
   public void testBuildSimpleWindows() throws MalformedURLException, IOException {
      testInitBuilder.render(OsFamily.WINDOWS);
   }

   @Test
   public void testBuildSimpleUNIX() throws MalformedURLException, IOException {
      assertEquals(
            testInitBuilder.render(OsFamily.UNIX),
            Resources.toString(Resources.getResource("test_init." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8));
   }

   @Test
   public void testBuildEBS() throws MalformedURLException, IOException {
      assertEquals(
            new InitBuilder("mkebsboot",// name of the script
                  "/tmp",// working directory
                  "/tmp/logs",// location of stdout.log and stderr.log
                  ImmutableMap.of("IMAGE_DIR", "/mnt/tmp", "EBS_DEVICE", "/dev/sdh", "EBS_MOUNT_POINT", "/mnt/ebs"),// variables
                                                                                                                // used
                                                                                                                // inside
                                                                                                                // of
                                                                                                                // the
                                                                                                                // script
                  ImmutableList.<Statement> of(Statements
                        .interpret(
                              "echo creating a filesystem and mounting the ebs volume",// what
                                                                                       // to
                                                                                       // execute
                              "{md} {varl}IMAGE_DIR{varr} {varl}EBS_MOUNT_POINT{varr}",
                              "rm -rf {varl}IMAGE_DIR{varr}/*",
                              "yes| mkfs -t ext3 {varl}EBS_DEVICE{varr} 2>&-",
                              "mount {varl}EBS_DEVICE{varr} {varl}EBS_MOUNT_POINT{varr}",
                              "echo making a local working copy of the boot disk",
                              "rsync -ax --exclude /ubuntu/.bash_history --exclude /home/*/.bash_history --exclude /etc/ssh/ssh_host_* --exclude /etc/ssh/moduli --exclude /etc/udev/rules.d/*persistent-net.rules --exclude /var/lib/ec2/* --exclude=/mnt/* --exclude=/proc/* --exclude=/tmp/* --exclude=/dev/log / {varl}IMAGE_DIR{varr}",
                              "echo preparing the local working copy",
                              "touch {varl}IMAGE_DIR{varr}/etc/init.d/ec2-init-user-data",
                              "echo copying the local working copy to the ebs mount", "{cd} {varl}IMAGE_DIR{varr}",
                              "tar -cSf - * | tar xf - -C {varl}EBS_MOUNT_POINT{varr}", "echo size of ebs",
                              "du -sk {varl}EBS_MOUNT_POINT{varr}", "echo size of source",
                              "du -sk {varl}IMAGE_DIR{varr}", "rm -rf {varl}IMAGE_DIR{varr}/*",
                              "umount {varl}EBS_MOUNT_POINT{varr}", "echo ----COMPLETE----"))).render(OsFamily.UNIX),
            Resources.toString(Resources.getResource("test_ebs." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8));
   }
}
