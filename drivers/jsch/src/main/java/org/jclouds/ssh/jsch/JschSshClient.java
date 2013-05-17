/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ssh.jsch;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.any;
import static com.google.common.hash.Hashing.md5;
import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.ssh.SshKeys.fingerprintPrivateKey;
import static org.jclouds.ssh.SshKeys.sha1PrivateKey;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.io.Closeables;
import com.google.common.net.HostAndPort;
import com.google.inject.Inject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * This class needs refactoring. It is not thread safe.
 * 
 * @author Adrian Cole
 */
public class JschSshClient implements SshClient {

   private static final class CloseFtpChannelOnCloseInputStream extends FilterInputStream {

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
   
   private final String toString;

   @Inject(optional = true)
   @Named("jclouds.ssh.max-retries")
   @VisibleForTesting
   int sshRetries = 5;

   @Inject(optional = true)
   @Named("jclouds.ssh.retry-auth")
   @VisibleForTesting
   boolean retryAuth;

   @Inject(optional = true)
   @Named("jclouds.ssh.retryable-messages")
   @VisibleForTesting
   String retryableMessages = "failed to send channel request,channel is not opened,invalid data,End of IO Stream Read,Connection reset,connection is closed by foreign host,socket is not established";

   @Inject(optional = true)
   @Named("jclouds.ssh.retry-predicate")
   Predicate<Throwable> retryPredicate = or(instanceOf(ConnectException.class), instanceOf(IOException.class));

   @Resource
   @Named("jclouds.ssh")
   protected Logger logger = Logger.NULL;

   private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   final SessionConnection sessionConnection;
   final String user;
   final String host;


   public JschSshClient(ProxyConfig proxyConfig, BackoffLimitedRetryHandler backoffLimitedRetryHandler, HostAndPort socket,
            LoginCredentials loginCredentials, int timeout) {
      this.user = checkNotNull(loginCredentials, "loginCredentials").getUser();
      this.host = checkNotNull(socket, "socket").getHostText();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      checkArgument(loginCredentials.getPassword() != null || loginCredentials.getPrivateKey() != null,
               "you must specify a password or a key");
      this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");
      if (loginCredentials.getPrivateKey() == null) {
         this.toString = String.format("%s:pw[%s]@%s:%d", loginCredentials.getUser(),
               base16().lowerCase().encode(md5().hashString(loginCredentials.getPassword(), UTF_8).asBytes()), host,
               socket.getPort());
      } else {
         String fingerPrint = fingerprintPrivateKey(loginCredentials.getPrivateKey());
         String sha1 = sha1PrivateKey(loginCredentials.getPrivateKey());
         this.toString = String.format("%s:rsa[fingerprint(%s),sha1(%s)]@%s:%d", loginCredentials.getUser(),
                  fingerPrint, sha1, host, socket.getPort());
      }
      sessionConnection = SessionConnection.builder().hostAndPort(HostAndPort.fromParts(host, socket.getPort())).loginCredentials(
               loginCredentials).proxy(checkNotNull(proxyConfig, "proxyConfig")).connectTimeout(timeout).sessionTimeout(timeout).build();
   }

   @Override
   public void put(String path, String contents) {
      put(path, Payloads.newStringPayload(checkNotNull(contents, "contents")));
   }

   private void checkConnected() {
      checkState(sessionConnection.getSession() != null && sessionConnection.getSession().isConnected(), String.format(
               "(%s) Session not connected!", toString()));
   }

   public static interface Connection<T> {
      void clear();

      T create() throws Exception;
   }

   public static interface ConnectionWithStreams<T> extends Connection<T> {
      InputStream getInputStream();
      InputStream getErrStream();
   }

   protected <T, C extends Connection<T>> T acquire(C connection) {
      connection.clear();
      String errorMessage = String.format("(%s) error acquiring %s", toString(), connection);
      for (int i = 0; i < sshRetries; i++) {
         try {
            logger.debug(">> (%s) acquiring %s", toString(), connection);
            T returnVal = connection.create();
            logger.debug("<< (%s) acquired %s", toString(), returnVal);
            return returnVal;
         } catch (Exception from) {
            connection.clear();

            if (i + 1 == sshRetries) {
               throw propagate(from, errorMessage);
            } else if (shouldRetry(from)) {
               logger.warn(from, "<< " + errorMessage + ": " + from.getMessage());
               backoffForAttempt(i + 1, errorMessage + ": " + from.getMessage());
               continue;
            }
         }
      }
      assert false : "should not reach here";
      return null;
   }

   public void connect() {
      acquire(sessionConnection);
   }

   Connection<ChannelSftp> sftpConnection = new Connection<ChannelSftp>() {

      private ChannelSftp sftp;

      @Override
      public void clear() {
         if (sftp != null)
            sftp.disconnect();
      }

      @Override
      public ChannelSftp create() throws JSchException {
         checkConnected();
         String channel = "sftp";
         sftp = (ChannelSftp) sessionConnection.getSession().openChannel(channel);
         sftp.connect();
         return sftp;
      }

      @Override
      public String toString() {
         return "ChannelSftp()";
      }
   };

   class GetConnection implements Connection<Payload> {
      private final String path;
      private ChannelSftp sftp;

      GetConnection(String path) {
         this.path = checkNotNull(path, "path");
      }

      @Override
      public void clear() {
         if (sftp != null)
            sftp.disconnect();
      }

      @Override
      public Payload create() throws Exception {
         sftp = acquire(sftpConnection);
         return Payloads.newInputStreamPayload(new CloseFtpChannelOnCloseInputStream(sftp.get(path), sftp));
      }

      @Override
      public String toString() {
         return "Payload(path=[" + path + "])";
      }
   };

   public Payload get(String path) {
      return acquire(new GetConnection(path));
   }

   class PutConnection implements Connection<Void> {
      private final String path;
      private final Payload contents;
      private ChannelSftp sftp;

      PutConnection(String path, Payload contents) {
         this.path = checkNotNull(path, "path");
         this.contents = checkNotNull(contents, "contents");
      }

      @Override
      public void clear() {
         if (sftp != null)
            sftp.disconnect();
      }

      @Override
      public Void create() throws Exception {
         sftp = acquire(sftpConnection);
         InputStream is = checkNotNull(contents.getInput(), "inputstream for path %s", path);
         try {
            sftp.put(is, path);
         } finally {
            Closeables.closeQuietly(contents);
         }
         return null;
      }

      @Override
      public String toString() {
         return "Put(path=[" + path + "])";
      }
   };

   @Override
   public void put(String path, Payload contents) {
      acquire(new PutConnection(path, contents));
   }

   @VisibleForTesting
   boolean shouldRetry(Exception from) {
      Predicate<Throwable> predicate = retryAuth ? Predicates.<Throwable> or(retryPredicate,
               instanceOf(AuthorizationException.class)) : retryPredicate;
      if (any(getCausalChain(from), predicate))
         return true;
      if (!retryableMessages.equals(""))
         return any(Splitter.on(",").split(retryableMessages), causalChainHasMessageContaining(from));
      return false;
   }

   @VisibleForTesting
   Predicate<String> causalChainHasMessageContaining(final Exception from) {
      return new Predicate<String>() {

         @Override
         public boolean apply(final String input) {
            return any(getCausalChain(from), new Predicate<Throwable>() {

               @Override
               public boolean apply(Throwable arg0) {
                  return (arg0.toString().indexOf(input) != -1)
                           || (arg0.getMessage() != null && arg0.getMessage().indexOf(input) != -1);
               }

            });
         }

      };
   }

   private void backoffForAttempt(int retryAttempt, String message) {
      backoffLimitedRetryHandler.imposeBackoffExponentialDelay(200L, 2, retryAttempt, sshRetries, message);
   }

   SshException propagate(Exception e, String message) {
      message += ": " + e.getMessage();
      if (e.getMessage() != null && e.getMessage().indexOf("Auth fail") != -1)
         throw new AuthorizationException("(" + toString() + ") " + message, e);
      throw e instanceof SshException ? SshException.class.cast(e) : new SshException(
               "(" + toString() + ") " + message, e);
   }

   @Override
   public String toString() {
      return toString;
   }

   @PreDestroy
   public void disconnect() {
      sessionConnection.clear();
   }

   protected ConnectionWithStreams<ChannelExec> execConnection(final String command) {
      checkNotNull(command, "command");
      return new ConnectionWithStreams<ChannelExec>() {

         private ChannelExec executor = null;
         private InputStream inputStream;
         private InputStream errStream;
         
         @Override
         public void clear() {
            if (inputStream != null)
               Closeables.closeQuietly(inputStream);
            if (errStream != null)
               Closeables.closeQuietly(errStream);
            if (executor != null)
               executor.disconnect();
         }

         @Override
         public ChannelExec create() throws Exception {
            checkConnected();
            String channel = "exec";
            executor = (ChannelExec) sessionConnection.getSession().openChannel(channel);
            executor.setPty(true);
            executor.setCommand(command);
            inputStream = executor.getInputStream();
            errStream = executor.getErrStream();
            executor.connect();
            
            return executor;
         }

         @Override
         public InputStream getInputStream() {
            return inputStream;
         }

         @Override
         public InputStream getErrStream() {
            return errStream;
         }

         @Override
         public String toString() {
            return "ChannelExec()";
         }
      };

   }

   class ExecConnection implements Connection<ExecResponse> {
      private final String command;
      private ChannelExec executor;

      ExecConnection(String command) {
         this.command = checkNotNull(command, "command");
      }

      @Override
      public void clear() {
         if (executor != null)
            executor.disconnect();
      }

      @Override
      public ExecResponse create() throws Exception {
         try {
            ConnectionWithStreams<ChannelExec> connection = execConnection(command);
            executor = acquire(connection);
            String outputString = Strings2.toStringAndClose(connection.getInputStream());
            String errorString = Strings2.toStringAndClose(connection.getErrStream());
            int errorStatus = executor.getExitStatus();
            int i = 0;
            String message = String.format("bad status -1 %s", toString());
            while ((errorStatus = executor.getExitStatus()) == -1 && i < JschSshClient.this.sshRetries) {
               logger.warn("<< " + message);
               backoffForAttempt(++i, message);
            }
            if (errorStatus == -1)
               throw new SshException(message);
            return new ExecResponse(outputString, errorString, errorStatus);
         } finally {
            clear();
         }
      }

      @Override
      public String toString() {
         return "ExecResponse(command=[" + command + "])";
      }
   }

   public ExecResponse exec(String command) {
      return acquire(new ExecConnection(command));
   }

   @Override
   public String getHostAddress() {
      return this.host;
   }

   @Override
   public String getUsername() {
      return this.user;
   }

   class ExecChannelConnection implements Connection<ExecChannel> {
      private final String command;
      private ChannelExec executor = null;
      private Session sessionConnection;
      
      ExecChannelConnection(String command) {
         this.command = checkNotNull(command, "command");
      }

      @Override
      public void clear() {
         if (executor != null)
            executor.disconnect();
         if (sessionConnection != null)
            sessionConnection.disconnect();
      }

      @Override
      public ExecChannel create() throws Exception {
         this.sessionConnection = acquire(SessionConnection.builder().from(JschSshClient.this.sessionConnection)
               .sessionTimeout(0).build());
         String channel = "exec";
         executor = (ChannelExec) sessionConnection.openChannel(channel);
         executor.setCommand(command);
         executor.setErrStream(new ByteArrayOutputStream());
         InputStream inputStream = executor.getInputStream();
         InputStream errStream = executor.getErrStream();
         OutputStream outStream = executor.getOutputStream();
         executor.connect();
         return new ExecChannel(outStream, inputStream, errStream,
                  new Supplier<Integer>() {

                     @Override
                     public Integer get() {
                        int exitStatus = executor.getExitStatus();
                        return exitStatus != -1 ? exitStatus : null;
                     }

                  }, new Closeable() {

                     @Override
                     public void close() throws IOException {
                        clear();
                     }

                  });
      }

      @Override
      public String toString() {
         return "ExecChannel(command=[" + command + "])";
      }
   };

   @Override
   public ExecChannel execChannel(String command) {
      return acquire(new ExecChannelConnection(command));
   }

}
