package hu.farago.web;

import hu.farago.ib.dto.IBError;
import hu.farago.ib.dto.OpenOrder;
import hu.farago.web.dto.editor.OrderContractEditor;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.ContractDetails;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Theme("valo")
@Title("IB Trader")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	private TextField ticker;
	private Button sendTicker;
	
	private Label response;
	
	@Autowired
	private OrderContractEditor ocEditor;
	
//	@Autowired
//	private EWrapperImpl wrapper;

	@Autowired
	private EventBus eventBus;

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		eventBus.register(this);
		
//		FormLayout form = buildForm();
//		response = new Label("Contract details...");
//		
//		response.setSizeFull();
//
//		VerticalLayout mainLayout = new VerticalLayout(form, response);
//		mainLayout.setMargin(true);
//		mainLayout.setSpacing(true);
//
//		setContent(mainLayout);
		response = new Label("IB client response...");
		response.setSizeFull();
		
		VerticalLayout mainLayout = new VerticalLayout(ocEditor, response);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		setContent(mainLayout);
	}

	/*
	private FormLayout buildForm() {
		FormLayout form = new FormLayout();
		ticker = new TextField("Ticker symbol: ");
		ticker.setIcon(FontAwesome.TICKET);
		ticker.setRequired(true);
		ticker.addValidator(new NullValidator("Must be given", false));

		sendTicker = new Button("Go!");
		sendTicker.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		sendTicker.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 4883535706088275064L;

			@Override
			public void buttonClick(ClickEvent event) {
				EClientSocket client = wrapper.getClientSocket();

				Contract contract = new Contract();
				contract.symbol(ticker.getValue());
				contract.secType("STK");
				contract.currency("USD");
				contract.exchange("ISLAND");

				client.reqContractDetails(wrapper.getCurrentOrderId() + 1,
						contract);
			}
		});

		form.addComponents(ticker, sendTicker);
		return form;
	}
	*/

	@Subscribe
	public void contractDetails(ContractDetails cd) {
		response.setValue(cd.toString());
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