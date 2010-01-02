/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.tools.ant;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Environment.Variable;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "jclouds.JavaOverSshTest")
public class JavaOverSshTest {

   public void testFull() throws SecurityException, NoSuchMethodException {
      JavaOverSsh task = createTask();
      assertEquals(
               String
                        .format(
                                 "cd /tmp/foo\njar -xf cwd.zip\n%s -Xms256 -cp classpath.jar -Dfooble=baz -Dfoo=bar org.jclouds.tools.ant.TestClass hello world\n",
                                 System.getProperty("java.home") + "/bin/java", System
                                          .getProperty("user.dir")), task.convertJavaToScript(task
                        .getCommandLine()));
   }

   private JavaOverSsh createTask() {
      Project p = new Project();
      JavaOverSsh task = new JavaOverSsh();
      task.setProject(p);
      task.setClassname(TestClass.class.getName());
      task.createClasspath().add(new Path(p, "target/test-classes"));
      Variable prop1 = new Environment.Variable();
      prop1.setKey("fooble");
      prop1.setValue("baz");

      task.addSysproperty(prop1);
      Variable prop2 = new Environment.Variable();
      prop2.setKey("foo");
      prop2.setValue("bar");

      task.addSysproperty(prop2);
      task.createJvmarg().setValue("-Xms256");
      task.createArg().setValue("hello");
      task.createArg().setValue("world");
      task.setDir(new File(System.getProperty("user.dir")));
      task.setRemotedir(new File("/tmp/foo"));
      task.setFork(true);
      task.setJvm(System.getProperty("java.home") + "/bin/java");
      return task;
   }

   @Test(enabled = false, groups = { "live" })
   public void testSsh() throws NumberFormatException, FileNotFoundException, IOException {
      String sshHost = System.getProperty("jclouds.test.ssh.host");
      String sshPort = System.getProperty("jclouds.test.ssh.port");
      String sshUser = System.getProperty("jclouds.test.ssh.username");
      String sshPass = System.getProperty("jclouds.test.ssh.password");
      String sshKeyFile = System.getProperty("jclouds.test.ssh.keyfile");

      int port = (sshPort != null) ? Integer.parseInt(sshPort) : 22;
      InetAddress host = (sshHost != null) ? InetAddress.getByName(sshHost) : InetAddress
               .getLocalHost();

      JavaOverSsh task = createTask();
      task.setHost(host.getHostAddress());
      task.setPort(port);
      task.setTrust(true);
      task.setUsername(sshUser);
      if (sshKeyFile != null && !sshKeyFile.trim().equals("")) {
         task.setKeyfile(sshKeyFile);
      } else {
         task.setPassword(sshPass);
      }
      task.setOutputproperty("out");
      task.setErrorProperty("err");
      task.setResultProperty("result");
      task.execute();
      assertEquals(task.getProject().getProperty("out"), "[hello, world]\n");
      assertEquals(task.getProject().getProperty("err"), "err\n");
      assertEquals(task.getProject().getProperty("result"), "3");
   }
}
