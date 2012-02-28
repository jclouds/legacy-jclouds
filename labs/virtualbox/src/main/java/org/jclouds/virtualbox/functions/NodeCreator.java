package org.jclouds.virtualbox.functions;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Function;

public class NodeCreator implements Function<IMachine, NodeAndInitialCredentials<IMachine>> {

  @Override
  public NodeAndInitialCredentials<IMachine> apply(IMachine input) {
    throw new UnsupportedOperationException();
  }

}
