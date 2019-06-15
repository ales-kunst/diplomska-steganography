package org.diplomska.util;

import org.diplomska.util.structs.Bit;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/20
 * @author         Ales Kunst
 */
public class BitUtilsTest {

   /**
    * Setup
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Teardown.
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Test method.
    *
    */
   @Test
   public void testClearBitInByte() {
      byte value_01010011    = 0x53;
      Byte changedByte_0_lsb = BitUtils.clearBitInByte(value_01010011, 0, BitNumbering.LSB_FIRST);
      Byte changedByte_4_lsb = BitUtils.clearBitInByte(value_01010011, 4, BitNumbering.LSB_FIRST);
      Byte changedByte_6_lsb = BitUtils.clearBitInByte(value_01010011, 6, BitNumbering.LSB_FIRST);
      Byte changedByte_7_lsb = BitUtils.clearBitInByte(value_01010011, 7, BitNumbering.LSB_FIRST);
      Byte changedByte_8_lsb = BitUtils.clearBitInByte(value_01010011, 8, BitNumbering.LSB_FIRST);

      Assert.assertTrue(changedByte_0_lsb.byteValue() == 82);
      Assert.assertTrue(changedByte_4_lsb.byteValue() == 67);
      Assert.assertTrue(changedByte_6_lsb.byteValue() == 19);
      Assert.assertTrue(changedByte_7_lsb.byteValue() == 83);
      Assert.assertTrue(changedByte_8_lsb == null);

      Byte changedByte_0_msb = BitUtils.clearBitInByte(value_01010011, 0, BitNumbering.MSB_FIRST);
      Byte changedByte_1_msb = BitUtils.clearBitInByte(value_01010011, 1, BitNumbering.MSB_FIRST);
      Byte changedByte_4_msb = BitUtils.clearBitInByte(value_01010011, 4, BitNumbering.MSB_FIRST);
      Byte changedByte_6_msb = BitUtils.clearBitInByte(value_01010011, 6, BitNumbering.MSB_FIRST);
      Byte changedByte_7_msb = BitUtils.clearBitInByte(value_01010011, 7, BitNumbering.MSB_FIRST);
      Byte changedByte_8_msb = BitUtils.clearBitInByte(value_01010011, 8, BitNumbering.MSB_FIRST);

      Assert.assertTrue(changedByte_0_msb.byteValue() == 83);
      Assert.assertTrue(changedByte_1_msb.byteValue() == 19);
      Assert.assertTrue(changedByte_4_msb.byteValue() == 83);
      Assert.assertTrue(changedByte_6_msb.byteValue() == 81);
      Assert.assertTrue(changedByte_7_msb.byteValue() == 82);
      Assert.assertTrue(changedByte_8_msb == null);
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testConvertBitsToNumber() {
      byte[] bits_msb_01010011 = { 0, 1, 0, 1, 0, 0, 1, 1 };
      byte[] bits_lsb_11001010 = { 1, 1, 0, 0, 1, 0, 1, 0 };
      Long   value             = BitUtils.convertBitsToNumber(bits_msb_01010011, BitNumbering.MSB_FIRST, Type.BYTE);

      // Assert.assertEquals("1010011", value.toBinaryString(value.longValue()));
      Assert.assertEquals(83, value.longValue());
      value = BitUtils.convertBitsToNumber(bits_lsb_11001010, BitNumbering.LSB_FIRST, Type.BYTE);
      Assert.assertEquals(83, value.longValue());

      byte[] bits_msb_010100111 = { 0, 1, 0, 1, 0, 0, 1, 1, 1 };

      value = BitUtils.convertBitsToNumber(bits_msb_010100111, BitNumbering.LSB_FIRST, Type.BYTE);
      Assert.assertNull(value);

      byte[] bits_11111111 = { 1, 1, 1, 1, 1, 1, 1, 1 };

      value = BitUtils.convertBitsToUnsignedNumber(bits_11111111, BitNumbering.LSB_FIRST, Type.BYTE);
      Assert.assertEquals(255, value.longValue());
      Assert.assertEquals(-1, value.byteValue());

      byte[] bits_1000000000000000 = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

      value = BitUtils.convertBitsToNumber(bits_1000000000000000, BitNumbering.MSB_FIRST, Type.SHORT);
      Assert.assertEquals(-32768, value.shortValue());

      for (int i = -32768; i < 32768; i++) {
         byte[] bits     = BitUtils.convertNumberToBits(i, BitNumbering.MSB_FIRST, Type.SHORT);
         Long   value_01 = BitUtils.convertBitsToNumber(bits, BitNumbering.MSB_FIRST, Type.SHORT);
         Long   value_02 = BitUtils.convertBitsToUnsignedNumber(bits, BitNumbering.MSB_FIRST, Type.SHORT);

         Assert.assertEquals(i, value_01.shortValue());

         if (value_01.longValue() < 0) {
            Assert.assertEquals(value_01.longValue() + 65536, value_02.longValue());
         } else {
            Assert.assertEquals(value_01.longValue(), value_02.longValue());
         }
      }
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testConvertNumberToBits() {
      byte   value_01010011 = 0x53;
      byte[] bitsLsbFirst   = BitUtils.convertNumberToBits(value_01010011, BitNumbering.LSB_FIRST, Type.BYTE);
      byte[] bitsMsbFirst   = BitUtils.convertNumberToBits(value_01010011, BitNumbering.MSB_FIRST, Type.BYTE);

      Assert.assertArrayEquals(new byte[] { 1, 1, 0, 0, 1, 0, 1, 0 }, bitsLsbFirst);
      Assert.assertArrayEquals(new byte[] { 0, 1, 0, 1, 0, 0, 1, 1 }, bitsMsbFirst);

      // short value = -32678;
      short  valueShort_01   = -32768;
      byte[] bitsMsbFirst_01 = BitUtils.convertNumberToBits(valueShort_01, BitNumbering.MSB_FIRST, Type.SHORT);

      Assert.assertArrayEquals(new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, bitsMsbFirst_01);

      short  valueShort_02   = -1;
      byte[] bitsMsbFirst_02 = BitUtils.convertNumberToBits(valueShort_02, BitNumbering.MSB_FIRST, Type.SHORT);
      long   valueLong       = (bitsMsbFirst_02[0] == 1)
                               ? -(1 << 15)
                               : 0;

      for (int i = 0; i <= 14; i++) {
         if (bitsMsbFirst_02[i] != 0) {
            long power = (long) Math.pow(2, 14 - i);

            valueLong = valueLong + power;
         }
      }

      Assert.assertEquals(valueShort_02, valueLong);

      int    valueInt_01             = Integer.MIN_VALUE;
      int    valueInt_02             = -1;
      byte[] bitsMsbFirst_03         = BitUtils.convertNumberToBits(valueInt_01, BitNumbering.MSB_FIRST, Type.INT);
      byte[] bitsMsbFirst_04         = BitUtils.convertNumberToBits(valueInt_02, BitNumbering.MSB_FIRST, Type.INT);
      byte[] expectedBitsMsbFirst_03 = new byte[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0, 0, 0, 0, 0 };
      byte[] expectedBitsMsbFirst_04 = new byte[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                                                    1, 1, 1, 1, 1, 1, 1, 1, 1 };

      Assert.assertArrayEquals(expectedBitsMsbFirst_03, bitsMsbFirst_03);
      Assert.assertArrayEquals(expectedBitsMsbFirst_04, bitsMsbFirst_04);
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testGetBit() {
      byte value_01010011 = 0x53;

      Assert.assertTrue(BitUtils.getBit(value_01010011, 0, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 1, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 2, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 3, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 4, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 5, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 6, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 7, BitNumbering.LSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 0, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 1, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 2, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 3, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 4, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 5, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ZERO);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 6, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ONE);
      Assert.assertTrue(BitUtils.getBit(value_01010011, 7, BitNumbering.MSB_FIRST, Type.BYTE) == Bit.ONE);
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testReverseBits() {
      byte[] bits_01010011 = { 0, 1, 0, 1, 0, 0, 1, 1 };

      Assert.assertArrayEquals(new byte[] { 1, 1, 0, 0, 1, 0, 1, 0 }, BitUtils.reverseBits(bits_01010011));
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testSetBitInByte() {
      byte value_01010011 = 0x53;    // 01010011

      Assert.assertTrue(BitUtils.setBitValue(value_01010011, Bit.ZERO, 0, BitNumbering.LSB_FIRST, Type.BYTE) == 82);
      Assert.assertTrue(BitUtils.setBitValue(value_01010011, Bit.ONE, 7, BitNumbering.LSB_FIRST, Type.BYTE) == 211);
      Assert.assertTrue(BitUtils.setBitValue(value_01010011, Bit.ONE, 0, BitNumbering.MSB_FIRST, Type.BYTE) == 211);
      Assert.assertTrue(BitUtils.setBitValue(value_01010011, Bit.ZERO, 7, BitNumbering.MSB_FIRST, Type.BYTE) == 82);
   }

   /**
    * Test method.
    *
    */
   @Test
   public void testSetBitValues() {
      byte   value_01010011 = 0x53;    // 01010011
      byte[] bits_101       = { 1, 0, 1 };
      Long   value_01010101 = BitUtils.setBitValues(value_01010011, bits_101, 0, BitNumbering.LSB_FIRST, Type.BYTE);

      Assert.assertEquals(85, value_01010101.intValue());

      Long value_01011011 = BitUtils.setBitValues(value_01010011, bits_101, 1, BitNumbering.LSB_FIRST, Type.BYTE);

      Assert.assertEquals(91, value_01011011.intValue());

      Long value_lsb_null = BitUtils.setBitValues(value_01010011, bits_101, 6, BitNumbering.LSB_FIRST, Type.BYTE);

      Assert.assertNull(value_lsb_null);

      Long value_10110011 = BitUtils.setBitValues(value_01010011, bits_101, 0, BitNumbering.MSB_FIRST, Type.BYTE);

      Assert.assertEquals(179, value_10110011.intValue());

      Long value_01101011 = BitUtils.setBitValues(value_01010011, bits_101, 2, BitNumbering.MSB_FIRST, Type.BYTE);

      Assert.assertEquals(107, value_01101011.intValue());

      Long value_msb_null = BitUtils.setBitValues(value_01010011, bits_101, -5, BitNumbering.MSB_FIRST, Type.BYTE);

      Assert.assertNull(value_msb_null);
   }
}
