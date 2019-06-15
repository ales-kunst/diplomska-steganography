package org.diplomska.jpeg.jfif;

import org.diplomska.jpeg.decoder.ReadJpegInJfif;
import org.diplomska.jpeg.jfif.struct.HuffmanTable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 25.nov.2015
 * @author         Ales Kunst
 */
public class JfifHuffmanTableSegmentTest extends JfifSegmentTest {

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @Before
   public void setUp() throws Exception {}

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @throws Exception
    */
   @After
   public void tearDown() throws Exception {}

   /**
    * Method description
    *
    */
   @Test
   public void testGetCodeLengthSizes() {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.DHT_MARKER);

      Assert.assertEquals(4, jfifSegments.size());

      JfifHuffmanTableSegment huffmanTableSegment_00 = (JfifHuffmanTableSegment) jfifSegments.get(0);
      JfifHuffmanTableSegment huffmanTableSegment_01 = (JfifHuffmanTableSegment) jfifSegments.get(1);
      JfifHuffmanTableSegment huffmanTableSegment_02 = (JfifHuffmanTableSegment) jfifSegments.get(2);
      JfifHuffmanTableSegment huffmanTableSegment_03 = (JfifHuffmanTableSegment) jfifSegments.get(3);

      Assert.assertArrayEquals(new int[] { 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 },
                               huffmanTableSegment_00.getCodeLengthSizes());
      Assert.assertArrayEquals(new int[] { 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125 },
                               huffmanTableSegment_01.getCodeLengthSizes());
      Assert.assertArrayEquals(new int[] { 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 },
                               huffmanTableSegment_02.getCodeLengthSizes());
      Assert.assertArrayEquals(new int[] { 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119 },
                               huffmanTableSegment_03.getCodeLengthSizes());
   }

   /**
    * Method description
    *
    */
   @Test
   public void testGetHuffmanTable() {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.DHT_MARKER);

      Assert.assertEquals(4, jfifSegments.size());

      JfifHuffmanTableSegment huffmanTableSegment_00 = (JfifHuffmanTableSegment) jfifSegments.get(0);
      JfifHuffmanTableSegment huffmanTableSegment_01 = (JfifHuffmanTableSegment) jfifSegments.get(1);
      JfifHuffmanTableSegment huffmanTableSegment_02 = (JfifHuffmanTableSegment) jfifSegments.get(2);
      JfifHuffmanTableSegment huffmanTableSegment_03 = (JfifHuffmanTableSegment) jfifSegments.get(3);
      HuffmanTable            huffmanTable_00        = huffmanTableSegment_00.getHuffmanTable();
      HuffmanTable            huffmanTable_01        = huffmanTableSegment_01.getHuffmanTable();

      Assert.assertNull(huffmanTable_00.getCodes(1));
      Assert.assertArrayEquals(new short[] { 0 }, huffmanTable_00.getCodes(2));
      Assert.assertArrayEquals(new short[] { 1, 2, 3, 4, 5 }, huffmanTable_00.getCodes(3));
      Assert.assertArrayEquals(new short[] { 1, 2 }, huffmanTable_01.getCodes(2));
      Assert.assertEquals(125, huffmanTable_01.getCodes(16).length);
   }
}
