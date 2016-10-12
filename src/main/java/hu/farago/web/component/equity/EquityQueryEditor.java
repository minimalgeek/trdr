package hu.farago.web.component.equity;

import hu.farago.ib.model.dto.equity.EquityQuery;
import hu.farago.ib.service.EquityService;
import hu.farago.web.utils.Converters;

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

	private EquityService equityService;

	private EquityQuery eq = new EquityQuery();;

	DateField from = new DateField("From");
	DateField to = new DateField("To");
	TextField strategy = new TextField("Strategy");

	private Button send = new Button("Query", FontAwesome.ANGELLIST);
	private CssLayout actions = new CssLayout(send);
	private BeanFieldGroup<?> bfg;

	@Autowired
	public EquityQueryEditor(EquityService equityService) {
		this.equityService = equityService;

		this.strategy.setNullRepresentation("");
		this.strategy.setIcon(FontAwesome.ANDROID);

		this.from.setConverter(Converters.DATE_TO_DATETIME);
		this.to.setConverter(Converters.DATE_TO_DATETIME);

		addComponents(from, to, strategy, actions);
		setWidth(300, Unit.PIXELS);

		configureSendButton();
		bfg = BeanFieldGroup.bindFieldsUnbuffered(eq, this);
	}

	private void configureSendButton() {
		send.setStyleName(ValoTheme.BUTTON_TINY);
		send.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		send.addClickListener(e -> {
			if (bfg.isValid()) {
				this.equityService.getOrders(eq);
			}
		});
	}
}
