package org.jclouds.overthere.config;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.overthere.OverthereSshClient;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.ConfiguresSshClient;
import org.jclouds.ssh.SshClient;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.OperatingSystemFamily;
import com.xebialabs.overthere.cifs.CifsConnectionBuilder;
import com.xebialabs.overthere.cifs.CifsConnectionType;

/**
 * Module for the Overthere library for remote access to hosts.
 *
 * This is currently biased towards Windows but could theoretically also be
 * used for Linux sessions too via ssh.
 *
 * @author Richard Downer
 * @Deprecated Use OverthereRunScriptClientModule
 */
@Deprecated // Use OverthereRunScriptClientModule
@ConfiguresSshClient
public class OverthereSshClientModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(SshClient.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
      bind(SocketOpen.class).to(InetSocketAddressConnect.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements SshClient.Factory {
      @Override
      public SshClient create(HostAndPort hostAndPort, Credentials credentials) {
         return create(hostAndPort, LoginCredentials.fromCredentials(credentials));
      }

      @Override
      public SshClient create(HostAndPort hostAndPort, LoginCredentials credentials) {
         ConnectionOptions options = new ConnectionOptions();
         options.set(ConnectionOptions.ADDRESS, hostAndPort.getHostText());
         options.set(ConnectionOptions.USERNAME, credentials.getUser());
         options.set(ConnectionOptions.PASSWORD, credentials.getPassword());
         options.set(ConnectionOptions.OPERATING_SYSTEM, OperatingSystemFamily.WINDOWS);
         options.set(CifsConnectionBuilder.CONNECTION_TYPE, CifsConnectionType.WINRM_HTTPS);
         return new OverthereSshClient(options);
      }
   }

}
