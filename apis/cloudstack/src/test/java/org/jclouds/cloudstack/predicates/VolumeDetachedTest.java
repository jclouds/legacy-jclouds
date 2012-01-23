package com.gravitant.cloud.adapters.provision.util;

/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * junit test class for the volumeDetached predicate.
 * Provides test cases to check if the volume is detached after the detach 
 * command is issued to the volume and after it waits on the volumeDetached 
 * predicate
 * 
 * @author Karthik Arunachalam

 * How to use this junit test class?
 * - Create an AWS EC2 instance (directly by logging on to your AWS account of by other means at your disposal)
 * - Create a volume and attach that volume to this instance
 * 		- Note down the volume id. (Example: vol-0d8d8462)
 * 		- Note down the region in which this volume is present (Example: us-east-1)
 * 	- Fill in the attributes that needs to be customized for testing below.
 *  	These include your AWS access credentials, the region in which your volume is present and the id of the volume.
 *  - Now run the junit test case.
 *  	- If the volume is detached the the test case should pass
 *  	- If the volume is not detached the test case should fail
 * 
 */

public class VolumeDetachedTest {

	private AWSEC2Client client;
	private ComputeServiceContext computeServicecontext;
	RetryablePredicate<Attachment> detachmentPredicate;

	/*Attributes that needs to be customized for testing*/
	String accessKeyId = "<AWS access key id>"; 							
	String secretAccessKey = "<AWS secret access key>"; 	
	String volumeRegion = "<volume region>"; //Example: us-east-1			
	String volumeId = "<volume id>"; //Example: vol-81f0f9ec							
		
	{ initiatePredicate(); }

	/**
	 * Get the AWS EC2 client
	 * 
	 * @return
	 * 	AWSEC2Client - the client for AWS using which we could perform various tasks on AWS EC2
	 */
	private AWSEC2Client getClient(){
		Properties overrides = new Properties();
		overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "state=available;image-type=machine;owner-alias=amazon;");
		computeServicecontext = new ComputeServiceContextFactory()
		.createContext("aws-ec2", accessKeyId, secretAccessKey, ImmutableSet.<Module> of(new SshjSshClientModule()), overrides);
		RestContext<AWSEC2Client, AWSEC2AsyncClient> awsContext = computeServicecontext.getProviderSpecificContext();
		client = awsContext.getApi();
		return client;
	}

	/**
	 * Get the attachment for a given volumeid in a given region
	 * 
	 * @param region
	 * 	String - region in which the volume is present
	 * 
	 * @param volumeId
	 * 	String - id of the volume
	 * 
	 * @return
	 * 	Attachment - the attachment corresponding to this volume
	 */
	private Attachment getAttachment(String region, String volumeId) {
		Attachment rattachment = null;
		Set<Volume> volumes = client.getElasticBlockStoreServices().describeVolumesInRegion(region, (String[])null);
		for(Volume volume : volumes) {
			Set<Attachment> attachments = volume.getAttachments();
			for(Attachment attachment : attachments) {
				if(attachment.getVolumeId().equals(volumeId)) {
					rattachment = attachment;
					break;
				}
			}
		}
		return rattachment;
	}

	/**
	 * Method to initiate the retryable predicate to check if the volume is detached. 
	 */
	public void initiatePredicate() {
		long maxTimeToWaitToDetach = 300;
		long waitTimeBetweenChecks = 20;
		detachmentPredicate = 
			new RetryablePredicate<Attachment>(new VolumeDetached(getClient().getElasticBlockStoreServices()), 
					maxTimeToWaitToDetach, waitTimeBetweenChecks, TimeUnit.SECONDS);
	}

	/**
	 * Test case to see if the volume gets detached as expected or not
	 */
	@Test
	public void testVolumeDetached() {	

		//Get the current attachment for the volume
		Attachment attachment = getAttachment(volumeRegion, volumeId);
		Assert.assertNotNull(attachment);		
		Assert.assertTrue(attachment.getStatus().toString().toLowerCase().equals("attached"));

		//Issue the detach command for the volume
		client.getElasticBlockStoreServices().detachVolumeInRegion(volumeRegion, volumeId, false);

		//preciate blocks (until time out value) until detachment
		boolean volumeDetached = detachmentPredicate.apply(attachment);
		
		//Get the attachment after the predicate is done
		attachment = getAttachment(volumeRegion, volumeId);

		//Assert the test conditions
		if(volumeDetached) {
			//If volume is detached, then the attachment for that volume should be null
			Assert.assertNull(attachment);
		}
		else {
			//If the volume is not detached, then the attachment for that volume should not be null
			Assert.assertNotNull(attachment);
			//We can't assert anything with certainty about the status of the attachment because it 
			//could be in any state (ATTACHED, DETACHING etc.)
		}
	}
}