package hu.farago.web;

import hu.farago.ib.model.dto.IBError;
import hu.farago.ib.model.dto.OpenOrder;
import hu.farago.web.component.OrderCommonPropertiesEditor;
import hu.farago.web.component.order.CVTSPasteGrid;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;

@SpringUI
@Theme("mytheme")
@Title("IB Trader")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;
	
	@Autowired
	private EventBus eventBus;
	
	// Tab content
	@Autowired
	private OrderCommonPropertiesEditor ocpe;
	
	@Autowired
	private CVTSPasteGrid orderPasteGrid;
	// End
	
	
	private TabSheet tabSheet;
	private VerticalSplitPanel vsp;
	private Label response;
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		eventBus.register(this);
		
		tabSheet = new TabSheet();

		buildTabs();
		
		response = new Label("IB client response...");
		response.setSizeFull();
		
		vsp = new VerticalSplitPanel(tabSheet, response);
		vsp.setSplitPosition(70, Unit.PERCENTAGE);
		setContent(vsp);
	}

	private void buildTabs() {
		tabSheet.addTab(ocpe, "Order Editor Sample", new ThemeResource("img/planets/01.png"));
		tabSheet.addTab(orderPasteGrid, "CVTS strategy", new ThemeResource("img/planets/02.png"));
	}
	
	@Subscribe
	public void ibError(IBError ibError) {
		Notification notif = new Notification("Error",
				ibError.toString(),
				Notification.Type.ERROR_MESSAGE);

		// Customize it
		notif.setDelayMsec(5000);
		notif.setPosition(Position.BOTTOM_CENTER);

		// Show it in the page
		notif.show(this.getPage());
	}
	
	@Subscribe
	public void openOrder(OpenOrder oo){
		response.setValue(oo.toString());
	}
}