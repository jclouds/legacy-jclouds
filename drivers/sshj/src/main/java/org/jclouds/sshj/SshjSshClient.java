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
package org.jclouds.sshj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;
import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.any;
import static org.jclouds.crypto.SshKeys.fingerprint;

import java.io.IOException;
import java.io.InputStream;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Named;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.xfer.InMemorySourceFile;

import org.apache.commons.io.input.ProxyInputStream;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.crypto.Pems;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * This class needs refactoring. It is not thread safe.
 * 
 * @author Adrian Cole
 */
public class SshjSshClient implements SshClient {

   private final class CloseFtpChannelOnCloseInputStream extends ProxyInputStream {

      private final SFTPClient sftp;

      private CloseFtpChannelOnCloseInputStream(InputStream proxy, SFTPClient sftp) {
         super(proxy);
         this.sftp = sftp;
      }

      @Override
      public void close() throws IOException {
         super.close();
         if (sftp != null)
            sftp.close();
      }
   }

   private final String host;
   private final int port;
   private final String username;
   private final String password;
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
   String retryableMessages = "";

   @Inject(optional = true)
   @Named("jclouds.ssh.retry-predicate")
   // NOTE cannot retry io exceptions, as SSHException is a part of the chain
   private Predicate<Throwable> retryPredicate = or(instanceOf(ConnectionException.class),
         instanceOf(TransportException.class));

   @Resource
   @Named("jclouds.ssh")
   protected Logger logger = Logger.NULL;

   private net.schmizz.sshj.SSHClient ssh;
   private final byte[] privateKey;
   final byte[] emptyPassPhrase = new byte[0];
   private final int timeoutMillis;
   private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;

   public SshjSshClient(BackoffLimitedRetryHandler backoffLimitedRetryHandler, IPSocket socket, int timeout,
         String username, String password, byte[] privateKey) {
      this.host = checkNotNull(socket, "socket").getAddress();
      checkArgument(socket.getPort() > 0, "ssh port must be greater then zero" + socket.getPort());
      checkArgument(password != null || privateKey != null, "you must specify a password or a key");
      this.port = socket.getPort();
      this.username = checkNotNull(username, "username");
      this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");
      this.timeoutMillis = timeout;
      this.password = password;
      this.privateKey = privateKey;
      if (privateKey == null) {
         this.toString = String.format("%s@%s:%d", username, host, port);
      } else {
         RSAPrivateCrtKeySpec key = (RSAPrivateCrtKeySpec) Pems.privateKeySpec(new String(privateKey));
         String fingerPrint = fingerprint(key.getPublicExponent(), key.getModulus());
         this.toString = String.format("%s:[%s]@%s:%d", username, fingerPrint, host, port);
      }
   }

   @Override
   public void put(String path, String contents) {
      put(path, Payloads.newStringPayload(checkNotNull(contents, "contents")));
   }

   private void checkConnected() {
      checkState(ssh != null && ssh.isConnected(), String.format("(%s) ssh not connected!", toString()));
   }

   public static interface Connection<T> {
      void clear() throws Exception;

      T create() throws Exception;
   }

   Connection<net.schmizz.sshj.SSHClient> sshConnection = new Connection<net.schmizz.sshj.SSHClient>() {

      @Override
      public void clear() {
         if (ssh != null && ssh.isConnected()) {
            try {
               ssh.disconnect();
            } catch (IOException e) {
               Throwables.propagate(e);
            }
            ssh = null;
         }
      }

      @Override
      public net.schmizz.sshj.SSHClient create() throws Exception {
         net.schmizz.sshj.SSHClient ssh = new net.schmizz.sshj.SSHClient();
         ssh.addHostKeyVerifier(new PromiscuousVerifier());
         if (timeoutMillis != 0) {
            ssh.setTimeout(timeoutMillis);
            ssh.setConnectTimeout(timeoutMillis);
         }
         ssh.connect(host, port);
         if (password != null) {
            ssh.authPassword(username, password);
         } else {
            OpenSSHKeyFile key = new OpenSSHKeyFile();
            key.init(new String(privateKey), null);
            ssh.authPublickey(username, key);
         }
         return ssh;
      }

      @Override
      public String toString() {
         return String.format("SSHClient(%s)", SshjSshClient.this.toString());
      }
   };

   private void backoffForAttempt(int retryAttempt, String message) {
      backoffLimitedRetryHandler.imposeBackoffExponentialDelay(200L, 2, retryAttempt, sshRetries, message);
   }

   protected <T, C extends Connection<T>> T acquire(C connection) {
      String errorMessage = String.format("(%s) error acquiring %s", toString(), connection);
      for (int i = 0; i < sshRetries; i++) {
         try {
            connection.clear();
            logger.debug(">> (%s) acquiring %s", toString(), connection);
            T returnVal = connection.create();
            logger.debug("<< (%s) acquired %s", toString(), returnVal);
            return returnVal;
         } catch (Exception from) {
            try {
               connection.clear();
            } catch (Exception e1) {
               logger.warn(from, "<< (%s) error closing connection", toString());
            }
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

   @PostConstruct
   public void connect() {
      try {
         ssh = acquire(sshConnection);
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   Connection<SFTPClient> sftpConnection = new Connection<SFTPClient>() {

      private SFTPClient sftp;

      @Override
      public void clear() {
         if (sftp != null)
            try {
               sftp.close();
            } catch (IOException e) {
               Throwables.propagate(e);
            }
      }

      @Override
      public SFTPClient create() throws IOException {
         checkConnected();
         sftp = ssh.newSFTPClient();
         return sftp;
      }

      @Override
      public String toString() {
         return "SFTPClient(" + SshjSshClient.this.toString() + ")";
      }
   };

   class GetConnection implements Connection<Payload> {
      private final String path;
      private SFTPClient sftp;

      GetConnection(String path) {
         this.path = checkNotNull(path, "path");
      }

      @Override
      public void clear() throws IOException {
         if (sftp != null)
            sftp.close();
      }

      @Override
      public Payload create() throws Exception {
         sftp = acquire(sftpConnection);
         return Payloads.newInputStreamPayload(new CloseFtpChannelOnCloseInputStream(sftp.getSFTPEngine().open(path)
               .getInputStream(), sftp));
      }

      @Override
      public String toString() {
         return "Payload(" + SshjSshClient.this.toString() + ")[" + path + "]";
      }
   };

   public Payload get(String path) {
      return acquire(new GetConnection(path));
   }

   class PutConnection implements Connection<Void> {
      private final String path;
      private final Payload contents;
      private SFTPClient sftp;

      PutConnection(String path, Payload contents) {
         this.path = checkNotNull(path, "path");
         this.contents = checkNotNull(contents, "contents");
      }

      @Override
      public void clear() {
         if (sftp != null)
            try {
               sftp.close();
            } catch (IOException e) {
               Throwables.propagate(e);
            }
      }

      @Override
      public Void create() throws Exception {
         sftp = acquire(sftpConnection);
         try {
            sftp.put(new InMemorySourceFile() {

               @Override
               public String getName() {
                  return path;
               }

               @Override
               public long getLength() {
                  return contents.getContentMetadata().getContentLength();
               }

               @Override
               public InputStream getInputStream() throws IOException {
                  return checkNotNull(contents.getInput(), "inputstream for path %s", path);
               }

            }, path);
         } finally {
            contents.release();
         }
         return null;
      }

      @Override
      public String toString() {
         return "Put(" + SshjSshClient.this.toString() + ")[" + path + "]";
      }
   };

   @Override
   public void put(String path, Payload contents) {
      acquire(new PutConnection(path, contents));
   }

   @VisibleForTesting
   boolean shouldRetry(Exception from) {
      Predicate<Throwable> predicate = retryAuth ?  Predicates.<Throwable>or(retryPredicate, instanceOf(AuthorizationException.class))
            : retryPredicate;
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
                  return arg0.getMessage() != null && arg0.getMessage().indexOf(input) != -1;
               }

            });
         }

      };
   }

   @VisibleForTesting
   SshException propagate(Exception e, String message) {
      message += ": " + e.getMessage();
      logger.error(e, "<< " + message);
      if (e instanceof UserAuthException)
         throw new AuthorizationException("(" + toString() + ") " + message, e);
      throw e instanceof SshException ? SshException.class.cast(e) : new SshException(
            "(" + toString() + ") " + message, e);
   }

   @Override
   public String toString() {
      return toString ;
   }

   @PreDestroy
   public void disconnect() {
      try {
         sshConnection.clear();
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   protected Connection<Session> execConnection() {

      return new Connection<Session>() {

         private Session session = null;

         @Override
         public void clear() throws TransportException, ConnectionException {
            if (session != null)
               session.close();
         }

         @Override
         public Session create() throws Exception {
            checkConnected();
            session = ssh.startSession();
            session.allocateDefaultPTY();
            return session;
         }

         @Override
         public String toString() {
            return "Session(" + SshjSshClient.this.toString() + ")";
         }
      };

   }

   class ExecConnection implements Connection<ExecResponse> {
      private final String command;
      private Session session;

      ExecConnection(String command) {
         this.command = checkNotNull(command, "command");
      }

      @Override
      public void clear() throws TransportException, ConnectionException {
         if (session != null)
            session.close();
      }

      @Override
      public ExecResponse create() throws Exception {
         try {
            session = acquire(execConnection());
            Command output = session.exec(checkNotNull(command, "command"));
            String outputString = IOUtils.readFully(output.getInputStream()).toString();
            output.join(timeoutMillis, TimeUnit.SECONDS);
            int errorStatus = output.getExitStatus();
            String errorString = IOUtils.readFully(output.getErrorStream()).toString();
            return new ExecResponse(outputString, errorString, errorStatus);
         } finally {
            clear();
         }
      }

      @Override
      public String toString() {
         return "ExecResponse(" + SshjSshClient.this.toString() + ")[" + command + "]";
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
      return this.username;
   }

}
