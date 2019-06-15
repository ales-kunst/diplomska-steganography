package org.diplomska.stego;

/**
 * Interface for data which is going to be written to.
 *
 *
 * @version        1.0, 15/11/15
 * @author         Ales Kunst
 */
public interface DataContext {

   /**
    * Gets imageData on the row and column.
    *
    *
    *
    * @param aRow
    * @param aColumn
    * @param aColorComponentType
    *
    * @return Returns data for this pixel and color component number
    */
   int getImageData(int aRow, int aColumn, int aColorComponentType);

   /**
    * Image height.
    *
    *
    * @return
    */
   int getImageHeight();

   /**
    * Image width.
    *
    *
    * @return
    */
   int getImageWidth();

   /**
    * Method should return how many color components does this image has.
    *
    *
    * @return
    */
   int getNumberOfColorComponents();

   //~--- set methods ---------------------------------------------------------

   /**
    * Sets data to the row and column.
    *
    *
    *
    * @param aRow
    * @param aColumn
    * @param colorComponentValue
    * @param aColorComponentType
    */
   void setImageData(int aRow, int aColumn, int colorComponentValue, int aColorComponentType);
}
