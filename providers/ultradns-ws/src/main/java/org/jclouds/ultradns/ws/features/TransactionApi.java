/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ultradns.ws.features;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.ScopedTransaction;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.TooManyTransactionsException;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;

/**
 * Adds transaction support when performing multiple write commands.
 * 
 * <p/>
 * ex.
 * 
 * <pre>
 * String txId = ultraDNSApi.getTransactionApi().start();
 * try {
 *    // perform operations
 *    ultraDNSApi.getTransactionApi().commit(txId);
 * } catch (Throwable t) {
 *    ultraDNSApi.getTransactionApi().rollback(txId);
 *    throw propagate(t);
 * }
 * </pre>
 * 
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface TransactionApi {
   /**
    * Starts a transaction, if possible. Note that 3 simultaneous ones are
    * allowed per account, and they have a 1 hr timeout. All write commands will
    * use this transaction until delete is called.
    * 
    * @return id of the transaction created
    * @throws TooManyTransactionsException
    *            if the maximum concurrent exception limit was hit.
    * @throws IllegalStateException
    *            if another transaction is in progress.
    */
   @Named("startTransaction")
   @POST
   @XMLResponseParser(ElementTextHandler.TransactionID.class)
   @Payload("<v01:startTransaction/>")
   @Transform(ScopedTransaction.Set.class)
   String start() throws TooManyTransactionsException, IllegalStateException;

   /**
    * This request commits all of a transaction’s requests and writes them to
    * the Neustar Ultra Services
    * 
    * @param transactionID
    *           transaction id to commit.
    * @throws ResourceNotFoundException
    *            if the transaction has already been committed or never existed.
    */
   @Named("commitTransaction")
   @POST
   @Payload("<v01:commitTransaction><transactionID>{transactionID}</transactionID></v01:commitTransaction>")
   @Transform(ScopedTransaction.Remove.class)
   void commit(@PayloadParam("transactionID") String transactionID) throws ResourceNotFoundException;

   /**
    * This request rolls back any changes included in a transaction. This will
    * not error, if the transaction has timed out or does not exist.
    * 
    * @param transactionID
    *           transaction id to rollback.
    */
   @Named("rollbackTransaction")
   @POST
   @Payload("<v01:rollbackTransaction><transactionID>{transactionID}</transactionID></v01:rollbackTransaction>")
   @Fallback(VoidOnNotFoundOr404.class)
   @Transform(ScopedTransaction.Remove.class)
   void rollback(@PayloadParam("transactionID") String transactionID);
}
