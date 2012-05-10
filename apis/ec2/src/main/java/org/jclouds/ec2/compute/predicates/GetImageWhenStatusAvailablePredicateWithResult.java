package org.jclouds.ec2.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.collect.Iterables;

public final class GetImageWhenStatusAvailablePredicateWithResult implements PredicateWithResult<String, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final String region;
   private final EC2Client ec2Client;
   private final EC2ImageParser ec2ImageToImage;
   private org.jclouds.ec2.domain.Image result;
   private RuntimeException lastFailure;

   public GetImageWhenStatusAvailablePredicateWithResult(EC2Client ec2Client, EC2ImageParser ec2ImageToImage,
            String region) {
      this.region = region;
      this.ec2Client = ec2Client;
      this.ec2ImageToImage = ec2ImageToImage;
   }

   @Override
   public boolean apply(String input) {
      result = checkNotNull(findImage(input));
      switch (result.getImageState()) {
         case AVAILABLE:
            logger.info("<< Image %s is available for use.", input);
            return true;
         case UNRECOGNIZED:
            logger.debug("<< Image %s is not available yet.", input);
            return false;
         default:
            lastFailure = new IllegalStateException("Image was not created: " + input);
            throw lastFailure;
      }
   }

   @Override
   public Image getResult() {
      return ec2ImageToImage.apply(result);
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

   private org.jclouds.ec2.domain.Image findImage(String id) {
      return Iterables.getOnlyElement(ec2Client.getAMIServices().describeImagesInRegion(region,
               new DescribeImagesOptions().imageIds(id)));

   }
}