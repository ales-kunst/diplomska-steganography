package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//~--- classes ----------------------------------------------------------------

/**
 * Test class for bit buffer.
 *
 *
 * @version        1.0, 15/11/26
 * @author         Ales Kunst
 */
public class BitBufferTest {

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
    */
   @Test
   public void testPutByte() {
      BitBuffer bitBufferMsb = new BitBuffer(BitNumbering.MSB_FIRST);
      BitBuffer bitBufferLsb = new BitBuffer(BitNumbering.LSB_FIRST);

      bitBufferMsb.put(0).put(0).put(0).put(1).put(1).put(0).put(0).put(1);

      // 00011001
      Assert.assertArrayEquals(new byte[] { 25 }, bitBufferMsb.toByteArray());
      bitBufferMsb.put(1);

      //
      Assert.assertArrayEquals(new byte[] { 25, -128 }, bitBufferMsb.toByteArray());
      bitBufferLsb.put(0).put(0).put(0).put(1).put(1).put(0).put(0).put(1);

      // 10011000
      Assert.assertArrayEquals(new byte[] { -104 }, bitBufferLsb.toByteArray());
      bitBufferLsb.put(1);

      // 10011000 00000001
      Assert.assertArrayEquals(new byte[] { -104, 1 }, bitBufferLsb.toByteArray());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testPutByteArray() {
      BitBuffer bitBufferMsb = new BitBuffer(BitNumbering.MSB_FIRST);
      BitBuffer bitBufferLsb = new BitBuffer(BitNumbering.LSB_FIRST);

      bitBufferMsb.put(new int[] { 0, 0, 0, 1, 1, 0, 0, 1 });
      Assert.assertArrayEquals(new byte[] { 25 }, bitBufferMsb.toByteArray());
      bitBufferMsb.put(new int[] { 1 });
      Assert.assertArrayEquals(new byte[] { 25, -128 }, bitBufferMsb.toByteArray());
      bitBufferLsb.put(new int[] { 0, 0, 0, 1, 1, 0, 0, 1 });
      Assert.assertArrayEquals(new byte[] { -104 }, bitBufferLsb.toByteArray());
      bitBufferLsb.put(new int[] { 1 });

      // 10011000 00000001
      Assert.assertArrayEquals(new byte[] { -104, 1 }, bitBufferLsb.toByteArray());
   }

   /**
    * Method description
    * @throws IOException
    *
    */
   @Test
   public void testToByteArray() throws IOException {
      checkToByteArrayMsb();
      checkToByteArrayLsb();
   }

   /**
    * Method for checking of toByteArray for lsb first bit numbering.
    * @throws IOException
    *
    */
   private void checkToByteArrayLsb() throws IOException {
      BitBuffer             bitBufferLsb = new BitBuffer(BitNumbering.LSB_FIRST);
      byte[]                bits_39131   = new byte[] { 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1 };
      ByteArrayOutputStream longValue    = new ByteArrayOutputStream();

      // 10011000 11011011
      bitBufferLsb.put(bits_39131);
      longValue.write(0);
      longValue.write(0);
      longValue.write(bitBufferLsb.toByteArray());

      long valueActual = ByteBuffer.wrap(longValue.toByteArray()).order(ByteOrder.BIG_ENDIAN).getInt();

      Assert.assertEquals(39131, valueActual);

      byte[] bits_38939 = new byte[] { 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0 };

      longValue = new ByteArrayOutputStream();

      // 10011000 00011011
      bitBufferLsb.reset();
      bitBufferLsb.put(bits_38939);
      longValue.write(0);
      longValue.write(0);
      longValue.write(bitBufferLsb.toByteArray());
      valueActual = ByteBuffer.wrap(longValue.toByteArray()).order(ByteOrder.BIG_ENDIAN).getInt();
      Assert.assertEquals(38939, valueActual);
   }

   /**
    * Method for checking of toByteArray for msb first bit numbering.
    *
    */
   private void checkToByteArrayMsb() {
      BitBuffer bitBufferMsb = new BitBuffer(BitNumbering.MSB_FIRST);

      // 0001100111011011
      byte[] bits_6619 = new byte[] { 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1 };

      // 0001100111011011
      bitBufferMsb.put(bits_6619);

      long valueActual = ByteBuffer.wrap(bitBufferMsb.toByteArray()).order(ByteOrder.BIG_ENDIAN).getShort();

      Assert.assertEquals(6619, valueActual);

      long valueExpected = BitUtils.convertBitsToNumber(bits_6619, BitNumbering.MSB_FIRST, Type.SHORT);

      Assert.assertEquals(valueExpected, valueActual);

      // 0001100111011
      bitBufferMsb.reset();

      byte[] bits_6616 = new byte[] { 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1 };

      bitBufferMsb.put(bits_6616);
      valueActual = ByteBuffer.wrap(bitBufferMsb.toByteArray()).order(ByteOrder.BIG_ENDIAN).getShort();
      Assert.assertEquals(6616, valueActual);
      valueExpected = BitUtils.convertBitsToNumber(bits_6616, BitNumbering.MSB_FIRST, Type.SHORT);
      Assert.assertEquals(valueExpected, valueActual);
   }
}
