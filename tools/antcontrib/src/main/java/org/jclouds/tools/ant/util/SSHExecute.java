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
package org.jclouds.tools.ant.util;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHUserInfo;
import org.apache.tools.ant.util.FileUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Executes a command on a remote machine via ssh.
 * 
 * <p/>
 * adapted from SSHBase and SSHExec ant tasks, and Execute from the ant 1.7.1 release.
 * 
 * @author Adrian Cole
 * 
 */
public class SSHExecute {

   private static final int RETRY_INTERVAL = 500;

   /** units are milliseconds, default is 0=infinite */
   private long maxwait = 0;

   private ExecuteStreamHandler streamHandler;
   private String host;
   private SSHUserInfo userInfo;
   private int port = 22;
   private Project project;
   private String knownHosts = System.getProperty("user.home") + "/.ssh/known_hosts";

   /**
    * Creates a new execute object using <code>PumpStreamHandler</code> for stream handling.
    */
   public SSHExecute() {
      this(new PumpStreamHandler());
   }

   /**
    * Creates a new ssh object.
    * 
    * @param streamHandler
    *           the stream handler used to handle the input and output streams of the subprocess.
    */
   public SSHExecute(ExecuteStreamHandler streamHandler) {
      setStreamHandler(streamHandler);
      userInfo = new SSHUserInfo();
   }

   /**
    * Set the stream handler to use.
    * 
    * @param streamHandler
    *           ExecuteStreamHandler.
    */
   public void setStreamHandler(ExecuteStreamHandler streamHandler) {
      this.streamHandler = streamHandler;
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
    * Used for logging
    */
   public void setProject(Project project) {
      this.project = project;
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
    * Remote host, either DNS name or IP.
    * 
    * @param host
    *           The new host value
    */
   public void setHost(String host) {
      this.host = host;
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
    * The connection can be dropped after a specified number of milliseconds. This is sometimes
    * useful when a connection may be flaky. Default is 0, which means &quot;wait forever&quot;.
    * 
    * @param timeout
    *           The new timeout value in seconds
    */
   public void setTimeout(long timeout) {
      maxwait = timeout;
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
    * Execute the command on the remote host.
    * 
    * @param command
    *           - what to execute on the remote host.
    * 
    * @return return code of the process.
    * @throws BuildException
    *            bad parameter.
    * @throws JSchException
    *            if there's an underlying problem exposed in SSH
    * @throws IOException
    *            if there's a problem attaching streams.
    * @throws TimeoutException
    *            if we exceeded our timeout
    */
   public int execute(String command) throws BuildException, JSchException, IOException,
            TimeoutException {
      if (command == null) {
         throw new BuildException("Command is required.");
      }
      if (host == null) {
         throw new BuildException("Host is required.");
      }
      if (userInfo.getName() == null) {
         throw new BuildException("Username is required.");
      }
      if (userInfo.getKeyfile() == null && userInfo.getPassword() == null) {
         throw new BuildException("Password or Keyfile is required.");
      }

      Session session = null;
      try {
         session = openSession();
         return executeCommand(session, command);
      } finally {
         if (session != null && session.isConnected()) {
            session.disconnect();
         }
      }
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
         project.log("Using known hosts: " + knownHosts, Project.MSG_DEBUG);
         jsch.setKnownHosts(knownHosts);
      }

      Session session = jsch.getSession(userInfo.getName(), host, port);
      session.setUserInfo(userInfo);
      project.log("Connecting to " + host + ":" + port, Project.MSG_VERBOSE);
      session.connect();
      return session;
   }

   /**
    * 
    * FIXME Comment this
    * 
    * @param session
    * @param cmd
    * @return return code of the process.
    * @throws JSchException
    *            if there's an underlying problem exposed in SSH
    * @throws IOException
    *            if there's a problem attaching streams.
    * @throws TimeoutException
    *            if we exceeded our timeout
    */
   private int executeCommand(Session session, String cmd) throws JSchException, IOException,
            TimeoutException {
      final ChannelExec channel;
      session.setTimeout((int) maxwait);
      /* execute the command */
      channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(cmd);
      attachStreams(channel);
      project.log("executing command: " + cmd, Project.MSG_VERBOSE);
      channel.connect();
      try {
         waitFor(channel);
      } finally {
         streamHandler.stop();
         closeStreams(channel);
      }
      return channel.getExitStatus();
   }

   private void attachStreams(final ChannelExec channel) throws IOException {
      streamHandler.setProcessInputStream(channel.getOutputStream());
      streamHandler.setProcessOutputStream(channel.getInputStream());
      streamHandler.setProcessErrorStream(channel.getErrStream());
      streamHandler.start();
   }

   /**
    * Close the streams belonging to the given Process.
    * 
    * @param process
    *           the <code>Process</code>.
    * @throws IOException
    */
   public static void closeStreams(ChannelExec process) throws IOException {
      FileUtils.close(process.getInputStream());
      FileUtils.close(process.getOutputStream());
      FileUtils.close(process.getErrStream());
   }

   /**
    * @throws TimeoutException
    */
   @SuppressWarnings("deprecation")
   private void waitFor(final ChannelExec channel) throws TimeoutException {
      // wait for it to finish
      Thread thread = new Thread() {
         public void run() {
            while (!channel.isClosed()) {
               try {
                  sleep(RETRY_INTERVAL);
               } catch (InterruptedException e) {
                  // ignored
               }
            }
         }
      };

      thread.start();
      try {
         thread.join(maxwait);
      } catch (InterruptedException e) {
         // ignored
      }

      if (thread.isAlive()) {
         thread.destroy();
         throw new TimeoutException("command still running");
      }
   }

}
