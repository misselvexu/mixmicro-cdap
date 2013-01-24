package com.continuuity.data.operation.executor.omid;

import com.continuuity.data.hbase.HBaseTestBase;
import com.continuuity.data.operation.executor.OperationExecutor;
import com.continuuity.data.runtime.DataFabricDistributedModule;
import com.continuuity.data.table.OVCTableHandle;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestHBaseOmidExecutorLikeAFlow extends TestOmidExecutorLikeAFlow {

  private static Injector injector;

  private static OmidTransactionalOperationExecutor executor;

  private static OVCTableHandle handle;

  @BeforeClass
  public static void startEmbeddedHBase() {
    try {
      HBaseTestBase.startHBase();
      injector = Guice.createInjector(
          new DataFabricDistributedModule(HBaseTestBase.getConfiguration()));
      executor = (OmidTransactionalOperationExecutor)injector.getInstance(
          Key.get(OperationExecutor.class,
              Names.named("DataFabricOperationExecutor")));
      handle = executor.getTableHandle();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterClass
  public static void stopEmbeddedHBase() {
    try {
      HBaseTestBase.stopHBase();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected OmidTransactionalOperationExecutor getOmidExecutor() {
    return executor;
  }

  @Override
  protected OVCTableHandle getTableHandle() {
    return handle;
  }

  @Override
  protected int getNumIterations() {
    return 100;
  }

  // Test Overrides
  /**
   * Currently not working.  Will be fixed in ???-???.
   */
  @Test @Override @Ignore
  public void testThreadedProducersAndThreadedConsumers() throws Exception {}

  /**
   * Currently not working.  Will be fixed in ENG-421.
   */
  @Test @Override @Ignore
  public void testUserReadOwnWritesAndWritesStableSorted() throws Exception {}

  /**
   * Currently not working.  Will be fixed in ENG-421.
   */
  @Test @Override @Ignore
  public void testWriteBatchWithMultiWritesMultiEnqueuesPlusSuccessfulAck() {}
}
