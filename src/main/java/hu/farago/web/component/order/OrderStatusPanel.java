package hu.farago.web.component.order;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Execution;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import hu.farago.ib.model.dto.order.IBOrderStatus;
import hu.farago.ib.service.OrderService;
import hu.farago.web.component.GridWithActionList;

@SpringComponent
@UIScope
public class OrderStatusPanel extends VerticalLayout {

	private static final long serialVersionUID = -5346838633309699275L;
	
	private class GridWithBackendService <T> {
		private GridWithActionList gridWAL;
		private BeanItemContainer<T> container;
		private Button refreshButton;
		
		public GridWithBackendService(Button btn, Class<T> clazz) {
			refreshButton = btn;
			refreshButton.addClickListener((e) -> container.removeAllItems());
			gridWAL = new GridWithActionList(refreshButton);
			container = new BeanItemContainer<T>(clazz);
			gridWAL.getGrid().setContainerDataSource(container);
		}
	}

	private OrderService orderService;
	private EventBus eventBus;
	
	private GridWithBackendService<IBOrderStatus> openOrdersBS;
	private GridWithBackendService<Execution> executionsBS;
	
	@Autowired
	public OrderStatusPanel(OrderService os, EventBus eb) {
		this.orderService = os;
		this.eventBus = eb;
		this.eventBus.register(this);
		
		this.openOrdersBS = new GridWithBackendService<IBOrderStatus>( new Button("Refresh order status list", (e) -> {
			this.orderService.reqAllOpenOrders();
		}), IBOrderStatus.class);
		
		this.executionsBS = new GridWithBackendService<Execution>( new Button("Refresh daily executions", (e) -> {
			this.orderService.reqExecutions();
		}), Execution.class);
				
		this.addComponents(openOrdersBS.gridWAL, executionsBS.gridWAL);
	}
	
	@Subscribe
	public void openOrder(IBOrderStatus orderStatus) {
		this.openOrdersBS.container.addBean(orderStatus);
	}
	
	@Subscribe
	public void execution(Execution execution) {
		this.executionsBS.container.addBean(execution);
	}

}
