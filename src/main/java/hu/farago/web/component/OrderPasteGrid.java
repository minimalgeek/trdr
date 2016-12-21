package hu.farago.web.component;

import java.util.List;

import com.google.common.collect.Lists;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import hu.farago.ib.model.dto.order.AbstractStrategyOrder;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.OrderService;

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
	
	protected GridWithActionList oldOrderGrid;
	protected Button queryOldOrders;
	
	protected GridWithActionList waitingOrderGrid;
	protected Button querywaitingOrders;
	
	public OrderPasteGrid(OrderCommonPropertiesEditor ocpe, OrderService os) {
		this.ocpe = ocpe;
		this.ocpe.setChangeHandler(() -> ocpeWindow.close());
		this.save = new Button("Save", FontAwesome.SAVE);
		this.clear = new Button("Clear", FontAwesome.TRASH_O);
		this.ocpeWindowOpener = new Button("Common Properties", FontAwesome.ANGELLIST);
		this.grid = new GridWithActionList(save, clear, ocpeWindowOpener);
		
		this.save.addClickListener((e) -> {
			os.placeOrders(this.converter.getConvertedItems());
			clearList();
		});
		this.clear.addClickListener((e) -> clearList());
		
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

		this.queryOldOrders = new Button("Show old orders", FontAwesome.ARCHIVE);
		this.oldOrderGrid = new GridWithActionList(queryOldOrders);
		this.queryOldOrders.addClickListener((e) -> {
			 List<T> list = os.listOrders(this.strategy);
			 this.converter.populate(oldOrderGrid, list);
		});
		
		this.querywaitingOrders = new Button("Show active untriggered orders", FontAwesome.ROAD);
		this.waitingOrderGrid = new GridWithActionList(querywaitingOrders);
		this.querywaitingOrders.addClickListener((e) -> {
			 List<T> list = os.listQueueOrders(this.strategy);
			 this.converter.populate(waitingOrderGrid, list);
		});
		
		HorizontalLayout gridHL = new HorizontalLayout(waitingOrderGrid, oldOrderGrid);
		gridHL.setSizeFull();
		
		addComponents(converter, grid, gridHL);
		setupOrderCommonPropertiesEditor();
	}
	private void clearList() {
		converter.setValue("");
		converter.populate(Lists.newArrayList());
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
