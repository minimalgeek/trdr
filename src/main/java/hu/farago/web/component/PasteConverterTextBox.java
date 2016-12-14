package hu.farago.web.component;

import java.util.List;

import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.vaadin.ui.TextArea;

public abstract class PasteConverterTextBox<T> extends TextArea {

	private static final long serialVersionUID = 4891347979236614959L;

	private List<T> convertedItems;

	public PasteConverterTextBox() {
		super();
		
		this.addStyleName("hiddenStuff");
//		this.setHeight(40, Unit.PIXELS);
//		this.setWidth(100, Unit.PERCENTAGE);
		
		this.focus();
		this.setImmediate(true);
		this.setTextChangeEventMode(TextChangeEventMode.EAGER);
		
		this.addTextChangeListener((e) -> {
			convertItems();
			populate(convertedItems);
		});
		
		this.addBlurListener((e) -> {
			convertItems();
			populate(convertedItems);
		});
		
		this.addFocusListener((e) -> {
			convertItems();
			populate(convertedItems);
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
	
	public abstract void populate(GridWithActionList toGrid, List<T> items);

}
