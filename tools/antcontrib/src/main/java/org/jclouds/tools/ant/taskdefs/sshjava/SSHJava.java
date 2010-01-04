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
package org.jclouds.tools.ant.taskdefs.sshjava;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHUserInfo;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Version of the Java task that executes over ssh.
 * 
 * @author Adrian Cole
 */
public class SSHJava extends Java {
   private final SSHExec exec;
   private final Scp scp;
   private final SSHUserInfo userInfo;

   private String jvm = "/usr/bin/java";
   private File localDirectory;
   private File remoteDirectory;
   private Environment env = new Environment();

   private OsFamily osFamily = OsFamily.UNIX;
   private File errorFile;
   private String errorProperty;
   private File outputFile;
   private String outputProperty;
   private boolean append;

   public SSHJava() {
      super();
      setFork(true);
      exec = new SSHExec();
      scp = new Scp();
      userInfo = new SSHUserInfo();
   }

   public SSHJava(Task owner) {
      this();
      bindToOwner(owner);
   }

   @Override
   public int executeJava() throws BuildException {
      checkNotNull(jvm, "jvm must be set");
      checkNotNull(remoteDirectory, "remotedir must be set");
      // must copy the files over first as we are changing the system properties based on this.

      if (localDirectory != null) {
         FileSet cwd = new FileSet();
         cwd.setDir(localDirectory);
         mkdirAndCopyTo(remoteDirectory.getAbsolutePath(), ImmutableList.of(cwd));
      }

      if (getCommandLine().getClasspath() != null) {
         copyPathTo(getCommandLine().getClasspath(), remoteDirectory.getAbsolutePath()
                  + "/classpath");
      }
      
      if (getCommandLine().getBootclasspath() != null) {
         copyPathTo(getCommandLine().getBootclasspath(), remoteDirectory.getAbsolutePath()
                  + "/bootclasspath");
      }

      String command = convertJavaToScriptNormalizingPaths(getCommandLine());

      String random = new SecureRandom().nextInt() + "";
      exec.setResultProperty(random);
      exec.setFailonerror(false);
      exec.setCommand(command);
      exec.setError(errorFile);
      exec.setErrorproperty(errorProperty);
      exec.setOutput(outputFile);
      exec.setOutputproperty(outputProperty);
      exec.setAppend(append);
      exec.execute();
      return Integer.parseInt(getProject().getProperty(random));
   }

   private void mkdirAndCopyTo(String destination, Iterable<FileSet> sets) {
      scp.init();
      exec.setCommand(exec("{md} " + destination).render(osFamily));
      exec.execute();
      String scpDestination = getScpDir(destination);
      System.out.println("Sending to: " + scpDestination);
      for (FileSet set : sets)
         scp.addFileset(set);
      scp.setTodir(scpDestination);
      scp.execute();
   }

   private String getScpDir(String path) {
      return String.format("%s:%s@%s:%s", userInfo.getName(),
               userInfo.getKeyfile() == null ? userInfo.getPassword() : userInfo.getPassphrase(),
               scp.getHost(), path);
   }

   void resetPathToUnderPrefixIfExistsAndIsFileIfNotExistsAddAsIs(Path path, String prefix,
            StringBuilder destination) {
      if (path == null)
         return;
      String[] paths = path.list();
      if (paths != null && paths.length > 0) {
         for (int i = 0; i < paths.length; i++) {
            File file = new File(paths[i]);
            if (file.exists()) {
               // directories are flattened under the prefix anyway, so there's no need to add them
               // to the path
               if (file.isFile())
                  destination.append("{ps}").append(file.getName());
            } else {
               // if the file doesn't exist, it is probably a "forward reference" to something that
               // is already on the remote machine
               destination.append("{ps}").append(file.getAbsolutePath());
            }
         }
      }
   }

   void copyPathTo(Path path, String destination) {
      List<FileSet> filesets = Lists.newArrayList();
      if (path.list() != null && path.list().length > 0) {
         for (String filepath : path.list()) {
            File file = new File(filepath);
            if (file.exists()) {
               FileSet fileset = new FileSet();
               if (file.isFile()) {
                  fileset.setFile(file);
               } else {
                  fileset.setDir(file);
               }
               filesets.add(fileset);
            }
         }
      }
      mkdirAndCopyTo(destination, filesets);
   }

   String convertJavaToScriptNormalizingPaths(CommandlineJava commandLine) {
      List<Statement> statements = Lists.newArrayList();
      String[] environment = env.getVariables();
      if (environment != null) {
         for (int i = 0; i < environment.length; i++) {
            log("Setting environment variable: " + environment[i], Project.MSG_VERBOSE);
            String[] keyValue = environment[i].split("=");
            statements.add(exec(String.format("{export} %s={vq}%s{vq}", keyValue[0], keyValue[1])));
         }
      }
      statements.add(exec("{cd} " + remoteDirectory));
      StringBuilder commandBuilder = new StringBuilder(jvm);
      if (getCommandLine().getBootclasspath() != null) {
         commandBuilder.append(" -Xbootclasspath:bootclasspath");
         resetPathToUnderPrefixIfExistsAndIsFileIfNotExistsAddAsIs(commandLine.getBootclasspath(),
                  "bootclasspath", commandBuilder);
      }

      if (commandLine.getVmCommand().getArguments() != null
               && commandLine.getVmCommand().getArguments().length > 0) {
         commandBuilder.append(" ").append(
                  Joiner.on(' ').join(commandLine.getVmCommand().getArguments()));
      }
      commandBuilder.append(" -cp classpath");
      resetPathToUnderPrefixIfExistsAndIsFileIfNotExistsAddAsIs(commandLine.getClasspath(),
               "classpath", commandBuilder);

      if (commandLine.getSystemProperties() != null
               && commandLine.getSystemProperties().getVariables() != null
               && commandLine.getSystemProperties().getVariables().length > 0) {
         commandBuilder.append(" ").append(
                  Joiner.on(' ').join(commandLine.getSystemProperties().getVariables()));
      }

      commandBuilder.append(" ").append(commandLine.getClassname());

      if (commandLine.getJavaCommand().getArguments() != null
               && commandLine.getJavaCommand().getArguments().length > 0) {
         commandBuilder.append(" ").append(
                  Joiner.on(' ').join(commandLine.getJavaCommand().getArguments()));
      }
      statements.add(exec(commandBuilder.toString()));

      String command = new StatementList(statements).render(osFamily);
      return command;
   }

   @Override
   public void addEnv(Environment.Variable var) {
      env.addVariable(var);
   }

   /**
    * Note that if the {@code dir} property is set, this will be copied recursively to the remote
    * host.
    */
   @Override
   public void setDir(File localDir) {
      this.localDirectory = checkNotNull(localDir, "dir");
   }

   public void setRemotedir(File remotedir) {
      this.remoteDirectory = checkNotNull(remotedir, "remotedir");
   }

   @Override
   public void setFork(boolean fork) {
      if (!fork)
         throw new IllegalArgumentException("this only operates when fork is set");
   }

   @Override
   public void setJvm(String jvm) {
      this.jvm = checkNotNull(jvm, "jvm");
   }

   /**
    * Remote host, either DNS name or IP.
    * 
    * @param host
    *           The new host value
    */
   public void setHost(String host) {
      exec.setHost(host);
      scp.setHost(host);
   }

   /**
    * Get the host.
    * 
    * @return the host
    */
   public String getHost() {
      return exec.getHost();
   }

   /**
    * Username known to remote host.
    * 
    * @param username
    *           The new username value
    */
   public void setUsername(String username) {
      exec.setUsername(username);
      scp.setUsername(username);
      userInfo.setName(username);
   }

   /**
    * Sets the password for the user.
    * 
    * @param password
    *           The new password value
    */
   public void setPassword(String password) {
      exec.setPassword(password);
      scp.setPassword(password);
      userInfo.setPassword(password);
   }

   /**
    * Sets the keyfile for the user.
    * 
    * @param keyfile
    *           The new keyfile value
    */
   public void setKeyfile(String keyfile) {
      exec.setKeyfile(keyfile);
      scp.setKeyfile(keyfile);
      userInfo.setKeyfile(keyfile);
      if (userInfo.getPassphrase() == null)
         userInfo.setPassphrase("");
   }

   /**
    * Sets the passphrase for the users key.
    * 
    * @param passphrase
    *           The new passphrase value
    */
   public void setPassphrase(String passphrase) {
      exec.setPassphrase(passphrase);
      scp.setPassphrase(passphrase);
      userInfo.setPassphrase(passphrase);
   }

   /**
    * Sets the path to the file that has the identities of all known hosts. This is used by SSH
    * protocol to validate the identity of the host. The default is
    * <i>${user.home}/.ssh/known_hosts</i>.
    * 
    * @param knownHosts
    *           a path to the known hosts file.
    */
   public void setKnownhosts(String knownHosts) {
      exec.setKnownhosts(knownHosts);
      scp.setKnownhosts(knownHosts);
   }

   /**
    * Setting this to true trusts hosts whose identity is unknown.
    * 
    * @param yesOrNo
    *           if true trust the identity of unknown hosts.
    */
   public void setTrust(boolean yesOrNo) {
      exec.setTrust(yesOrNo);
      scp.setTrust(yesOrNo);
      userInfo.setTrust(yesOrNo);
   }

   /**
    * Changes the port used to connect to the remote host.
    * 
    * @param port
    *           port number of remote host.
    */
   public void setPort(int port) {
      exec.setPort(port);
      scp.setPort(port);
   }

   /**
    * Get the port attribute.
    * 
    * @return the port
    */
   public int getPort() {
      return exec.getPort();
   }

   /**
    * Initialize the task. This initializizs the known hosts and sets the default port.
    * 
    * @throws BuildException
    *            on error
    */
   public void init() throws BuildException {
      super.init();
      exec.init();
      scp.init();
   }

   /**
    * The connection can be dropped after a specified number of milliseconds. This is sometimes
    * useful when a connection may be flaky. Default is 0, which means &quot;wait forever&quot;.
    * 
    * @param timeout
    *           The new timeout value in seconds
    */
   public void setTimeout(long timeout) {
      exec.setTimeout(timeout);
   }

   /**
    * Set the verbose flag.
    * 
    * @param verbose
    *           if true output more verbose logging
    * @since Ant 1.6.2
    */
   public void setVerbose(boolean verbose) {
      exec.setVerbose(verbose);
      scp.setVerbose(verbose);
   }

   @Override
   public void setError(File error) {
      this.errorFile = error;
   }

   @Override
   public void setErrorProperty(String property) {
      errorProperty = property;
   }

   @Override
   public void setOutput(File out) {
      outputFile = out;
   }

   @Override
   public void setOutputproperty(String outputProp) {
      outputProperty = outputProp;
   }

   @Override
   public void setAppend(boolean append) {
      this.append = append;
   }

   @Override
   public void setProject(Project project) {
      super.setProject(project);
      exec.setProject(project);
      scp.setProject(project);
   }

   @Override
   public void setOwningTarget(Target target) {
      super.setOwningTarget(target);
      exec.setOwningTarget(target);
      scp.setOwningTarget(target);
   }

   @Override
   public void setTaskName(String taskName) {
      super.setTaskName(taskName);
      exec.setTaskName(taskName);
      scp.setTaskName(taskName);
   }

   @Override
   public void setDescription(String description) {
      super.setDescription(description);
      exec.setDescription(description);
      scp.setDescription(description);
   }

   @Override
   public void setLocation(Location location) {
      super.setLocation(location);
      exec.setLocation(location);
      scp.setLocation(location);
   }

   @Override
   public void setTaskType(String type) {
      super.setTaskType(type);
      exec.setTaskType(type);
      scp.setTaskType(type);
   }
}
