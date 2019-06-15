package org.diplomska.jpeg.jfif;

import org.diplomska.jpeg.decoder.ReadJpegInJfif;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        1.0, 26.nov.2015
 * @author         Ales Kunst
 */
public class JfifSosSegmentTest extends JfifSegmentTest {

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
    * @throws IOException
    *
    */
   @Test
   public void testGetImageData() throws IOException {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.SOS_MARKER);
      JfifSosSegment    jfifSosSegment = (JfifSosSegment) jfifSegments.get(0);
      byte[]            imageData      = jfifSosSegment.getImageData();

      Assert.assertEquals(51185, imageData.length);
      Assert.assertEquals(-43, imageData[0]);                      // 0xD5
      Assert.assertEquals(63, imageData[imageData.length - 1]);    // 0xF8
      alpeTriglavJpgInStream.close();
   }

   /**
    * Method description
    * @throws IOException
    *
    */
   @Test
   public void testGetRawImageData() throws IOException {
      checkRawDataOnSlovenskeAlpeJpeg();
      checkRawDataOnSimpleBlackWhiteJpeg();
   }

   /**
    * Method description
    *
    *
    * @throws IOException
    */
   private void checkRawDataOnSimpleBlackWhiteJpeg() throws IOException {
      String      filename = "simple_black_white.jpg";
      InputStream inStream = null;

      inStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(inStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.SOS_MARKER);
      JfifSosSegment    jfifSosSegment = (JfifSosSegment) jfifSegments.get(0);
      byte[]            imageData      = jfifSosSegment.getRawImageData();

      Assert.assertEquals(7, imageData.length);
      Assert.assertEquals(-12, imageData[0]);                      // 0xF4
      Assert.assertEquals(63, imageData[imageData.length - 1]);    // 0x3F
      inStream.close();
   }

   /**
    * Method description
    *
    *
    * @throws IOException
    */
   private void checkRawDataOnSlovenskeAlpeJpeg() throws IOException {
      String      filename               = "slovenske_alpe_triglav.jpg";
      InputStream alpeTriglavJpgInStream = null;

      alpeTriglavJpgInStream = getResourceStream(filename);

      ReadJpegInJfif    readJpegInJfif = new ReadJpegInJfif(alpeTriglavJpgInStream);
      List<JfifSegment> jfifSegments   = readJpegInJfif.getJfifSegments(JfifSegmentFactory.SOS_MARKER);
      JfifSosSegment    jfifSosSegment = (JfifSosSegment) jfifSegments.get(0);
      byte[]            imageData      = jfifSosSegment.getRawImageData();

      Assert.assertEquals(51227, imageData.length);
      Assert.assertEquals(-43, imageData[0]);                      // 0xD5
      Assert.assertEquals(63, imageData[imageData.length - 1]);    // 0xF8
      alpeTriglavJpgInStream.close();
   }
}
