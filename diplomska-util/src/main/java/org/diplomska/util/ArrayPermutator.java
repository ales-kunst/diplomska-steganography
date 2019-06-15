package org.diplomska.util;

import java.security.SecureRandom;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/26
 * @author         Enter your name here...
 */
public class ArrayPermutator {

   /** Field description */
   private SecureRandom secureRandonGenerator = null;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param password
    */
   public ArrayPermutator(byte[] password) {
      this.secureRandonGenerator = new SecureRandom(password);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aMaxValue
    *
    * @return
    */
   public int[] createArray(int aMaxValue) {
      int[] resultArray = new int[aMaxValue];
      int   randomIndex = -1;
      int   tempIndex   = -1;

      // Initialize array elements with initial values
      for (int index = 0; index < aMaxValue; index++) {
         resultArray[index] = index;
      }

      int maxRandom = aMaxValue;    // set number of entries to shuffle

      for (int index = 0; index < aMaxValue; index++) {    // shuffle entries
         randomIndex              = nextIntValue(maxRandom--);
         tempIndex                = resultArray[randomIndex];
         resultArray[randomIndex] = resultArray[maxRandom];
         resultArray[maxRandom]   = tempIndex;
      }

      return resultArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   private int nextByte() {
      byte[] currentRandomByte = new byte[1];

      this.secureRandonGenerator.nextBytes(currentRandomByte);

      return currentRandomByte[0];
   }

   /**
    * Method description
    *
    *
    * @param maxValue
    *
    * @return
    */
   private int nextIntValue(final int maxValue) {
      int resultValue = nextByte() | nextByte() << 8 | nextByte() << 16 | nextByte() << 24;

      resultValue %= maxValue;

      if (resultValue < 0) {
         resultValue += maxValue;
      }

      return resultValue;
   }
}
