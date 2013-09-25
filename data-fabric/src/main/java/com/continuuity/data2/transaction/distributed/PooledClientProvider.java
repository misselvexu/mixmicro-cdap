package com.continuuity.data2.transaction.distributed;

import com.continuuity.common.conf.CConfiguration;
import com.continuuity.common.conf.Constants;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an tx client provider that uses a bounded size pool of connections.
 */
public class PooledClientProvider extends AbstractClientProvider {

  private static final Logger Log =
      LoggerFactory.getLogger(PooledClientProvider.class);

  // we will use this as a pool of tx clients
  class OpexClientPool extends ElasticPool<TransactionServiceThriftClient, TException>
  {
    OpexClientPool(int sizeLimit) {
      super(sizeLimit);
    }

    @Override
    protected TransactionServiceThriftClient create() throws TException {
      return newClient();
    }

    @Override
    protected void destroy(TransactionServiceThriftClient client) {
      client.close();
    }
  }

  // we will use this as a pool of tx clients
  OpexClientPool clients;

  // the limit for the number of active clients
  int maxClients;

  public PooledClientProvider(CConfiguration conf) {
    super(conf);
  }

  @Override
  public void initialize() throws TException {
    // initialize the super class (needed for service discovery)
    super.initialize();

    // create a (empty) pool of tx clients
    maxClients = configuration.getInt(Constants.Transaction.CFG_DATA_TX_CLIENT_COUNT,
        Constants.Transaction.DEFAULT_DATA_TX_CLIENT_COUNT);
    if (maxClients < 1) {
      Log.warn("Configuration of " + Constants.Transaction.CFG_DATA_TX_CLIENT_COUNT +
          " is invalid: value is " + maxClients + " but must be at least 1. " +
          "Using 1 as a fallback. ");
      maxClients = 1;
    }
    this.clients = new OpexClientPool(maxClients);
  }

  @Override
  public TransactionServiceThriftClient getClient() throws TException {
    return clients.obtain();
  }

  @Override
  public void returnClient(TransactionServiceThriftClient client) {
    clients.release(client);
  }

  @Override
  public void discardClient(TransactionServiceThriftClient client) {
    clients.discard(client);
    client.close();
  }

  @Override
  public String toString() {
    return "Elastic pool of size " + this.maxClients;
  }
}
