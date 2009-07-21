package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.jclouds.util.Utils;
import org.testng.ITestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability of a {@link JschSshConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ssh.JschSshConnectionLiveTest")
public class JschSshConnectionLiveTest {
   protected static final String sshHost = System.getProperty("jclouds.test.ssh.host");
   protected static final String sshPort = System.getProperty("jclouds.test.ssh.port");
   protected static final String sshUser = System.getProperty("jclouds.test.ssh.username");
   protected static final String sshPass = System.getProperty("jclouds.test.ssh.password");

   protected SshConnection connection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection(ITestContext context) throws NumberFormatException,
            UnknownHostException {
      int port = (sshPort != null) ? Integer.parseInt(sshPort) : 22;
      InetAddress host = (sshHost != null) ? InetAddress.getByName(sshHost) : InetAddress
               .getLocalHost();
      if (sshUser == null || sshPass == null || sshUser.trim().equals("")
               || sshPass.trim().equals("")) {
         System.err.println("ssh credentials not present.  Tests will be lame");
         connection = new SshConnection() {

            public void connect() {
            }

            public void disconnect() {
            }

            public InputStream get(String path) {
               if (path.equals("/etc/passwd")) {
                  return IOUtils.toInputStream("root");
               }
               throw new RuntimeException("path " + path + " not stubbed");
            }

            public ExecResponse exec(String command) {
               if (command.equals("hostname")) {
                  try {
                     return new ExecResponse(InetAddress.getLocalHost().getHostName(), "");
                  } catch (UnknownHostException e) {
                     throw new RuntimeException(e);
                  }
               }
               throw new RuntimeException("command " + command + " not stubbed");
            }

         };
      } else {
         Injector i = Guice.createInjector(new JschSshConnectionModule());
         SshConnection.Factory factory = i.getInstance(SshConnection.Factory.class);
         connection = factory.create(host, port, sshUser, sshPass);
         connection.connect();
      }
   }

   public void testGetEtcPassword() throws IOException {
      InputStream input = connection.get("/etc/passwd");
      String contents = Utils.toStringAndClose(input);
      assert contents.indexOf("root") >= 0 : "no root in " + contents;
   }

   public void testExecHostname() throws IOException {
      ExecResponse response = connection.exec("hostname");
      assertEquals(response.getError(), "");
      assertEquals(response.getOutput().trim(), InetAddress.getLocalHost().getHostName());
   }

}