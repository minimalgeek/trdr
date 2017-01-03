package hu.farago.web.component.order;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.ib.client.Execution;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import hu.farago.ib.model.dto.order.IBOrderStatusWrap;
import hu.farago.ib.service.OrderService;
import hu.farago.web.component.GridWithBackendService;

@SpringComponent
@UIScope
public class OrderStatusPanel extends VerticalLayout {

	private static final long serialVersionUID = -5346838633309699275L;
	
	private OrderService orderService;
	private EventBus eventBus;
	
	private GridWithBackendService<IBOrderStatusWrap> openOrdersBS;
	private GridWithBackendService<Execution> executionsBS;
	
	@Autowired
	public OrderStatusPanel(OrderService os, EventBus eb) {
		this.orderService = os;
		this.eventBus = eb;
		this.eventBus.register(this);
		
		this.openOrdersBS = new GridWithBackendService<IBOrderStatusWrap>( new Button("Refresh order status list", (e) -> {
			this.orderService.reqAllOpenOrders();
		}), IBOrderStatusWrap.class);
		
		this.executionsBS = new GridWithBackendService<Execution>( new Button("Refresh daily executions", (e) -> {
			this.orderService.reqExecutions();
		}), Execution.class);
				
		this.addComponents(openOrdersBS.getGridWAL(), executionsBS.getGridWAL());
	}
	
	@Subscribe
	public void openOrder(IBOrderStatusWrap orderStatus) {
		this.openOrdersBS.getContainer().addBean(orderStatus);
	}
	
	@Subscribe
	public void execution(Execution execution) {
		this.executionsBS.getContainer().addBean(execution);
	}

}
