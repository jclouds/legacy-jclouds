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
package org.jclouds.ssh.jsch;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.io.input.ProxyInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * @author Adrian Cole
 */
public class JschSshClient implements SshClient {

   private final class CloseFtpChannelOnCloseInputStream extends ProxyInputStream {

      private final ChannelSftp sftp;

      private CloseFtpChannelOnCloseInputStream(InputStream proxy, ChannelSftp sftp) {
         super(proxy);
         this.sftp = sftp;
      }

      @Override
      public void close() throws IOException {
         super.close();
         if (sftp != null)
            sftp.disconnect();
      }
   }

   private final InetAddress host;
   private final int port;
   private final String username;
   private final String password;

   @Resource
   protected Logger logger = Logger.NULL;
   private Session session;
   private final byte[] privateKey;
   final byte[] emptyPassPhrase = new byte[0];

   @Inject
   public JschSshClient(InetSocketAddress socket, String username, String password) {
      this.host = checkNotNull(socket, "socket").getAddress();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      this.port = socket.getPort();
      this.username = checkNotNull(username, "username");
      this.password = checkNotNull(password, "password");
      this.privateKey = null;
   }

   @Inject
   public JschSshClient(InetSocketAddress socket, String username, byte[] privateKey) {
      this.host = checkNotNull(socket, "socket").getAddress();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      this.port = socket.getPort();
      this.username = checkNotNull(username, "username");
      this.password = null;
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   public InputStream get(String path) {
      checkNotNull(path, "path");

      checkConnected();
      logger.debug("%s@%s:%d: Opening sftp Channel.", username, host.getHostAddress(), port);
      ChannelSftp sftp = null;
      try {
         sftp = (ChannelSftp) session.openChannel("sftp");
         sftp.connect();
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error connecting to sftp.", username, host
                  .getHostAddress(), port), e);
      }
      try {
         return new CloseFtpChannelOnCloseInputStream(sftp.get(path), sftp);
      } catch (SftpException e) {
         throw new SshException(String.format("%s@%s:%d: Error getting path: %s", username, host
                  .getHostAddress(), port, path), e);
      }
   }

   public void put(String path, InputStream contents) {
      checkNotNull(path, "path");
      checkNotNull(contents, "contents");

      checkConnected();
      logger.debug("%s@%s:%d: Opening sftp Channel.", username, host.getHostAddress(), port);
      ChannelSftp sftp = null;
      try {
         sftp = (ChannelSftp) session.openChannel("sftp");
         sftp.connect();
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error connecting to sftp.", username, host
                  .getHostAddress(), port), e);
      }
      try {
         sftp.put(contents, path);
      } catch (SftpException e) {
         throw new SshException(String.format("%s@%s:%d: Error putting path: %s", username, host
                  .getHostAddress(), port, path), e);
      } finally {
         Closeables.closeQuietly(contents);
      }
   }

   private void checkConnected() {
      checkState(session != null && session.isConnected(), String.format(
               "%s@%s:%d: SFTP not connected!", username, host.getHostAddress(), port));
   }

   @PostConstruct
   public void connect() {
      disconnect();
      JSch jsch = new JSch();
      session = null;
      try {
         session = jsch.getSession(username, host.getHostAddress(), port);
         session.setTimeout(120 * 1000);
         logger.debug("%s@%s:%d: Session created.", username, host.getHostAddress(), port);
         if (password != null) {
            session.setPassword(password);
         } else {
            jsch.addIdentity(username, privateKey, null, emptyPassPhrase);
         }
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error creating session.", username, host
                  .getHostAddress(), port), e);
      }
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      try {
         session.connect();
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error connecting to session.", username,
                  host.getHostAddress(), port), e);
      }
      logger.debug("%s@%s:%d: Session connected.", username, host.getHostAddress(), port);
   }

   @PreDestroy
   public void disconnect() {
      if (session != null && session.isConnected())
         session.disconnect();
   }

   public ExecResponse exec(String command) {
      checkConnected();
      ChannelExec executor = null;
      try {
         try {
            executor = (ChannelExec) session.openChannel("exec");
         } catch (JSchException e) {
            throw new SshException(String.format("%s@%s:%d: Error connecting to exec.", username,
                     host.getHostAddress(), port), e);
         }
         executor.setCommand(command);
         ByteArrayOutputStream error = new ByteArrayOutputStream();
         executor.setErrStream(error);
         try {
            executor.connect();
            return new ExecResponse(Utils.toStringAndClose(executor.getInputStream()), error
                     .toString());
         } catch (Exception e) {
            throw new SshException(String.format("%s@%s:%d: Error executing command: ", username,
                     host.getHostAddress(), port, command), e);
         }
      } finally {
         if (executor != null)
            executor.disconnect();
      }
   }

}
