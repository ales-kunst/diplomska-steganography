package org.diplomska.jpeg.huffman;

import org.diplomska.util.BitWalker;
import org.diplomska.util.JpegImageEncoderUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 15/07/23
 * @author         Enter your name here...
 */
public class JpegHuffmanEncoderWithDefaultTables implements HuffmanEncoder {

   /** Field description */
   private short[] fLumaDcHuffmanTableEntriesCount = { 0x00, 0x01, 0x05, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00, 0x00,
                                                       0x00, 0x00, 0x00, 0x00, 0x00 };

   /** Field description */
   private short[] fLumaDcHuffmanTable = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B };

   /** Field description */
   private short[] fLumaAcHuffmanTableEntriesCount = { 0x00, 0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05, 0x04,
                                                       0x04, 0x00, 0x00, 0x01, 0x7D };

   /** Field description */
   private short[] fLumaAcHuffmanTable = { 0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13,
                                           0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xA1, 0x08, 0x23, 0x42,
                                           0xB1, 0xC1, 0x15, 0x52, 0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A,
                                           0x16, 0x17, 0x18, 0x19, 0x1A, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2A, 0x34, 0x35,
                                           0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A,
                                           0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64, 0x65, 0x66, 0x67,
                                           0x68, 0x69, 0x6A, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x83, 0x84,
                                           0x85, 0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98,
                                           0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3,
                                           0xB4, 0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7,
                                           0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE1,
                                           0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF1, 0xF2, 0xF3, 0xF4,
                                           0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA };

   /** Field description */
   private short[] fChromaDcHuffmanTableEntriesCount = { 0x00, 0x03, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01,
                                                         0x01, 0x00, 0x00, 0x00, 0x00, 0x00 };

   /** Field description */
   private short[] fChromaDcHuffmanTable = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B };

   /** Field description */
   private short[] fChromaAcHuffmanTableEntriesCount = { 0x00, 0x02, 0x01, 0x02, 0x04, 0x04, 0x03, 0x04, 0x07, 0x05,
                                                         0x04, 0x04, 0x00, 0x01, 0x02, 0x77 };

   /** Field description */
   private short[] fChromaAcHuffmanTable = { 0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41,
                                             0x51, 0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91,
                                             0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33, 0x52, 0xF0, 0x15, 0x62, 0x72, 0xD1,
                                             0x0A, 0x16, 0x24, 0x34, 0xE1, 0x25, 0xF1, 0x17, 0x18, 0x19, 0x1A, 0x26,
                                             0x27, 0x28, 0x29, 0x2A, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x43, 0x44,
                                             0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58,
                                             0x59, 0x5A, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74,
                                             0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87,
                                             0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9A,
                                             0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4,
                                             0xB5, 0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 0xC7,
                                             0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6, 0xD7, 0xD8, 0xD9, 0xDA,
                                             0xE2, 0xE3, 0xE4, 0xE5, 0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF2, 0xF3, 0xF4,
                                             0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA };

   /** Field description */
   private List<HuffmanElement> fLumaDcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fLumaAcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fChromaDcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fChromaAcHuffmanElements;

   //~--- constant enums ------------------------------------------------------

   /**
    * Type of the element which is in the zig zag array
    *
    */
   private enum MatrixElementType {
      Y_DC, Y_AC, CB_DC, CB_AC, CR_DC, CR_AC;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    *
    * @param aBitArray
    *
    * @return
    */
   @Override
   public HuffmanOutputStructure encode(byte[] aBitArray) {
      HuffmanOutputStructure resultHuffmanOutputStructure = null;

      initializeHuffmanElementsCollections();
      generateHuffmanCodes(fLumaDcHuffmanElements);
      generateHuffmanCodes(fLumaAcHuffmanElements);
      generateHuffmanCodes(fChromaDcHuffmanElements);
      generateHuffmanCodes(fChromaAcHuffmanElements);

      // printOutHuffmanCodes();
      try {
         byte[] huffmanEncodedBits = encodeBitArrayWithHuffman(aBitArray);

         // decodeBitArrayWithHuffman(huffmanEncodedBits);
         resultHuffmanOutputStructure = new HuffmanOutputStructure(fLumaDcHuffmanElements, fLumaAcHuffmanElements,
                                                                   fChromaDcHuffmanElements, fChromaAcHuffmanElements,
                                                                   huffmanEncodedBits);
      } catch (IOException ioe) {
         JpegImageEncoderUtils.throwJpegImageEncoderExeption(ioe.getMessage());
      }

      return resultHuffmanOutputStructure;
   }

   @Override
   public HuffmanOutputStructure encode(BitWalker aBitWalker) {
      HuffmanOutputStructure resultHuffmanOutputStructure = null;

      initializeHuffmanElementsCollections();
      generateHuffmanCodes(fLumaDcHuffmanElements);
      generateHuffmanCodes(fLumaAcHuffmanElements);
      generateHuffmanCodes(fChromaDcHuffmanElements);
      generateHuffmanCodes(fChromaAcHuffmanElements);

      // printOutHuffmanCodes();
      try {
         byte[] huffmanEncodedBits = encodeBitArrayWithHuffman(aBitWalker);

         // decodeBitArrayWithHuffman(huffmanEncodedBits);
         resultHuffmanOutputStructure = new HuffmanOutputStructure(fLumaDcHuffmanElements, fLumaAcHuffmanElements,
                                                                   fChromaDcHuffmanElements, fChromaAcHuffmanElements,
                                                                   huffmanEncodedBits);
      } catch (IOException ioe) {
         JpegImageEncoderUtils.throwJpegImageEncoderExeption(ioe.getMessage());
      }

      return resultHuffmanOutputStructure;
   }

   /**
    *    Method description
    *
    *
    *
    *    @param aHuffmanTableEntriesCount
    *    @param aHuffmanTable
    *
    *    @return
    */
   protected List<HuffmanElement> createHuffmanElementsFromPredefinedHuffmanTables(short[] aHuffmanTableEntriesCount,
                                                                                   short[] aHuffmanTable) {
      List<HuffmanElement> resultHuffmanElements = new ArrayList<HuffmanElement>();
      int                  startPosition         = 0;
      int                  endPosition           = 0;

      for (int codeLength = 1; codeLength <= 16; codeLength++) {
         int position      = codeLength - 1;
         int elementsCount = aHuffmanTableEntriesCount[position];

         startPosition = endPosition;
         endPosition   = startPosition + elementsCount;

         for (int index = startPosition; index < endPosition; index++) {
            byte huffmanElementValue = (byte) aHuffmanTable[index];

            resultHuffmanElements.add(new HuffmanElement(huffmanElementValue, 0, codeLength));
         }
      }

      return resultHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @param aBitArray
    *
    *
    * @throws IOException
    */
   protected void decodeBitArrayWithHuffman(byte[] aBitArray) throws IOException {
      MatrixElementType     currentElementType        = MatrixElementType.Y_DC;
      List<HuffmanElement>  currentHuffmanTable       = getHuffmanTableForElementType(currentElementType);
      byte                  decodedValue              = 0;
      ByteArrayOutputStream huffmanEncodedBits        = new ByteArrayOutputStream();
      ByteArrayOutputStream notHuffmanEncodeBits      = new ByteArrayOutputStream();
      int                   numberOfNotCompressedBits = 0;
      boolean               isHuffmanEncodedNumber    = true;
      boolean               isDcElementType           = true;
      boolean               isAcElementType           = false;
      boolean               isEob                     = false;

      for (int index = 0; index < aBitArray.length; index++) {
         byte currentBit = aBitArray[index];

         if (isHuffmanEncodedNumber) {
            huffmanEncodedBits.write(currentBit);

            HuffmanElement huffmanElement = searchForBitPattern(huffmanEncodedBits.toByteArray(), currentHuffmanTable);

            if (huffmanElement != null) {
               isHuffmanEncodedNumber    = false;
               decodedValue              = huffmanElement.getValue().byteValue();
               numberOfNotCompressedBits = decodedValue & 0x0F;
               huffmanEncodedBits        = new ByteArrayOutputStream();
               isDcElementType           = (currentElementType == MatrixElementType.Y_DC)
                                           || (currentElementType == MatrixElementType.CB_DC)
                                           || (currentElementType == MatrixElementType.CR_DC);
               isAcElementType = (currentElementType == MatrixElementType.Y_AC)
                                 || (currentElementType == MatrixElementType.CB_AC)
                                 || (currentElementType == MatrixElementType.CR_AC);
               isEob = (decodedValue == 0x00);
            }
         } else if (numberOfNotCompressedBits > 0) {
            notHuffmanEncodeBits.write(currentBit);
            numberOfNotCompressedBits--;
         }

         if (!isHuffmanEncodedNumber && (numberOfNotCompressedBits == 0)) {
            isHuffmanEncodedNumber = true;

            byte[] notEncodedNumberBits = notHuffmanEncodeBits.toByteArray();
            int    notEncodedNumber     = JpegImageEncoderUtils.convertBitsToInt(notEncodedNumberBits);
            int    decodedZrlcNumber    =
               JpegImageEncoderUtils.convertFromEncodedValueToCoefficientValue(notEncodedNumberBits.length,
                                                                               notEncodedNumber);

            // TODO Kunst: Write out the numbers
            System.out.print(String.format(" %02X ", decodedValue));
            System.out.print(decodedZrlcNumber);

            if (isEob && isAcElementType) {
               System.out.println("");
            } else {
               System.out.print(", ");
            }

            if (isDcElementType) {
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
            } else if (isAcElementType && isEob) {
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
            } else if (isAcElementType) {}

            notHuffmanEncodeBits = new ByteArrayOutputStream();
         }
      }
   }

   /**
    *    Encode array of bits with Huffman.
    *
    *
    *
    *
    *    @param aBitArray
    *
    *    @return
    *    @throws IOException
    */
   protected byte[] encodeBitArrayWithHuffman(byte[] aBitArray) throws IOException {
      int                   bitIndex                   = 0;
      short                 skipNumberOfBits           = 0;
      byte[]                byteValueBits              = new byte[8];
      ByteArrayOutputStream resultHuffmanEncodedStream = new ByteArrayOutputStream();
      List<Byte>            huffmanEncodedArray        = new ArrayList<Byte>();
      MatrixElementType     currentElementType         = MatrixElementType.Y_DC;
      List<HuffmanElement>  currentHuffmanTable        = getHuffmanTableForElementType(currentElementType);
      int                   encodeByteCount            = 0;
      int                   currentAcElement           = 0;
      int                   lastElementPosition        = 0;

      for (int index = 0; index < aBitArray.length; index++) {
         byte currentBit = aBitArray[index];

         if ((bitIndex < 7) && (skipNumberOfBits == 0)) {
            byteValueBits[bitIndex] = currentBit;
            bitIndex++;
         } else if ((bitIndex == 7) && (skipNumberOfBits == 0)) {    // check if we can create byte  and if we are not skipping the bits
            encodeByteCount++;
            byteValueBits[bitIndex] = currentBit;

            byte byteValue = JpegImageEncoderUtils.convertBitsToByte(byteValueBits);

            skipNumberOfBits = (short) (byteValue & 0x0F);

            boolean isDcElementType = (currentElementType == MatrixElementType.Y_DC)
                                      || (currentElementType == MatrixElementType.CB_DC)
                                      || (currentElementType == MatrixElementType.CR_DC);
            boolean isAcElementType = (currentElementType == MatrixElementType.Y_AC)
                                      || (currentElementType == MatrixElementType.CB_AC)
                                      || (currentElementType == MatrixElementType.CR_AC);

            if ((isAcElementType && (byteValue != 0x00)) || isDcElementType) {
               int preceedingNumberOfZeroes = ((byteValue & 0xF0) >>> 4);

               lastElementPosition += preceedingNumberOfZeroes + 1;    // preceeding zeroes + 1 value
            }

            boolean        isEob                 = (byteValue == 0x00) || (lastElementPosition == 64);
            HuffmanElement currentHuffmanElement = getHuffmanElement(byteValue, currentHuffmanTable);

            // write rlc bits to stream
            resultHuffmanEncodedStream.write(currentHuffmanElement.getHuffmanCodeBits());

            for (byte b : currentHuffmanElement.getHuffmanCodeBits()) {
               huffmanEncodedArray.add(b);
            }

            if (isDcElementType) {
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
            } else if (isAcElementType && isEob) {

               // System.out.println("St AC: " + currentAcElement);
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
               currentAcElement    = 0;
               lastElementPosition = 0;
            }

            bitIndex = 0;
         } else if (skipNumberOfBits > 0) {

            // write one bit of color component value to stream
            resultHuffmanEncodedStream.write(currentBit);
            huffmanEncodedArray.add(currentBit);
            skipNumberOfBits--;
         }
      }

      resultHuffmanEncodedStream.flush();

      return resultHuffmanEncodedStream.toByteArray();
   }

   /**
    *    Encode array of bits with Huffman.
    *
    *
    *
    *
    *
    * @param aBitWalker
    *
    *    @return
    *    @throws IOException
    */
   protected byte[] encodeBitArrayWithHuffman(BitWalker aBitWalker) throws IOException {
      int                   bitIndex                   = 0;
      short                 skipNumberOfBits           = 0;
      byte[]                byteValueBits              = new byte[8];
      ByteArrayOutputStream resultHuffmanEncodedStream = new ByteArrayOutputStream();
      List<Byte>            huffmanEncodedArray        = new ArrayList<Byte>();
      MatrixElementType     currentElementType         = MatrixElementType.Y_DC;
      List<HuffmanElement>  currentHuffmanTable        = getHuffmanTableForElementType(currentElementType);
      int                   encodeByteCount            = 0;
      int                   currentAcElement           = 0;
      int                   lastElementPosition        = 0;
      Byte                  currentBit                 = null;

      while ((currentBit = aBitWalker.getNextBit()) != null) {

         // byte currentBit = aBitArray[index];
         if ((bitIndex < 7) && (skipNumberOfBits == 0)) {
            byteValueBits[bitIndex] = currentBit;
            bitIndex++;
         } else if ((bitIndex == 7) && (skipNumberOfBits == 0)) {    // check if we can create byte  and if we are not skipping the bits
            encodeByteCount++;
            byteValueBits[bitIndex] = currentBit;

            byte byteValue = JpegImageEncoderUtils.convertBitsToByte(byteValueBits);

            skipNumberOfBits = (short) (byteValue & 0x0F);

            boolean isDcElementType = (currentElementType == MatrixElementType.Y_DC)
                                      || (currentElementType == MatrixElementType.CB_DC)
                                      || (currentElementType == MatrixElementType.CR_DC);
            boolean isAcElementType = (currentElementType == MatrixElementType.Y_AC)
                                      || (currentElementType == MatrixElementType.CB_AC)
                                      || (currentElementType == MatrixElementType.CR_AC);

            if ((isAcElementType && (byteValue != 0x00)) || isDcElementType) {
               int preceedingNumberOfZeroes = ((byteValue & 0xF0) >>> 4);

               lastElementPosition += preceedingNumberOfZeroes + 1;    // preceeding zeroes + 1 value
            }

            boolean        isEob                 = (byteValue == 0x00) || (lastElementPosition == 64);
            HuffmanElement currentHuffmanElement = getHuffmanElement(byteValue, currentHuffmanTable);

            // write rlc bits to stream
            resultHuffmanEncodedStream.write(currentHuffmanElement.getHuffmanCodeBits());

            for (byte b : currentHuffmanElement.getHuffmanCodeBits()) {
               huffmanEncodedArray.add(b);
            }

            if (isDcElementType) {
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
            } else if (isAcElementType && isEob) {

               // System.out.println("St AC: " + currentAcElement);
               currentElementType  = getNextElementType(currentElementType);
               currentHuffmanTable = getHuffmanTableForElementType(currentElementType);
               currentAcElement    = 0;
               lastElementPosition = 0;
            }

            bitIndex = 0;
         } else if (skipNumberOfBits > 0) {

            // write one bit of color component value to stream
            resultHuffmanEncodedStream.write(currentBit);
            huffmanEncodedArray.add(currentBit);
            skipNumberOfBits--;
         }
      }

      resultHuffmanEncodedStream.flush();

      return resultHuffmanEncodedStream.toByteArray();
   }

   /**
    * Generate Huffman codes.
    *
    *
    * @param aHuffmanElements
    *
    */
   protected void generateHuffmanCodes(List<HuffmanElement> aHuffmanElements) {
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
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   protected List<HuffmanElement> getChromaAcHuffmanElements() {
      return fChromaAcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected List<HuffmanElement> getChromaDcHuffmanElements() {
      return fChromaDcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @param aElementType
    *
    * @return
    */
   protected List<HuffmanElement> getHuffmanTableForElementType(MatrixElementType aElementType) {
      List<HuffmanElement> resultHuffmanElements = null;

      switch (aElementType) {
      case Y_DC :
         resultHuffmanElements = getLumaDcHuffmanElements();

         break;

      case Y_AC :
         resultHuffmanElements = getLumaAcHuffmanElements();

         break;

      case CB_DC :
         resultHuffmanElements = getChromaDcHuffmanElements();

         break;

      case CB_AC :
         resultHuffmanElements = getChromaAcHuffmanElements();

         break;

      case CR_DC :
         resultHuffmanElements = getChromaDcHuffmanElements();

         break;

      case CR_AC :
         resultHuffmanElements = getChromaAcHuffmanElements();

         break;

      default :
         JpegImageEncoderUtils.throwJpegImageEncoderExeption("Incorrect element type!");

         break;
      }

      return resultHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected List<HuffmanElement> getLumaAcHuffmanElements() {
      return fLumaAcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   protected List<HuffmanElement> getLumaDcHuffmanElements() {
      return fLumaDcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @param aCurrentElementType
    *
    * @return
    */
   protected MatrixElementType getNextElementType(MatrixElementType aCurrentElementType) {
      MatrixElementType resultElementType = null;

      switch (aCurrentElementType) {
      case Y_DC :
         resultElementType = MatrixElementType.Y_AC;

         break;

      case Y_AC :
         resultElementType = MatrixElementType.CB_DC;

         break;

      case CB_DC :
         resultElementType = MatrixElementType.CB_AC;

         break;

      case CB_AC :
         resultElementType = MatrixElementType.CR_DC;

         break;

      case CR_DC :
         resultElementType = MatrixElementType.CR_AC;

         break;

      case CR_AC :
         resultElementType = MatrixElementType.Y_DC;

         break;

      default :
         resultElementType = MatrixElementType.Y_DC;

         break;
      }

      return resultElementType;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    */
   protected void initializeHuffmanElementsCollections() {
      fLumaDcHuffmanElements = createHuffmanElementsFromPredefinedHuffmanTables(fLumaDcHuffmanTableEntriesCount,
                                                                                fLumaDcHuffmanTable);
      fLumaAcHuffmanElements = createHuffmanElementsFromPredefinedHuffmanTables(fLumaAcHuffmanTableEntriesCount,
                                                                                fLumaAcHuffmanTable);
      fChromaDcHuffmanElements = createHuffmanElementsFromPredefinedHuffmanTables(fChromaDcHuffmanTableEntriesCount,
                                                                                  fChromaDcHuffmanTable);
      fChromaAcHuffmanElements = createHuffmanElementsFromPredefinedHuffmanTables(fChromaAcHuffmanTableEntriesCount,
                                                                                  fChromaAcHuffmanTable);
   }

   //~--- get methods ---------------------------------------------------------

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
    * Expanded Form of Codes:
    * Codes of length 02 bits:
    *  00 = 00                            (Total Len =  2)
    * Codes of length 03 bits:
    *  010 = 01                           (Total Len =  4)
    *  011 = 02                           (Total Len =  5)
    *  100 = 03                           (Total Len =  6)
    *  101 = 04                           (Total Len =  7)
    *  110 = 05                           (Total Len =  8)
    * Codes of length 04 bits:
    *  1110 = 06                          (Total Len = 10)
    * Codes of length 05 bits:
    *  11110 = 07                         (Total Len = 12)
    * Codes of length 06 bits:
    *  111110 = 08                        (Total Len = 14)
    * Codes of length 07 bits:
    *  1111110 = 09                       (Total Len = 16)
    * Codes of length 08 bits:
    *  11111110 = 0A                      (Total Len = 18)
    * Codes of length 09 bits:
    *  111111110 = 0B                     (Total Len = 20)
    *
    *
    */
   private void printOutHuffmanCodes() {
      List<HuffmanElement> huffmanElements = null;

      huffmanElements = getHuffmanTableForElementType(MatrixElementType.Y_DC);
      printOutHuffmanElement(huffmanElements);
      huffmanElements = getHuffmanTableForElementType(MatrixElementType.Y_AC);
      printOutHuffmanElement(huffmanElements);
      huffmanElements = getHuffmanTableForElementType(MatrixElementType.CB_DC);
      printOutHuffmanElement(huffmanElements);
      huffmanElements = getHuffmanTableForElementType(MatrixElementType.CB_AC);
      printOutHuffmanElement(huffmanElements);
   }

   /**
    * Method description
    *
    *
    * @param aHuffmanElements
    */
   private void printOutHuffmanElement(List<HuffmanElement> aHuffmanElements) {
      int currentLength = 0;

      System.out.println("Expanded Form of Codes:");

      for (HuffmanElement huffmanElement : aHuffmanElements) {
         if (huffmanElement.getCodeLength() != currentLength) {
            System.out.println(String.format("Codes of length %02d bits:", huffmanElement.getCodeLength()));
            currentLength = huffmanElement.getCodeLength();
         }

         System.out.print(" ");

         for (byte bit : huffmanElement.getHuffmanCodeBits()) {
            System.out.print("" + bit);
         }

         System.out.print(" = ");
         System.out.println(String.format("%02X", huffmanElement.getValue()));
      }
   }

   /**
    * Method description
    *
    *
    *
    * @param aByteArray
    * @param aHuffmanTable
    *
    * @return
    */
   private HuffmanElement searchForBitPattern(byte[] aByteArray, List<HuffmanElement> aHuffmanTable) {
      HuffmanElement resultHuffmanElement = null;

      for (HuffmanElement huffmanElement : aHuffmanTable) {
         if (Arrays.equals(huffmanElement.getHuffmanCodeBits(), aByteArray)) {
            resultHuffmanElement = huffmanElement;

            break;
         }
      }

      return resultHuffmanElement;
   }
}
