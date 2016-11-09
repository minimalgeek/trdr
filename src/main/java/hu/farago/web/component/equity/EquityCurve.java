package hu.farago.web.component.equity;

import hu.farago.ib.model.dto.equity.EquityOfOrder;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;

@SpringComponent
@UIScope
public class EquityCurve extends HorizontalLayout {

	private static final long serialVersionUID = 4706329507498798057L;

	//private Grid equityGrid;
	private TreeTable equityTable;
	private EquityQueryEditor equityQE;
	
	@Autowired
	public EquityCurve(EquityQueryEditor eqe) {
		equityQE = eqe;
		equityTable = new TreeTable();
		equityQE.setCallback((e) -> buildTreeFromEquityOfOrderList(e));
		addComponents(equityQE, equityTable);
		formatting();
	}

	private void formatting() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
		setResponsive(true);
		
		equityTable.addContainerProperty("Order ID", Integer.class, null);
		equityTable.addContainerProperty("Open Date", DateTime.class, null);
		equityTable.addContainerProperty("Close Date", DateTime.class, null);
		equityTable.addContainerProperty("Profit and Loss", Double.class, 0.0);
		equityTable.setSizeFull();
		setExpandRatio(equityTable, 1);
	}
	
	private void buildTreeFromEquityOfOrderList(List<EquityOfOrder> list) {
		for (EquityOfOrder eoo : list) {
			equityTable.addItem(new Object[]{eoo.orderId, eoo.openDate, eoo.closeDate, eoo.profitAndLoss}, eoo.orderId);
			if (eoo.parentOrderId != null && eoo.parentOrderId != 0) {
				equityTable.setParent(eoo.orderId, eoo.parentOrderId);
			}
		}
	}
	
}
