package org.jclouds.imagemaker.internal;

import java.util.List;
import java.util.Set;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.imagemaker.PackageProcessor;

import com.google.common.collect.ImmutableSet;

public class PipInstaller implements PackageProcessor {

   @Override
   public Type type() {
      return Type.INSTALLER;
   }

   @Override
   public String name() {
      return "pip";
   }

   @Override
   public Set<OsFamily> compatibleOSs() {
      return ImmutableSet.of(OsFamily.AMZN_LINUX, OsFamily.CENTOS, OsFamily.DEBIAN, OsFamily.FEDORA, OsFamily.LINUX,
               OsFamily.UBUNTU);
   }

   @Override
   public ExecResponse process(NodeMetadata node, List<String> packages) {
      // TODO Auto-generated method stub
      return null;
   }

}
