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
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Named;

import org.apache.commons.io.input.ProxyInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.Constants;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Utils;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * This class needs refactoring. It is not thread safe.
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
   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private int sshRetries = 5;

   @Resource
   protected Logger logger = Logger.NULL;
   private Session session;
   private final byte[] privateKey;
   final byte[] emptyPassPhrase = new byte[0];
   private final int timeout;
   private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   public JschSshClient(BackoffLimitedRetryHandler backoffLimitedRetryHandler,
            InetSocketAddress socket, int timeout, String username, String password,
            byte[] privateKey) {
      this.host = checkNotNull(socket, "socket").getAddress();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      checkArgument(password != null || privateKey != null, "you must specify a password or a key");
      this.port = socket.getPort();
      this.username = checkNotNull(username, "username");
      this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler,
               "backoffLimitedRetryHandler");
      this.timeout = timeout;
      this.password = password;
      this.privateKey = privateKey;
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
      Exception e = null;
      RETRY_LOOP: for (int i = 0; i < sshRetries; i++) {
         try {
            newSession();
            e = null;
            break RETRY_LOOP;
         } catch (Exception from) {
            e = from;
            disconnect();
            String rootMessage = Throwables.getRootCause(from).getMessage();
            if (i == sshRetries)
               throw propagate(from);
            if (Iterables.size(Iterables.filter(Throwables.getCausalChain(from),
                     ConnectException.class)) >= 1
                     || Iterables.size(Iterables.filter(Throwables.getCausalChain(from),
                              IOException.class)) >= 1
                     || rootMessage.indexOf("invalid privatekey") != -1
                     || rootMessage.indexOf("Auth fail") != -1// auth fail sometimes happens in EC2,
                     // as the script that injects the
                     // authorized key executes after ssh
                     // has started
                     || rootMessage.indexOf("invalid data") != -1
                     || rootMessage.indexOf("End of IO Stream Read") != -1) {
               backoffLimitedRetryHandler.imposeBackoffExponentialDelay(200L, 2, i + 1, String
                        .format("%s@%s:%d: connection error: %s", username, host.getHostAddress(),
                                 port, from.getMessage()));
               continue;
            }
            throw propagate(from);
         }
      }
      if (e != null)
         throw propagate(e);
   }

   private void newSession() throws JSchException {
      JSch jsch = new JSch();
      session = null;
      try {
         session = jsch.getSession(username, host.getHostAddress(), port);
         if (timeout != 0)
            session.setTimeout(timeout);
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
      session.connect();
      logger.debug("%s@%s:%d: Session connected.", username, host.getHostAddress(), port);
   }

   private SshException propagate(Exception e) {
      throw new SshException(String.format("%s@%s:%d: Error connecting to session.", username, host
               .getHostAddress(), port), e);
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
                     .toString(), executor.getExitStatus());
         } catch (Exception e) {
            throw new SshException(String.format("%s@%s:%d: Error executing command: ", username,
                     host.getHostAddress(), port, command), e);
         }
      } finally {
         if (executor != null)
            executor.disconnect();
      }
   }

   @Override
   public String getHostAddress() {
      return this.host.getHostAddress();
   }

   @Override
   public String getUsername() {
      return this.username;
   }

}
