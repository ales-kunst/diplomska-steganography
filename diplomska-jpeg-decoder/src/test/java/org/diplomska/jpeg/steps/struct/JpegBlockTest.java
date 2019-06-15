package org.diplomska.jpeg.steps.struct;

import org.diplomska.util.SizeOf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/12/01
 * @author         Ales Kunst
 */
public class JpegBlockTest {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Test
   public void testMemoryConsumption() throws Exception {
      SizeOf.Block block = new SizeOf.Block() {

         @Override
         public Object instance() {
            return new JpegBlock(JpegBlock.BlockType.LUMA);
         }
      };
      SizeOf.Block block_bytePrim = new SizeOf.Block() {

         @Override
         public Object instance() {
            return new byte[10];
         }
      };
      SizeOf.Block block_byte = new SizeOf.Block() {

         @Override
         public Object instance() {
            return new Byte((byte) 0);
         }
      };
      SizeOf.MemoryConsuptionInfo memInfo = SizeOf.memoryConsumption(block_bytePrim);
      long                        cs0     = memInfo.classSize;

      memInfo = SizeOf.memoryConsumption(block_byte);

      long cs1 = memInfo.classSize;
   }
}
