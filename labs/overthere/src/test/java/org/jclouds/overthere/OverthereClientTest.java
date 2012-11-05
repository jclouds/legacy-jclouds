package org.jclouds.overthere;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.overthere.config.OverthereRunScriptClientModule;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.OperatingSystemFamily;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereProcessOutputHandler;

@Test
public class OverthereClientTest {

   private static final Logger logger = Logger.CONSOLE;
   
   private RunScriptOnNode.Factory factory;
   private OverthereConnectionFactory connectionFactory;
   
   @BeforeMethod
   public void setUp() {
      connectionFactory = createMock(OverthereConnectionFactory.class);
      
      Module connectionFactoryModule = new Module() {  
         @Override public void configure(Binder binder) {  
             binder.bind(OverthereConnectionFactory.class).toInstance(connectionFactory);  
         }  
      }; 
      Injector injector = Guice.createInjector(new OverthereRunScriptClientModule(), connectionFactoryModule);
      
      factory = injector.getInstance(RunScriptOnNode.Factory.class);
   }

   /**
    * Uses a mock Overthere to check that all args are being passed in correctly, when invoking the command.
    * 
    * Note: I'd have liked to split this up into multiple test methods (e.g. one checks the existStatus/out/err; 
    * another checks the overthere-options; another checks that command was passed in correctly). But the amount 
    * of duplication between tests is off-putting. And it's not as simple as just putting it into setUp, because
    * the different test methods would require the mocks to behave in slightly different ways (e.g. one just returns
    * an exit-status, while another records the args).
    */
   @Test
   public void testOverthere() throws Exception {
      String hostname = "myhost";
      String username = "myusername";
      String password = "mypasswd";
      final String cmd = "mycmd";
      final String cmdOut = "myout";
      final String cmdErr = "myerr";
      final int cmdExitStatus = 123;
      final List<CmdLine> cmdLines = new CopyOnWriteArrayList<CmdLine>();
      final List<ConnectionOptions> connectionOptionses = new CopyOnWriteArrayList<ConnectionOptions>();
      
      // Args for executing command
      LoginCredentials credentials = LoginCredentials.builder().identity(username).password(password).build();
      Statement script = Statements.exec(cmd);
      RunScriptOptions options = RunScriptOptions.Builder.overrideLoginCredentials(credentials).wrapInInitScript(false);

      // Mocks for overthere calls
      final OverthereConnection connection = createMock(OverthereConnection.class);
      expect(connectionFactory.getConnection(anyObject(String.class), anyObject(ConnectionOptions.class)))
               .andAnswer(new IAnswer<OverthereConnection>() {
                        @Override
                        public OverthereConnection answer() throws Throwable {
                           Object[] args = EasyMock.getCurrentArguments();
                           assertEquals((String)args[0], "cifs");
                           connectionOptionses.add((ConnectionOptions)args[1]);
                           return connection;
                        }});
      
      expect(connection.execute(anyObject(OverthereProcessOutputHandler.class), anyObject(CmdLine.class)))
               .andAnswer(new IAnswer<Integer>() {
                        @Override
                        public Integer answer() throws Throwable {
                           Object[] args = EasyMock.getCurrentArguments();
                           ((OverthereProcessOutputHandler)args[0]).handleOutputLine(cmdOut);
                           ((OverthereProcessOutputHandler)args[0]).handleErrorLine(cmdErr);
                           cmdLines.add((CmdLine)args[1]);
                           return cmdExitStatus;
                        }});
      connection.close(); expectLastCall();
                     
      // Mock of node to execute on
      NodeMetadata node = createMock(NodeMetadata.class);
      expect(node.getPublicAddresses()).andReturn(ImmutableSet.of(hostname));
      expect(node.getOperatingSystem()).andReturn(OperatingSystem.builder().family(OsFamily.WINDOWS).description("mydescr").build());
      expect(node.getCredentials()).andReturn(credentials).anyTimes();
      
      replay(connectionFactory);
      replay(connection);
      replay(node);
      
      // Invoke the command
      RunScriptOnNode runner = factory.create(node, script, options);
      ExecResponse response = runner.call();

      // Assert returns our expected mock result
      assertEquals(response.getOutput(), cmdOut);
      assertEquals(response.getError(), cmdErr);
      assertEquals(response.getExitStatus(), cmdExitStatus);
      
      // Assert passed correct command to Overthere
      assertEquals(cmdLines.size(), 1);
      assertEquals(cmdLines.get(0).getArguments().size(), 1);
      assertEquals(cmdLines.get(0).getArguments().get(0).toString(OperatingSystemFamily.WINDOWS, false), cmd+"\r\n");
      
      // Assert used correct overthere connection options
      assertEquals(connectionOptionses.size(), 1);
      assertEquals(connectionOptionses.get(0).get(ConnectionOptions.ADDRESS), hostname);
      assertEquals(connectionOptionses.get(0).get(ConnectionOptions.USERNAME), username);
      assertEquals(connectionOptionses.get(0).get(ConnectionOptions.PASSWORD), password);
      assertEquals(connectionOptionses.get(0).get(ConnectionOptions.PORT), 5986);
   }
}
