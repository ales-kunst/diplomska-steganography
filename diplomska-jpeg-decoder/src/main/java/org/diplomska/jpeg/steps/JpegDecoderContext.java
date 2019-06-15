package org.diplomska.jpeg.steps;

import org.diplomska.jpeg.decoder.ReadJpegInJfif;
import org.diplomska.jpeg.jfif.JfifHuffmanTableSegment;
import org.diplomska.jpeg.jfif.JfifQuntizationTableSegment;
import org.diplomska.jpeg.jfif.JfifSegment;
import org.diplomska.jpeg.jfif.JfifSegmentFactory;
import org.diplomska.jpeg.jfif.JfifSofSegment;
import org.diplomska.jpeg.jfif.JfifSosSegment;
import org.diplomska.jpeg.jfif.struct.HuffmanTable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 26.nov.2015
 * @author         Ales Kunst
 */
public class JpegDecoderContext {

   /** Result map */
   private Map<Class<? extends JpegDecoderStep>, Object> resultMap;

   /** Huffman table segments */
   private Map<HuffmanTableType, JfifHuffmanTableSegment> huffmanTableSegments;

   /** Field description */
   private JfifQuntizationTableSegment chromaQuantizationTableSegment;

   /** Field description */
   private JfifQuntizationTableSegment lumaQuantizationTableSegment;

   /** SOF Segment. */
   private JfifSofSegment jfifSofSegment;

   /** Sos segment */
   private JfifSosSegment jfifSosSegment;

   /** Image data */
   private byte[] imageData;

   //~--- constant enums ------------------------------------------------------

   /**
    * Enum Huffman table type
    *
    */
   public enum HuffmanTableType { DC_LUMA, AC_LUMA, DC_CHROMA, AC_CHROMA; }

   //~--- constructors --------------------------------------------------------

   /**
    * Default construcotor.
    *
    *
    * @param readJpegInJfif
    */
   public JpegDecoderContext(ReadJpegInJfif readJpegInJfif) {
      huffmanTableSegments = new HashMap<HuffmanTableType, JfifHuffmanTableSegment>();
      resultMap            = new LinkedHashMap<Class<? extends JpegDecoderStep>, Object>();
      initializeSegments(readJpegInJfif);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param clazz
    * @param value
    *
    * @return
    */
   public Object addResult(Class<? extends JpegDecoderStep> clazz, Object value) {
      return resultMap.put(clazz, value);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Getter.
    *
    *
    * @return
    */
   public JfifQuntizationTableSegment getChromaQuantizationTableSegment() {
      return chromaQuantizationTableSegment;
   }

   /**
    * Method description
    *
    *
    * @param tableType
    *
    * @return
    */
   public HuffmanTable getHuffmanTable(HuffmanTableType tableType) {
      return huffmanTableSegments.get(tableType).getHuffmanTable();
   }

   /**
    * Method description
    *
    *
    * @param huffmanTableType
    *
    * @return
    */
   public JfifHuffmanTableSegment getHuffmanTableSegments(HuffmanTableType huffmanTableType) {
      return huffmanTableSegments.get(huffmanTableType);
   }

   /**
    * Getter.
    *
    *
    * @return
    */
   public byte[] getImageData() {
      return imageData;
   }

   /**
    * Getter.
    *
    *
    * @return
    */
   public InputStream getImageDataAsStream() {
      return new ByteArrayInputStream(getImageData());
   }

   /**
    * Get start of frame segment.
    *
    *
    * @return
    */
   public JfifSofSegment getJfifSofSegment() {
      return jfifSofSegment;
   }

   /**
    * Getter.
    *
    *
    * @return
    */
   public JfifQuntizationTableSegment getLumaQuantizationTableSegment() {
      return lumaQuantizationTableSegment;
   }

   /**
    * Get result.
    *
    *
    * @param key
    *
    * @return
    */
   public List<Object> getResult(Class<? extends JpegDecoderStep> key) {
      List<Object> returnList = new ArrayList<Object>();

      for (Map.Entry<Class<? extends JpegDecoderStep>, Object> entry : resultMap.entrySet()) {
         if (key.isAssignableFrom(entry.getKey())) {
            returnList.add(entry.getValue());
         }
      }

      return returnList;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Initialization of class.
    *
    *
    * @param readJpegInJfif
    */
   private void initializeSegments(ReadJpegInJfif readJpegInJfif) {
      jfifSofSegment = (JfifSofSegment) readJpegInJfif.getJfifSegments(JfifSegmentFactory.SOF_MARKER).get(0);

      for (JfifSegment jfifSegment : readJpegInJfif.getJfifSegments(JfifSegmentFactory.DHT_MARKER)) {
         JfifHuffmanTableSegment currjfifSegment = (JfifHuffmanTableSegment) jfifSegment;

         if (currjfifSegment.isDcTypeClass() && currjfifSegment.isLumaDestination()) {
            huffmanTableSegments.put(HuffmanTableType.DC_LUMA, currjfifSegment);
         } else if (currjfifSegment.isAcTypeClass() && currjfifSegment.isLumaDestination()) {
            huffmanTableSegments.put(HuffmanTableType.AC_LUMA, currjfifSegment);
         } else if (currjfifSegment.isDcTypeClass() && currjfifSegment.isChromaDestination()) {
            huffmanTableSegments.put(HuffmanTableType.DC_CHROMA, currjfifSegment);
         } else if (currjfifSegment.isAcTypeClass() && currjfifSegment.isChromaDestination()) {
            huffmanTableSegments.put(HuffmanTableType.AC_CHROMA, currjfifSegment);
         }
      }

      for (JfifSegment jfifSegment : readJpegInJfif.getJfifSegments(JfifSegmentFactory.DQT_MARKER)) {
         JfifQuntizationTableSegment currJfifSegment = (JfifQuntizationTableSegment) jfifSegment;

         if (currJfifSegment.isForChrominance()) {
            chromaQuantizationTableSegment = currJfifSegment;
         } else if (currJfifSegment.isForLuminance()) {
            lumaQuantizationTableSegment = currJfifSegment;
         }
      }

      jfifSosSegment = (JfifSosSegment) readJpegInJfif.getJfifSegments(JfifSegmentFactory.SOS_MARKER).get(0);
      imageData      = jfifSosSegment.getImageData();
   }
}
