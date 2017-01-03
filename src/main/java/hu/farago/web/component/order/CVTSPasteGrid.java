package hu.farago.web.component.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;

import hu.farago.ib.model.dto.order.strategy.CVTSOrder;
import hu.farago.ib.order.AbstractStrategyOrderQueue.QueueChanged;
import hu.farago.ib.order.strategy.enums.ActionType;
import hu.farago.ib.order.strategy.enums.Strategy;
import hu.farago.ib.service.OrderService;
import hu.farago.web.component.GridWithActionList;
import hu.farago.web.component.OrderCommonPropertiesEditor;
import hu.farago.web.component.OrderPasteGrid;
import hu.farago.web.component.PasteConverterTextBox;
import hu.farago.web.utils.Converters;
import hu.farago.web.utils.Formatters;

@SpringComponent
@UIScope
public class CVTSPasteGrid extends OrderPasteGrid<CVTSOrder> {

	private static final long serialVersionUID = -797998577130962477L;

	@Autowired
	public CVTSPasteGrid(OrderCommonPropertiesEditor ocpe, OrderService os, EventBus eb) {
		super(ocpe, os, eb);
		this.eventBus.register(this);
	}

	@Override
	protected PasteConverterTextBox<CVTSOrder> createConverter() {
		return new PasteConverterTextBox<CVTSOrder>() {

			private static final long serialVersionUID = 6290489353761030423L;

			@Override
			public CVTSOrder convertLine(String line) {
				// LMCA 2016.09.13 CVTS 24,03 24,27 12,31
				String[] items = line.split("[\\t]+");
				CVTSOrder eio = new CVTSOrder();

				try {
					eio.setTicker(items[0]);
					eio.setStartDateTime(Formatters.parseYYYY_MM_DD(items[1]));
					eio.setAction(ActionType.valueOf(items[2]));
					eio.setPreviousDayClosePrice(Formatters.parseDouble(items[3]));
					eio.setLimitPrice(Formatters.parseDouble(items[4]));
					eio.setHistoricalVolatility(Formatters.parseDouble(items[5]));
				} catch (Exception e) {
					Notification.show("Parse Error", e.getMessage(), Notification.Type.ERROR_MESSAGE);
				}
				return eio;
			}

			@Override
			public void populate(List<CVTSOrder> items) {
				populate(grid, items);
			}

			@Override
			public void populate(GridWithActionList toGrid, List<CVTSOrder> items) {
				toGrid.getGrid().setContainerDataSource(new BeanItemContainer<CVTSOrder>(CVTSOrder.class, items));
				Grid.Column column = toGrid.getGrid().getColumn("id");
				Grid.Column startDateTime = toGrid.getGrid().getColumn("startDateTime");
				column.setHidden(true);
				startDateTime.setConverter(Converters.DATETIME_TO_STRING);
			}

		};
	}

	@Override
	protected Strategy createStrategy() {
		return Strategy.CVTS;
	}

	@Override
	protected Class<CVTSOrder> createTypeClass() {
		return CVTSOrder.class;
	}

	@Subscribe
	private void queueChanged(QueueChanged change) {
		if (change.strategy == Strategy.CVTS) {
			this.waitingOrderGrid.refresh();
			this.triggeredOrderGrid.refresh();
		}
	}

}
