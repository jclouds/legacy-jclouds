package org.jclouds.ssh;

import java.io.InputStream;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.inject.assistedinject.Assisted;

/**
 * @author Adrian Cole
 */
public interface SshConnection {
   
   public interface Factory {
      SshConnection create(InetAddress host, int port, @Assisted("username") String username,
               @Assisted("password") String password);
   }

   InputStream get(String path);

   @PostConstruct
   void connect();

   @PreDestroy
   void disconnect();

}