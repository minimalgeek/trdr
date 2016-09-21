package hu.farago.web.dto.editor;

import hu.farago.web.dto.OrderContract;
import hu.farago.ib.service.OrderService;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.ib.client.OrderType;
import com.ib.client.Types.SecType;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class OrderContractEditor extends VerticalLayout {

	private static final long serialVersionUID = -4085171580771112473L;

	private OrderService orderService;

	private OrderContract oc;

	// fields
	TextField symbol = new TextField("Symbol");
	ComboBox secType = new ComboBox("Sec Type", Arrays.asList(SecType.values()));
	TextField currency = new TextField("Currency");
	TextField exchange = new TextField("Exchange");
	TextField primaryExchange = new TextField("Primary Exchange");

	ComboBox action = new ComboBox("Action", Arrays.asList("BUY", "SELL"));
	ComboBox orderType = new ComboBox("Order Type", Arrays.asList(OrderType
			.values()));
	TextField totalQuantity = new TextField("Total Quantity");
	TextField limitPrice = new TextField("Limit Price");
	// fields end

	// action buttons
	private Button send = new Button("Send", FontAwesome.ANGELLIST);
	private Button reset = new Button("Reset", FontAwesome.TRASH_O);
	private CssLayout actions = new CssLayout(send, reset);

	@Autowired
	public OrderContractEditor(OrderService orderService) {
		this.orderService = orderService;
		addComponents(symbol, secType, currency, exchange, primaryExchange,
				action, orderType, totalQuantity, limitPrice, actions);
		
		if (oc == null) {
			oc = new OrderContract();
		}
		BeanFieldGroup.bindFieldsUnbuffered(oc, this);

		// Configure and style components
		setSpacing(true);

		totalQuantity.setConverter(Double.class);
		limitPrice.setConverter(Double.class);

		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		send.setStyleName(ValoTheme.BUTTON_PRIMARY);
		send.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		send.addClickListener(e -> sendOrder());
	}
	
	public void sendOrder() {
		orderService.placeOrder(oc);
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler(ChangeHandler h) {
		send.addClickListener(e -> h.onChange());
		reset.addClickListener(e -> h.onChange());
	}

}
