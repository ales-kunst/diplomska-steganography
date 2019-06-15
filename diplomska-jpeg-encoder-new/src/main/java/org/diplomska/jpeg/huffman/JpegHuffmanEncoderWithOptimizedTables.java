package org.diplomska.jpeg.huffman;

import org.diplomska.util.BitWalker;
import org.diplomska.util.JpegImageEncoderUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/06/11
 * @author         Ales Kunst
 */
public class JpegHuffmanEncoderWithOptimizedTables implements HuffmanEncoder {

   /** Huffman elements */
   private List<HuffmanElement> fHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fHuffmanElementsWithGeneratedCodeLengths;

   /** Field description */
   private List<HuffmanElement> fHuffmanElementWithShortenedCodeLengths;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   public JpegHuffmanEncoderWithOptimizedTables() {
      fHuffmanElements                         = new ArrayList<HuffmanElement>();
      fHuffmanElementsWithGeneratedCodeLengths = null;
      fHuffmanElementWithShortenedCodeLengths  = null;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Encode bit array
    *
    *
    * @param aBitArray
    *
    * @return
    */
   @Override
   public HuffmanOutputStructure encode(byte[] aBitArray) {
      HuffmanOutputStructure resultHuffmanOutputStructure = null;

      fHuffmanElements                         = gatherByteFrequencies(aBitArray);
      fHuffmanElementsWithGeneratedCodeLengths = generateCodeLengths(fHuffmanElements);

      // The jpeg format takes only code length until the size of 16
      if (getMaxCodeLength(fHuffmanElementsWithGeneratedCodeLengths) > 16) {
         fHuffmanElementWithShortenedCodeLengths = shortenCodeLengths(fHuffmanElementsWithGeneratedCodeLengths, 16);
      } else {
         fHuffmanElementWithShortenedCodeLengths = fHuffmanElementsWithGeneratedCodeLengths;
      }

      fHuffmanElementWithShortenedCodeLengths = generateHuffmanCodes(fHuffmanElementWithShortenedCodeLengths);

      try {
         resultHuffmanOutputStructure = encodeBitArrayWithHuffman(aBitArray, fHuffmanElementWithShortenedCodeLengths);
      } catch (IOException ioe) {
         JpegImageEncoderUtils.throwJpegImageEncoderExeption(ioe.getMessage());
      }

      return resultHuffmanOutputStructure;
   }

   @Override
   public HuffmanOutputStructure encode(BitWalker aBitWalker) {
      return null;
   }

   /**
    * Encode array of bits with Huffman.
    *
    *
    *
    *
    * @param aBitArray
    * @param aHuffmanElements
    *
    * @return
    * @throws IOException
    */
   protected HuffmanOutputStructure encodeBitArrayWithHuffman(byte[] aBitArray,
                                                              final List<HuffmanElement> aHuffmanElements)
           throws IOException {
      HuffmanOutputStructure resultHuffmanStructure = null;
      int                    bitIndex               = 0;
      short                  skipNumberOfBits       = 0;
      byte[]                 byteValueBits          = new byte[8];
      ByteArrayOutputStream  huffmanencodedStream   = new ByteArrayOutputStream();

      for (int index = 0; index < aBitArray.length; index++) {
         byte currentBit = aBitArray[index];

         if ((bitIndex < 7) && (skipNumberOfBits == 0)) {
            byteValueBits[bitIndex] = currentBit;
            bitIndex++;
         } else if ((bitIndex == 7) && (skipNumberOfBits == 0)) {    // check if we can create byte  and if we are not skipping the bits
            byteValueBits[bitIndex] = currentBit;

            byte           byteValue             = JpegImageEncoderUtils.convertBitsToByte(byteValueBits);
            HuffmanElement currentHuffmanElement = getHuffmanElement(byteValue, aHuffmanElements);

            // write rlc bits to stream
            huffmanencodedStream.write(currentHuffmanElement.getHuffmanCodeBits());
            skipNumberOfBits = skipNumberOfBits(byteValue);
            bitIndex         = 0;
         } else if (skipNumberOfBits > 0) {

            // write one bit of color component value to stream
            huffmanencodedStream.write(currentBit);
            skipNumberOfBits--;
         }
      }

      resultHuffmanStructure = new HuffmanOutputStructure(aHuffmanElements, aHuffmanElements, aHuffmanElements,
                                                          aHuffmanElements, huffmanencodedStream.toByteArray());

      return resultHuffmanStructure;
   }

   /**
    *    Gather frequencies of
    *
    *
    *    @param aByteArrayOfBits
    *
    *    @return
    */
   protected List<HuffmanElement> gatherByteFrequencies(byte[] aByteArrayOfBits) {
      List<HuffmanElement> returnByteFrequencies = new ArrayList<HuffmanElement>();
      int                  bitIndex              = 0;
      short                skipNumberOfBits      = 0;
      byte[]               byteValueBits         = new byte[8];

      for (int index = 0; index < aByteArrayOfBits.length; index++) {
         byte currentBit = aByteArrayOfBits[index];

         if ((bitIndex < 7) && (skipNumberOfBits == 0)) {
            byteValueBits[bitIndex] = currentBit;
            bitIndex++;
         } else if ((bitIndex == 7) && (skipNumberOfBits == 0)) {    // check if we can create byte  and if we are not skipping the bits
            byteValueBits[bitIndex] = currentBit;

            byte byteValue = JpegImageEncoderUtils.convertBitsToByte(byteValueBits);

            skipNumberOfBits = skipNumberOfBits(byteValue);

            HuffmanElement huffmanElement = getHuffmanElement(byteValue, returnByteFrequencies);

            if (huffmanElement != null) {
               huffmanElement.increaseFrequency();
            } else {
               huffmanElement = new HuffmanElement(byteValue, 1);
               returnByteFrequencies.add(huffmanElement);
            }

            bitIndex = 0;
         } else if (skipNumberOfBits > 0) {
            skipNumberOfBits--;
         }
      }

      return returnByteFrequencies;
   }

   /**
    * Generate code lengths. This is done according to the Compressed Image File Formats book
    * (Chapter Huffman Coding Using Code Lengths).
    *
    *
    * @param aHuffmanElements
    *
    * @return
    */
   protected List<HuffmanElement> generateCodeLengths(List<HuffmanElement> aHuffmanElements) {
      List<HuffmanElement> resultHuffmanElements  = new ArrayList<HuffmanElement>();
      List<HuffmanElement> workingHuffmanElements = new ArrayList<HuffmanElement>();

      workingHuffmanElements.addAll(aHuffmanElements);
      Collections.sort(workingHuffmanElements, new FrequencyComparator());

      while (workingHuffmanElements.size() > 1) {
         int            lastElementIndex      = workingHuffmanElements.size() - 1;
         int            beforLastElementIndex = lastElementIndex - 1;
         HuffmanElement lastElement           = workingHuffmanElements.get(lastElementIndex);
         HuffmanElement beforeLastElement     = workingHuffmanElements.get(beforLastElementIndex);

         // merge last two elements
         lastElement.mergeWithElement(beforeLastElement);
         workingHuffmanElements.remove(beforeLastElement);
         Collections.sort(workingHuffmanElements, new FrequencyComparator());
      }

      //
      HuffmanElement rootHuffmanElement = workingHuffmanElements.get(0);

      resultHuffmanElements.add(rootHuffmanElement);
      resultHuffmanElements.addAll(rootHuffmanElement.getHuffmanElements());
      rootHuffmanElement.removeAllElements();
      Collections.sort(resultHuffmanElements, new CodeLengthComparator());

      return resultHuffmanElements;
   }

   /**
    * Generate Huffman codes.
    *
    *
    * @param aHuffmanElements
    *
    *
    * @return
    */
   protected List<HuffmanElement> generateHuffmanCodes(List<HuffmanElement> aHuffmanElements) {
      int huffmanCode       = 0;
      int currentCodeLength = aHuffmanElements.get(0).getCodeLength();

      for (HuffmanElement huffmanElement : aHuffmanElements) {
         if (currentCodeLength != huffmanElement.getCodeLength()) {
            int leftShiftTimes = huffmanElement.getCodeLength() - currentCodeLength;

            huffmanCode       = huffmanCode << leftShiftTimes;
            currentCodeLength = huffmanElement.getCodeLength();
         }

         huffmanElement.setHuffmanCode(huffmanCode);
         huffmanCode++;
      }

      return aHuffmanElements;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    *          Find Huffman element by its value
    *
    *
    *          @param aByteValue
    *
    *          @return
    */
   protected HuffmanElement getHuffmanElementByValue(Byte aByteValue) {
      return getHuffmanElement(aByteValue, fHuffmanElements);
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected List<HuffmanElement> getHuffmanElementWithShortenedCodeLengths() {
      return fHuffmanElementWithShortenedCodeLengths;
   }

   /**
    * Getter method
    *
    *
    * @return
    */
   protected List<HuffmanElement> getHuffmanElements() {
      return fHuffmanElements;
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   protected List<HuffmanElement> getHuffmanElementsWithGeneratedCodeLengths() {
      return fHuffmanElementsWithGeneratedCodeLengths;
   }

   /**
    * Gets max code length in the huffman elements array.
    *
    * @param aHuffmanElements
    *
    * @return
    */
   protected int getMaxCodeLength(List<HuffmanElement> aHuffmanElements) {
      int resultMaxCodeLength = 0;

      for (HuffmanElement huffmanElement : aHuffmanElements) {
         resultMaxCodeLength = Math.max(resultMaxCodeLength, huffmanElement.getCodeLength());
      }

      return resultMaxCodeLength;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Shorten the code lengths to specified max code length.
    *
    *
    * @param aHuffmanElements
    * @param aDestinationCodeLength
    *
    * @return
    */
   protected List<HuffmanElement> shortenCodeLengths(List<HuffmanElement> aHuffmanElements,
                                                     int aDestinationCodeLength) {
      List<HuffmanElement> resultHuffmanElements = new ArrayList<HuffmanElement>();

      resultHuffmanElements.addAll(aHuffmanElements);

      int currentMaxCodeLength         = getMaxCodeLength(resultHuffmanElements);
      int indexMaxCodeLengthElement    = -1;
      int indexFirstAppropriateElement = -1;

      while (currentMaxCodeLength > aDestinationCodeLength) {
         indexMaxCodeLengthElement    = getFirstElementIndexForCodeLength(resultHuffmanElements, currentMaxCodeLength);
         indexFirstAppropriateElement = getFirstAppropriateElement(resultHuffmanElements, currentMaxCodeLength);

         // loop until there are elements to process
         while ((indexMaxCodeLengthElement != -1) && (indexFirstAppropriateElement != -1)) {
            HuffmanElement elementToMoveUpTheTree               = resultHuffmanElements.get(indexMaxCodeLengthElement);
            HuffmanElement siblingElementToMoveUpTheTree        = resultHuffmanElements.get(indexMaxCodeLengthElement
                                                                                            + 1);
            HuffmanElement elementToMoveDownTheTree             =
               resultHuffmanElements.get(indexFirstAppropriateElement);
            int            codeLengthOfElementToMoveDownTheTree = elementToMoveDownTheTree.getCodeLength();

            elementToMoveUpTheTree.setCodeLength(codeLengthOfElementToMoveDownTheTree + 1);
            elementToMoveDownTheTree.setCodeLength(codeLengthOfElementToMoveDownTheTree + 1);
            siblingElementToMoveUpTheTree.setCodeLength(currentMaxCodeLength - 1);

            // get next element to move up the tree
            indexMaxCodeLengthElement = getFirstElementIndexForCodeLength(resultHuffmanElements, currentMaxCodeLength);

            // get next element to move down the tree
            indexFirstAppropriateElement = getFirstAppropriateElement(resultHuffmanElements, currentMaxCodeLength);
         }

         Collections.sort(resultHuffmanElements, new CodeLengthComparator());
         currentMaxCodeLength = getMaxCodeLength(resultHuffmanElements);
      }

      Collections.sort(resultHuffmanElements, new CodeLengthComparator());

      return resultHuffmanElements;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get first element which is code length - 2 or smaller.
    *
    *
    * @param aHuffmanElements
    * @param aCodeLength
    *
    * @return
    */
   private int getFirstAppropriateElement(List<HuffmanElement> aHuffmanElements, int aCodeLength) {
      int returnIndex = -1;

      for (int searchCodeLength = aCodeLength - 2; searchCodeLength > 0; searchCodeLength--) {
         for (int index = 0; index < aHuffmanElements.size(); index++) {
            if (aHuffmanElements.get(index).getCodeLength() == searchCodeLength) {
               returnIndex = index;

               break;
            }
         }

         // if found go out of the search loop
         if (returnIndex > 0) {
            break;
         }
      }

      return returnIndex;
   }

   /**
    *    Get first element in array which matches the code length.
    *
    *
    *    @param aHuffmanElements
    *    @param aCodeLength
    *
    *    @return
    */
   private int getFirstElementIndexForCodeLength(List<HuffmanElement> aHuffmanElements, int aCodeLength) {
      int returnIndex = -1;

      for (int index = 0; index < aHuffmanElements.size(); index++) {
         if (aHuffmanElements.get(index).getCodeLength() == aCodeLength) {
            returnIndex = index;

            break;
         }
      }

      return returnIndex;
   }

   /**
    *    Method description
    *
    *
    *    @param aKey
    *    @param aHuffmanElements
    *
    *    @return
    */
   private HuffmanElement getHuffmanElement(Byte aKey, List<HuffmanElement> aHuffmanElements) {
      HuffmanElement resultHuffmanElement = null;

      for (HuffmanElement huffmanElement : aHuffmanElements) {
         if (huffmanElement.getValue().equals(aKey)) {
            resultHuffmanElement = huffmanElement;

            break;
         }
      }

      return resultHuffmanElement;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aByteValue
    *
    * @return
    */
   private short skipNumberOfBits(byte aByteValue) {
      short returnSkipNumberOfBits = 0;

      if (aByteValue != 0) {
         returnSkipNumberOfBits = (short) (aByteValue & 0x0F);
      }

      return returnSkipNumberOfBits;
   }
}


/**
 * Class description
 *
 *
 * @version        1.0, 15/07/15
 * @author         Ales Kunst
 */
class CodeLengthComparator implements Comparator<HuffmanElement> {

   /**
    * Method description
    *
    *
    * @param aHuffmanElement1
    * @param aHuffmanElement2
    *
    * @return
    */
   @Override
   public int compare(HuffmanElement aHuffmanElement1, HuffmanElement aHuffmanElement2) {
      int resultOfCompare = 0;

      if (aHuffmanElement1.getCodeLength() > aHuffmanElement2.getCodeLength()) {
         resultOfCompare = 1;
      } else if (aHuffmanElement1.getCodeLength() < aHuffmanElement2.getCodeLength()) {
         resultOfCompare = -1;
      }

      return resultOfCompare;
   }
}


/**
 * Frequency comparator.
 *
 *
 * @version        1.0, 15/07/15
 * @author         Ales Kunst
 */
class FrequencyComparator implements Comparator<HuffmanElement> {

   /**
    * Method description
    *
    *
    * @param aHuffmanElement1
    * @param aHuffmanElement2
    *
    * @return
    */
   @Override
   public int compare(HuffmanElement aHuffmanElement1, HuffmanElement aHuffmanElement2) {
      int resultOfCompare = 0;

      if (aHuffmanElement1.getFrequencySum() > aHuffmanElement2.getFrequencySum()) {
         resultOfCompare = -1;
      } else if (aHuffmanElement1.getFrequencySum() < aHuffmanElement2.getFrequencySum()) {
         resultOfCompare = 1;
      }

      return resultOfCompare;
   }
}
