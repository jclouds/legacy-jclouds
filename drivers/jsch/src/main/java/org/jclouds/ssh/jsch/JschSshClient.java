/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.base.Throwables.getRootCause;
import static com.google.common.collect.Iterables.any;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Named;

import org.apache.commons.io.input.ProxyInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
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

   private final String host;
   private final int port;
   private final String username;
   private final String password;

   @Inject(optional = true)
   @Named("jclouds.ssh.max_retries")
   @VisibleForTesting
   int sshRetries = 5;

   @Inject(optional = true)
   @Named("jclouds.ssh.retryable_messages")
   @VisibleForTesting
   String retryableMessages = "invalid data,End of IO Stream Read,Connection reset";

   @Inject(optional = true)
   @Named("jclouds.ssh.retry_predicate")
   private Predicate<Throwable> retryPredicate = or(instanceOf(ConnectException.class), instanceOf(IOException.class));

   @Resource
   @Named("jclouds.ssh")
   protected Logger logger = Logger.NULL;

   private Session session;
   private final byte[] privateKey;
   final byte[] emptyPassPhrase = new byte[0];
   private final int timeout;
   private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   public JschSshClient(BackoffLimitedRetryHandler backoffLimitedRetryHandler, IPSocket socket, int timeout,
            String username, String password, byte[] privateKey) {
      this.host = checkNotNull(socket, "socket").getAddress();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      checkArgument(password != null || privateKey != null, "you must specify a password or a key");
      this.port = socket.getPort();
      this.username = checkNotNull(username, "username");
      this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");
      this.timeout = timeout;
      this.password = password;
      this.privateKey = privateKey;
   }

   public Payload get(String path) {
      checkNotNull(path, "path");

      ChannelSftp sftp = getSftp();
      try {
         return Payloads.newInputStreamPayload(new CloseFtpChannelOnCloseInputStream(sftp.get(path), sftp));
      } catch (SftpException e) {
         throw new SshException(String.format("%s@%s:%d: Error getting path: %s", username, host, port, path), e);
      }
   }

   @Override
   public void put(String path, Payload contents) {
      checkNotNull(path, "path");
      checkNotNull(contents, "contents");
      ChannelSftp sftp = getSftp();
      try {
         sftp.put(contents.getInput(), path);
      } catch (SftpException e) {
         throw new SshException(String.format("%s@%s:%d: Error putting path: %s", username, host, port, path), e);
      } finally {
         Closeables.closeQuietly(contents);
      }
   }

   @Override
   public void put(String path, String contents) {
      put(path, Payloads.newStringPayload(checkNotNull(contents, "contents")));
   }

   private ChannelSftp getSftp() {
      checkConnected();
      logger.debug("%s@%s:%d: Opening sftp Channel.", username, host, port);
      ChannelSftp sftp = null;
      try {
         sftp = (ChannelSftp) session.openChannel("sftp");
         sftp.connect();
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error connecting to sftp.", username, host, port), e);
      }
      return sftp;
   }

   private void checkConnected() {
      checkState(session != null && session.isConnected(), String.format("%s@%s:%d: SFTP not connected!", username,
               host, port));
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

            if (i == sshRetries)
               throw propagate(from);

            if (shouldRetry(from)) {
               backoffForAttempt(i + 1, String.format("%s@%s:%d: connection error: %s", username, host, port, from
                        .getMessage()));
               continue;
            }

            throw propagate(from);
         }
      }
      if (e != null)
         throw propagate(e);
   }

   @VisibleForTesting
   boolean shouldRetry(Exception from) {
      final String rootMessage = getRootCause(from).getMessage();
      return any(getCausalChain(from), retryPredicate)
               || Iterables.any(Splitter.on(",").split(retryableMessages), new Predicate<String>() {

                  @Override
                  public boolean apply(String input) {
                     return rootMessage.indexOf(input) != -1;
                  }

               });
   }

   private void backoffForAttempt(int retryAttempt, String message) {
      backoffLimitedRetryHandler.imposeBackoffExponentialDelay(200L, 2, retryAttempt, sshRetries, message);
   }

   private void newSession() throws JSchException {
      JSch jsch = new JSch();
      session = null;
      try {
         session = jsch.getSession(username, host, port);
         if (timeout != 0)
            session.setTimeout(timeout);
         logger.debug("%s@%s:%d: Session created.", username, host, port);
         if (password != null) {
            session.setPassword(password);
         } else {
            // jsch wipes out your private key
            jsch.addIdentity(username, Arrays.copyOf(privateKey, privateKey.length), null, emptyPassPhrase);
         }
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error creating session.", username, host, port), e);
      }
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect();
      logger.debug("%s@%s:%d: Session connected.", username, host, port);
   }

   private SshException propagate(Exception e) {
      throw new SshException(String.format("%s@%s:%d: Error connecting to session.", username, host, port), e);
   }

   @PreDestroy
   public void disconnect() {
      if (session != null && session.isConnected()) {
         session.disconnect();
         session = null;
      }
   }

   public ExecResponse exec(String command) {
      checkConnected();
      ChannelExec executor = null;
      try {
         try {
            executor = (ChannelExec) session.openChannel("exec");
            executor.setPty(true);
         } catch (JSchException e) {
            throw new SshException(String.format("%s@%s:%d: Error connecting to exec.", username, host, port), e);
         }
         executor.setCommand(command);
         ByteArrayOutputStream error = new ByteArrayOutputStream();
         executor.setErrStream(error);
         try {
            executor.connect();
            String outputString = Strings2.toStringAndClose(executor.getInputStream());
            String errorString = error.toString();
            int errorStatus = executor.getExitStatus();
            int i = 0;
            while ((errorStatus = executor.getExitStatus()) == -1 && i < this.sshRetries)
               backoffForAttempt(++i, String.format("%s@%s:%d: bad status: -1", username, host, port));
            if (errorStatus == -1)
               throw new SshException(String.format("%s@%s:%d: received exit status %d executing %s", username, host,
                        port, executor.getExitStatus(), command));
            return new ExecResponse(outputString, errorString, errorStatus);
         } catch (Exception e) {
            throw new SshException(String
                     .format("%s@%s:%d: Error executing command: %s", username, host, port, command), e);
         }
      } finally {
         if (executor != null)
            executor.disconnect();
      }
   }

   @Override
   public String getHostAddress() {
      return this.host;
   }

   @Override
   public String getUsername() {
      return this.username;
   }

}
