package org.diplomska.stego.decode;

import org.diplomska.stego.DataContext;
import org.diplomska.stego.StegoDecodingAlgorithm;
import org.diplomska.util.ArrayPermutator;
import org.diplomska.util.BitBuffer;
import org.diplomska.util.JpegImageUtils;
import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.JpegImageConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/15
 * @author         Enter your name here...
 */
public class JpegF5Decode implements StegoDecodingAlgorithm, JpegImageConstants {

   /** LOGGER */
   private static final Logger LOGGER = LoggerFactory.getLogger(JpegF5Decode.class);

   //~--- fields --------------------------------------------------------------

   /** Field description */

   // private static final int HUMMING_MATRIX_LEVEL_SIZE = Integer.BYTES;

   /** Field description */
   // private static final int HEADER_LENGTH_IN_SIZE = SIZE_LENGTH_IN_BYTES + HUMMING_MATRIX_LEVEL_SIZE;

   /** Field description */
   // private static final int HEADER_LENGTH_IN_SIZE_IN_BITS = HEADER_LENGTH_IN_SIZE * Byte.SIZE;

   /** Field description */
   // private static final String OUT_FILE = "D:/Temp/tmp/images/dec_log.txt";

   /** Field description */
   byte[] password;

   /** Field description */
   private int[] permutatedCoeffArray;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aPassword
    */
   public JpegF5Decode(byte[] aPassword) {
      password = aPassword;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public void decode(DataContext aDataContext, OutputStream aOutputStream) throws Exception {
      BitBuffer header  = new BitBuffer(BitNumbering.MSB_FIRST);
      BitBuffer message = new BitBuffer(BitNumbering.MSB_FIRST);

      permutatedCoeffArray = createPermutatedArray(aDataContext);

      // writePermutationArray();
      int     arrayIndex      = 0;
      boolean coeffAvailable  = (arrayIndex < permutatedCoeffArray.length);
      int     coeffIndex      = -1;
      int     coeffValue      = 0;
      int     headerBitsRead  = 0;
      int     messageBitsRead = 0;

      // writeCoefficientsIntoFile(aDataContext, "D:/temp/tmp/Diplomska/Stego_Program/coefficients_decode_before.log");
      // Gets size of message. It is 32 bits long
      while (headerBitsRead < SIZE_LENGTH_IN_BITS) {
         coeffIndex = permutatedCoeffArray[arrayIndex];
         coeffValue = getCoefficientValue(coeffIndex, aDataContext);

         if ((coeffValue != 0) && !isDc(coeffIndex)) {
            int headerBit = getBitValue((byte) coeffValue);

            header.put(headerBit);
            headerBitsRead++;
         }

         arrayIndex++;
      }

      ByteBuffer headerBuffer = ByteBuffer.wrap(header.toByteArray()).order(ByteOrder.BIG_ENDIAN);
      int        messageSize  = headerBuffer.getInt() - SIZE_LENGTH_IN_BITS;

      // new File(OUT_FILE).delete();
      // new File(OUT_FILE).createNewFile();
      while (coeffAvailable && (messageBitsRead < messageSize)) {
         coeffIndex = permutatedCoeffArray[arrayIndex];
         coeffValue = getCoefficientValue(coeffIndex, aDataContext);

         if ((coeffValue != 0) && !isDc(coeffIndex)) {
            int bit = getBitValue((byte) coeffValue);

            message.put(bit);
            messageBitsRead++;
         }

         arrayIndex++;
         coeffAvailable = (arrayIndex < permutatedCoeffArray.length);
      }

      aOutputStream.write(message.toByteArray());
   }    // decode

   /**
    * Method description
    *
    *
    * @param aDataContext
    *
    * @return
    */
   private int[] createPermutatedArray(DataContext aDataContext) {
      int             imageWidth        = aDataContext.getImageWidth();
      int             imageHeight       = aDataContext.getImageHeight();
      int             coefficientNumber = imageHeight * imageWidth * 3;
      ArrayPermutator arrayPermutator   = new ArrayPermutator(password);

      return arrayPermutator.createArray(coefficientNumber);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aCoefficientValue
    *
    * @return
    */
   private byte getBitValue(byte aCoefficientValue) {
      byte resultBit = 0;

      if (aCoefficientValue > 0) {
         resultBit = (byte) (aCoefficientValue & 0x01);
      } else {
         resultBit = (byte) ((aCoefficientValue + 1) & 0x01);
      }

      return resultBit;
   }

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
      int imageWidth     = aDataContext.getImageWidth();
      int realCoeffIndex = aCoefficientIndex / 3;
      int coeffType      = aCoefficientIndex % 3;
      int coeffRow       = realCoeffIndex / imageWidth;
      int coeffColumn    = realCoeffIndex % imageWidth;

      return aDataContext.getImageData(coeffRow, coeffColumn, coeffType);
   }

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

   /**
    * Method description
    *
    */
   private void writePermutationArray() {
      StringBuffer sb = new StringBuffer();

      for (int elem : permutatedCoeffArray) {
         sb.append(elem + "\n");
      }

      writeAppend("D:/Temp/tmp/images/de_pa_log.txt", sb.toString(), StandardOpenOption.CREATE);
   }
}
