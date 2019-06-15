package org.diplomska.util;

/**
 * Class description
 *
 *
 * @version        1.0, 15/12/01
 * @author         Ales Kunst
 */
public class SizeOf {

   /** Field description */
   private static final Runtime s_runtime = Runtime.getRuntime();

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param block
    *
    * @return
    *
    * @throws Exception
    */
   public static MemoryConsuptionInfo memoryConsumption(Block block) throws Exception {
      MemoryConsuptionInfo returnMemoryInfo = new MemoryConsuptionInfo();

      runGC();
      usedMemory();

      // Array to keep strong references to allocated objects
      final int count   = 100000;
      Object[]  objects = new Object[count];

      // Allocate count+1 objects, discard the first one
      for (int i = -1; i < count; ++i) {
         Object object = null;

         // Instantiate your data here and assign it to object
         object = block.instance();

         if (i >= 0) {
            objects[i] = object;
         } else {
            object = null;                                     // Discard the warm up object
            runGC();
            returnMemoryInfo.heapSizeBefore = usedMemory();    // Take a before heap snapshot
         }
      }

      runGC();
      returnMemoryInfo.heapSizeAfter = usedMemory();    // Take an after heap snapshot:
      returnMemoryInfo.heapDelta     = returnMemoryInfo.heapSizeAfter - returnMemoryInfo.heapSizeBefore;
      returnMemoryInfo.classSize     = Math.round((float) returnMemoryInfo.heapDelta / count);
      returnMemoryInfo.clazz         = objects[0].getClass();

      for (int i = 0; i < count; ++i) {
         objects[i] = null;
      }

      objects = null;

      return returnMemoryInfo;
   }

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   public static void runGC() throws Exception {

      // It helps to call Runtime.gc()
      // using several method calls:
      for (int r = 0; r < 4; ++r) {
         _runGC();
      }
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public static long usedMemory() {
      return s_runtime.totalMemory() - s_runtime.freeMemory();
   }

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   private static void _runGC() throws Exception {
      long usedMem1 = usedMemory(),
           usedMem2 = Long.MAX_VALUE;

      for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
         s_runtime.runFinalization();
         s_runtime.gc();
         Thread.currentThread().yield();
         usedMem2 = usedMem1;
         usedMem1 = usedMemory();
      }
   }

   //~--- inner interfaces ----------------------------------------------------

   /**
    * Block interface
    *
    *
    *
    * @version        1.0, 15/12/01
    * @author         Ales Kunst
    */
   public static interface Block {

      /**
       * Creates instance
       *
       *
       * @return
       */
      Object instance();
   }


   //~--- inner classes -------------------------------------------------------

   /**
    * Class description
    *
    *
    * @version        1.0, 15/12/01
    * @author         Ales Kunst
    */
   public static class MemoryConsuptionInfo {

      /** Field description */
      public long heapSizeBefore = 0;

      /** Field description */
      public long heapSizeAfter = 0;

      /** Field description */
      public long heapDelta = 0;

      /** Field description */
      public long classSize = 0;

      /** Field description */
      Class<?> clazz = null;
   }

}    // End of class

