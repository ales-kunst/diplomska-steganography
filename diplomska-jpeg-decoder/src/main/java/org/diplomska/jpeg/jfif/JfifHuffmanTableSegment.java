package org.diplomska.jpeg.jfif;

import org.diplomska.jpeg.jfif.struct.HuffmanTable;

import java.util.Arrays;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 25.nov.2015
 * @author         Ales Kunst
 */
public class JfifHuffmanTableSegment extends JfifSegment {

   /** Field description */
   private static final int OFFSET_CLASS_DESTINATION_BYTE = 0;

   /** Field description */
   private static final int OFFSET_CODE_LENGTH_SIZE_TABLE = 1;

   /** Field description */
   private static final int SIZE_OF_CODE_LENGTH_SIZE_TABLE = 16;

   //~--- fields --------------------------------------------------------------

   /** Field description */
   private HuffmanTable huffmanTable;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param marker
    */
   public JfifHuffmanTableSegment(byte[] marker) {
      super(marker);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get huffman table.
    *
    *
    * @return
    */
   public HuffmanTable getHuffmanTable() {
      if (huffmanTable == null) {
         byte[] segmentData   = getPureSegmentContents();
         int    otherDataSize = (SIZE_OF_CODE_LENGTH_SIZE_TABLE + 1);
         int    fromIndex     = otherDataSize;
         int    toIndex       = segmentData.length;

         // Copy only data of code langths
         byte[] codesData = Arrays.copyOfRange(segmentData, fromIndex, toIndex);

         huffmanTable = new HuffmanTable(getCodeLengthSizes(), codesData);
      }

      return huffmanTable;
   }

   /**
    * Is this huffman for Ac coefficients.
    *
    *
    * @return
    */
   public boolean isAcTypeClass() {
      byte    classDestinationByte = getPureSegmentContents()[OFFSET_CLASS_DESTINATION_BYTE];
      int     coeffClassBit        = ((classDestinationByte & 0x10) >>> 4) & 1;
      boolean isAc                 = (coeffClassBit == 1);

      return isAc;
   }

   /**
    * Is Huffman table for chroma coefficients.
    *
    *
    * @return
    */
   public boolean isChromaDestination() {
      byte    classDestinationByte = getPureSegmentContents()[OFFSET_CLASS_DESTINATION_BYTE];
      int     coeffDestinationBit  = (classDestinationByte & 0x01) & 1;
      boolean isForChromaCoeff     = (coeffDestinationBit == 1);

      return isForChromaCoeff;
   }

   /**
    * Is this huffman for Ac coefficients.
    *
    *
    * @return
    */
   public boolean isDcTypeClass() {
      byte    classDestinationByte = getPureSegmentContents()[OFFSET_CLASS_DESTINATION_BYTE];
      int     coeffClassBit        = ((classDestinationByte & 0x10) >>> 4) | 0;
      boolean isDc                 = (coeffClassBit == 0);

      return isDc;
   }

   /**
    * Is Huffman table for luma coefficients.
    *
    *
    * @return
    */
   public boolean isLumaDestination() {
      byte    classDestinationByte = getPureSegmentContents()[OFFSET_CLASS_DESTINATION_BYTE];
      int     coeffDestinationBit  = (classDestinationByte & 0x01) | 0;
      boolean isForLumaCoeff       = (coeffDestinationBit == 0);

      return isForLumaCoeff;
   }

   /**
    * Number of code lengths.
    *
    *
    * @return
    */
   protected int[] getCodeLengthSizes() {
      int[]  returnNumberOfCodes = new int[16];
      byte[] segmentData         = getPureSegmentContents();
      int    codesIndex          = 0;

      for (int valueIndex = OFFSET_CODE_LENGTH_SIZE_TABLE; valueIndex < (OFFSET_CODE_LENGTH_SIZE_TABLE + 16);
              valueIndex++) {
         returnNumberOfCodes[codesIndex] = segmentData[valueIndex];
         codesIndex++;
      }

      return returnNumberOfCodes;
   }
}
