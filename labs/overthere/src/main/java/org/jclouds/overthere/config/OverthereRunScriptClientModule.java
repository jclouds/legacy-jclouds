package org.jclouds.overthere.config;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.overthere.OverthereRunner;
import org.jclouds.overthere.OverthereRunnerFactoryImpl;
import org.jclouds.ssh.ConfiguresSshClient;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * Module for the Overthere library for remote access to hosts.
 *
 * This is currently biased towards Windows but could theoretically also be
 * used for Linux sessions too via RunScriptOnNode.
 *
 * @author Aled Sage
 */
@ConfiguresSshClient
public class OverthereRunScriptClientModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(RunScriptOnNode.Factory.class).to(OverthereRunnerFactoryImpl.class).in(Scopes.SINGLETON);
      
      install(new FactoryModuleBuilder()
               .implement(RunScriptOnNode.class, Names.named("direct"), OverthereRunner.class)
               .build(OverthereRunnerFactoryImpl.Factory.class));
   }
}
