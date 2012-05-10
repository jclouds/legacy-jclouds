package org.jclouds.cloudservers.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.options.ListOptions;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateWithResult;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public final class GetImageWhenStatusActivePredicateWithResult implements PredicateWithResult<Integer, Image> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CloudServersClient client;
   private final Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage;
   private org.jclouds.cloudservers.domain.Image result;
   private RuntimeException lastFailure;

   @Inject
   public GetImageWhenStatusActivePredicateWithResult(CloudServersClient client,
            Function<org.jclouds.cloudservers.domain.Image, Image> cloudserversImageToImage) {
      this.client = client;
      this.cloudserversImageToImage = cloudserversImageToImage;
   }

   @Override
   public boolean apply(Integer input) {
      result = checkNotNull(findImage(input));
      switch (result.getStatus()) {
         case ACTIVE:
            logger.info("<< Image %s is available for use.", input);
            return true;
         case QUEUED:
         case SAVING:
            logger.debug("<< Image %s is not available yet.", input);
            return false;
         default:
            lastFailure = new IllegalStateException("Image was not created: " + input);
            throw lastFailure;
      }
   }

   @Override
   public Image getResult() {
      return cloudserversImageToImage.apply(result);
   }

   @Override
   public Throwable getLastFailure() {
      return lastFailure;
   }

   private org.jclouds.cloudservers.domain.Image findImage(final int id) {
      return Iterables.tryFind(client.listImages(new ListOptions().withDetails()),
               new Predicate<org.jclouds.cloudservers.domain.Image>() {
                  @Override
                  public boolean apply(org.jclouds.cloudservers.domain.Image input) {
                     return input.getId() == id;
                  }
               }).orNull();

   }
}