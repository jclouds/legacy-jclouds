package org.jclouds.virtualbox.domain;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

@Test(groups = "unit")
public class InstallGuestAdditionsTest {
  @Test
  public void testUnix() throws IOException {
	  InstallGuestAdditions statement = new InstallGuestAdditions("4.1.6");
	  assertEquals(statement.render(OsFamily.UNIX), CharStreams.toString(Resources.newReaderSupplier(Resources
           .getResource("test_install_guest_additions." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
  }
}