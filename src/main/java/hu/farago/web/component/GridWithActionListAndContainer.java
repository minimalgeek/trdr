package hu.farago.web.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;

public class GridWithActionListAndContainer<T> extends GridWithActionList {

	private static final long serialVersionUID = -5117578452215271166L;

	private BeanItemContainer<T> container;
	private Button refreshButton;
	
	public GridWithActionListAndContainer(Class<T> clazz, Button... btns) {
		super(btns);
		refreshButton = btns[0];
		refreshButton.addClickListener((e) -> container.removeAllItems());
		
		container = new BeanItemContainer<T>(clazz);
		getGrid().setContainerDataSource(container);
	}
	
	public BeanItemContainer<T> getContainer() {
		return container;
	}
	
	public void refresh() {
		this.refreshButton.click();
	}
}