/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.taskdefs.Replace.Replacefilter;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHUserInfo;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Environment.Variable;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.tools.ant.util.SSHExecute;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.JSchException;

/**
 * Version of the Java task that executes over ssh.
 * 
 * @author Adrian Cole
 */
public class SSHJava extends Java {
   private final SSHExecute exec;
   private final Scp scp;
   private final SSHUserInfo userInfo;
   private File localDirectory;
   File remotebase;
   @VisibleForTesting
   File remotedir;
   @VisibleForTesting
   Environment env = new Environment();

   private OsFamily osFamily = OsFamily.UNIX;
   private File errorFile;
   private String errorProperty;
   private File outputFile;
   private String outputProperty;
   String id = "sshjava" + new SecureRandom().nextLong();
   private boolean append;

   @VisibleForTesting
   final LinkedHashMap<String, String> shiftMap = Maps.newLinkedHashMap();
   @VisibleForTesting
   final LinkedHashMap<String, String> replace = Maps.newLinkedHashMap();

   public SSHJava() {
      super();
      exec = new SSHExecute();
      exec.setProject(getProject());
      scp = new Scp();
      userInfo = new SSHUserInfo();
      scp.init();
      setFork(true);
      setTrust(true);
   }

   public SSHJava(Task owner) {
      this();
      bindToOwner(owner);
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public int executeJava() throws BuildException {
      checkNotNull(remotebase, "remotebase must be set");

      if (localDirectory == null) {
         try {
            localDirectory = File.createTempFile("sshjava", "dir");
            localDirectory.delete();
            localDirectory.mkdirs();
         } catch (IOException e) {
            throw new BuildException(e);
         }
      }

      if (remotedir == null)
         remotedir = new File(remotebase, id);

      String command = createInitScript(osFamily, id, remotedir.getAbsolutePath(), env, getCommandLine());

      try {
         BufferedWriter out = new BufferedWriter(new FileWriter(new File(localDirectory, "init."
                  + ShellToken.SH.to(osFamily))));
         out.write(command);
         out.close();
      } catch (IOException e) {
         throw new BuildException(e);
      }

      replaceAllTokensIn(localDirectory);

      FileSet cwd = new FileSet();
      cwd.setDir(localDirectory);
      if (osFamily == OsFamily.UNIX) {
         log("removing old contents: " + remotedir.getAbsolutePath(), Project.MSG_VERBOSE);
         sshexec(exec("rm -rf " + remotedir.getAbsolutePath()).render(osFamily));
      } else {
         // TODO need recursive remove on windows
      }
      mkdirAndCopyTo(remotedir.getAbsolutePath(), ImmutableList.of(cwd));

      for (Entry<String, String> entry : shiftMap.entrySet()) {
         FileSet set = new FileSet();
         File source = new File(entry.getKey());
         if (source.isDirectory()) {
            set.setDir(new File(entry.getKey()));
            mkdirAndCopyTo(remotebase.getAbsolutePath() + ShellToken.FS.to(osFamily) + entry.getValue(), ImmutableList
                     .of(set));
         } else {
            String destination = remotebase.getAbsolutePath() + ShellToken.FS.to(osFamily)
                     + new File(entry.getValue()).getParent();
            sshexec(exec("{md} " + destination).render(osFamily));
            scp.init();
            String scpDestination = getScpDir(destination);
            log("staging: " + scpDestination, Project.MSG_VERBOSE);
            scp.setFile(source.getAbsolutePath());
            scp.setTodir(scpDestination);
            scp.execute();
         }
      }

      if (getCommandLine().getClasspath() != null) {
         copyPathTo(getCommandLine().getClasspath(), remotedir.getAbsolutePath() + "/classpath");
      }

      if (getCommandLine().getBootclasspath() != null) {
         copyPathTo(getCommandLine().getBootclasspath(), remotedir.getAbsolutePath() + "/bootclasspath");
      }

      if (osFamily == OsFamily.UNIX) {
         sshexec(exec("chmod 755 " + remotedir.getAbsolutePath() + "{fs}init.{sh}").render(osFamily));
      }

      Statement statement = new StatementList(exec("{cd} " + remotedir.getAbsolutePath()), exec(remotedir
               .getAbsolutePath()
               + "{fs}init.{sh} init"), exec(remotedir.getAbsolutePath() + "{fs}init.{sh} run"));
      try {
         return sshexecRedirectStreams(statement);
      } catch (IOException e) {
         throw new BuildException(e, getLocation());
      }
   }

   void replaceAllTokensIn(File directory) {
      Replace replacer = new Replace();
      replacer.setProject(getProject());
      replacer.setDir(directory);

      Map<String, String> map = Maps.newLinkedHashMap();
      // this has to go first
      map.put(directory.getAbsolutePath(), remotedir.getAbsolutePath());

      map.putAll(Maps.transformValues(shiftMap, new Function<String, String>() {

         @Override
         public String apply(String in) {
            return remotebase + ShellToken.FS.to(osFamily) + in;
         }

      }));
      map.putAll(replace);

      for (Entry<String, String> entry : map.entrySet()) {
         Replacefilter filter = replacer.createReplacefilter();
         filter.setToken(entry.getKey());
         filter.setValue(entry.getValue());
      }
      replacer.execute();
   }

   private int sshexec(String command) {
      try {
         return exec.execute(command);
      } catch (JSchException e) {
         throw new BuildException(e, getLocation());
      } catch (IOException e) {
         throw new BuildException(e, getLocation());
      } catch (TimeoutException e) {
         throw new BuildException(e, getLocation());
      }
   }

   private int sshexecRedirectStreams(Statement statement) throws IOException {
      exec.setStreamHandler(redirector.createHandler());
      log("starting java as:\n" + statement.render(osFamily), Project.MSG_VERBOSE);
      int rc;
      try {
         rc = sshexec(statement.render(osFamily));
      } finally {
         redirector.complete();
      }
      return rc;
   }

   private void mkdirAndCopyTo(String destination, Iterable<FileSet> sets) {
      if (Iterables.size(sets) == 0) {
         log("no content: " + destination, Project.MSG_DEBUG);
         return;
      }
      if (sshexec(exec("test -d " + destination).render(osFamily)) == 0) {// TODO windows
         log("already created: " + destination, Project.MSG_VERBOSE);
         return;
      }
      sshexec(exec("{md} " + destination).render(osFamily));
      scp.init();
      String scpDestination = getScpDir(destination);
      log("staging: " + scpDestination, Project.MSG_VERBOSE);
      for (FileSet set : sets)
         scp.addFileset(set);
      scp.setTodir(scpDestination);
      scp.execute();
   }

   private String getScpDir(String path) {
      return String.format("%s:%s@%s:%s", userInfo.getName(), userInfo.getKeyfile() == null ? userInfo.getPassword()
               : userInfo.getPassphrase(), scp.getHost(), path);
   }

   void resetPathToUnderPrefixIfExistsAndIsFileIfNotExistsAddAsIs(Path path, String prefix, StringBuilder destination) {
      if (path == null)
         return;
      String[] paths = path.list();
      if (paths != null && paths.length > 0) {
         for (int i = 0; i < paths.length; i++) {
            log("converting: " + paths[i], Project.MSG_DEBUG);
            File file = new File(reprefix(paths[i]));
            if (file.getAbsolutePath().equals(paths[i]) && file.exists() && file.isFile()) {
               String newPath = prefix + "{fs}" + file.getName();
               log("adding new: " + newPath, Project.MSG_DEBUG);
               destination.append("{ps}").append(prefix + "{fs}" + file.getName());
            } else {
               // if the file doesn't exist, it is probably a "forward reference" to something that
               // is already on the remote machine
               destination.append("{ps}").append(file.getAbsolutePath());
               log("adding existing: " + file.getAbsolutePath(), Project.MSG_DEBUG);
            }
         }
      }
   }

   void copyPathTo(Path path, String destination) {
      List<FileSet> filesets = Lists.newArrayList();
      if (path.list() != null && path.list().length > 0) {
         for (String filepath : path.list()) {
            if (!filepath.equals(reprefix(filepath)))
               continue;// we've already copied
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

   String reprefix(String in) {
      log("comparing: " + in, Project.MSG_DEBUG);
      for (Entry<String, String> entry : shiftMap.entrySet()) {
         if (in.startsWith(entry.getKey())) {
            log("match shift map: " + entry.getKey(), Project.MSG_DEBUG);
            in = remotebase + ShellToken.FS.to(osFamily) + entry.getValue() + in.substring(entry.getKey().length());
         }
      }
      for (Entry<String, String> entry : replace.entrySet()) {
         if (in.startsWith(entry.getKey())) {
            log("match replaceMap: " + entry.getKey(), Project.MSG_DEBUG);
            in = entry.getValue() + in.substring(entry.getKey().length());
         }
      }
      log("now: " + in, Project.MSG_DEBUG);
      return in;
   }

   String createInitScript(OsFamily osFamily, String id, String basedir, Environment env,
            CommandlineJava commandLine) {
      Map<String, String> envVariables = Maps.newHashMap();
      String[] environment = env.getVariables();
      if (environment != null) {
         for (int i = 0; i < environment.length; i++) {
            log("Setting environment variable: " + environment[i], Project.MSG_DEBUG);
            String[] keyValue = environment[i].split("=");
            envVariables.put(keyValue[0], keyValue[1]);
         }
      }
      StringBuilder commandBuilder = new StringBuilder(commandLine.getVmCommand().getExecutable());
      if (commandLine.getBootclasspath() != null) {
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

      InitBuilder testInitBuilder = new InitBuilder(id, basedir, basedir, envVariables,
               ImmutableList.<Statement> of(Statements.interpret( commandBuilder.toString())));
      return testInitBuilder.render(osFamily);
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

   /**
    * All files transfered to the host will be relative to this. The java process itself will be at
    * this path/{@code id}.
    */
   public void setRemotebase(File remotebase) {
      this.remotebase = checkNotNull(remotebase, "remotebase");
   }

   @Override
   public void setFork(boolean fork) {
      if (!fork)
         throw new IllegalArgumentException("this only operates when fork is set");
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
    * The connection can be dropped after a specified number of milliseconds. This is sometimes
    * useful when a connection may be flaky. Default is 0, which means &quot;wait forever&quot;.
    * 
    * @param timeout
    *           The new timeout value in seconds
    */
   public void setTimeout(long timeout) {
      exec.setTimeout(timeout);
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
      scp.setOwningTarget(target);
   }

   @Override
   public void setTaskName(String taskName) {
      super.setTaskName(taskName);
      scp.setTaskName(taskName);
   }

   @Override
   public void setDescription(String description) {
      super.setDescription(description);
      scp.setDescription(description);
   }

   @Override
   public void setLocation(Location location) {
      super.setLocation(location);
      scp.setLocation(location);
   }

   @Override
   public void setTaskType(String type) {
      super.setTaskType(type);
      scp.setTaskType(type);
   }

   @Override
   public String toString() {
      return "SSHJava [append=" + append + ", env=" + env + ", errorFile=" + errorFile + ", errorProperty="
               + errorProperty + ", localDirectory=" + localDirectory + ", osFamily=" + osFamily + ", outputFile="
               + outputFile + ", outputProperty=" + outputProperty + ", remoteDirectory=" + remotebase + ", userInfo="
               + userInfo + "]";
   }

   @Override
   public void addSysproperty(Variable sysp) {
      if (sysp.getKey().startsWith("sshjava.shift.")) {
         shiftMap.put(sysp.getKey().replaceFirst("sshjava.shift.", ""), sysp.getValue());
      } else if (sysp.getKey().startsWith("sshjava.replace.")) {
         replace.put(sysp.getKey().replaceFirst("sshjava.replace.", ""), sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.id")) {
         setId(sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.host")) {
         setHost(sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.port") && !sysp.getValue().equals("")) {
         setPort(Integer.parseInt(sysp.getValue()));
      } else if (sysp.getKey().equals("sshjava.username")) {
         setUsername(sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.password") && !sysp.getValue().equals("")) {
         setPassword(sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.keyfile") && !sysp.getValue().equals("")) {
         setKeyfile(sysp.getValue());
      } else if (sysp.getKey().equals("sshjava.remotebase")) {
         setRemotebase(new File(sysp.getValue()));
      } else {
         super.addSysproperty(sysp);
      }
   }

}