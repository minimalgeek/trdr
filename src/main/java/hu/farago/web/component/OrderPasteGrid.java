package hu.farago.web.component;

import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.OrderService;
import hu.farago.web.component.order.dto.AbstractStrategyOrder;

import com.google.common.collect.Lists;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public abstract class OrderPasteGrid<T extends AbstractStrategyOrder> extends VerticalLayout {

	private static final long serialVersionUID = 534910030402563189L;

	protected GridWithActionList grid;
	protected Button save;
	protected Button clear;
	protected PasteConverterTextBox<T> converter;
	
	private Button ocpeWindowOpener;
	private Window ocpeWindow;
	
	private Strategy strategy;
	
	protected OrderCommonPropertiesEditor ocpe;
	protected abstract PasteConverterTextBox<T> createConverter();
	protected abstract Strategy createStrategy();
	
	public OrderPasteGrid(OrderCommonPropertiesEditor ocpe, OrderService os) {
		this.ocpe = ocpe;
		this.ocpe.setChangeHandler(() -> ocpeWindow.close());
		this.save = new Button("Save", FontAwesome.SAVE);
		this.clear = new Button("Clear", FontAwesome.TRASH_O);
		this.ocpeWindowOpener = new Button("Common Properties", FontAwesome.ANGELLIST);
		this.grid = new GridWithActionList(save, clear, ocpeWindowOpener);
		
		this.save.addClickListener((e) -> os.placeOrders(this.converter.getConvertedItems()));
		this.clear.addClickListener((e) -> {
			converter.setValue("");
			converter.populate(Lists.newArrayList());	
		});
		
		this.converter = createConverter();
		this.strategy = createStrategy();
		this.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1375505724138035283L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				OrderPasteGrid.this.focus();
				converter.selectAll();
				converter.focus();
				converter.selectAll();
			}
		});

		addComponents(converter, grid);
		setupOrderCommonPropertiesEditor();
	}

	private void setupOrderCommonPropertiesEditor() {
		ocpeWindow = new Window(strategy.name() + " common properties");
		ocpeWindow.setResizable(true);
		ocpeWindow.setDraggable(true);
		ocpeWindow.setModal(true);
		ocpeWindow.setClosable(true);
		ocpeWindowOpener.addClickListener((e) -> {
			ocpe.tryToBindOrderCommonProperties(this.strategy);
			ocpeWindow.setContent(ocpe);
			UI.getCurrent().addWindow(ocpeWindow);	
		});
	}
	
}
