package hu.farago.web.component.order;

import hu.farago.ib.strategy.ActionType;
import hu.farago.ib.strategy.Strategy;
import hu.farago.web.component.OrderCommonPropertiesEditor;
import hu.farago.web.component.PasteConverterTextBox;
import hu.farago.web.component.PasteGrid;
import hu.farago.web.component.order.dto.CVTSOrder;
import hu.farago.web.utils.Formatters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Notification;

@SpringComponent
@UIScope
public class CVTSPasteGrid extends PasteGrid<CVTSOrder> {

	private static final long serialVersionUID = -797998577130962477L;

	@Autowired
	public CVTSPasteGrid(OrderCommonPropertiesEditor ocpe) {
		super(ocpe);
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
				grid.setContainerDataSource(new BeanItemContainer<CVTSOrder>(
						CVTSOrder.class, items));
			}
			
		};
	}

	@Override
	protected Strategy createStrategy() {
		return Strategy.CVTS;
	}

}
