package org.jclouds.overthere;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Factory for creating OverthereRunner.
 * 
 * Uses same design pattern as RunScriptOnNodeFactoryImpl and RunScriptOnNodeUsingSsh
 * 
 * @author aled
 */
public class OverthereRunnerFactoryImpl implements RunScriptOnNode.Factory {

   public static interface Factory {
      // TODO Do we want the "blocking" and "nonblocking" factory methods
      @Named("direct")
      OverthereRunner exec(NodeMetadata node, Statement script, RunScriptOptions options);
   }
   
   private final Factory factory;

   @Inject
   OverthereRunnerFactoryImpl(Factory factory) {
      this.factory = checkNotNull(factory, "factory");
   }

   @Override
   public RunScriptOnNode create(NodeMetadata node, Statement runScript, RunScriptOptions options) {
      // TODO Support non-blocking etc
      checkNotNull(node, "node");
      checkNotNull(runScript, "runScript");
      checkNotNull(options, "options");
      checkArgument(!options.shouldWrapInInitScript(), "wrapping in init script unsupported");
      checkArgument(options.shouldBlockOnComplete(), "not blocking on complete is unsupported");
      return factory.exec(node, runScript, options);
   }

   @Override
   public ListenableFuture<ExecResponse> submit(NodeMetadata node, Statement script, RunScriptOptions options) {
      throw new UnsupportedOperationException(); // TODO
   }
}
