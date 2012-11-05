package org.jclouds.overthere;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.Overthere;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereProcess;
import com.xebialabs.overthere.util.CapturingOverthereProcessOutputHandler;
import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.ssh.SshClient;

import java.io.Closeable;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of SshClient that uses the Overthere remote access library.
 *
 * This is currently biased towards Windows but could theoretically also be
 * used for Linux sessions too via ssh. This also slightly abuses the concept
 * of "SshClient" as it does not use ssh!
 * 
 * FIXME Fails when used for computeService.runScriptOnNode(...) because:
 * <ul>
 *   <li> waits for port 22 to be reachable (workaround is to change NodeMetadataBuilder.loginPort to 5986)
 *   <li> tries to copy bash script to machine, which fails
 * </ul>
 * 
 * @author Richard Downer
 * @deprecated Use OverthereRunner; TODO Is the SshClient still useful?
 */
@Deprecated
public class OverthereSshClient implements SshClient {
   private OverthereConnection connection;
   private final ConnectionOptions options;

   public OverthereSshClient(ConnectionOptions options) {
      this.options = options;
   }

   @Override
   public String getUsername() {
      return options.get(ConnectionOptions.USERNAME);
   }

   @Override
   public String getHostAddress() {
      return options.get(ConnectionOptions.ADDRESS);
   }

   @Override
   public void connect() {
      connection = Overthere.getConnection("cifs", options);
   }

   @Override
   public void disconnect() {
      connection.close();
   }

   @Override
   public void put(String path, Payload contents) {
      throw new UnsupportedOperationException("File transfers via Overthere not yet implemented");
   }

   @Override
   public void put(String path, String contents) {
      put(path, Payloads.newStringPayload(checkNotNull(contents, "contents")));
   }

   @Override
   public Payload get(String path) {
      throw new UnsupportedOperationException("File transfers via Overthere not yet implemented");
   }

   @Override
   public ExecResponse exec(String command) {
      // FIXME: Overthere wants individual arguments, not a full string. Is this a problem?
      CapturingOverthereProcessOutputHandler outputHandler = CapturingOverthereProcessOutputHandler.capturingHandler();
      int exitCode = connection.execute(outputHandler, CmdLine.build(command));
      return new ExecResponse(outputHandler.getOutput(), outputHandler.getError(), exitCode);
   }

   @Override
   public ExecChannel execChannel(String command) {

      final OverthereProcess process = connection.startProcess(CmdLine.build(command));

      final Closeable closer = new Closeable() {
         @Override
         public void close() throws IOException {
            process.destroy();
         }
      };

      final Supplier<Integer> exitStatus = new Supplier<Integer>() {
         @Override
         public Integer get() {
            try {
               int exitCode = process.waitFor();
               closer.close();
               return exitCode;
            } catch (Exception e) {
               throw Throwables.propagate(e);
            }
         }
      };

      return new ExecChannel(process.getStdin(), process.getStdout(), process.getStderr(), exitStatus, closer);
   }

}
