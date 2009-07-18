package org.jclouds.ssh.jsch;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.SshException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * @author Adrian Cole
 */
public class JschSshConnection implements SshConnection {

   private final InetAddress host;
   private final int port;
   private final String username;
   private final String password;
   private ChannelSftp sftp;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public JschSshConnection(@Assisted InetAddress host, @Assisted int port,
            @Assisted("username") String username, @Assisted("password") String password) {
      this.host = checkNotNull(host, "host");
      checkArgument(port > 0, "ssh port must be greater then zero" + port);
      this.port = port;
      this.username = checkNotNull(username, "username");
      this.password = checkNotNull(password, "password");
   }

   public InputStream get(String path) {
      checkConnected();
      checkNotNull(path, "path");
      try {
         return sftp.get(path);
      } catch (SftpException e) {
         throw new SshException(String.format("%s@%s:%d: Error getting path: %s", username, host
                  .getHostAddress(), port, path), e);
      }
   }

   private void checkConnected() {
      checkState(sftp.isConnected(), String.format("%s@%s:%d: SFTP not connected!", username, host
               .getHostAddress(), port));
   }

   @PostConstruct
   public void connect() {
      if (sftp != null && sftp.isConnected())
         return;
      JSch jsch = new JSch();
      Session session = null;
      Channel channel = null;
      try {
         session = jsch.getSession(username, host.getHostAddress(), port);
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error creating session.", username, host
                  .getHostAddress(), port), e);
      }
      logger.debug("%s@%s:%d: Session created.", username, host.getHostAddress(), port);
      session.setPassword(password);
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
      logger.debug("%s@%s:%d: Opening sftp Channel.", username, host.getHostAddress(), port);
      try {
         channel = session.openChannel("sftp");
         channel.connect();
      } catch (JSchException e) {
         throw new SshException(String.format("%s@%s:%d: Error connecting to sftp.", username, host
                  .getHostAddress(), port), e);
      }
      sftp = (ChannelSftp) channel;
   }

   @PreDestroy
   public void disconnect() {
      if (sftp != null && sftp.isConnected())
         sftp.quit();
   }

}
