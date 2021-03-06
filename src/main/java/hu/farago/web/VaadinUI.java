package hu.farago.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.renderers.HtmlRenderer;

import hu.farago.ib.EWrapperImpl;
import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.ib.order.strategy.AbstractStrategyOrderQueue.QueueChanged;
import hu.farago.web.component.chart.CandleStick;
import hu.farago.web.component.equity.EquityTab;
import hu.farago.web.component.order.CVTSPasteGrid;
import hu.farago.web.component.order.OrderStatusPanel;
import hu.farago.web.response.Response;
import hu.farago.web.response.ResponseType;

@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
@SpringUI
@Theme("mytheme")
@Title("IB Trader")
@Widgetset("AppWidgetset")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private EWrapperImpl eWrapper;

	// Tab content
	@Autowired
	private OrderStatusPanel openedOrders;

	@Autowired
	private CVTSPasteGrid orderPasteGrid;

	@Autowired
	private CandleStick candleStick;

	@Autowired
	private EquityTab equityCurve;
	// End

	private TabSheet tabSheet;
	private VerticalSplitPanel vsp;
	private Grid responseGrid;
	private BeanItemContainer<Response> responses;

	public static class UIDetachedEvent {
	}
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		eventBus.register(this);
		this.addDetachListener((event) -> eventBus.post(new UIDetachedEvent()));
		VaadinSession.getCurrent().getSession().setMaxInactiveInterval(-1);
		
		tabSheet = new TabSheet();

		buildTabs();
		setupResponseGrid();

		vsp = new VerticalSplitPanel(tabSheet, responseGrid);
		vsp.setSplitPosition(70, Unit.PERCENTAGE);

		Button reconnect = new Button("Reconnect", (e) -> eWrapper.reInitConnection());
		AbsoluteLayout rootLayout = new AbsoluteLayout();
		rootLayout.addComponent(vsp);
		rootLayout.addComponent(reconnect, "right:0px;");
		rootLayout.setSizeFull();

		setContent(rootLayout);
	}

	private void setupResponseGrid() {
		responses = new BeanItemContainer<Response>(Response.class, Lists.newArrayList());
		responseGrid = new Grid();
		responseGrid.setSizeFull();
		responseGrid.setContainerDataSource(responses);
		responseGrid.setColumnReorderingAllowed(true);
		responseGrid.setDescription("Responses sent by the IB client");
		Grid.Column htmlColumn = responseGrid.getColumn("htmlResponse");
		Grid.Column cdtColumn = responseGrid.getColumn("clientDateTime");
		htmlColumn.setRenderer(new HtmlRenderer(""));
		htmlColumn.setEditable(true);
		htmlColumn.setWidthUndefined();
		// expand ratios
		htmlColumn.setExpandRatio(6);
		cdtColumn.setExpandRatio(1);
		// show content on hover
		responseGrid.setColumns(cdtColumn.getPropertyId(), htmlColumn.getPropertyId());
		responseGrid.setCellDescriptionGenerator((cell) -> cell.getValue().toString());
	}

	private void buildTabs() {
		tabSheet.addTab(openedOrders, "Services", new ThemeResource("img/planets/01.png"));
		tabSheet.addTab(orderPasteGrid, "CVTS strategy", new ThemeResource("img/planets/02.png"));
		tabSheet.addTab(equityCurve, "Equity curve", new ThemeResource("img/planets/03.png"));
		tabSheet.addTab(candleStick, "Stock prices", new ThemeResource("img/planets/04.png"));
	}

	@Subscribe
	public void ibError(IBError ibError) {
		addResponseToGrid(ibError.toString(), ResponseType.ERROR);
	}

	@Subscribe
	public void openOrder(IBOrder oo) {
		addResponseToGrid(oo.toString());
	}
	
	@Subscribe
	public void queueChanged(QueueChanged qc) {
		addResponseToGrid("Queue changed in " + qc.strategy.name());
	}
	
	@Subscribe
	public void uiDetached(UIDetachedEvent e) {
		eventBus.unregister(this);
	}

	private void addResponseToGrid(String responseText) {
		addResponseToGrid(responseText, ResponseType.NOTIFICATION);
	}

	private void addResponseToGrid(String responseText, ResponseType type) {
		getUI().access(new Runnable() {
			@Override
			public void run() {
				Response resp = new Response(type, responseText);
				responses.addBean(resp);
				responseGrid.sort(responseGrid.getColumn("clientDateTime").getPropertyId(), SortDirection.DESCENDING);
			}
		});
	}

}