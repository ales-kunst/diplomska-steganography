package org.diplomska.jpeg.encoder;

import org.diplomska.jpeg.huffman.HuffmanElement;
import org.diplomska.jpeg.util.JpegImageUtils;

import java.awt.image.BufferedImage;

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
   int[][] fImageColorValues;

   /** Image subsampled to jpeg image blocks */
   private JpegImageBlock[][] fJpegImageBlocks;

   /** Height of jpeg image block matrix */
   private int fJpegImageBlocksHeight;

   /** Width of jpeg image block matrix */
   private int fJpegImageBlocksWidth;

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
    * Constructor
    *
    *
    *
    * @param aImage
    */
   public JpegImageContext(BufferedImage aImage) {
      initialize(aImage.getWidth(), aImage.getHeight(), null);
      fillImageColorValues(aImage);
      splitImageIntoBlocks();
   }

   /**
    *    Constructor
    *
    *
    *    @param aImageColorValues
    */
   public JpegImageContext(int[][] aImageColorValues) {
      initialize(aImageColorValues[0].length, aImageColorValues.length, aImageColorValues);
      fImageColorValues = aImageColorValues;
      splitImageIntoBlocks();
   }

   //~--- methods -------------------------------------------------------------

   /**
    * This method should be called only when Image values are not needed anymore. This
    * method is for memory optimization purpose.
    *
    */
   public void clearImageValues() {
      fImageColorValues = null;
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
    * Get RGB color value on row and column position.
    *
    *
    * @param aRow
    * @param aColumn
    *
    * @return
    */
   public int getImageColor(int aRow, int aColumn) {
      return fImageColorValues[aRow][aColumn];
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getImageHeight() {
      return fImageColorValues.length;
   }

   /**
    * Getter method.
    *
    *
    * @return
    */
   public int getImageWidth() {
      return fImageColorValues[0].length;
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
         JpegImageUtils.throwJpegImageEncoderExeption("JpegImageBlock already exists!");
      }

      fJpegImageBlocks[aRow][aColumn] = aJpegImageBlock;

      return fJpegImageBlocks[aRow][aColumn];
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Setter method.
    *
    *
    * @param fAcChromaHuffmanTable
    */
   public void setAcChromaHuffmanTable(List<HuffmanElement> fAcChromaHuffmanTable) {
      this.fAcChromaHuffmanTable = fAcChromaHuffmanTable;
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
            int            rgbColor              = getImageColor(row, column);
            long           imageCoordinates      = JpegImageUtils.getPoint(row, column);
            long           blockCoordinates      = JpegImageUtils.getJpegImageBlockCoordinates(imageCoordinates);
            int            jpegImageBlockRow     = JpegImageUtils.getRow(blockCoordinates);
            int            jpegImageBlockColumn  = JpegImageUtils.getColumn(blockCoordinates);
            JpegImageBlock currentJpegImageBlock = getJpegImageBlock(jpegImageBlockColumn, jpegImageBlockRow);

            if (currentJpegImageBlock == null) {
               JpegImageBlock newJpegImageBlock = new JpegImageBlock(jpegImageBlockRow, jpegImageBlockColumn);

               newJpegImageBlock.addRgbColor(imageCoordinates, rgbColor);
               putJpegImageBlock(jpegImageBlockRow, jpegImageBlockColumn, newJpegImageBlock);
            } else {
               currentJpegImageBlock.addRgbColor(imageCoordinates, rgbColor);
            }
         }
      }
   }

   /**
    * Fill image color values.
    *
    *
    * @param aBufferedImage
    */
   private void fillImageColorValues(BufferedImage aBufferedImage) {
      for (int row = 0; row < aBufferedImage.getHeight(); row++) {
         for (int column = 0; column < aBufferedImage.getWidth(); column++) {
            fImageColorValues[row][column] = aBufferedImage.getRGB(column, row) & 0xFFFFFF;
         }
      }
   }

   /**
    * Initialize fields.
    *
    *
    * @param aWidth
    * @param aHeight
    * @param aImageColorValues
    */
   private void initialize(int aWidth, int aHeight, int[][] aImageColorValues) {
      fImageColorValues      = (aImageColorValues != null)
                               ? aImageColorValues
                               : new int[aHeight][aWidth];
      fJpegImageBlocksWidth  = JpegImageUtils.divideWithRoundUp(aWidth, JpegImageUtils.JPEG_IMAGE_BLOCK_WIDTH);
      fJpegImageBlocksHeight = JpegImageUtils.divideWithRoundUp(aHeight, JpegImageUtils.JPEG_IMAGE_BLOCK_HEIGHT);
      fJpegImageBlocks       = new JpegImageBlock[fJpegImageBlocksHeight][fJpegImageBlocksWidth];
   }
}
