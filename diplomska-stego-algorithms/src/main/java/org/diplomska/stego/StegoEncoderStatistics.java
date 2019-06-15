package org.diplomska.stego;

import org.diplomska.jpeg.encoder.JpegImageContext;
import org.diplomska.util.JpegImageEncoderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

//~--- classes ----------------------------------------------------------------

/**
 * Class description
 *
 *
 * @version        Enter version here..., 16/04/18
 * @author         Enter your name here...
 */
public class StegoEncoderStatistics {

   /** Field description */
   private Map<Byte, Long> coefficientValuesStatisticsOrig;

   /** Field description */
   private Map<Byte, Long> coefficientValuesStatistics;

   /** Field description */
   private Map<CoeffTransition, Long> coefficientTransitionCount;

   /** Capacity in bytes */
   private int estimatedCapacityInBytes;

   //~--- constructors --------------------------------------------------------

   /**
    * Constructs ...
    *
    *
    * @param aJpegImageContext
    */
   public StegoEncoderStatistics(JpegImageContext aJpegImageContext) {
      estimatedCapacityInBytes   = 0;
      coefficientTransitionCount = new HashMap<CoeffTransition, Long>();
      initializeCoefficientValuesStatistics(aJpegImageContext);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aFromValue
    * @param aToValue
    */
   public void addTransition(int aFromValue, int aToValue) {
      CoeffTransition coeffTransition = findCoefficientTransition(aFromValue, aToValue);

      if (coeffTransition == null) {
         coeffTransition = new CoeffTransition(aFromValue, aToValue);
         coefficientTransitionCount.put(coeffTransition, 1L);
      } else {
         long newCountValue = coefficientTransitionCount.get(coeffTransition) + 1;

         coefficientTransitionCount.put(coeffTransition, newCountValue);
      }

      long oldValuesCount = coefficientValuesStatistics.get((byte) aFromValue) - 1;
      long newValuesCount = coefficientValuesStatistics.get((byte) aToValue) + 1;

      coefficientValuesStatistics.put((byte) aFromValue, oldValuesCount);
      coefficientValuesStatistics.put((byte) aToValue, newValuesCount);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public Map<CoeffTransition, Long> getCoefficientTransitionCount() {
      return coefficientTransitionCount;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Map<Byte, Long> getCoefficientValuesStatistics() {
      return coefficientValuesStatistics;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public Map<Byte, Long> getCoefficientValuesStatisticsOrig() {
      return coefficientValuesStatisticsOrig;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getEstimatedCapacityInBits() {
      return (getEstimatedCapacityInBytes() * Byte.SIZE);
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getEstimatedCapacityInBytes() {
      return estimatedCapacityInBytes;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getInjectedValueWithChange() {
      int resultSum = 0;

      for (Map.Entry<CoeffTransition, Long> entry : coefficientTransitionCount.entrySet()) {
         CoeffTransition entryKey = entry.getKey();

         if (entryKey.fromValue != entryKey.toValue) {
            resultSum += entry.getValue().intValue();
         }
      }

      return resultSum;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getInjectedValueWithoutChange() {
      int resultSum = 0;

      for (Map.Entry<CoeffTransition, Long> entry : coefficientTransitionCount.entrySet()) {
         CoeffTransition entryKey = entry.getKey();

         if (entryKey.fromValue == entryKey.toValue) {
            resultSum += entry.getValue().intValue();
         }
      }

      return resultSum;
   }

   /**
    * Method description
    *
    *
    * @return
    */
   public int getShrinkage() {
      int resultSum = 0;

      for (Map.Entry<CoeffTransition, Long> entry : coefficientTransitionCount.entrySet()) {
         CoeffTransition entryKey = entry.getKey();

         if ((entryKey.fromValue != 0) && (entryKey.toValue == 0)) {
            resultSum += entry.getValue().intValue();
         }
      }

      return resultSum;
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aEstimatedCapacity
    */
   public void setEstimatedCapacityInBytes(int aEstimatedCapacity) {
      estimatedCapacityInBytes = aEstimatedCapacity;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @return
    */
   public String toString() {
      StringBuffer          resultBuffer    = new StringBuffer();
      SortedMap<Byte, Long> coeffValuesOrig = getCoeffValuesStatisticsSorted(coefficientValuesStatisticsOrig);
      SortedMap<Byte, Long> coeffValues     = getCoeffValuesStatisticsSorted(coefficientValuesStatistics);

      resultBuffer.append("------------------------------------------------\n");
      resultBuffer.append(String.format("Estimated Capacity [bytes]          : %10d",
                                        getEstimatedCapacityInBytes()) + "\n");
      resultBuffer.append(String.format("Number of injections with change    : %10d",
                                        getInjectedValueWithChange()) + "\n");
      resultBuffer.append(String.format("Number of injections without change : %10d",
                                        getInjectedValueWithoutChange()) + "\n");
      resultBuffer.append(String.format("Number of shrinkages                : %10d", getShrinkage()) + "\n");
      resultBuffer.append("------------------- Transitions -----------------\n");

      for (Map.Entry<CoeffTransition, Long> entry : createCoefficientTransitionCountSorted().entrySet()) {
         resultBuffer.append(String.format("[%4d, %4d] -> %10d",
                                           entry.getKey().fromValue,
                                           entry.getKey().toValue,
                                           entry.getValue()) + "\n");
      }

      resultBuffer.append("------------------- Transitions -----------------\n");

      // Histogram before stego encoding
      resultBuffer.append("\n----------------- Histrogram Orig ---------------\n");

      for (Map.Entry<Byte, Long> entry : coeffValuesOrig.entrySet()) {
         resultBuffer.append(String.format("%10d\t", entry.getKey()));
      }

      resultBuffer.append("\n");

      for (Map.Entry<Byte, Long> entry : coeffValuesOrig.entrySet()) {
         resultBuffer.append(String.format("%10d\t", entry.getValue()));
      }

      resultBuffer.append("\n----------------- Histrogram Orig ---------------\n");

      // Histogram after stego encoding
      resultBuffer.append("\n------------------- Histrogram ------------------\n");

      for (Map.Entry<Byte, Long> entry : coeffValues.entrySet()) {
         resultBuffer.append(String.format("%10d\t", entry.getKey()));
      }

      resultBuffer.append("\n");

      for (Map.Entry<Byte, Long> entry : coeffValues.entrySet()) {
         resultBuffer.append(String.format("%10d\t", entry.getValue()));
      }

      resultBuffer.append("\n------------------- Histrogram ------------------\n");
      resultBuffer.append("\n------------------------------------------------\n");

      return resultBuffer.toString();
   }

   /**
    * Constructs ...
    *
    *
    * @return
    */
   private SortedMap<CoeffTransition, Long> createCoefficientTransitionCountSorted() {
      return new TreeMap<CoeffTransition, Long>(getCoefficientTransitionCount());
   }

   /**
    * Method description
    *
    *
    * @param aFromCoeff
    * @param aToCoeff
    *
    * @return
    */
   private CoeffTransition findCoefficientTransition(int aFromCoeff, int aToCoeff) {
      CoeffTransition resultCoeffTransition = null;

      for (CoeffTransition coeffTransition : coefficientTransitionCount.keySet()) {
         if ((coeffTransition.fromValue == aFromCoeff) && (coeffTransition.toValue == aToCoeff)) {
            resultCoeffTransition = coeffTransition;

            break;
         }
      }

      return resultCoeffTransition;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aMap
    *
    * @return
    */
   private SortedMap<Byte, Long> getCoeffValuesStatisticsSorted(Map<Byte, Long> aMap) {
      return new TreeMap<Byte, Long>(aMap);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Method description
    *
    *
    * @param aJpegImageContext
    *
    */
   private void initializeCoefficientValuesStatistics(JpegImageContext aJpegImageContext) {
      int imageWidth  = aJpegImageContext.getImageWidth();
      int imageHeight = aJpegImageContext.getImageHeight();

      coefficientValuesStatisticsOrig = new HashMap<Byte, Long>();
      coefficientValuesStatistics     = new HashMap<Byte, Long>();

      // initialize statistics map -128 .. 127
      for (int spanIndex = -128; spanIndex < 128; spanIndex++) {
         coefficientValuesStatisticsOrig.put((byte) spanIndex, 0L);
         coefficientValuesStatistics.put((byte) spanIndex, 0L);
      }

      for (int row = 0; row < imageHeight; row++) {
         for (int column = 0; column < imageWidth; column++) {
            int  value         = aJpegImageContext.getImageValue(row, column);
            byte yLumaValue    = JpegImageEncoderUtils.getQuantizedYLumaValue(value);
            byte cbChromaValue = JpegImageEncoderUtils.getQuantizedCbChromaValue(value);
            byte crChromaValue = JpegImageEncoderUtils.getQuantizedCrChromaValue(value);

            coefficientValuesStatisticsOrig.put(yLumaValue, (coefficientValuesStatisticsOrig.get(yLumaValue) + 1));
            coefficientValuesStatisticsOrig.put(cbChromaValue, coefficientValuesStatisticsOrig.get(cbChromaValue) + 1);
            coefficientValuesStatisticsOrig.put(crChromaValue,
                                                (coefficientValuesStatisticsOrig.get(crChromaValue) + 1));
            coefficientValuesStatistics.put(yLumaValue, (coefficientValuesStatistics.get(yLumaValue) + 1));
            coefficientValuesStatistics.put(cbChromaValue, coefficientValuesStatistics.get(cbChromaValue) + 1);
            coefficientValuesStatistics.put(crChromaValue, (coefficientValuesStatistics.get(crChromaValue) + 1));
         }
      }
   }

   //~--- inner classes -------------------------------------------------------

   /**
    * Class description
    *
    *
    * @version        Enter version here..., 16/04/18
    * @author         Enter your name here...
    */
   public static class CoeffTransition implements Comparable<CoeffTransition> {

      /** Field description */
      public int fromValue;

      /** Field description */
      public int toValue;

      //~--- constructors -----------------------------------------------------

      /**
       * Constructs ...
       *
       *
       * @param aFromValue
       * @param aToValue
       */
      public CoeffTransition(int aFromValue, int aToValue) {
         fromValue = aFromValue;
         toValue   = aToValue;
      }

      //~--- methods ----------------------------------------------------------

      @Override
      public int compareTo(CoeffTransition aCoeffTransition) {
         int resultOfComparison = 0;
         int thisValue          = fromValue + toValue;
         int otherValue         = aCoeffTransition.fromValue + aCoeffTransition.toValue;

         if (thisValue > otherValue) {
            resultOfComparison = 1;
         } else {
            resultOfComparison = -1;
         }

         return resultOfComparison;
      }
   }
}
