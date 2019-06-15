package org.diplomska.stego.context;

import org.diplomska.jpeg.encoder.JpegImageBlock;
import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.stego.DataContext;
import org.diplomska.util.JpegImageEncoderUtils;
import org.diplomska.util.JpegImageUtils;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/08
 * @author         Enter your name here...
 */
public class JpegStegoEncoderContext implements DataContext {

   /** Luma color component number */
   public static final int Y_LUMA = 0;

   /** Chroma color component number */
   public static final int CB_CHROMA = 1;

   /** Chroma color component number */
   public static final int CR_CHROMA = 2;

   //~--- fields --------------------------------------------------------------

   /** Encoder image context */
   JpegImageContext jpegImageContext;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param jpegImageContext
    */
   public JpegStegoEncoderContext(JpegImageContext jpegImageContext) {
      this.jpegImageContext = jpegImageContext;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public int getImageData(int aRow, int aColumn, int aColorComponentType) {
      JpegImageBlock workingBlock     = getImageBlock(aRow, aColumn);
      int            localPixelRow    = aRow - workingBlock.getStartPixelRow();
      int            localPixelColumn = aColumn - workingBlock.getStartPixelColumn();
      int            resultImageValue = 0;

      if (aColorComponentType == Y_LUMA) {
         resultImageValue = workingBlock.getQuantizedYLumaValue(localPixelRow, localPixelColumn);
      } else if (aColorComponentType == CB_CHROMA) {
         resultImageValue = workingBlock.getQuantizedCbChromaValue(localPixelRow, localPixelColumn);
      } else if (aColorComponentType == CR_CHROMA) {
         resultImageValue = workingBlock.getQuantizedCrChromaValue(localPixelRow, localPixelColumn);
      } else {
         JpegImageUtils.throwJpegImageExeption("Not supported color component!");
      }

      return resultImageValue;
   }

   @Override
   public int getImageHeight() {
      return jpegImageContext.getImageHeight();
   }

   @Override
   public int getImageWidth() {
      return jpegImageContext.getImageWidth();
   }

   @Override
   public int getNumberOfColorComponents() {
      return 3;
   }

   //~--- set methods ---------------------------------------------------------

   @Override
   public void setImageData(int aRow, int aColumn, int aColorComponentValue, int aColorComponentType) {
      JpegImageBlock workingBlock     = getImageBlock(aRow, aColumn);
      int            localPixelRow    = aRow - workingBlock.getStartPixelRow();
      int            localPixelColumn = aColumn - workingBlock.getStartPixelColumn();
      byte           newImageValue    = (byte) aColorComponentValue;

      if (aColorComponentType == Y_LUMA) {
         workingBlock.setQuantizedYLumaValue(localPixelRow, localPixelColumn, newImageValue);
      } else if (aColorComponentType == CB_CHROMA) {
         workingBlock.setQuantizedCbChromaValue(localPixelRow, localPixelColumn, newImageValue);
      } else if (aColorComponentType == CR_CHROMA) {
         workingBlock.setQuantizedCrChromaValue(localPixelRow, localPixelColumn, newImageValue);
      } else {
         JpegImageUtils.throwJpegImageExeption("Not supported color component!");
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param row
    * @param column
    *
    * @return
    */
   private JpegImageBlock getImageBlock(int row, int column) {
      long imageCoordinates     = JpegImageEncoderUtils.getPoint(row, column);
      long blockCoordinates     = JpegImageEncoderUtils.getJpegImageBlockCoordinates(imageCoordinates);
      int  jpegImageBlockRow    = JpegImageEncoderUtils.getRow(blockCoordinates);
      int  jpegImageBlockColumn = JpegImageEncoderUtils.getColumn(blockCoordinates);

      return jpegImageContext.getJpegImageBlock(jpegImageBlockColumn, jpegImageBlockRow);
   }
}
