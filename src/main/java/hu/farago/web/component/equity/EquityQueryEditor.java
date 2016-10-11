package hu.farago.web.component.equity;

import hu.farago.ib.model.dto.order.OrderCommonProperties;
import hu.farago.ib.service.OrderService;
import hu.farago.web.component.equity.dto.EquityQueryDTO;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class EquityQueryEditor extends FormLayout {

	private static final long serialVersionUID = 7175758823593081474L;

	private OrderService orderService;
	
	private EquityQueryDTO eq = new EquityQueryDTO();;
	
	DateField from = new DateField("From");
	DateField to = new DateField("To");
	TextField strategy = new TextField("Strategy");
	
	private Button send = new Button("Query", FontAwesome.ANGELLIST);
	private CssLayout actions = new CssLayout(send);
	
	@Autowired
	public EquityQueryEditor(OrderService orderService) {
		this.orderService = orderService;
		
		this.strategy.setRequired(true);
		this.from.setConverter(DateTime.class);
		this.to.setConverter(DateTime.class);
		addComponents(from, to, strategy, actions);

		// Configure and style components
		send.setStyleName(ValoTheme.BUTTON_PRIMARY);
		send.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		//send.addClickListener(e -> orderService.saveOrModifyOcp(oc));
		BeanFieldGroup.bindFieldsUnbuffered(eq, this);
	}
	
}
