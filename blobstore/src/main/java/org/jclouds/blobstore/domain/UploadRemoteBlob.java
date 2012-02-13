package org.jclouds.blobstore.domain;

import org.jclouds.blobstore.BlobStoreContext;

/**
 * Prepares and uploads a file to a BlobStore.
 * 
 * @author dralves
 * 
 */
public class UploadRemoteBlob {

  private String remoteDirectory;
  private String container;
  private BlobStoreContext ctx;
  private String fileName;

  public UploadRemoteBlob(BlobStoreContext ctx, String container,
      String remoteDirectory, String fileName) {
    this.ctx = ctx;
    this.container = container;
    this.remoteDirectory = remoteDirectory;
    this.fileName = fileName;

  }

  public Blob buildBlob() {
    Blob blob = ctx.getAsyncBlobStore().blobBuilder(this.fileName).forSigning()
        .contentLength(0).contentType(container).build();
    return blob;
  }

}
