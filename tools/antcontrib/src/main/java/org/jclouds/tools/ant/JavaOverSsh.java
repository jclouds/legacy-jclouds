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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHUserInfo;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.util.TeeOutputStream;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Ported from Jsch SSHExec task.
 * 
 * @author Adrian Cole
 */
public class JavaOverSsh extends Java {
   /** Default listen port for SSH daemon */
   private static final int SSH_PORT = 22;
   private String jvm = "/usr/bin/java";
   private File localDirectory;
   private File remoteDirectory;
   private Environment env = new Environment();
   private String host;
   private String knownHosts;
   private int port = SSH_PORT;
   private SSHUserInfo userInfo = new SSHUserInfo();
   private OsFamily osFamily = OsFamily.UNIX;

   /** units are milliseconds, default is 0=infinite */
   private long maxwait = 0;

   /** for waiting for the command to finish */
   private Thread watchDog = null;
   private File outputFile;
   private String outputProperty;
   private String resultProperty;
   private File errorFile;
   private String errorProperty;
   private boolean append;

   private static final String TIMEOUT_MESSAGE = "Timeout period exceeded, connection dropped.";

   public JavaOverSsh() {
      super();
      setFork(true);
   }

   public JavaOverSsh(Task owner) {
      super(owner);
      setFork(true);
   }

   @Override
   public int executeJava() throws BuildException {
      String command = convertJavaToScript(getCommandLine());

      InputStream classpathJar = (getCommandLine().getClasspath() != null) ? makeClasspathJarOrNull(getCommandLine()
               .getClasspath().list())
               : null;

      InputStream bootClasspathJar = (getCommandLine().getBootclasspath() != null) ? makeClasspathJarOrNull(getCommandLine()
               .getBootclasspath().list())
               : null;

      InputStream currentDirectoryZip = Archives.create("cwd.zip", ZipExporter.class).as(
               ExplodedImporter.class).importDirectory(localDirectory).as(ZipExporter.class)
               .exportZip();

      if (getHost() == null) {
         throw new BuildException("Host is required.");
      }
      if (getUserInfo().getName() == null) {
         throw new BuildException("Username is required.");
      }
      if (getUserInfo().getKeyfile() == null && getUserInfo().getPassword() == null) {
         throw new BuildException("Password or Keyfile is required.");
      }

      Session session = null;
      try {
         // execute the command
         session = openSession();
         session.setTimeout((int) maxwait);
         ChannelSftp sftp = null;
         sftp = (ChannelSftp) session.openChannel("sftp");
         sftp.connect();
         sftp.put(currentDirectoryZip, remoteDirectory + "/cwd.zip");
         Closeables.closeQuietly(currentDirectoryZip);

         if (classpathJar != null || bootClasspathJar != null) {
            if (classpathJar != null) {
               sftp.put(classpathJar, remoteDirectory + "/classpath.jar");
               Closeables.closeQuietly(classpathJar);
            }
            if (bootClasspathJar != null) {
               sftp.put(classpathJar, remoteDirectory + "/boot-classpath.jar");
               Closeables.closeQuietly(classpathJar);
            }
         }

         final ChannelExec channel = (ChannelExec) session.openChannel("exec");
         channel.setCommand(command);

         ByteArrayOutputStream out = new ByteArrayOutputStream();
         TeeOutputStream teeOut = new TeeOutputStream(out, new KeepAliveOutputStream(System.out));
         ByteArrayOutputStream err = new ByteArrayOutputStream();
         TeeOutputStream teeErr = new TeeOutputStream(err, new KeepAliveOutputStream(System.err));

         channel.setOutputStream(teeOut);
         channel.setExtOutputStream(teeOut);
         channel.setErrStream(teeErr);

         channel.connect();

         // wait for it to finish
         watchDog = new Thread() {
            public void run() {
               while (!channel.isEOF()) {
                  if (watchDog == null) {
                     return;
                  }
                  try {
                     sleep(500);
                  } catch (Exception e) {
                     // ignored
                  }
               }
            }
         };

         watchDog.start();
         watchDog.join(maxwait);

         if (watchDog.isAlive()) {
            // ran out of time

            throw new BuildException(TIMEOUT_MESSAGE);
         } else {
            // completed successfully
            return writeOutputAndReturnExitStatus(channel, out, err);
         }
      } catch (BuildException e) {
         throw e;
      } catch (JSchException e) {
         if (e.getMessage().indexOf("session is down") >= 0) {
            throw new BuildException(TIMEOUT_MESSAGE, e);
         } else {
            throw new BuildException(e);
         }
      } catch (Exception e) {
         throw new BuildException(e);
      } finally {
         if (session != null && session.isConnected()) {
            session.disconnect();
         }
      }
   }

   InputStream makeClasspathJarOrNull(String... paths) {
      if (paths != null && paths.length > 0) {
         JavaArchive classpathArchive = Archives.create("classpath.jar", JavaArchive.class);
         for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
               if (file.isFile()) {
                  try {
                     classpathArchive.as(ZipImporter.class).importZip(
                              new ZipFile(file.getAbsolutePath()));
                  } catch (IOException e) {
                     throw new BuildException(e);
                  }
               } else {
                  classpathArchive.as(ExplodedImporter.class).importDirectory(file);
               }
            }
         }
         return classpathArchive.as(ZipExporter.class).exportZip();
      }
      return null;
   }

   private int writeOutputAndReturnExitStatus(final ChannelExec channel, ByteArrayOutputStream out,
            ByteArrayOutputStream err) throws IOException {
      if (outputProperty != null) {
         getProject().setProperty(outputProperty, out.toString());
      }
      if (outputFile != null) {
         writeToFile(err.toString(), append, outputFile);
      }
      if (errorProperty != null) {
         getProject().setProperty(errorProperty, err.toString());
      }
      if (errorFile != null) {
         writeToFile(out.toString(), append, errorFile);
      }
      if (resultProperty != null) {
         getProject().setProperty(resultProperty, channel.getExitStatus() + "");
      }
      return channel.getExitStatus();
   }

   /**
    * Writes a string to a file. If destination file exists, it may be overwritten depending on the
    * "append" value.
    * 
    * @param from
    *           string to write
    * @param to
    *           file to write to
    * @param append
    *           if true, append to existing file, else overwrite
    * @exception Exception
    *               most likely an IOException
    */
   private void writeToFile(String from, boolean append, File to) throws IOException {
      FileWriter out = null;
      try {
         out = new FileWriter(to.getAbsolutePath(), append);
         StringReader in = new StringReader(from);
         char[] buffer = new char[8192];
         int bytesRead;
         while (true) {
            bytesRead = in.read(buffer);
            if (bytesRead == -1) {
               break;
            }
            out.write(buffer, 0, bytesRead);
         }
         out.flush();
      } finally {
         if (out != null) {
            out.close();
         }
      }
   }

   String convertJavaToScript(CommandlineJava commandLine) {
      checkNotNull(jvm, "jvm must be set");
      checkNotNull(localDirectory, "dir must be set");
      checkNotNull(remoteDirectory, "remotedir must be set");
      List<Statement> statements = Lists.newArrayList();
      String[] environment = env.getVariables();
      if (environment != null) {
         for (int i = 0; i < environment.length; i++) {
            log("Setting environment variable: " + environment[i], Project.MSG_VERBOSE);
            statements.add(exec("{export} " + environment[i]));
         }
      }
      statements.add(exec("{cd} " + remoteDirectory));
      statements.add(exec("jar -xf cwd.zip"));

      StringBuilder commandBuilder = new StringBuilder(jvm);
      if (commandLine.getBootclasspath() != null
               && commandLine.getBootclasspath().list().length > 0) {
         commandBuilder.append(" -Xbootclasspath:boot-classpath.jar");
      }

      if (commandLine.getVmCommand().getArguments() != null
               && commandLine.getVmCommand().getArguments().length > 0) {
         commandBuilder.append(" ");
         String[] variables = commandLine.getVmCommand().getArguments();
         for (int i = 0; i < variables.length; i++) {
            commandBuilder.append(variables[i]);
            if (i + 1 < variables.length)
               commandBuilder.append(" ");
         }
      }
      if (commandLine.getClasspath() != null && commandLine.getClasspath().list().length > 0) {
         commandBuilder.append(" -cp classpath.jar");
      }
      if (commandLine.getSystemProperties() != null
               && commandLine.getSystemProperties().getVariables() != null
               && commandLine.getSystemProperties().getVariables().length > 0) {
         commandBuilder.append(" ");
         String[] variables = commandLine.getSystemProperties().getVariables();
         for (int i = 0; i < variables.length; i++) {
            commandBuilder.append(variables[i]);
            if (i + 1 < variables.length)
               commandBuilder.append(" ");
         }
      }

      commandBuilder.append(" ").append(commandLine.getClassname());

      if (commandLine.getJavaCommand().getArguments() != null
               && commandLine.getJavaCommand().getArguments().length > 0) {
         commandBuilder.append(" ");
         String[] variables = commandLine.getJavaCommand().getArguments();
         for (int i = 0; i < variables.length; i++) {
            commandBuilder.append(variables[i]);
            if (i + 1 < variables.length)
               commandBuilder.append(" ");
         }
      }
      statements.add(exec(commandBuilder.toString()));

      String command = new StatementList(statements).render(osFamily);
      return command;
   }

   @Override
   public void addEnv(Environment.Variable var) {
      env.addVariable(var);
   }

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
      this.host = host;
   }

   /**
    * Get the host.
    * 
    * @return the host
    */
   public String getHost() {
      return host;
   }

   /**
    * Username known to remote host.
    * 
    * @param username
    *           The new username value
    */
   public void setUsername(String username) {
      userInfo.setName(username);
   }

   /**
    * Sets the password for the user.
    * 
    * @param password
    *           The new password value
    */
   public void setPassword(String password) {
      userInfo.setPassword(password);
   }

   /**
    * Sets the keyfile for the user.
    * 
    * @param keyfile
    *           The new keyfile value
    */
   public void setKeyfile(String keyfile) {
      userInfo.setKeyfile(keyfile);
   }

   /**
    * Sets the passphrase for the users key.
    * 
    * @param passphrase
    *           The new passphrase value
    */
   public void setPassphrase(String passphrase) {
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
      this.knownHosts = knownHosts;
   }

   /**
    * Setting this to true trusts hosts whose identity is unknown.
    * 
    * @param yesOrNo
    *           if true trust the identity of unknown hosts.
    */
   public void setTrust(boolean yesOrNo) {
      userInfo.setTrust(yesOrNo);
   }

   /**
    * Changes the port used to connect to the remote host.
    * 
    * @param port
    *           port number of remote host.
    */
   public void setPort(int port) {
      this.port = port;
   }

   /**
    * Get the port attribute.
    * 
    * @return the port
    */
   public int getPort() {
      return port;
   }

   /**
    * Initialize the task. This initializizs the known hosts and sets the default port.
    * 
    * @throws BuildException
    *            on error
    */
   public void init() throws BuildException {
      super.init();
      this.knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";
      this.port = SSH_PORT;
   }

   /**
    * Open an ssh seession.
    * 
    * @return the opened session
    * @throws JSchException
    *            on error
    */
   protected Session openSession() throws JSchException {
      JSch jsch = new JSch();
      if (null != userInfo.getKeyfile()) {
         jsch.addIdentity(userInfo.getKeyfile());
      }

      if (!userInfo.getTrust() && knownHosts != null) {
         log("Using known hosts: " + knownHosts, Project.MSG_DEBUG);
         jsch.setKnownHosts(knownHosts);
      }

      Session session = jsch.getSession(userInfo.getName(), host, port);
      session.setUserInfo(userInfo);
      log("Connecting to " + host + ":" + port);
      session.connect();
      return session;
   }

   /**
    * Get the user information.
    * 
    * @return the user information
    */
   protected SSHUserInfo getUserInfo() {
      return userInfo;
   }

   /**
    * The connection can be dropped after a specified number of milliseconds. This is sometimes
    * useful when a connection may be flaky. Default is 0, which means &quot;wait forever&quot;.
    * 
    * @param timeout
    *           The new timeout value in seconds
    */
   public void setTimeout(long timeout) {
      maxwait = timeout;
   }

   @Override
   public void setError(File out) {
      this.errorFile = out;
   }

   @Override
   public void setErrorProperty(String errorProp) {
      this.errorProperty = errorProp;
   }

   @Override
   public void setOutput(File out) {
      this.outputFile = out;
   }

   @Override
   public void setOutputproperty(String outputProp) {
      this.outputProperty = outputProp;
   }

   @Override
   public void setResultProperty(String resultProperty) {
      this.resultProperty = resultProperty;
   }

   @Override
   public void setAppend(boolean append) {
      this.append = append;
   }
}
