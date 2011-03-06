package org.jclouds.netty.config;

import org.jclouds.io.PayloadSlicer;
import org.jclouds.netty.io.NettyPayloadSlicer;

import com.google.inject.AbstractModule;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class NettyPayloadModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(PayloadSlicer.class).to(NettyPayloadSlicer.class);
   }

}
