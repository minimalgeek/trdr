package hu.farago.web;

import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.order.IBOrder;
import hu.farago.web.component.chart.CandleStick;
import hu.farago.web.component.order.CVTSPasteGrid;
import hu.farago.web.component.order.OrderStatusPanel;
import hu.farago.web.response.Response;
import hu.farago.web.response.ResponseType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

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
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.renderers.HtmlRenderer;

@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
@SpringUI
@Theme("mytheme")
@Title("IB Trader")
@Widgetset("AppWidgetset")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	@Autowired
	private EventBus eventBus;

	// Tab content
	@Autowired
	private OrderStatusPanel openedOrders;

	@Autowired
	private CVTSPasteGrid orderPasteGrid;
	
	@Autowired
	private CandleStick candleStick;
	// End

	private TabSheet tabSheet;
	private VerticalSplitPanel vsp;
	private Grid responseGrid;
	private BeanItemContainer<Response> responses;

	@Autowired
    private ServletContext servletContext;
    
    @PostConstruct
    public void init() throws IOException {
        Theme annotation = getUI().getClass().getAnnotation(Theme.class);
        if (annotation != null) {
            String root = servletContext.getRealPath("/");
            if (root != null && Files.isDirectory(Paths.get(root))) {
                Files.createDirectories(Paths.get(servletContext.getRealPath("/VAADIN/themes/" + annotation.value())));
            }
        }
    }
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		eventBus.register(this);
		tabSheet = new TabSheet();

		buildTabs();
		setupResponseGrid();

		vsp = new VerticalSplitPanel(tabSheet, responseGrid);
		vsp.setSplitPosition(70, Unit.PERCENTAGE);
		setContent(vsp);
	}

	private void setupResponseGrid() {
		responses = new BeanItemContainer<Response>(Response.class,
				Lists.newArrayList());
		responseGrid = new Grid();
		responseGrid.setSizeFull();
		responseGrid.setContainerDataSource(responses);
		responseGrid.setColumnReorderingAllowed(true);
		responseGrid.setDescription("Responses sent by the IB client");
		Grid.Column htmlColumn = responseGrid.getColumn("htmlResponse");
		htmlColumn.setRenderer(new HtmlRenderer(""));
		htmlColumn.setEditable(true);
		htmlColumn.setWidthUndefined();
		// expand ratios
		htmlColumn.setExpandRatio(6);
		responseGrid.getColumn("clientDateTime").setExpandRatio(1);
		// show content on hover
		responseGrid.setCellDescriptionGenerator((cell) -> cell.getValue()
				.toString());
	}

	private void buildTabs() {
		tabSheet.addTab(openedOrders, "Orders current status",
				new ThemeResource("img/planets/01.png"));
		tabSheet.addTab(orderPasteGrid, "CVTS strategy", new ThemeResource(
				"img/planets/02.png"));
		tabSheet.addTab(candleStick, "Stock prices", new ThemeResource(
				"img/planets/03.png"));
	}

	@Subscribe
	public void ibError(IBError ibError) {
		// access(new Runnable() {
		// @Override
		// public void run() {
		// Notification notif = new Notification("Error",
		// ibError.toString(), Notification.Type.ERROR_MESSAGE);
		//
		// // Customize it
		// notif.setDelayMsec(5000);
		// notif.setPosition(Position.BOTTOM_CENTER);
		//
		// // Show it in the page
		// notif.show(VaadinUI.this.getPage());
		// }
		// });
		addResponseToGrid(ibError.toString(), ResponseType.ERROR);
	}

	@Subscribe
	public void openOrder(IBOrder oo) {
		addResponseToGrid(oo.toString());
	}

	private void addResponseToGrid(String responseText) {
		addResponseToGrid(responseText, ResponseType.NOTIFICATION);
	}

	private void addResponseToGrid(String responseText, ResponseType type) {
		access(new Runnable() {
			@Override
			public void run() {
				Response resp = new Response(type, responseText);
				responses.addBean(resp);
				responseGrid.sort(responseGrid.getColumn("clientDateTime")
						.getPropertyId(), SortDirection.DESCENDING);
			}
		});
	}

}