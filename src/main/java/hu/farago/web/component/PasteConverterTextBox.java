package hu.farago.web.component;

import java.util.List;

import org.springframework.util.StringUtils;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.TextArea;

public abstract class PasteConverterTextBox<T> extends TextArea {

	private static final long serialVersionUID = 4891347979236614959L;

	private List<T> convertedItems;

	public PasteConverterTextBox() {
		super();
		this.addStyleName("hiddenStuff");
		this.focus();
		this.setImmediate(true);
		this.setTextChangeEventMode(TextChangeEventMode.EAGER);
		this.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -4033788902737982230L;

			@Override
			public void textChange(TextChangeEvent event) {
				convertItems();
				populate(convertedItems);
			}
		});
	}

	private void convertItems() {
		convertedItems = Lists.newArrayList();
		String[] lines = this.getValue().split("[\\r\\n]+");
		for (String line : lines) {
			if (!StringUtils.isEmpty(line))
				convertedItems.add(convertLine(line));
		}
	}
	
	public List<T> getConvertedItems() {
		return convertedItems;
	}

	public abstract T convertLine(String line);

	public abstract void populate(List<T> items);

}
