package org.diplomska.jpeg.jfif.struct;

import java.util.ArrayList;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 25.nov.2015
 * @author         Ales Kunst
 */
public class HuffmanTable {

   /** Huffman table */
   private List<HuffmanCode>[] huffmanTable;

   /** Codes array */
   private byte[] codesData;

   /** Sizes for certain codeslengths */
   private int[] codeLengthSizes;

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor.
    *
    */
   @SuppressWarnings("unchecked")
   private HuffmanTable() {
      huffmanTable = new List[16];
   }

   /**
    * Default constructor.
    *
    *
    * @param codeLengthSizes
    * @param codesData
    */
   public HuffmanTable(int[] codeLengthSizes, byte[] codesData) {
      this();
      this.codeLengthSizes = codeLengthSizes;
      this.codesData       = codesData;
      initialize();
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get array of codes for one code length.
    *
    *
    * @param codeLength
    *
    * @return
    */
   public short[] getCodes(int codeLength) {
      short[] returnCodes       = null;
      int     huffmanTableIndex = codeLength - 1;

      if (huffmanTable[huffmanTableIndex].size() > 0) {
         returnCodes = new short[huffmanTable[huffmanTableIndex].size()];

         for (int index = 0; index < huffmanTable[huffmanTableIndex].size(); index++) {
            returnCodes[index] = huffmanTable[huffmanTableIndex].get(index).code;
         }
      }

      return returnCodes;
   }

   /**
    * Method description
    *
    *
    * @param value
    * @param codeLength
    *
    * @return
    */
   public HuffmanCode getHuffmanCode(int value, int codeLength) {
      HuffmanCode returnHuffmanCode = null;

      for (List<HuffmanCode> huffmanCodes : huffmanTable) {
         for (HuffmanCode huffmanCode : huffmanCodes) {
            if ((huffmanCode.huffmanTreeCode == value) && (huffmanCode.codeLength == codeLength)) {
               returnHuffmanCode = huffmanCode;

               break;
            }
         }

         // Huffman code found
         if (returnHuffmanCode != null) {
            break;
         }
      }

      return returnHuffmanCode;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   private void initialize() {
      int currentPosition = 0;
      int lengthOfCode    = 1;
      int huffmanTreeCode = 0;

      for (int codeLengthSize : codeLengthSizes) {
         int huffmanTablePosition = lengthOfCode - 1;

         huffmanTable[huffmanTablePosition] = new ArrayList<HuffmanCode>();

         for (int index = 0; index < codeLengthSize; index++) {
            byte        code        = codesData[currentPosition];
            HuffmanCode huffmanCode = new HuffmanCode(code, huffmanTreeCode, lengthOfCode);

            huffmanTable[huffmanTablePosition].add(huffmanCode);

            // System.out.println("Length: " + lengthOfCode + " " + code + " | "
            // + Integer.toBinaryString(huffmanTreeCode));
            huffmanTreeCode++;
            currentPosition++;
         }

         huffmanTreeCode = (huffmanTreeCode << 1);
         lengthOfCode++;
      }
   }
}
