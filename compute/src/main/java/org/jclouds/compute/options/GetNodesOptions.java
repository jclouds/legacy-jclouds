package org.jclouds.compute.options;


/**
 * @author Ivan Meredith
 */
public class GetNodesOptions implements Cloneable {
   public static final ImmutableGetNodesOptions NONE = new ImmutableGetNodesOptions(
            new GetNodesOptions());

   private boolean detailed;

   public GetNodesOptions() {
   }

   GetNodesOptions(boolean detailed) {
      this.detailed = detailed;
   }

    public static class ImmutableGetNodesOptions extends GetNodesOptions {
      private final GetNodesOptions delegate;



      public ImmutableGetNodesOptions(GetNodesOptions delegate) {
         this.delegate = delegate;
      }

      @Override
      public boolean isDetailed() {
         return delegate.isDetailed();
      }


      @Override
      public GetNodesOptions clone() {
         return delegate.clone();
      }

      @Override
      public String toString() {
         return delegate.toString();
      }

   }

   public boolean isDetailed() {
      return detailed;
   }
   /**
    * populate each result with detailed such as metadata even if it incurs extra requests to the
    * service.
    */
   public GetNodesOptions withDetails() {
      this.detailed = true;
      return this;
   }

   public static class Builder {


      /**
       * @see GetNodesOptions#withDetails()
       */
      public static GetNodesOptions withDetails() {
         GetNodesOptions options = new GetNodesOptions();
         return options.withDetails();
      }
   }

   @Override
   public GetNodesOptions clone() {
      return new GetNodesOptions(detailed);
   }

   @Override
   public String toString() {
      return "[detailed=" + detailed + "]";
   }
}
