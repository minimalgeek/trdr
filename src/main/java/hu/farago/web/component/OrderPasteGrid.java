package hu.farago.web.component;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
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

	private final Class<T> typeParameterClass;

	protected GridWithActionList grid;
	protected Button save;
	protected Button clear;
	protected PasteConverterTextBox<T> converter;

	private Button ocpeWindowOpener;
	private Window ocpeWindow;
	private Strategy strategy;

	protected OrderCommonPropertiesEditor ocpe;
	protected EventBus eventBus;

	protected GridWithBackendService<T> oldOrderGrid;
	protected GridWithBackendService<T> waitingOrderGrid;
	protected GridWithBackendService<T> triggeredOrderGrid;

	// **********************
	// ** Abstract methods **
	// **********************
	protected abstract PasteConverterTextBox<T> createConverter();
	protected abstract Strategy createStrategy();
	protected abstract Class<T> createTypeClass();
	
	public OrderPasteGrid(OrderCommonPropertiesEditor ocpe, OrderService os, EventBus eb) {
		this.ocpe = ocpe;
		this.eventBus = eb;
		this.ocpe.setChangeHandler(() -> ocpeWindow.close());
		this.save = new Button("Submit", FontAwesome.SAVE);
		this.clear = new Button("Clear", FontAwesome.TRASH_O);
		this.ocpeWindowOpener = new Button("Common Properties", FontAwesome.ANGELLIST);
		this.grid = new GridWithActionList(save, clear, ocpeWindowOpener);

		this.save.addClickListener((e) -> {
			os.placeOrders(this.converter.getConvertedItems());
			clearList();
		});
		this.clear.addClickListener((e) -> clearList());

		this.typeParameterClass = createTypeClass();
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

		this.oldOrderGrid = new GridWithBackendService<T>(new Button("Show old orders", (e) -> {
			List<T> list = os.listOrders(this.strategy);
			this.converter.populate(oldOrderGrid.getGridWAL(), list);
		}), typeParameterClass);
		
		this.waitingOrderGrid = new GridWithBackendService<T>(new Button("Show active untriggered orders", (e) -> {
			List<T> list = os.listQueueOrders(this.strategy);
			this.converter.populate(waitingOrderGrid.getGridWAL(), list);
		}), typeParameterClass);
		
		this.triggeredOrderGrid = new GridWithBackendService<T>(new Button("Show triggered orders", (e) -> {
			List<T> list = os.listTriggeredOrders(this.strategy);
			this.converter.populate(triggeredOrderGrid.getGridWAL(), list);
		}), typeParameterClass);
		
		HorizontalLayout gridHL = new HorizontalLayout(grid, triggeredOrderGrid.getGridWAL());
		gridHL.setSizeFull();
		
		HorizontalLayout gridHLSecond = new HorizontalLayout(waitingOrderGrid.getGridWAL(), oldOrderGrid.getGridWAL());
		gridHLSecond.setSizeFull();

		addComponents(converter, gridHL, gridHLSecond);
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
