package hu.farago.web.component;

import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.order.OrderService;
import hu.farago.web.component.order.dto.StrategyOrder;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public abstract class OrderPasteGrid<T extends StrategyOrder> extends VerticalLayout {

	private static final long serialVersionUID = 534910030402563189L;

	protected Grid grid;
	protected Button save;
	protected Button clear;
	private CssLayout actions;
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
		this.grid = new Grid();
		this.save = new Button("Save", FontAwesome.SAVE);
		this.clear = new Button("Clear", FontAwesome.TRASH_O);
		this.ocpeWindowOpener = new Button("Common Properties", FontAwesome.ANGELLIST);
		
		this.actions = new CssLayout(save, clear, ocpeWindowOpener);
		this.actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		this.save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		this.save.addClickListener((e) -> os.placeOrders(this.converter.getConvertedItems()));
		this.clear.addClickListener((e) -> {
			converter.setValue("");
			converter.populate(Lists.newArrayList());	
		});

		setMargin(true);
		setSpacing(true);
		grid.setSizeFull();
		
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

		addComponents(converter, grid, actions);
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
