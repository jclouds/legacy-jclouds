package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.util.Collections;

import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

public class InstallGuestAdditions implements Statement {
	
   private final String vboxVersion;
   private final String mountPoint;
   
   public InstallGuestAdditions(String vboxVersion) {
      this(vboxVersion, "/mnt");
   }   

   public InstallGuestAdditions(String vboxVersion, String mountPoint) {
      this.vboxVersion = checkNotNull(vboxVersion, "vboxVersion");
      this.mountPoint = checkNotNull(mountPoint, "mountPoint");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return Collections.emptyList();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      
      String vboxGuestAdditionsIso = "VBoxGuestAdditions_" + vboxVersion + ".iso";
      ScriptBuilder scriptBuilder = new ScriptBuilder()
      .addStatement(exec("{cd} {fs}tmp"))
      .addStatement(exec("wget http://download.virtualbox.org/virtualbox/" + vboxVersion + "/" + vboxGuestAdditionsIso))
      .addStatement(exec(String.format("mount -o loop %s %s", vboxGuestAdditionsIso, mountPoint)))
      .addStatement(call("installGuestAdditions"))
      .addStatement(exec(String.format("sh %s%s", mountPoint, "/VBoxLinuxAdditions.run")))
      .addStatement(exec(String.format("umount %s", mountPoint)));
      return scriptBuilder.render(family);
   }

}
