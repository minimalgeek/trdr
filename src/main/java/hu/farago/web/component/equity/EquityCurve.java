package hu.farago.web.component.equity;

import hu.farago.web.component.order.dto.CVTSOrder;

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
	
	@Autowired
	public EquityCurve(EquityQueryEditor eqe) {
		equityGrid = new Grid();
		equityGrid.setContainerDataSource(new BeanItemContainer<CVTSOrder>(
				CVTSOrder.class));
		equityGrid.setSizeFull();
		
		setSpacing(true);
		setMargin(true);
		setSizeFull();
		
		addComponents(eqe, equityGrid);
		setExpandRatio(equityGrid, 4.0f);
		setExpandRatio(eqe, 1.0f);
	}
	
}
