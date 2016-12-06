package hu.farago.web.component.equity;

import hu.farago.ib.model.dto.equity.EquityOfOrder;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class EquityTab extends HorizontalLayout {

	private static final long serialVersionUID = 4706329507498798057L;

	private VerticalLayout vl;
	private TreeTable equityTable;
	private EquityQueryEditor equityQE;
	private EquityChart equityChart;
	
	@Autowired
	public EquityTab(EquityQueryEditor eqe, EquityChart ec) {
		equityQE = eqe;
		equityChart = ec;
		equityTable = new TreeTable();
		formatting();
		equityQE.setCallback((e) -> { buildTreeFromEquityOfOrderList(e); equityChart.reBuildChart(e); });
		addComponents(equityQE, vl);
		setExpandRatio(vl, 1);
	}

	private void formatting() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
		setResponsive(true);
		
		equityTable.addContainerProperty("Order ID", Integer.class, null);
		equityTable.addContainerProperty("Ticker", String.class, null);
		equityTable.addContainerProperty("Action", String.class, null);
		equityTable.addContainerProperty("Open Date", DateTime.class, null);
		equityTable.addContainerProperty("Close Date", DateTime.class, null);
		equityTable.addContainerProperty("Profit and Loss", Double.class, 0.0);
		equityTable.addContainerProperty("Status", String.class, null);
		equityTable.addContainerProperty("Accounting P&L", Double.class, null);
		
		equityTable.setSizeFull();
		equityChart.setSizeFull();
		vl = new VerticalLayout(equityTable, equityChart);
		vl.setSpacing(true);
		equityTable.setHeight(300, Unit.PIXELS);
	}
	
	private void buildTreeFromEquityOfOrderList(List<EquityOfOrder> list) {
		for (EquityOfOrder eoo : list) {
			equityTable.addItem(new Object[]{eoo.orderId, eoo.ticker, eoo.action, 
					eoo.openDate, eoo.closeDate, eoo.profitAndLoss, eoo.status, eoo.accountingPNL}, eoo.orderId);
			if (eoo.parentOrderId != null && eoo.parentOrderId != 0) {
				equityTable.setParent(eoo.orderId, eoo.parentOrderId);
			}
		}
	}
	
}
