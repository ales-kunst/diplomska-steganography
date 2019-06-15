package org.diplomska.stego.encode;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoEncoderStatistics;
import org.diplomska.stego.StegoEncodingAlgorithm;
import org.diplomska.util.ArrayPermutator;
import org.diplomska.util.BitWalker;
import org.diplomska.util.HummingCodesUtil;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.JpegImageConstants;

import org.jblas.DoubleMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.Map;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/14
 * @author         Enter your name here...
 */
public class JpegF5MeEncode implements StegoEncodingAlgorithm, JpegImageConstants {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegF5MeEncode.class);

   /** Field description */
   private static final int HUMMING_MATRIX_LEVEL_SIZE = Integer.BYTES;

   /** Field description */
   private static final int HEADER_LENGTH_IN_SIZE = SIZE_LENGTH_IN_BYTES + HUMMING_MATRIX_LEVEL_SIZE;

   /** Field description */
   private static final int HEADER_LENGTH_IN_SIZE_IN_BITS = HEADER_LENGTH_IN_SIZE * Byte.SIZE;

   //~--- fields --------------------------------------------------------------

   /** Field description */

   // private static final String OUT_FILE = "D:/Temp/tmp/images/enc_log.txt";

   /** Field description */
   int bitPosition;

   /** Field description */
   JpegImageContext jpegImageContext;

   /** Field description */
   private int numOfBitsInMessage;

   /** Field description */
   private StegoEncoderStatistics stegoEncoderStatistics;

   /** Field description */
   private int matrixLevelK;

   /** Field description */
   private int[] permutatedCoeffArray;

   /** Field description */
   private byte[] password;

   /** Field description */
   private DoubleMatrix hummingMatrix;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aJpegImageContext
    * @param aPassword
    */
   public JpegF5MeEncode(JpegImageContext aJpegImageContext, byte[] aPassword) {
      jpegImageContext       = aJpegImageContext;
      stegoEncoderStatistics = new StegoEncoderStatistics(aJpegImageContext);
      password               = aPassword;
      initializeEstimatedCapacity(stegoEncoderStatistics);
      initializePermutatedArray();
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void encode(DataContext aDataContext, InputStream aMessage) throws Exception {
      int                  capacity           = stegoEncoderStatistics.getEstimatedCapacityInBits();
      byte[]               sizeBytes          = new byte[SIZE_LENGTH_IN_BYTES];
      byte[]               matrixLevelBytes   = new byte[HUMMING_MATRIX_LEVEL_SIZE];
      byte[]               messageBytes       = prepareMessageInputStream(aMessage);
      ByteArrayInputStream messageInputStream = new ByteArrayInputStream(messageBytes);
      BitWalker            messageBitWalker   = new BitWalker(messageInputStream, BitNumbering.MSB_FIRST);

      System.arraycopy(messageBytes, 0, sizeBytes, 0, SIZE_LENGTH_IN_BYTES);
      numOfBitsInMessage = ByteBuffer.wrap(sizeBytes).order(ByteOrder.BIG_ENDIAN).getInt();
      System.arraycopy(messageBytes, SIZE_LENGTH_IN_BYTES, matrixLevelBytes, 0, HUMMING_MATRIX_LEVEL_SIZE);
      matrixLevelK  = ByteBuffer.wrap(matrixLevelBytes).order(ByteOrder.BIG_ENDIAN).getInt();
      hummingMatrix = HummingCodesUtil.createHummingMatrix(matrixLevelK);

      if (capacity < numOfBitsInMessage) {
         String message = "There is not enough bit capacity [" + capacity + "] in this image to hide data ["
                          + numOfBitsInMessage + "]!";

         JpegImageUtils.throwJpegImageExeption(message);
      }

      int     arrayIndex                = 0;
      boolean coeffAvailable            = (arrayIndex < permutatedCoeffArray.length);
      int[]   validCoefficientPositions = new int[hummingMatrix.getColumns()];
      int[]   validCoefficientValues    = new int[hummingMatrix.getColumns()];
      int[]   messageBits               = new int[matrixLevelK];
      int     validCoefficientsIndex    = 0;
      int     coeffIndex                = -1;
      int     coeffValue                = 0;
      int     elementsRead              = 1;
      boolean shrinkageHappened         = false;
      boolean messageBitsAvailable      = true;
      Byte    headerBit                 = null;
      int     headerBitsInjected        = 0;
      int     markerPosition            = arrayIndex;

      // new File(OUT_FILE).delete();
      // new File(OUT_FILE).createNewFile();
      // Encode header bits
      while (coeffAvailable && (headerBitsInjected < HEADER_LENGTH_IN_SIZE_IN_BITS)) {
         coeffIndex = permutatedCoeffArray[arrayIndex];
         coeffValue = getCoefficientValue(coeffIndex, aDataContext);

         if ((coeffValue != 0) && !isDc(coeffIndex)) {

            // If before this run shrinkage happened then do not read next byte
            if (!shrinkageHappened) {
               headerBit = messageBitWalker.getNextBit();
            }

            messageBitsAvailable = (headerBit != null);

            int newCoeffValue = coeffValue;

            if (headerBit == 1) {
               newCoeffValue = coeffValue | 0x01;
            } else {
               newCoeffValue = coeffValue & 0xFFFFFFFE;
            }

            shrinkageHappened = (newCoeffValue == 0);

            if (!shrinkageHappened) {
               headerBitsInjected++;
            }

            setCoefficientValue(coeffIndex, newCoeffValue, aDataContext);
            stegoEncoderStatistics.addTransition(coeffValue, newCoeffValue);
         }

         arrayIndex++;
         coeffAvailable = (arrayIndex < permutatedCoeffArray.length);
      }    // while

      // Set the marker to the first valid element from permutation array for the first run
      markerPosition = arrayIndex;

      // Encode message bits
      while (coeffAvailable && messageBitsAvailable) {
         coeffIndex = permutatedCoeffArray[arrayIndex];
         coeffValue = getCoefficientValue(coeffIndex, aDataContext);

         if ((coeffValue != 0) && !isDc(coeffIndex)) {
            validCoefficientPositions[validCoefficientsIndex] = coeffIndex;
            validCoefficientValues[validCoefficientsIndex]    = coeffValue;
            validCoefficientsIndex++;
         }

         if (validCoefficientsIndex == hummingMatrix.getColumns()) {
            if (!shrinkageHappened) {
               messageBitsAvailable = readMessageBits(messageBitWalker, messageBits);
            }

            if (messageBitsAvailable) {
               shrinkageHappened = calculateNewCoeffValues(validCoefficientPositions, validCoefficientValues,
                                                           messageBits, aDataContext);
            }

            validCoefficientsIndex = 0;

            if (!shrinkageHappened) {
               elementsRead = 0;

               // Set marker position the the starting element for the rewind in case of shrinkage
               markerPosition = arrayIndex + 1;
            } else {

               // We need to sub 1 because later arrayIndex is incremented by 1 which set the array index to correct
               // starting element from perm array
               arrayIndex   = markerPosition - 1;
               elementsRead = 0;
            }
         }

         elementsRead++;
         arrayIndex++;
         coeffAvailable = (arrayIndex < permutatedCoeffArray.length);
      }    // while

      if (messageBitWalker.getNextBit() != null) {
         JpegImageUtils.throwJpegImageExeption("Message could not be encoded!!!");
      }
   }    // encode

   //~--- get methods ---------------------------------------------------------

   @Override
   public StegoEncoderStatistics getStatistics() {
      return stegoEncoderStatistics;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    *
    * @param aCoefficientPositions
    * @param aCoefficientValues
    * @param aMessageBits
    * @param aDataContext
    *
    * @return
    */
   private boolean calculateNewCoeffValues(int[] aCoefficientPositions, int[] aCoefficientValues, int[] aMessageBits,
                                           DataContext aDataContext) {
      boolean resultShrinkageOccured = false;
      int     newCoeffValue          = 0;
      int[]   coefficientsLsbs       = new int[aCoefficientValues.length];

      for (int index = 0; index < aCoefficientValues.length; index++) {
         coefficientsLsbs[index] = aCoefficientValues[index] & 0x01;
      }

      DoubleMatrix hxVector         = HummingCodesUtil.getHxVector(hummingMatrix, coefficientsLsbs);
      DoubleMatrix substVector      = HummingCodesUtil.subtractMatrices(hxVector, aMessageBits);
      int          valueToChangePos = HummingCodesUtil.getIdenticalColumnNumber(hummingMatrix, substVector.toArray());

      if (valueToChangePos != -1) {
         if (coefficientsLsbs[valueToChangePos] == 1) {
            newCoeffValue = aCoefficientValues[valueToChangePos] & 0xFFFFFFFE;
         } else {
            newCoeffValue = aCoefficientValues[valueToChangePos] | 0x01;
         }

         stegoEncoderStatistics.addTransition(aCoefficientValues[valueToChangePos], newCoeffValue);
         aCoefficientValues[valueToChangePos] = newCoeffValue;
         setCoefficientValue(aCoefficientPositions[valueToChangePos], newCoeffValue, aDataContext);
         resultShrinkageOccured = (newCoeffValue == 0)
                                  ? true
                                  : false;
      }

      return resultShrinkageOccured;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aCoefficientIndex
    * @param aDataContext
    *
    * @return
    */
   private int getCoefficientValue(int aCoefficientIndex, DataContext aDataContext) {
      int imageWidth     = jpegImageContext.getImageWidth();
      int realCoeffIndex = aCoefficientIndex / 3;
      int coeffType      = aCoefficientIndex % 3;
      int coeffRow       = realCoeffIndex / imageWidth;
      int coeffColumn    = realCoeffIndex % imageWidth;

      return aDataContext.getImageData(coeffRow, coeffColumn, coeffType);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aStegoEncoderStatistics
    */
   private void initializeEstimatedCapacity(StegoEncoderStatistics aStegoEncoderStatistics) {
      int estimatedCapacity = 0;

      for (Map.Entry<Byte, Long> entry : aStegoEncoderStatistics.getCoefficientValuesStatisticsOrig().entrySet()) {
         if (entry.getKey() != 0) {
            estimatedCapacity += entry.getValue();
         }
      }

      // Estimated size in bytes
      aStegoEncoderStatistics.setEstimatedCapacityInBytes((estimatedCapacity / Byte.SIZE));
   }

   /**
    * Method description
    *
    */
   private void initializePermutatedArray() {
      int             imageWidth        = jpegImageContext.getImageWidth();
      int             imageHeight       = jpegImageContext.getImageHeight();
      int             coefficientNumber = imageHeight * imageWidth * 3;
      ArrayPermutator arrayPermutator   = new ArrayPermutator(password);

      permutatedCoeffArray = arrayPermutator.createArray(coefficientNumber);

      // writePermutationArray();
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aCoefficientIndex
    *
    * @return
    */
   private boolean isDc(int aCoefficientIndex) {
      int realCoeffIndex = aCoefficientIndex / 3;

      return (realCoeffIndex % 64) == 0;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aMessage
    *
    * @return
    * @throws IOException
    */
   private byte[] prepareMessageInputStream(InputStream aMessage) throws IOException {
      ByteArrayOutputStream outputStream     = new ByteArrayOutputStream();
      int                   wholeMessageSize = HEADER_LENGTH_IN_SIZE;
      int                   capacityInBits   = stegoEncoderStatistics.getEstimatedCapacityInBits();
      byte[]                resultArray      = null;

      // Write initial header. It is 64bit/8 bytes long.
      for (int headerIndex = 0; headerIndex < HEADER_LENGTH_IN_SIZE; headerIndex++) {
         outputStream.write(0);
      }

      // Read bytes from message
      int     byteRead = aMessage.read();
      boolean isEof    = (byteRead == -1);

      while (!isEof) {
         outputStream.write(byteRead);
         wholeMessageSize++;
         byteRead = aMessage.read();
         isEof    = (byteRead == -1);
      }

      resultArray = outputStream.toByteArray();

      // encode size of stream
      ByteBuffer sizeByteBuffer = ByteBuffer.allocate(SIZE_LENGTH_IN_BYTES)
                                            .order(ByteOrder.BIG_ENDIAN)
                                            .putInt(wholeMessageSize * Byte.SIZE);
      int        rawMessageSize        = wholeMessageSize - HEADER_LENGTH_IN_SIZE;
      int        matrixLevelSize       = HummingCodesUtil.getEmbedingRate(capacityInBits, rawMessageSize * Byte.SIZE);
      ByteBuffer matrixLevelSizeBuffer = ByteBuffer.allocate(HUMMING_MATRIX_LEVEL_SIZE)
                                                   .order(ByteOrder.BIG_ENDIAN)
                                                   .putInt(matrixLevelSize);

      System.arraycopy(sizeByteBuffer.array(), 0, resultArray, 0, SIZE_LENGTH_IN_BYTES);
      System.arraycopy(matrixLevelSizeBuffer.array(), 0, resultArray, SIZE_LENGTH_IN_BYTES, HUMMING_MATRIX_LEVEL_SIZE);

      return resultArray;
   }

   /**
    * Method description
    *
    *
    * @param aMessageBitWalker
    * @param aMessageBits
    *
    * @return
    */
   private boolean readMessageBits(BitWalker aMessageBitWalker, int[] aMessageBits) {
      int  index = 0;
      Byte bit   = aMessageBitWalker.getNextBit();

      while ((bit != null) && (index < aMessageBits.length)) {
         aMessageBits[index] = bit;
         index++;

         if (index < aMessageBits.length) {
            bit = aMessageBitWalker.getNextBit();
         }
      }

      return bit != null;
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aCoefficientIndex
    * @param aCoefficientValue
    * @param aDataContext
    */
   private void setCoefficientValue(int aCoefficientIndex, int aCoefficientValue, DataContext aDataContext) {
      int imageWidth     = aDataContext.getImageWidth();
      int realCoeffIndex = aCoefficientIndex / 3;
      int coeffType      = aCoefficientIndex % 3;
      int coeffRow       = realCoeffIndex / imageWidth;
      int coeffColumn    = realCoeffIndex % imageWidth;

      aDataContext.setImageData(coeffRow, coeffColumn, aCoefficientValue, coeffType);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param filename
    * @param message
    * @param so
    */
   private void writeAppend(String filename, String message, StandardOpenOption so) {
      try {
         Files.write(Paths.get(filename), message.getBytes(), so);
      } catch (IOException e) {
         JpegImageUtils.throwJpegImageExeption(e);
      }
   }

   /**
    * Method description
    *
    *
    * @param coefficientPositions
    * @param coefficientValues
    * @param bitsBuffer
    * @param outFilePath
    */
   private void writeArraysToFile(int[] coefficientPositions, int[] coefficientValues, int[] bitsBuffer,
                                  String outFilePath) {
      String s1 = "pos: [";

      for (int elem : coefficientPositions) {
         s1 += elem + ", ";
      }

      s1 += "]\n";
      writeAppend(outFilePath, s1, StandardOpenOption.APPEND);

      String s2 = "val: [";

      for (int elem : coefficientValues) {
         s2 += elem + ", ";
      }

      s2 += "]\n";
      writeAppend(outFilePath, s2, StandardOpenOption.APPEND);

      String s3 = "msg: [";

      for (int elem : bitsBuffer) {
         s3 += elem + ", ";
      }

      s3 += "]\n";
      writeAppend(outFilePath, s3, StandardOpenOption.APPEND);
   }
}
