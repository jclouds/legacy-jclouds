package org.jclouds.nodepool;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.StubComputeServiceIntegrationTest;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import com.google.inject.Module;

@Test(groups = "live", testName = "NodePoolStubTest", enabled = false)
public class NodePoolStubIntegrationTest extends StubComputeServiceIntegrationTest {

   public NodePoolStubIntegrationTest() {
      provider = "nodepool";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put("jclouds.identity", "foo");
      props.put("jclouds.nodepool.backend-provider", "stub");
      props.put("jclouds.nodepool.basedir", "target/test-data");
      props.put("jclouds.nodepool.node-user", "defaultAdminUsername");
      props.put("jclouds.nodepool.node-password", "randompassword");
      props.put("jclouds.nodepool.backend-modules",
               Joiner.on(",").join(getSshModule().getClass().getName(), SLF4JLoggingModule.class.getName()));
      return props;
   }

   @Override
   protected Module getSshModule() {
      return new NodePoolStubSshModule();
   }

   public static class NodePoolStubSshModule extends StubSshModule {
      @Override
      protected void configure() {
         bind(AdminAccess.Configuration.class).toInstance(new Configuration() {

            @Override
            public Supplier<String> defaultAdminUsername() {
               return Suppliers.ofInstance("defaultAdminUsername");
            }

            @Override
            public Supplier<Map<String, String>> defaultAdminSshKeys() {
               return Suppliers.<Map<String, String>> ofInstance(ImmutableMap.of("public", "publicKey", "private",
                        Pems.PRIVATE_PKCS1_MARKER));
            }

            @Override
            public Function<String, String> cryptFunction() {
               return new Function<String, String>() {

                  @Override
                  public String apply(String input) {
                     return String.format("crypt(%s)", input);
                  }

               };
            }

            public Supplier<String> passwordGenerator() {
               return Suppliers.ofInstance("randompassword");
            }
         });

         SshClient.Factory factory = createMock(SshClient.Factory.class);
         SshClient client = createMock(SshClient.class);
         expect(
                  factory.create(HostAndPort.fromParts("0.0.0.0", 22), LoginCredentials.builder().user("root")
                           .password("password1").build())).andReturn(client).anyTimes();
         client.connect();
         client.put((String) notNull(), (String) notNull());
         expect(client.exec((String) notNull())).andReturn(EXEC_GOOD).anyTimes();
         expect(client.getUsername()).andReturn("defaultAdminUsername").anyTimes();
         expect(client.getHostAddress()).andReturn("0.0.0.0").anyTimes();
         client.disconnect();
         expectLastCall().anyTimes();
         replay(factory);
         replay(client);
         bind(SshClient.Factory.class).toInstance(factory);

         bind(OpenSocketFinder.class).toInstance(new OpenSocketFinder() {
            @Override
            public HostAndPort findOpenSocketOnNode(NodeMetadata node, int port, long timeoutValue, TimeUnit timeUnits) {
               return HostAndPort.fromParts("0.0.0.0", port);
            }
         });
      }
   }

}
