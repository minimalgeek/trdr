package hu.farago.web;

import hu.farago.ib.dto.IBError;
import hu.farago.ib.dto.OpenOrder;
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
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("mytheme")
@Title("IB Trader")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;
	
	@Autowired
	private OrderCommonPropertiesEditor ocpe;
	private Label response;
	
	@Autowired
	private EventBus eventBus;
	
	@Autowired
	private CVTSPasteGrid orderPasteGrid;

	private TabSheet tabSheet;
	
	@Override
	protected void init(VaadinRequest vaadinRequest) {
		eventBus.register(this);
		
		tabSheet = new TabSheet();

		buildOrderEditorSampleTab();
		buildCVTSTab();
		
		VerticalLayout mainLayout = new VerticalLayout(tabSheet);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		setContent(mainLayout);
	}

	private void buildOrderEditorSampleTab() {
		response = new Label("IB client response...");
		response.setSizeFull();
		VerticalLayout tab = new VerticalLayout();
		tab.addComponents(ocpe, response);
		tabSheet.addTab(tab, "Order Editor Sample", new ThemeResource("img/planets/01.png"));
	}
	
	private void buildCVTSTab() {
		VerticalLayout tab = new VerticalLayout();
		tab.addComponents(orderPasteGrid);
		tabSheet.addTab(tab, "CVTS strategy", new ThemeResource("img/planets/02.png"));
	}

//	@Subscribe
//	public void contractDetails(ContractDetails cd) {
//		response.setValue(cd.toString());
//	}

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