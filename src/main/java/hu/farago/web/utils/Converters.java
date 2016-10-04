package hu.farago.web.utils;

import java.util.Locale;

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
	
}
