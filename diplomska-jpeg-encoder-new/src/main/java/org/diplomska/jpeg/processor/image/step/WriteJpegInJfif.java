package org.diplomska.jpeg.processor.image.step;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.jpeg.processor.image.JpegImageProcessorStep;
import org.diplomska.util.JpegImageEncoderUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/07/17
 * @author         Ales Kunst
 */
public class WriteJpegInJfif implements JpegImageProcessorStep {

   /** Start of image marker */
   private static final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };

   /** JFIF marker */
   private static final byte[] JFIF_MARKER = { (byte) 0xFF, (byte) 0xE0 };

   /** JFIF identifier */
   private static final byte[] JFIF_IDENTIFIER = { (byte) 0x4A, (byte) 0x46, (byte) 0x49, (byte) 0x46, (byte) 0x00 };

   /** JFIF version is 1.2 */
   private static final byte[] JFIF_VERSION = { (byte) 0x01, (byte) 0x02 };

   /** Define quantum tables marker */
   private static final byte[] DQT_MARKER = { (byte) 0xFF, (byte) 0xDB };

   /** Define Huffman tables marker */
   private static final byte[] DHT_MARKER = { (byte) 0xFF, (byte) 0xC4 };

   /** Start of frame marker */
   private static final byte[] SOF_MARKER = { (byte) 0xFF, (byte) 0xC0 };

   /** Start of scan marker */
   private static final byte[] SOS_MARKER = { (byte) 0xFF, (byte) 0xDA };

   /** End of image marker */
   private static final byte[] EOI_MARKER = { (byte) 0xFF, (byte) 0xD9 };

   //~--- fields --------------------------------------------------------------

   /** Otuput stream on which we write */
   private OutputStream fOutStream;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private WriteJpegInJfif() {}

   /**
    * Constructs ...
    *
    *
    * @param aOutStream
    */
   public WriteJpegInJfif(OutputStream aOutStream) {
      this();
      fOutStream = aOutStream;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aJpegImageContext
    */
   @Override
   public void execute(JpegImageContext aJpegImageContext) {
      try {
         writeJfifHeader(fOutStream);
         writeLumaQuantizationTables(fOutStream, aJpegImageContext);
         writeChromaQuantizationTables(fOutStream, aJpegImageContext);
         writeStartOfFrame(fOutStream, aJpegImageContext);
         writeHuffmanTableForLumaDC(fOutStream, aJpegImageContext);
         writeHuffmanTableForLumaAC(fOutStream, aJpegImageContext);
         writeHuffmanTableForChromaDC(fOutStream, aJpegImageContext);
         writeHuffmanTableForChromaAC(fOutStream, aJpegImageContext);
         writeStartOfScan(fOutStream, aJpegImageContext);
         writeHuffmanEncodedBytes(fOutStream, aJpegImageContext);
         fOutStream.write(EOI_MARKER);
      } catch (IOException ioe) {
         JpegImageEncoderUtils.throwJpegImageEncoderExeption(ioe.getMessage());
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    *       Get output stream.
    *
    *
    *       @return
    */
   public OutputStream getOutStream() {
      return fOutStream;
   }

   /**
    * Method description
    *
    *
    * @param aLength
    *
    * @return
    */
   protected byte[] getLengthBytes(int aLength) {
      byte[] resultBytes = new byte[2];

      resultBytes[0] = (byte) ((aLength >> 8) & 0xFF);    // divide by 256
      resultBytes[1] = (byte) (aLength & 0xFF);           // calculate rest

      return resultBytes;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Write luma quantization table.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    * @throws IOException
    */
   protected void writeChromaQuantizationTables(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      ByteArrayOutputStream chromaZigZagQuantizationMatrix = new ByteArrayOutputStream();

      aOutStream.write(DQT_MARKER);
      chromaZigZagQuantizationMatrix.write(0x01);

      for (int index = 0; index < JpegImageEncoderUtils.ZIG_ZAG_COORDINATES.length; index++) {
         long coordinates             = JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[index];
         int  row                     = JpegImageEncoderUtils.getRow(coordinates);
         int  column                  = JpegImageEncoderUtils.getColumn(coordinates);
         byte chromaQuantizationValue = (byte) CHROMINANCE_QUANTIZATION_MATRIX_RAW[row][column];

         chromaZigZagQuantizationMatrix.write(chromaQuantizationValue);
      }

      aOutStream.write(getLengthBytes(chromaZigZagQuantizationMatrix.size() + 2));    // length (2-bytes)
      aOutStream.write(chromaZigZagQuantizationMatrix.toByteArray());
   }

   /**
    * Write Huffman encoded bytes.
    *
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    * @throws IOException
    *
    */
   protected void writeHuffmanEncodedBytes(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      byte[] outputByteArray =
         JpegImageEncoderUtils.convertBitsToBytes(aJpegImageContext.getHuffmanEncodedImageBitsArray());
      ByteArrayOutputStream bytesToBeWritten = new ByteArrayOutputStream();

      for (byte currentByte : outputByteArray) {
         bytesToBeWritten.write(currentByte);

         if (currentByte == -1) {
            bytesToBeWritten.write(0);    // write stuff byte
         }
      }

      aOutStream.write(bytesToBeWritten.toByteArray());
   }

   /**
    * Write Huffman table for AC coefficients in chroma component.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    *
    * @throws IOException
    */
   protected void writeHuffmanTableForChromaAC(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      aOutStream.write(DHT_MARKER);

      byte[] huffmanTableBytes = getHuffmanTable(aJpegImageContext, JpegImageContext.HuffmanTableType.CHROMA_AC);
      int    length            = 2 + 1 + huffmanTableBytes.length;

      aOutStream.write(getLengthBytes(length));
      aOutStream.write(0x11);    // index (1-byte). If index < 15 then it is Huffman for DC else it is for AC coefficients.
      aOutStream.write(huffmanTableBytes);
   }

   /**
    * Write Huffman table for DC coefficient in chroma component.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    *
    * @throws IOException
    */
   protected void writeHuffmanTableForChromaDC(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      aOutStream.write(DHT_MARKER);

      byte[] huffmanTableBytes = getHuffmanTable(aJpegImageContext, JpegImageContext.HuffmanTableType.CHROMA_DC);
      int    length            = 2 + 1 + huffmanTableBytes.length;

      aOutStream.write(getLengthBytes(length));
      aOutStream.write(0x01);    // index (1-byte). If index < 15 then it is Huffman for DC else it is for AC coefficients.
      aOutStream.write(huffmanTableBytes);
   }

   /**
    * Write Huffman table for AC coefficients in luma component.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    *
    * @throws IOException
    */
   protected void writeHuffmanTableForLumaAC(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      aOutStream.write(DHT_MARKER);

      byte[] huffmanTableBytes = getHuffmanTable(aJpegImageContext, JpegImageContext.HuffmanTableType.LUMA_AC);
      int    length            = 2 + 1 + huffmanTableBytes.length;

      aOutStream.write(getLengthBytes(length));    // length (2-bytes)
      aOutStream.write(0x10);    // index (1-byte). If index < 15 then it is Huffman for DC else it is for AC coefficients.
      aOutStream.write(huffmanTableBytes);
   }

   /**
    * Write Huffman table for DC coefficient in luma component.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    *
    * @throws IOException
    */
   protected void writeHuffmanTableForLumaDC(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      aOutStream.write(DHT_MARKER);

      byte[] huffmanTableBytes = getHuffmanTable(aJpegImageContext, JpegImageContext.HuffmanTableType.LUMA_DC);
      int    length            = 2 + 1 + huffmanTableBytes.length;

      aOutStream.write(getLengthBytes(length));
      aOutStream.write(0x00);    // index (1-byte). If index < 15 then it is Huffman for DC else it is for AC coefficients.
      aOutStream.write(huffmanTableBytes);
   }

   /**
    * Write JFIF header.
    *
    *
    * @param aOutStream
    * @throws IOException
    */
   protected void writeJfifHeader(OutputStream aOutStream) throws IOException {
      aOutStream.write(SOI_MARKER);                   // start of image marker (2-bytes)
      aOutStream.write(JFIF_MARKER);                  // jfif marker (2-bytes)
      aOutStream.write(getLengthBytes(16));           // length (2-bytes)
      aOutStream.write(JFIF_IDENTIFIER);              // jfif identifier (5-bytes)
      aOutStream.write(JFIF_VERSION);                 // jfif version (2-bytes)
      aOutStream.write(0x00);                         // units (1-byte)
      aOutStream.write(new byte[] { 0x00, 0x00 });    // X density (2-bytes)
      aOutStream.write(new byte[] { 0x00, 0x00 });    // Y density (2-bytes)

      // no thumbnail
      aOutStream.write(0x00);    // X thumbnail (1-byte)
      aOutStream.write(0x00);    // Y thumbnail (1-byte)
   }

   /**
    * Write luma quantization table.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    * @throws IOException
    */
   protected void writeLumaQuantizationTables(OutputStream aOutStream, JpegImageContext aJpegImageContext)
           throws IOException {
      ByteArrayOutputStream lumaZigZagQuantizationMatrix = new ByteArrayOutputStream();

      aOutStream.write(DQT_MARKER);
      lumaZigZagQuantizationMatrix.write(0x00);

      for (int index = 0; index < JpegImageEncoderUtils.ZIG_ZAG_COORDINATES.length; index++) {
         long coordinates           = JpegImageEncoderUtils.ZIG_ZAG_COORDINATES[index];
         int  row                   = JpegImageEncoderUtils.getRow(coordinates);
         int  column                = JpegImageEncoderUtils.getColumn(coordinates);
         byte lumaQuantizationValue = (byte) LUMINANCE_QUANTIZATION_MATRIX_RAW[row][column];

         lumaZigZagQuantizationMatrix.write(lumaQuantizationValue);
      }

      aOutStream.write(getLengthBytes(lumaZigZagQuantizationMatrix.size() + 2));    // length (2-bytes)
      aOutStream.write(lumaZigZagQuantizationMatrix.toByteArray());
   }

   /**
    * Write start of frame.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    * @throws IOException
    */
   protected void writeStartOfFrame(OutputStream aOutStream, JpegImageContext aJpegImageContext) throws IOException {
      aOutStream.write(SOF_MARKER);
      aOutStream.write(getLengthBytes(17));
      aOutStream.write(0x08);    // precision (1-byte) 8 bits

      // TODO Kunst: This line is wrong because I should take the actual image width
      int imageWidth = aJpegImageContext.getJpegImageBlocksWidth() * 8;

      // int imageWidth = aJpegImageContext.getImageWidth();
      // TODO Kunst: This line is wrong because I should take the actual image width
      int imageHeight = aJpegImageContext.getJpegImageBlocksHeight() * 8;

      // int imageHeight = aJpegImageContext.getImageHeight();
      aOutStream.write(((imageHeight >> 8) & 0xFF));    // get the first byte of image height (1-byte)
      aOutStream.write(imageHeight & 0xFF);             // get the second byte of image height (1-byte)
      aOutStream.write(((imageWidth >> 8) & 0xFF));     // get the first byte of image width (1-byte)
      aOutStream.write(imageWidth & 0xFF);              // get the second byte of image width (1-byte)
      aOutStream.write(0x03);                           // number of components (1-byte). 3 components YCbCr.
      aOutStream.write(new byte[] { 0x01, 0x11, 0x00 });    // first component info (3-bytes) Y H and V are 1 and 1, luma qunatization table
      aOutStream.write(new byte[] { 0x02, 0x11, 0x01 });    // second component info (3-bytes) Cb H and V are 1 and 1, chroma qunatization table
      aOutStream.write(new byte[] { 0x03, 0x11, 0x01 });    // third component info (3-bytes) Cr H and V are 1 and 1, chroma qunatization table
   }

   /**
    * Write start of scan bytes.
    *
    *
    * @param aOutStream
    * @param aJpegImageContext
    * @throws IOException
    */
   protected void writeStartOfScan(OutputStream aOutStream, JpegImageContext aJpegImageContext) throws IOException {
      aOutStream.write(SOS_MARKER);
      aOutStream.write(getLengthBytes(12));
      aOutStream.write(0x03);    // number of components (1-byte)
      aOutStream.write(0x01);    // Component ID (1-byte)

      // DC and AC table numbers (1-byte): DC # is first four bits and AC # is last four bits
      aOutStream.write(0x00);
      aOutStream.write(0x02);    // Component ID (1-byte)

      // DC and AC table numbers (1-byte): DC # is first four bits and AC # is last four bits
      aOutStream.write(0x11);
      aOutStream.write(0x03);    // Component ID (1-byte)

      // DC and AC table numbers (1-byte): DC # is first four bits and AC # is last four bits
      aOutStream.write(0x11);
      aOutStream.write(0x00);    // spectral select start at 00 (1-byte)
      aOutStream.write(0x3F);    // spectral select end at 63 (1-byte)
      aOutStream.write(0x00);    // successive approximation (1-byte)
   }

   //~--- get methods ---------------------------------------------------------

   /**
    *    Write huffman table on stream.
    *
    *
    *    @param aJpegImageContext
    *    @param aHuffmanTableType
    *
    *    @return
    *    @throws IOException
    */
   private byte[] getHuffmanTable(JpegImageContext aJpegImageContext,
                                  JpegImageContext.HuffmanTableType aHuffmanTableType)
           throws IOException {
      ByteArrayOutputStream resultHuffmanOutputStream    = new ByteArrayOutputStream();
      ByteArrayOutputStream huffmanElementCountByteArray = new ByteArrayOutputStream();
      ByteArrayOutputStream huffmanElementsByteArray     = new ByteArrayOutputStream();
      int                   huffmanTableSize             = 0;

      for (byte codeLength = 1; codeLength <= 16; codeLength++) {
         byte[] huffmanValues = aJpegImageContext.getBytesForCodeLength(codeLength, aHuffmanTableType);

         huffmanElementCountByteArray.write(huffmanValues.length);
         huffmanElementsByteArray.write(huffmanValues);
         huffmanTableSize += huffmanValues.length;
      }

      assert(huffmanTableSize == huffmanElementsByteArray.size());
      resultHuffmanOutputStream.write(huffmanElementCountByteArray.toByteArray());
      resultHuffmanOutputStream.write(huffmanElementsByteArray.toByteArray());

      return resultHuffmanOutputStream.toByteArray();
   }
}
