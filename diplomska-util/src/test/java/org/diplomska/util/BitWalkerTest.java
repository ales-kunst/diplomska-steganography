package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/18
 * @author         Ales Kunst
 */
public class BitWalkerTest {

   /**
    * Setup.
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Tear down.
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Test getBit method.
    *
    *
    * @throws IOException
    */
   @Test
   public void testGetBit() throws IOException {
      byte[]                byteArray        = { 0x53, 0x53, 0x65 };
      ByteArrayInputStream  arrayInputStream = new ByteArrayInputStream(byteArray);
      BitWalker             bitWalker        = new BitWalker(arrayInputStream, BitNumbering.MSB_FIRST);
      ByteArrayOutputStream bits             = new ByteArrayOutputStream();

      for (int bitIndex = 0; bitIndex < 24; bitIndex++) {
         Byte bitValue = bitWalker.getBit(bitIndex);

         bits.write(bitValue);
      }

      byte[] expectedMsb = { 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1 };

      Assert.assertArrayEquals(expectedMsb, bits.toByteArray());
      arrayInputStream.reset();
      bitWalker = new BitWalker(arrayInputStream, BitNumbering.LSB_FIRST);
      bits      = new ByteArrayOutputStream();

      for (int bitIndex = 0; bitIndex < 24; bitIndex++) {
         Byte bitValue = bitWalker.getBit(bitIndex);

         bits.write(bitValue);
      }

      byte[] expectedLsb = { 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0 };

      Assert.assertArrayEquals(expectedLsb, bits.toByteArray());
   }

   /**
    * Test getBits method.
    *
    *
    * @throws IOException
    */
   @Test
   public void testGetBits() throws IOException {
      byte[]                byteArray        = { 0x53, 0x53, 0x65 };
      ByteArrayInputStream  arrayInputStream = new ByteArrayInputStream(byteArray);
      BitWalker             bitWalker        = new BitWalker(arrayInputStream, BitNumbering.MSB_FIRST);
      ByteArrayOutputStream bits             = new ByteArrayOutputStream();

      // byte[] expectedMsb = { 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1 };
      byte[] expectedMsb = { 1, 0, 0, 1, 1, 0, 1 };

      Assert.assertArrayEquals(expectedMsb, bitWalker.getBits(3, 7));
      arrayInputStream.reset();
      bitWalker = new BitWalker(arrayInputStream, BitNumbering.LSB_FIRST);
      bits      = new ByteArrayOutputStream();

      byte[] expectedLsb = { 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0 };

      Assert.assertArrayEquals(expectedLsb, bitWalker.getBits(3, 100));
   }
}
