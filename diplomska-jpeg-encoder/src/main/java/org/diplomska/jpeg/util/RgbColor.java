package org.diplomska.jpeg.util;

/**
 * RGB color class.
 *
 *
 * @version        1.0, 15/04/06
 * @author         Ales Kunst
 */
public class RgbColor {

   /** Blue color component */
   int fBlue;

   /** Green color component */
   int fGreen;

   /** Red color component */
   int fRed;

   /** Rgb number representation */
   int fRgbColor;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructor to create color from rgb color number.
    *
    *
    * @param aRgbColor
    */
   public RgbColor(int aRgbColor) {
      fRgbColor = aRgbColor & 0xFFFFFF;
      fRed      = RgbColor.getRed(aRgbColor);
      fGreen    = RgbColor.getGreen(aRgbColor);
      fBlue     = RgbColor.getBlue(aRgbColor);
   }

   /**
    *    Constructor to create color from red, green and blue color
    *    representation.
    *
    *
    *    @param aRed red color [0..255].
    *    @param aGreen green color [0..255].
    *    @param aBlue blue color [0..255].
    */
   public RgbColor(int aRed, int aGreen, int aBlue) {
      fRed      = aRed;
      fGreen    = aGreen;
      fBlue     = aBlue;
      fRgbColor = RgbColor.getRgbColor(aRed, aGreen, aBlue);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get blue color from rgb color.
    *
    *
    * @param aRgbColor rgb color.
    *
    * @return returns blue color [0..255].
    */
   public static final int getBlue(int aRgbColor) {
      return (aRgbColor & 0xFF);
   }

   /**
    * Get green color from rgb color.
    *
    *
    * @param aRgbColor rgb color.
    *
    * @return returns green color [0..255].
    */
   public static final int getGreen(int aRgbColor) {
      return ((aRgbColor & 0xFF00) >>> 8);
   }

   /**
    * Get red color from rgb color.
    *
    *
    * @param aRgbColor rgb color.
    *
    * @return returns red color [0..255].
    */
   public static final int getRed(int aRgbColor) {
      return ((aRgbColor & 0xFF0000) >>> 16);
   }

   /**
    * Get integer representation of rgb color.
    *
    *
    * @param aRed red color [0..255].
    * @param aGreen green color [0..255].
    * @param aBlue blue color [0..255].
    *
    * @return returns integer of rgb color.
    */
   public static final int getRgbColor(int aRed, int aGreen, int aBlue) {
      int resultRgbColor = 0;

      resultRgbColor = resultRgbColor | (aRed << 16) | (aGreen << 8) | aBlue;

      return resultRgbColor;
   }

   /**
    * Get blue color [0..255].
    *
    *
    * @return return blue color.
    */
   public int getBlue() {
      return fBlue;
   }

   /**
    * Get green color [0..255].
    *
    *
    * @return return green color.
    */
   public int getGreen() {
      return fGreen;
   }

   /**
    * Get red color [0..255].
    *
    *
    * @return return red color.
    */
   public int getRed() {
      return fRed;
   }

   /**
    * Get RGB color representation.
    *
    *
    * @return returns RGB color representation.
    */
   public int getRgbColor() {
      return fRgbColor;
   }
}
