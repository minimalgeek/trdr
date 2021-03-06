package hu.farago.web.component;

import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.OrderService;
import hu.farago.web.utils.Converters;

import java.util.Arrays;
import java.util.Locale;

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
import com.vaadin.ui.Slider;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class OrderCommonPropertiesEditor extends FormLayout {

	private static final long serialVersionUID = -4085171580771112473L;
	
	private class DoubleTextField extends TextField {
		
		private static final long serialVersionUID = -2807982926425707376L;

		public DoubleTextField(String caption) {
			super(caption);
			this.setConverter(Double.class);
			this.setLocale(Locale.US);
			this.setNullRepresentation("0.0");
		}
		
	}

	private OrderService orderService;

	private OrderCommonProperties oc;

	// fields
	ComboBox secType = new ComboBox("Sec Type", Arrays.asList(SecType.values()));
	TextField currency = new TextField("Currency");
	TextField exchange = new TextField("Exchange");
	TextField primaryExchange = new TextField("Primary Exchange");
	ComboBox orderType = new ComboBox("Order Type", Arrays.asList(OrderType
			.values()));
	DoubleTextField positionSize = new DoubleTextField("Position size");
	Slider targetVolatility = new Slider("Target Volatility", 0, 100);
	TextField faProfile = new TextField("FA Profile");
	Slider barStop = new Slider("N-Bar Stop", 0, 20);
	Slider maxOrders = new Slider("Maximum orders", 0, 100);
	
	DoubleTextField firstDayTarget = new DoubleTextField("First day target");
	DoubleTextField remainingDaysTarget = new DoubleTextField("Remaining days target");
	DoubleTextField stopLossTarget = new DoubleTextField("Stop-loss target");
	
	// fields end

	// action buttons
	private Button send = new Button("Set", FontAwesome.ANGELLIST);
	private CssLayout actions = new CssLayout(send);

	@Autowired
	public OrderCommonPropertiesEditor(OrderService orderService) {
		this.orderService = orderService;
		addComponents(secType, currency, exchange, primaryExchange, orderType,
				positionSize, targetVolatility, faProfile, barStop, maxOrders, 
				firstDayTarget, remainingDaysTarget, stopLossTarget, actions);

		// Configure and style components
		setSpacing(true);
		setStyleName("common-properties-editor");
		setWidthUndefined();

		targetVolatility.setConverter(Double.class);
		targetVolatility.setImmediate(true);
		barStop.setConverter(Converters.DOUBLE_TO_INT);
		barStop.setImmediate(true);
		maxOrders.setConverter(Converters.DOUBLE_TO_INT);
		maxOrders.setImmediate(true);
		
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		send.setStyleName(ValoTheme.BUTTON_PRIMARY);
		send.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		send.addClickListener(e -> orderService.saveOrModifyOcp(oc));
	}

	public interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler(ChangeHandler h) {
		send.addClickListener(e -> h.onChange());
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
