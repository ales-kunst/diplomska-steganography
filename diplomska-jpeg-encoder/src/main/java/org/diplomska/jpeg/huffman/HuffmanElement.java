package org.diplomska.jpeg.huffman;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/07/15
 * @author         Ales Kunst
 */
public class HuffmanElement {

   /** Byte value */
   private Byte fValue;

   /** Field description */
   private byte[] fBitRepresentationOfValue;

   /** Byte frequency */
   private long fFrequency;

   /** Huffman code length */
   private int fCodeLength;

   /** Huffman code */
   private int fHuffmanCode;

   /** List of Huffman elements */
   private List<HuffmanElement> fHuffmanElements;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aValue
    * @param aFrequency
    */
   public HuffmanElement(Byte aValue, long aFrequency) {
      this(aValue, aFrequency, 0);
   }

   /**
    * Constructs ...
    *
    *
    * @param aValue
    * @param aFrequency
    * @param aHuffmanCodeLength
    */
   public HuffmanElement(Byte aValue, long aFrequency, int aHuffmanCodeLength) {
      fValue                    = aValue;
      fFrequency                = aFrequency;
      fCodeLength               = aHuffmanCodeLength;
      fHuffmanElements          = new ArrayList<HuffmanElement>();
      fBitRepresentationOfValue = contvertValueTo8BitArray(fValue);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter for bit representation of Huffman value.
    *
    *
    * @return
    */
   public byte[] getBitRepresentationOfValue() {
      return fBitRepresentationOfValue;
   }

   /**
    *    Get Huffman code length.
    *
    *
    *    @return
    */
   public int getCodeLength() {
      return fCodeLength;
   }

   /**
    * Get frequency.
    *
    *
    * @return
    */
   public long getFrequency() {
      return fFrequency;
   }

   /**
    * Get sum of all frequencies.
    *
    *
    * @return
    */
   public long getFrequencySum() {
      long resultFrequencySum = fFrequency;

      for (HuffmanElement huffmanElement : fHuffmanElements) {
         resultFrequencySum += huffmanElement.getFrequency();
      }

      return resultFrequencySum;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getHuffmanCode() {
      return fHuffmanCode;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public byte[] getHuffmanCodeBits() {
      byte[] resultBits  = new byte[fCodeLength];
      int    maxPosition = fCodeLength - 1;

      for (int position = maxPosition; position >= 0; position--) {
         resultBits[maxPosition - position] = (byte) ((fHuffmanCode >> position) & 0x01);
      }

      return resultBits;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Byte getValue() {
      return fValue;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public long increaseFrequency() {
      return ++fFrequency;
   }

   /**
    * Increase codelength by 1.
    *
    *
    * @return
    */
   public long increaseHuffmanCodelength() {
      return ++fCodeLength;
   }

   /**
    * Merge this element with the other one.
    *
    *
    *
    * @param aOtherHuffmanElement
    */
   public void mergeWithElement(HuffmanElement aOtherHuffmanElement) {
      for (HuffmanElement huffmanElement : fHuffmanElements) {
         huffmanElement.increaseHuffmanCodelength();
      }

      for (HuffmanElement huffmanElement : aOtherHuffmanElement.getHuffmanElements()) {
         huffmanElement.increaseHuffmanCodelength();
         fHuffmanElements.add(huffmanElement);
      }

      aOtherHuffmanElement.removeAllElements();
      aOtherHuffmanElement.increaseHuffmanCodelength();
      fHuffmanElements.add(aOtherHuffmanElement);
      increaseHuffmanCodelength();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param fHuffmanCode
    */
   public void setHuffmanCode(int fHuffmanCode) {
      this.fHuffmanCode = fHuffmanCode;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * String representation.
    *
    *
    * @return
    */
   @Override
   public String toString() {
      StringBuffer resultString = new StringBuffer("");

      resultString.append("[ ");
      resultString.append("[" + fValue.toString() + ", " + fFrequency + ", " + fHuffmanCode + ", " + fCodeLength
                          + "] ");

      for (HuffmanElement huffmanElement : fHuffmanElements) {
         resultString.append("[" + huffmanElement.fValue.toString() + ", " + huffmanElement.fFrequency + ", "
                             + huffmanElement.fCodeLength + "] ");
      }

      resultString.append(getFrequencySum() + " ]");

      return resultString.toString();
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get huffman elements.
    *
    *
    * @return
    */
   protected List<HuffmanElement> getHuffmanElements() {
      return fHuffmanElements;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Removes all huffman elements.
    *
    *
    * @return
    */
   protected boolean removeAllElements() {
      return fHuffmanElements.removeAll(fHuffmanElements);
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Sets code length.
    *
    *
    * @param aCodeLength
    */
   protected void setCodeLength(int aCodeLength) {
      fCodeLength = aCodeLength;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Convert value to 8 bit array.
    *
    *
    * @param aValue
    *
    * @return
    */
   private byte[] contvertValueTo8BitArray(Byte aValue) {
      byte[] resultBits  = new byte[8];
      int    maxPosition = resultBits.length - 1;

      for (int position = maxPosition; position >= 0; position--) {
         resultBits[maxPosition - position] = (byte) ((fHuffmanCode >> position) & 1);
      }

      return resultBits;
   }
}
