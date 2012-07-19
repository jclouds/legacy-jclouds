package org.jclouds.overthere;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.events.StatementOnNodeCompletion;
import org.jclouds.compute.events.StatementOnNodeFailure;
import org.jclouds.compute.events.StatementOnNodeSubmission;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.EventBus;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.OperatingSystemFamily;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.cifs.CifsConnectionBuilder;
import com.xebialabs.overthere.cifs.CifsConnectionType;
import com.xebialabs.overthere.util.CapturingOverthereProcessOutputHandler;

/**
 * Implementation of RunScriptOnNode that uses the Overthere remote access library.
 *
 * This is currently biased towards Windows but could theoretically also be
 * used for Linux sessions too via ssh.
 *
 * @author Richard Downer, Aled Sage
 */
public class OverthereRunner implements RunScriptOnNode {
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final EventBus eventBus;
   private final OverthereConnectionFactory connectionFactory;
   private final NodeMetadata node;
   private final Statement statement;
   private final RunScriptOptions options;
   private OverthereConnection connection;

   @AssistedInject
   public OverthereRunner(EventBus eventBus, OverthereConnectionFactory connectionFactory,
         @Assisted NodeMetadata node, @Assisted Statement statement, @Assisted RunScriptOptions options) {
      this.eventBus = checkNotNull(eventBus, "eventBus");
      this.connectionFactory = connectionFactory;
      this.node = checkNotNull(node, "node");
      this.statement = checkNotNull(statement, "statement");
      this.options = checkNotNull(options, "options");
   }

   @Override
   public Statement getStatement() {
      return statement;
   }

   @Override
   public NodeMetadata getNode() {
      return node;
   }

   @Override
   public RunScriptOnNode init() {
      // TODO Do what in init?
      return this;
   }

   @Override
   public ExecResponse call() {
      // TODO Make port etc configurable
      String user = (options.getLoginUser() != null) ? options.getLoginUser() : node.getCredentials().getUser();
      String password = (options.getLoginPassword() != null) ? options.getLoginPassword() : node.getCredentials().getPassword();
      String hostAddress = getHostAddress();
      
      ConnectionOptions overthereOptions = new ConnectionOptions();
      overthereOptions.set(ConnectionOptions.USERNAME, user);
      overthereOptions.set(ConnectionOptions.PASSWORD, password);
      overthereOptions.set(ConnectionOptions.ADDRESS, hostAddress);
      overthereOptions.set(ConnectionOptions.PORT, 5986);
      overthereOptions.set(ConnectionOptions.CONNECTION_TIMEOUT_MILLIS, 60000);
      
      switch (node.getOperatingSystem().getFamily()) {
      case WINDOWS :
         overthereOptions.set(ConnectionOptions.OPERATING_SYSTEM, OperatingSystemFamily.WINDOWS);
         overthereOptions.set(CifsConnectionBuilder.CONNECTION_TYPE, CifsConnectionType.WINRM_HTTPS);
         break;
      case UNRECOGNIZED:
      case LINUX:
         // TODO Doesn't work on linux
         overthereOptions.set(ConnectionOptions.OPERATING_SYSTEM, OperatingSystemFamily.UNIX);
         overthereOptions.set(CifsConnectionBuilder.CONNECTION_TYPE, CifsConnectionType.TELNET);
         break;
      default:
         throw new UnsupportedOperationException("Unhandled OS family "+node.getOperatingSystem().getFamily());
      }
      
      connection = connectionFactory.getConnection("cifs", overthereOptions);
      
      ExecResponse returnVal;
      try {
         eventBus.post(new StatementOnNodeSubmission(statement, node));

         // FIXME: Overthere wants individual arguments, not a full string.
         // This causes all commands that take arguments to fail (e.g. `dir` is ok, but `echo hello` is not)
         String command = statement.render(OsFamily.WINDOWS);
         
         logger.debug(">> running [%s] as %s@%s", command.replace(node.getCredentials().getPassword() != null ? node
                  .getCredentials().getPassword() : "XXXXX", "XXXXX"), user, hostAddress);

         CapturingOverthereProcessOutputHandler outputHandler = CapturingOverthereProcessOutputHandler.capturingHandler();
         int exitCode = connection.execute(outputHandler, CmdLine.build(command));
         returnVal = new ExecResponse(outputHandler.getOutput(), outputHandler.getError(), exitCode);
      } catch (Throwable e) {
         eventBus.post(new StatementOnNodeFailure(statement, node, e));
         throw Throwables.propagate(e);
      } finally {
         connection.close();
      }

      eventBus.post(new StatementOnNodeCompletion(statement, node, returnVal));
      if (logger.isTraceEnabled())
         logger.trace("<< %s[%s]", statement, returnVal);
      else
         logger.debug("<< %s(%d)", statement, returnVal.getExitStatus());
      return returnVal;
   }

   private String getHostAddress() {
      // FIXME Figure out what's reachable
      Set<String> pubs = node.getPublicAddresses();
      return (pubs.size() > 0) ? Iterables.getFirst(pubs, null) : null;
   }
}
