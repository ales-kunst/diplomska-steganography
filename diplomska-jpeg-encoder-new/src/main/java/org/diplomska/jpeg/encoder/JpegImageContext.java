package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.huffman.HuffmanElement;
import org.diplomska.util.JpegImageEncoderUtils;
import org.diplomska.util.JpegImageUtils;

import java.io.ByteArrayOutputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/05/03
 * @author         Ales Kunst
 */
public class JpegImageContext {

   /** Image color values */
   int[][] fImageValues;

   /** Image subsampled to jpeg image blocks */
   private JpegImageBlock[][] fJpegImageBlocks;

   /** Height of jpeg image block matrix */
   private int fJpegImageBlocksHeight;

   /** Width of jpeg image block matrix */
   private int fJpegImageBlocksWidth;

   /** Field description */
   private int fRealImageWidth;

   /** Field description */
   private int fRealImageHeight;

   /** Huffman table for Dc element in Luma component matrix */
   private List<HuffmanElement> fDcLumaHuffmanTable;

   /** Huffman table for Ac element in Luma component matrix */
   private List<HuffmanElement> fAcLumaHuffmanTable;

   /** Huffman table for Dc element in Chroma component matrix */
   private List<HuffmanElement> fDcChromaHuffmanTable;

   /** Huffman table for Dc element in Chroma component matrix */
   private List<HuffmanElement> fAcChromaHuffmanTable;

   /** Bits array of the image which has been encoded with Huffman */
   private byte[] fHuffmanEncodedImageBitsArray;

   //~--- constant enums ------------------------------------------------------

   /**
    * Huffman table types.
    *
    */
   public enum HuffmanTableType { LUMA_DC, LUMA_AC, CHROMA_DC, CHROMA_AC }

   //~--- constructors --------------------------------------------------------

   /**
    *    Constructor
    *
    *
    *    @param aImageColorValues
    * @param aRealImageWidth
    * @param aRealImageHeight
    */
   public JpegImageContext(int[][] aImageColorValues, int aRealImageWidth, int aRealImageHeight) {
      initializeJpegBlocks(aImageColorValues[0].length, aImageColorValues.length);
      fImageValues     = aImageColorValues;
      fRealImageWidth  = aRealImageWidth;
      fRealImageHeight = aRealImageHeight;
      splitImageIntoBlocks();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * This method should be called only when Image values are not needed anymore. This
    * method is for memory optimization purpose.
    *
    */
   public void clearImageValues() {
      fImageValues = null;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getAcChromaHuffmanTable() {
      return fAcChromaHuffmanTable;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getAcLumaHuffmanTable() {
      return fAcLumaHuffmanTable;
   }

   /**
    * Get bytes for certain code length
    *
    *
    * @param aCodeLength
    * @param aHuffmanTableType
    *
    * @return
    */
   public byte[] getBytesForCodeLength(byte aCodeLength, HuffmanTableType aHuffmanTableType) {
      ByteArrayOutputStream resultByteArray = new ByteArrayOutputStream();
      List<HuffmanElement>  huffmanTable    = null;

      switch (aHuffmanTableType) {
      case LUMA_DC :
         huffmanTable = fDcLumaHuffmanTable;

         break;

      case LUMA_AC :
         huffmanTable = fAcLumaHuffmanTable;

         break;

      case CHROMA_DC :
         huffmanTable = fDcChromaHuffmanTable;

         break;

      case CHROMA_AC :
         huffmanTable = fAcChromaHuffmanTable;

         break;
      }

      // search for huffman table entries with the corresponding length
      for (HuffmanElement huffmanElement : huffmanTable) {
         if (huffmanElement.getCodeLength() == aCodeLength) {
            resultByteArray.write(huffmanElement.getValue().byteValue());
         }
      }

      return resultByteArray.toByteArray();
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getDcChromaHuffmanTable() {
      return fDcChromaHuffmanTable;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getDcLumaHuffmanTable() {
      return fDcLumaHuffmanTable;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public byte[] getHuffmanEncodedImageBitsArray() {
      return fHuffmanEncodedImageBitsArray;
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getImageHeight() {
      return fImageValues.length;
   }

   /**
    * Get RGB color value on row and column position.
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public int getImageValue(int aRow, int aColumn) {
      return fImageValues[aRow][aColumn];
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getImageWidth() {
      return fImageValues[0].length;
   }

   /**
    * Getter method.
    *
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public JpegImageBlock getJpegImageBlock(int aColumn, int aRow) {
      return fJpegImageBlocks[aRow][aColumn];
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public JpegImageBlock[][] getJpegImageBlocks() {
      return fJpegImageBlocks;
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getJpegImageBlocksHeight() {
      return fJpegImageBlocksHeight;
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getJpegImageBlocksWidth() {
      return fJpegImageBlocksWidth;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getRealImageHeight() {
      return fRealImageHeight;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getRealImageWidth() {
      return fRealImageWidth;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Inserts jpeg image block into the jpeg image block matrix.
    * If Jpeg image block already exists then throw exception. In case we want to
    * clear the cell (put null value) then no checking is performed.
    *
    *
    * @param aColumn
    * @param aRow
    * @param aJpegImageBlock
    *
    * @return
    */
   public JpegImageBlock putJpegImageBlock(int aRow, int aColumn, JpegImageBlock aJpegImageBlock) {

      // check if block already exists and do not check it if we want to clear the value
      if ((fJpegImageBlocks[aRow][aColumn] != null) && (aJpegImageBlock != null)) {
         JpegImageEncoderUtils.throwJpegImageEncoderExeption("JpegImageBlock already exists!");
      }

      fJpegImageBlocks[aRow][aColumn] = aJpegImageBlock;

      return fJpegImageBlocks[aRow][aColumn];
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Setter method.
    *
    *
    *
    * @param aAcChromaHuffmanTable
    */
   public void setAcChromaHuffmanTable(List<HuffmanElement> aAcChromaHuffmanTable) {
      this.fAcChromaHuffmanTable = aAcChromaHuffmanTable;
   }

   /**
    * Setter method.
    *
    *
    * @param fAcLumaHuffmanTable
    */
   public void setAcLumaHuffmanTable(List<HuffmanElement> fAcLumaHuffmanTable) {
      this.fAcLumaHuffmanTable = fAcLumaHuffmanTable;
   }

   /**
    * Setter method.
    *
    *
    * @param fDcChromaHuffmanTable
    */
   public void setDcChromaHuffmanTable(List<HuffmanElement> fDcChromaHuffmanTable) {
      this.fDcChromaHuffmanTable = fDcChromaHuffmanTable;
   }

   /**
    * Setter method.
    *
    *
    * @param fDcLumaHuffmanTable
    */
   public void setDcLumaHuffmanTable(List<HuffmanElement> fDcLumaHuffmanTable) {
      this.fDcLumaHuffmanTable = fDcLumaHuffmanTable;
   }

   /**
    * Setter method.
    *
    *
    * @param fHuffmanEncodedImageBitsArray
    */
   public void setHuffmanEncodedImageBitsArray(byte[] fHuffmanEncodedImageBitsArray) {
      this.fHuffmanEncodedImageBitsArray = fHuffmanEncodedImageBitsArray;
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setImageValue(int aRow, int aColumn, int aValue) {
      fImageValues[aRow][aColumn] = aValue;
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedCbChromaValue(int aRow, int aColumn, byte aValue) {
      int imageValue = fImageValues[aRow][aColumn];

      // Clear value
      imageValue = imageValue & 0xFF00FF;

      // Set value
      imageValue                  = imageValue | ((aValue << 8) & 0x00FF00);
      fImageValues[aRow][aColumn] = imageValue;
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedCrChromaValue(int aRow, int aColumn, byte aValue) {
      int imageValue = fImageValues[aRow][aColumn];

      // Clear value
      imageValue = imageValue & 0xFFFF00;

      // Set value
      imageValue                  = imageValue | (aValue & 0x0000FF);
      fImageValues[aRow][aColumn] = imageValue;
   }

   /**
    * Method description
    *
    *
    * @param aRow
    * @param aColumn
    * @param aValue
    */
   public void setQuantizedYLumaValue(int aRow, int aColumn, byte aValue) {
      int imageValue = fImageValues[aRow][aColumn];

      // Clear value
      imageValue = imageValue & 0x00FFFF;

      // Set value
      imageValue                  = imageValue | ((aValue << 16) & 0xFF0000);
      fImageValues[aRow][aColumn] = imageValue;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Split image into jpeg image blocks - MCU (minimal code unit).
    *
    */
   public void splitImageIntoBlocks() {
      int imageHeight = getImageHeight();
      int imageWidth  = getImageWidth();

      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            long           imageCoordinates      = JpegImageEncoderUtils.getPoint(row, column);
            long           blockCoordinates      = JpegImageEncoderUtils.getJpegImageBlockCoordinates(imageCoordinates);
            int            jpegImageBlockRow     = JpegImageEncoderUtils.getRow(blockCoordinates);
            int            jpegImageBlockColumn  = JpegImageEncoderUtils.getColumn(blockCoordinates);
            JpegImageBlock currentJpegImageBlock = getJpegImageBlock(jpegImageBlockColumn, jpegImageBlockRow);

            if (currentJpegImageBlock == null) {
               JpegImageBlock newJpegImageBlock = new JpegImageBlock(jpegImageBlockRow, jpegImageBlockColumn, this);

               putJpegImageBlock(jpegImageBlockRow, jpegImageBlockColumn, newJpegImageBlock);
            }
         }
      }
   }

   /**
    * Initialize fields.
    *
    *
    * @param aWidth
    * @param aHeight
    */
   private void initializeJpegBlocks(int aWidth, int aHeight) {
      fJpegImageBlocksWidth  = JpegImageUtils.divideWithRoundUp(aWidth, JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_WIDTH);
      fJpegImageBlocksHeight = JpegImageUtils.divideWithRoundUp(aHeight, JpegImageEncoderUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      fJpegImageBlocks       = new JpegImageBlock[fJpegImageBlocksHeight][fJpegImageBlocksWidth];
   }
}
