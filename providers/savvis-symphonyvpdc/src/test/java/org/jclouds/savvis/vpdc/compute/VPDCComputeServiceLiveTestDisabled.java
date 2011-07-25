package org.jclouds.savvis.vpdc.compute;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Takes a long time to list nodes. Average response time is about 10-15 seconds per vm.
 * Hence this test does not complete and is disabled until performance improves.
 * @author Kedar Dave
 *
 */
@Test(enabled = true, groups = "live")
public class VPDCComputeServiceLiveTestDisabled extends BaseComputeServiceLiveTest {

	public VPDCComputeServiceLiveTestDisabled(){
		provider = "savvis-symphonyvpdc";
	}
	
	@Override
	   public void setServiceDefaults() {
	      group = "savvis-symphonyvpdc";
	   }
	
	@Override
	protected Properties setupProperties() {
		Properties overrides = super.setupProperties();
		// savvis uses untrusted certificates
		overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
		overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
		return overrides;
	}

	@Override
	protected Module getSshModule() {
		return new SshjSshClientModule();
	}

}
