package hu.farago.web.component;

import hu.farago.web.dto.ExcelInputOrder;
import hu.farago.web.dto.ExcelInputOrder.ActionType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SpringComponent
@UIScope
public class OrderPasteGrid extends VerticalLayout {

	private static final long serialVersionUID = -797998577130962477L;
	private static final DateTimeFormatter simpleYMD = DateTimeFormat
			.forPattern("yyyy.MM.dd");
	
	private Grid grid;
	private Button save;
	private Button clear;
	private CssLayout actions;
	
	private NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
	private DecimalFormat formatter = (DecimalFormat)nf;

	private PasteConverterTextBox<ExcelInputOrder> converter = new PasteConverterTextBox<ExcelInputOrder>() {

		private static final long serialVersionUID = 6290489353761030423L;

		@Override
		public ExcelInputOrder convertLine(String line) {
			// LMCA 2016.09.13 CVTS 24,03 24,27 12,31
			String[] items = line.split("[\\t]+");
			ExcelInputOrder eio = new ExcelInputOrder();

			try {
				eio.setTicker(items[0]);
				eio.setStartDateTime(simpleYMD.parseDateTime(items[1]));
				eio.setStrategy(items[2]);
				eio.setAction(ActionType.valueOf(items[3]));
				eio.setPreviousDayClosePrice(formatter.parse(items[4])
						.doubleValue());
				eio.setLimitPrice(formatter.parse(items[5])
						.doubleValue());
				eio.setHistoricalVolatility(formatter.parse(items[6])
						.doubleValue());
			} catch (Exception e) {
				Notification.show("Parse Error", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			}
			return eio;
		}

		@Override
		public void populate(List<ExcelInputOrder> items) {
			grid.setContainerDataSource(new BeanItemContainer<ExcelInputOrder>(
					ExcelInputOrder.class, items));
		}
		
	};

	public OrderPasteGrid() {
		this.grid = new Grid();
		this.save = new Button("Save", FontAwesome.SAVE);
		this.clear = new Button("Clear", FontAwesome.TRASH_O);

		this.actions = new CssLayout(save, clear);
		this.actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		this.save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		setMargin(true);
		setSpacing(true);
		grid.setContainerDataSource(new BeanItemContainer<ExcelInputOrder>(
				ExcelInputOrder.class));
		grid.setSizeFull();
		this.addLayoutClickListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1375505724138035283L;

			@Override
			public void layoutClick(LayoutClickEvent event) {
				OrderPasteGrid.this.focus();
				converter.selectAll();
				converter.focus();
				converter.selectAll();
			}
		});

		addComponents(converter, grid, actions);
	}
}
