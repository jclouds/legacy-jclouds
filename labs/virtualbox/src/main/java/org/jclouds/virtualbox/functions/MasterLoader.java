package org.jclouds.virtualbox.functions;

import org.jclouds.virtualbox.domain.MasterSpec;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Function;

public class MasterLoader implements Function<MasterSpec, IMachine> {

  @Override
  public IMachine apply(MasterSpec input) {
    throw new UnsupportedOperationException();
  }

}
