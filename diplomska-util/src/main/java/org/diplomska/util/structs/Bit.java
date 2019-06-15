package org.diplomska.util.structs;

/**
 * Enum description
 *
 */
public enum Bit {
   ONE(1), ZERO(0);

   /** Field description */
   private final int bitValue;

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor.
    *
    *
    *
    * @param bitValue
    */
   private Bit(int bitValue) {
      this.bitValue = bitValue;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Converts byte to bit enum  representation
    *
    *
    * @param bitValue
    *
    * @return
    */
   public static Bit convertToBit(int bitValue) {
      Bit returnBit = Bit.ONE;

      if (bitValue == 0) {
         returnBit = Bit.ZERO;
      }

      return returnBit;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get type size.
    *
    *
    * @return
    */
   public int getBitValue() {
      return bitValue;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public byte toByte() {
      return (byte) bitValue;
   }
}
