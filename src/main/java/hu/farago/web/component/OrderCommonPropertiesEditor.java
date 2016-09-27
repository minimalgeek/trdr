package hu.farago.web.component;

import hu.farago.ib.model.dto.OrderCommonProperties;
import hu.farago.ib.service.OrderService;
import hu.farago.ib.strategy.Strategy;

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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class OrderCommonPropertiesEditor extends FormLayout {

	private static final long serialVersionUID = -4085171580771112473L;

	private OrderService orderService;

	private OrderCommonProperties oc;
	
	// fields
	ComboBox secType = new ComboBox("Sec Type", Arrays.asList(SecType.values()));
	TextField currency = new TextField("Currency");
	TextField exchange = new TextField("Exchange");
	TextField primaryExchange = new TextField("Primary Exchange");
	ComboBox orderType = new ComboBox("Order Type", Arrays.asList(OrderType
			.values()));
	// fields end

	// action buttons
	private Button send = new Button("Set", FontAwesome.ANGELLIST);
	private CssLayout actions = new CssLayout(send);

	@Autowired
	public OrderCommonPropertiesEditor(OrderService orderService) {
		this.orderService = orderService;
		addComponents(secType, currency, exchange, primaryExchange, orderType,
				actions);

		// Configure and style components
		setSpacing(true);
		setStyleName("common-properties-editor");
		setWidthUndefined();

		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		send.setStyleName(ValoTheme.BUTTON_PRIMARY);
		send.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to save, delete and reset
		send.addClickListener(e -> orderService.saveOrModifyOcp(oc));
	}
	
	public void tryToBindOrderCommonProperties(Strategy strategy) {
		oc = orderService.loadOcp(strategy);
		if (oc == null) {
			oc = new OrderCommonProperties();
			oc.id = strategy;
		}
		BeanFieldGroup.bindFieldsUnbuffered(oc, this);
	}
}
