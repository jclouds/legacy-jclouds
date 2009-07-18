package org.jclouds.ssh.jsch.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.jsch.JschSshConnection;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability to configure a {@link JschSshConnection}
 * 
 * @author Adrian Cole
 */
@Test
public class JschSshConnectionModuleTest {

   public void testConfigureBindsClient() throws UnknownHostException {

      Injector i = Guice.createInjector(new JschSshConnectionModule());
      SshConnection.Factory factory = i.getInstance(SshConnection.Factory.class);
      SshConnection connection = factory.create(InetAddress.getLocalHost(), 22, "username",
               "password");
      assert connection instanceof JschSshConnection;
   }
}