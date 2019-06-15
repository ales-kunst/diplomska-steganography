package org.diplomska.jpeg.huffman;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 15/06/17
 * @author         Ales Kunst
 */
public class HuffmanOutputStructure {

   /** Field description */
   private List<HuffmanElement> fLumaDcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fLumaAcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fChromaDcHuffmanElements;

   /** Field description */
   private List<HuffmanElement> fChromaAcHuffmanElements;

   /** Field description */
   private byte[] fHuffmanEncodedBitsArray;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    */
   private HuffmanOutputStructure() {}

   /**
    * Constructs ...
    *
    *
    *
    * @param aLumaDcHuffmanElements
    * @param aLumaAcHuffmanElements
    * @param aChromaDcHuffmanElements
    * @param aChromaAcHuffmanElements
    * @param aHuffmanEncodedBitsArray
    */
   public HuffmanOutputStructure(List<HuffmanElement> aLumaDcHuffmanElements,
                                 List<HuffmanElement> aLumaAcHuffmanElements,
                                 List<HuffmanElement> aChromaDcHuffmanElements,
                                 List<HuffmanElement> aChromaAcHuffmanElements, byte[] aHuffmanEncodedBitsArray) {
      this();
      fHuffmanEncodedBitsArray = aHuffmanEncodedBitsArray;
      fLumaDcHuffmanElements   = aLumaDcHuffmanElements;
      fLumaAcHuffmanElements   = aLumaAcHuffmanElements;
      fChromaDcHuffmanElements = aChromaDcHuffmanElements;
      fChromaAcHuffmanElements = aChromaAcHuffmanElements;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getChromaAcHuffmanElements() {
      return fChromaAcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getChromaDcHuffmanElements() {
      return fChromaDcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public byte[] getHuffmanEncodedBitsArray() {
      return fHuffmanEncodedBitsArray;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getLumaAcHuffmanElements() {
      return fLumaAcHuffmanElements;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public List<HuffmanElement> getLumaDcHuffmanElements() {
      return fLumaDcHuffmanElements;
   }
}
