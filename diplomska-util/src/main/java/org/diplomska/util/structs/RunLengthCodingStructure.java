package org.diplomska.util.structs;

import org.diplomska.util.JpegImageEncoderUtils;

//~--- classes ----------------------------------------------------------------

/**
 * Run length coding structure.
 *
 *
 * @version        1.0, 15/06/11
 * @author         Ales Kunst
 */
public class RunLengthCodingStructure {

   /** Number of preceedeing zeroes */
   public int fNumberOfPreceedingZeroes;

   /** Minimum size in bits in which we can store the value */
   public int fNumberOfBits;

   /** This is encoded zero run length coded number */
   public int fEncodedZrlcNumber;

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public int getCoefficientNumber() {
      return JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue(fNumberOfBits, fEncodedZrlcNumber);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Convert numbers to array.
    *
    *
    * @return
    */
   public int[] toArray() {
      int[] returnNumbers = new int[3];

      returnNumbers[0] = fNumberOfPreceedingZeroes;
      returnNumbers[1] = fNumberOfBits;
      returnNumbers[2] = fEncodedZrlcNumber;

      return returnNumbers;
   }

   /**
    * Bits representation of this structure.
    *
    *
    * @return
    */
   public byte[] toBits() {
      byte[] returnBits = new byte[4 + 4 + fNumberOfBits];
      int    index      = 0;

      // Convert to bits number of preceeding zeroes
      for (int position = 3; position >= 0; position--) {
         returnBits[index] = (byte) ((fNumberOfPreceedingZeroes >> position) & 1);
         index++;
      }

      // Convert to bits number of bits
      for (int position = 3; position >= 0; position--) {
         returnBits[index] = (byte) ((fNumberOfBits >> position) & 1);
         index++;
      }

      // Put bits of coefficient into the bits array.
      for (byte bit : JpegImageEncoderUtils.encodeToBitCodedRepresentation(getCoefficientNumber())) {
         returnBits[index] = bit;
         index++;
      }

      return returnBits;
   }
}
