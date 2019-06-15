package org.diplomska.util;

import org.diplomska.util.structs.Bit;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

//~--- classes ----------------------------------------------------------------

/**
 * This class contains utility methods for dealing with bits.
 *
 *
 * @version        1.0, 19.nov.2015
 * @author         Ales Kunst
 */
public final class BitUtils {

   /**
    * Private constructor.
    *
    */
   private BitUtils() {}

   //~--- methods -------------------------------------------------------------

   /**
    * Clear bit in byte.
    *
    *
    * @param value
    * @param bitIndex
    * @param bitNumbering
    *
    * @return
    */
   public static Byte clearBitInByte(long value, int bitIndex, BitNumbering bitNumbering) {
      Long returnByte = setBitValue(value, Bit.ZERO, bitIndex, bitNumbering, Type.BYTE);

      return (returnByte != null)
             ? returnByte.byteValue()
             : null;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static Long convertBitsToNumber(byte[] bits, BitNumbering bitNumbering, Type type) {
      return convertToNumberPrivate(bits, bitNumbering, type, false);
   }

   /**
    * Converts bits to long number.
    *
    *
    * @param bits
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static Long convertBitsToNumberOld(byte[] bits, BitNumbering bitNumbering, Type type) {
      Long returnValue = null;

      if (isBitIndexValid((bits.length - 1), type)) {
         int noOfShifts = getNoOfShifts(0, bitNumbering, type);

         returnValue = 0L;

         for (int bitValuesIndex = 0; bitValuesIndex < bits.length; bitValuesIndex++) {
            Bit bitValue = Bit.convertToBit(bits[bitValuesIndex]);

            if (bitValue == Bit.ONE) {
               returnValue |= (1L << noOfShifts);
            } else {
               returnValue &= ~(1L << noOfShifts);
            }

            if (bitNumbering == BitNumbering.MSB_FIRST) {
               noOfShifts--;
            } else if (bitNumbering == BitNumbering.LSB_FIRST) {
               noOfShifts++;
            }
         }
      }

      return returnValue;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static Long convertBitsToUnsignedNumber(byte[] bits, BitNumbering bitNumbering, Type type) {
      return convertToNumberPrivate(bits, bitNumbering, type, true);
   }

   /**
    * Convert number to type number of bits with bitnumbering direction.
    *
    *
    * @param value
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static byte[] convertNumberToBits(long value, BitNumbering bitNumbering, Type type) {
      byte[] returnBits = new byte[type.getTypeSize()];

      for (int bitIndex = 0; bitIndex < type.getTypeSize(); bitIndex++) {
         Bit bit = getBit(value, bitIndex, bitNumbering, type);

         returnBits[bitIndex] = bit.toByte();
      }

      return returnBits;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets bit from byte.
    *
    *
    * @param value
    * @param bitIndex
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static Bit getBit(long value, int bitIndex, BitNumbering bitNumbering, Type type) {
      Bit returnBit = null;

      if (isBitIndexValid(bitIndex, type)) {
         int  noOfShifts       = getNoOfShifts(bitIndex, bitNumbering, type);
         long transformedValue = 0;
         long bit              = 0;

         if (Type.BYTE == type) {
            transformedValue = (value < 0)
                               ? (value + 256)
                               : value;
         } else if (Type.SHORT == type) {
            transformedValue = (value < 0)
                               ? (value + 65536)
                               : value;
         } else if (Type.INT == type) {
            transformedValue = (value < 0)
                               ? (value + 4294967296L)
                               : value;
         } else if (Type.LONG == type) {
            transformedValue = value;
         } else {
            JpegImageUtils.throwJpegImageExeption("Not supported type!");
         }

         bit       = (transformedValue >>> noOfShifts) & 1;
         returnBit = (bit == 0)
                     ? Bit.ZERO
                     : Bit.ONE;
      }

      return returnBit;
   }

   /**
    * Return bits out of this number.
    *
    *
    * @param value
    * @param bitIndex
    * @param length
    * @param bitNumbering
    * @param type
    *
    * @return Return bits.
    */
   public static byte[] getBits(long value, int bitIndex, int length, BitNumbering bitNumbering, Type type) {
      byte[] returnBits = null;

      if (isBitIndexValid(bitIndex, type) && isBitIndexValid((bitIndex + (length - 1)), type)) {
         returnBits = new byte[length];

         int arrayBitsIndex = 0;

         if (bitNumbering == BitNumbering.MSB_FIRST) {
            for (int bitPosition = bitIndex; bitPosition < (bitIndex + length); bitPosition++) {
               returnBits[arrayBitsIndex] = getBit(value, bitPosition, bitNumbering, type).toByte();
               arrayBitsIndex++;
            }
         } else if (bitNumbering == BitNumbering.LSB_FIRST) {
            arrayBitsIndex = length - 1;

            for (int bitPosition = 0; bitPosition < (bitIndex + length); bitPosition++) {
               returnBits[arrayBitsIndex] = getBit(value, bitPosition, bitNumbering, type).toByte();
               arrayBitsIndex--;
            }
         }
      }

      return returnBits;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Reverse bits.
    *
    *
    * @param bits
    *
    * @return
    */
   public static byte[] reverseBits(byte[] bits) {
      byte[] returnReversedBits = new byte[bits.length];
      int    index              = 0;

      for (int bitPosition = (bits.length - 1); bitPosition >= 0; bitPosition--) {
         returnReversedBits[index] = bits[bitPosition];
         index++;
      }

      return returnReversedBits;
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Set bit to 1 on bitIndex position.
    *
    *
    * @param value
    * @param bitIndex
    * @param bitNumbering
    *
    * @return Returns new byte. If not succesfull returns null;
    */
   public static Byte setBitInByte(long value, int bitIndex, BitNumbering bitNumbering) {
      Long returnByte = setBitValue(value, Bit.ONE, bitIndex, bitNumbering, Type.BYTE);

      return (returnByte != null)
             ? returnByte.byteValue()
             : null;
   }

   /**
    * Set bit  on position to bit value
    *
    *
    * @param value
    * @param bitValue
    * @param bitIndex
    * @param bitNumbering
    * @param type
    *
    * @return Returns new byte. If not succesfull returns null;
    */
   public static Long setBitValue(long value, Bit bitValue, int bitIndex, BitNumbering bitNumbering, Type type) {
      Long returnByte = null;

      if (isBitIndexValid(bitIndex, type)) {
         returnByte = new Long(0);

         int noOfShifts = getNoOfShifts(bitIndex, bitNumbering, type);

         switch (bitValue) {
         case ZERO :
            returnByte = value & ~(1L << noOfShifts);

            break;

         case ONE :
            returnByte = value | (1L << noOfShifts);

            break;
         }
      }

      return returnByte;
   }

   /**
    * Injects bits from bytearray intto the value regarding bitnumbering.
    *
    *
    * @param value
    * @param bitValues
    * @param bitIndex
    * @param bitNumbering
    * @param type
    *
    * @return
    */
   public static Long setBitValues(long value, byte[] bitValues, int bitIndex, BitNumbering bitNumbering, Type type) {
      Long returnValue = null;
      int  endBitIndex = bitIndex + bitValues.length;

      // Check for validity
      if (isBitIndexValid(bitIndex, type) && isBitIndexValid(endBitIndex, type)) {
         int bitInjectionIndex = bitIndex;

         returnValue = value;

         if (bitNumbering == BitNumbering.MSB_FIRST) {
            for (int bitValuesIndex = 0; bitValuesIndex < bitValues.length; bitValuesIndex++) {
               Bit bit = Bit.convertToBit(bitValues[bitValuesIndex]);

               returnValue = setBitValue(returnValue, bit, bitInjectionIndex, bitNumbering, type);
               bitInjectionIndex++;
            }
         } else if (bitNumbering == BitNumbering.LSB_FIRST) {
            for (int bitValuesIndex = (bitValues.length - 1); bitValuesIndex >= 0; bitValuesIndex--) {
               Bit bit = Bit.convertToBit(bitValues[bitValuesIndex]);

               returnValue = setBitValue(returnValue, bit, bitInjectionIndex, bitNumbering, type);
               bitInjectionIndex++;
            }
         }
      }

      return returnValue;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get no of shifts to get bit on the bit index regarding bit numbering.
    *
    *
    * @param bitIndex Bitindex should be between 0 and (typesize - 1).
    * @param bitNumbering Bitnumbering is which bit is first
    * @param type For which type we are searching number of shifts.
    *
    * @return Returns number of shifts to get to the bit.
    */
   protected static int getNoOfShifts(int bitIndex, BitNumbering bitNumbering, Type type) {
      int returnNoOfShifts = -1;
      int maxBitIndex      = type.getTypeSize() - 1;

      if (bitNumbering == BitNumbering.MSB_FIRST) {
         returnNoOfShifts = maxBitIndex - bitIndex;
      } else if (bitNumbering == BitNumbering.LSB_FIRST) {
         returnNoOfShifts = bitIndex;
      }

      return returnNoOfShifts;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    *
    * @return
    */
   private static Byte convertBitsToByte(byte[] bits, BitNumbering bitNumbering) {
      byte returnValue = 0;

      if (isBitIndexValid((bits.length - 1), Type.BYTE)) {
         int noOfShifts = getNoOfShifts(0, bitNumbering, Type.BYTE);

         for (int bitValuesIndex = 0; bitValuesIndex < bits.length; bitValuesIndex++) {
            Bit bitValue = Bit.convertToBit(bits[bitValuesIndex]);

            if (bitValue == Bit.ONE) {
               returnValue |= (1L << noOfShifts);
            } else {
               returnValue &= ~(1L << noOfShifts);
            }

            if (bitNumbering == BitNumbering.MSB_FIRST) {
               noOfShifts--;
            } else if (bitNumbering == BitNumbering.LSB_FIRST) {
               noOfShifts++;
            }
         }
      }

      return returnValue;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    *
    * @return
    */
   private static int convertBitsToInt(byte[] bits, BitNumbering bitNumbering) {
      int returnValue = 0;

      if (isBitIndexValid((bits.length - 1), Type.INT)) {
         int noOfShifts = getNoOfShifts(0, bitNumbering, Type.INT);

         for (int bitValuesIndex = 0; bitValuesIndex < bits.length; bitValuesIndex++) {
            Bit bitValue = Bit.convertToBit(bits[bitValuesIndex]);

            if (bitValue == Bit.ONE) {
               returnValue |= (1L << noOfShifts);
            } else {
               returnValue &= ~(1L << noOfShifts);
            }

            if (bitNumbering == BitNumbering.MSB_FIRST) {
               noOfShifts--;
            } else if (bitNumbering == BitNumbering.LSB_FIRST) {
               noOfShifts++;
            }
         }
      }

      return returnValue;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    *
    * @return
    */
   private static long convertBitsToLong(byte[] bits, BitNumbering bitNumbering) {
      long returnValue = 0;

      if (isBitIndexValid((bits.length - 1), Type.LONG)) {
         int noOfShifts = getNoOfShifts(0, bitNumbering, Type.LONG);

         for (int bitValuesIndex = 0; bitValuesIndex < bits.length; bitValuesIndex++) {
            Bit bitValue = Bit.convertToBit(bits[bitValuesIndex]);

            if (bitValue == Bit.ONE) {
               returnValue |= (1L << noOfShifts);
            } else {
               returnValue &= ~(1L << noOfShifts);
            }

            if (bitNumbering == BitNumbering.MSB_FIRST) {
               noOfShifts--;
            } else if (bitNumbering == BitNumbering.LSB_FIRST) {
               noOfShifts++;
            }
         }
      }

      return returnValue;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    *
    * @return
    */
   private static short convertBitsToShort(byte[] bits, BitNumbering bitNumbering) {
      short returnValue = 0;

      if (isBitIndexValid((bits.length - 1), Type.SHORT)) {
         int noOfShifts = getNoOfShifts(0, bitNumbering, Type.SHORT);

         for (int bitValuesIndex = 0; bitValuesIndex < bits.length; bitValuesIndex++) {
            Bit bitValue = Bit.convertToBit(bits[bitValuesIndex]);

            if (bitValue == Bit.ONE) {
               returnValue |= (1L << noOfShifts);
            } else {
               returnValue &= ~(1L << noOfShifts);
            }

            if (bitNumbering == BitNumbering.MSB_FIRST) {
               noOfShifts--;
            } else if (bitNumbering == BitNumbering.LSB_FIRST) {
               noOfShifts++;
            }
         }
      }

      return returnValue;
   }

   /**
    * Method description
    *
    *
    * @param bits
    * @param bitNumbering
    * @param type
    * @param isUnsigned
    *
    * @return
    */
   private static Long convertToNumberPrivate(byte[] bits, BitNumbering bitNumbering, Type type, boolean isUnsigned) {
      Long returnValue = null;

      if (isBitIndexValid((bits.length - 1), type)) {
         if (Type.BYTE == type) {
            long tempValue = convertBitsToByte(bits, bitNumbering);

            if ((tempValue < 0) && isUnsigned) {
               tempValue += 256;
            }

            returnValue = new Long(tempValue);
         } else if (Type.SHORT == type) {
            long tempValue = convertBitsToShort(bits, bitNumbering);

            if ((tempValue < 0) && isUnsigned) {
               tempValue += 65536;
            }

            returnValue = new Long(tempValue);
         } else if (Type.INT == type) {
            long tempValue = convertBitsToInt(bits, bitNumbering);

            if ((tempValue < 0) && isUnsigned) {
               tempValue += 4294967296L;
            }

            returnValue = new Long(tempValue);
         } else if (Type.LONG == type) {
            returnValue = new Long(convertBitsToLong(bits, bitNumbering));
         } else {
            JpegImageUtils.throwJpegImageExeption("Not valid number!");
         }
      }

      return returnValue;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Checks if bit index is valid.
    *
    *
    * @param bitIndex
    * @param type
    *
    * @return Returns true if the bitindex for type is between the [0, (typesize-1)].
    */
   private static boolean isBitIndexValid(int bitIndex, Type type) {
      int maxBitIndex = type.getTypeSize() - 1;

      return (bitIndex >= 0) && (bitIndex <= maxBitIndex);
   }
}
