package hu.farago.web.component.equity;

import hu.farago.ib.model.dto.equity.EquityOfOrder;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

@SpringComponent
@UIScope
public class EquityCurve extends HorizontalLayout {

	private static final long serialVersionUID = 4706329507498798057L;

	private Grid equityGrid;
	private EquityQueryEditor equityQE;
	
	@Autowired
	public EquityCurve(EquityQueryEditor eqe) {
		equityGrid = new Grid();
		equityQE = eqe;
		equityGrid.setContainerDataSource(new BeanItemContainer<EquityOfOrder>(
				EquityOfOrder.class));
		equityQE.setCallback((e) -> equityGrid.setContainerDataSource(new BeanItemContainer<EquityOfOrder>(
				EquityOfOrder.class, e)));
		addComponents(equityQE, equityGrid);
		formatting();
	}

	private void formatting() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
		setResponsive(true);
		equityGrid.setSizeFull();
		setExpandRatio(equityGrid, 1);
	}
	
}
