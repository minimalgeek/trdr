package hu.farago.web.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;

public class GridWithBackendService<T> {
	private GridWithActionList gridWAL;
	private BeanItemContainer<T> container;
	private Button refreshButton;
	
	public GridWithBackendService(Button btn, Class<T> clazz) {
		refreshButton = btn;
		refreshButton.addClickListener((e) -> container.removeAllItems());
		gridWAL = new GridWithActionList(refreshButton);
		container = new BeanItemContainer<T>(clazz);
		gridWAL.getGrid().setContainerDataSource(container);
	}
	
	public BeanItemContainer<T> getContainer() {
		return container;
	}
	
	public GridWithActionList getGridWAL() {
		return gridWAL;
	}
	
	public void refresh() {
		this.refreshButton.click();
	}
}