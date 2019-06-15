package org.diplomska.util;

import org.diplomska.util.structs.BitNumbering;
import org.diplomska.util.structs.Type;

import java.io.ByteArrayOutputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/11/26
 * @author         Ales Kunst
 */
public class BitBuffer {

   /** Bit buffer */
   private byte[] bitBuffer;

   /** byte contents of previousl added bits */
   private ByteArrayOutputStream contents;

   /** Direction of bit numbering */
   private BitNumbering bitNumbering;

   /** Number of bits in bit buffer. */
   private int bitBufferSize;

   //~--- constructors --------------------------------------------------------

   /**
    * Private constructor.
    *
    */
   private BitBuffer() {
      initialize();
   }

   /**
    * Default constructor.
    *
    *
    * @param bitNumbering
    */
   public BitBuffer(BitNumbering bitNumbering) {
      this();
      this.bitNumbering = bitNumbering;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Put bit into the buffer.
    *
    *
    * @param bit
    *
    * @return
    */
   public BitBuffer put(int bit) {
      bitBufferSize++;

      int bitPosition = bitBufferSize - 1;

      bitBuffer[bitPosition] = convertToBit(bit);

      if (bitBufferSize == 8) {
         Long value = BitUtils.convertBitsToUnsignedNumber(bitBuffer, bitNumbering, Type.BYTE);

         contents.write(value.byteValue());
         resetBitBuffer();
      }

      return this;
   }

   /**
    * Put bits into the buffer.
    *
    *
    * @param bits
    *
    * @return
    */
   public BitBuffer put(int[] bits) {
      for (int bitIndex = 0; bitIndex < bits.length; bitIndex++) {
         put(bits[bitIndex]);
      }

      return this;
   }

   /**
    * Put bits into the buffer.
    *
    *
    * @param bits
    *
    * @return
    */
   public BitBuffer put(byte[] bits) {
      for (int bitIndex = 0; bitIndex < bits.length; bitIndex++) {
         put(bits[bitIndex]);
      }

      return this;
   }

   /**
    * Return byte array representation of all added bits.
    *
    *
    * @return
    */
   public byte[] toByteArray() {
      int    byteArraySize = (bitBufferSize > 0)
                             ? contents.size() + 1
                             : contents.size();
      byte   bufferValue   = BitUtils.convertBitsToNumber(bitBuffer, bitNumbering, Type.BYTE).byteValue();
      byte[] returnBytes   = new byte[byteArraySize];

      System.arraycopy(contents.toByteArray(), 0, returnBytes, 0, contents.size());

      if (bitBufferSize > 0) {
         returnBytes[byteArraySize - 1] = bufferValue;
      }

      return returnBytes;
   }

   /**
    * Reset this instance.
    *
    */
   protected void reset() {
      initialize();
   }

   /**
    * Method description
    *
    *
    * @param bit
    *
    * @return
    */
   private byte convertToBit(int bit) {
      return (bit == 0)
             ? (byte) 0
             : (byte) 1;
   }

   /**
    * Method description
    *
    */
   private void initialize() {
      bitBuffer     = new byte[Type.BYTE.getTypeSize()];
      contents      = new ByteArrayOutputStream();
      bitBufferSize = 0;
   }

   /**
    * Reset buffer and size.
    *
    */
   private void resetBitBuffer() {
      for (int index = 0; index < bitBuffer.length; index++) {
         bitBuffer[index] = 0;
      }

      bitBufferSize = 0;
   }
}
