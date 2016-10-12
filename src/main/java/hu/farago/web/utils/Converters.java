package hu.farago.web.utils;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import com.vaadin.data.util.converter.Converter;

public class Converters {

	public static final Converter<Double, Integer> DOUBLE_TO_INT = new Converter<Double, Integer>() {

		private static final long serialVersionUID = 8053554881422091483L;

		@Override
		public Integer convertToModel(Double value,
				Class<? extends Integer> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value == null ? 0 : value.intValue();
		}

		@Override
		public Double convertToPresentation(Integer value,
				Class<? extends Double> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			return value == null ? 0.0 : value.doubleValue();
		}

		@Override
		public Class<Integer> getModelType() {
			return Integer.class;
		}

		@Override
		public Class<Double> getPresentationType() {
			return Double.class;
		}
		
	};
	
	public static final Converter<Date, DateTime> DATE_TO_DATETIME = new Converter<Date, DateTime>() {

        @Override
        public DateTime convertToModel(Date value, Class<? extends DateTime> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            return value == null ? null : new DateTime(value.getTime());
        }

        @Override
        public Date convertToPresentation(DateTime value, Class<? extends Date> targetType, Locale locale)
                throws com.vaadin.data.util.converter.Converter.ConversionException {
            return value == null ? null : value.toDate();
        }

        @Override
        public Class<DateTime> getModelType() {
            return DateTime.class;
        }

        @Override
        public Class<Date> getPresentationType() {
            return Date.class;
        }
    };
}
