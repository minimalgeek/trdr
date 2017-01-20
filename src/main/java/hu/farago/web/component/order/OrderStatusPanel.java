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
import hu.farago.web.component.GridWithActionListAndContainer;

@SpringComponent
@UIScope
public class OrderStatusPanel extends VerticalLayout {

	private static final long serialVersionUID = -5346838633309699275L;

	private OrderService orderService;
	private EventBus eventBus;

	private GridWithActionListAndContainer<IBOrderStatusWrap> openOrdersBS;
	private GridWithActionListAndContainer<Execution> executionsBS;

	@Autowired
	public OrderStatusPanel(OrderService os, EventBus eb) {
		this.orderService = os;
		this.eventBus = eb;
		this.eventBus.register(this);

		this.openOrdersBS = new GridWithActionListAndContainer<IBOrderStatusWrap>(IBOrderStatusWrap.class,
				new Button("Refresh order status list", (e) -> {
					this.orderService.reqAllOpenOrders();
				}));

		this.executionsBS = new GridWithActionListAndContainer<Execution>(Execution.class,
				new Button("Refresh daily executions", (e) -> {
					this.orderService.reqExecutions();
				}));

		this.addComponents(openOrdersBS, executionsBS);
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
