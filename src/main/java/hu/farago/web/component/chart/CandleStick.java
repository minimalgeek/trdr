package hu.farago.web.component.chart;

import hu.farago.ib.model.dto.market.StockPrices;
import hu.farago.ib.model.dto.market.StockQueryDTO;
import hu.farago.ib.service.StockPriceService;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataGrouping;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.OhlcItem;
import com.vaadin.addon.charts.model.PlotOptionsCandlestick;
import com.vaadin.addon.charts.model.RangeSelector;
import com.vaadin.addon.charts.model.TimeUnit;
import com.vaadin.addon.charts.model.TimeUnitMultiples;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class CandleStick extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final VerticalLayout content;

	private EventBus eventBus;
	private StockPriceService service;
	private TextField ticker;
	private Chart chart;

	@Autowired
	public CandleStick(EventBus eventBus, StockPriceService service) {
		this.eventBus = eventBus;
		this.service = service;
		this.eventBus.register(this);

		buildTickerInput();
		buildChart();

		content = this;
		content.setSizeFull();
		content.setMargin(true);
		content.setSpacing(true);

		content.addComponent(this.ticker);
		content.addComponent(this.chart);
		content.setExpandRatio(this.chart, 1);
	}

	private void buildTickerInput() {
		this.ticker = new TextField("Ticker");
		this.ticker.addStyleName("inline-label");
		this.ticker.addTextChangeListener((e) -> this.service
				.getStockPrices(new StockQueryDTO(e.getText(), DateTime.now()
						.minusMonths(6), DateTime.now())));
	}

	private void buildChart() {
		this.chart = new Chart(ChartType.CANDLESTICK);
		this.chart.setHeight("450px");
		this.chart.setWidth("100%");
		this.chart.setTimeline(true);
	}

	@Subscribe
	public void ohlcDataList(List<StockPrices.OhlcData> dataList) {
		this.getUI().access(new Runnable() {
			@Override
			public void run() {
				Configuration configuration = chart.getConfiguration();
				configuration.getTitle().setText(ticker.getValue() + " Stock Price");

				DataSeries dataSeries = new DataSeries();
				PlotOptionsCandlestick plotOptionsCandlestick = new PlotOptionsCandlestick();
				DataGrouping grouping = new DataGrouping();
				grouping.addUnit(new TimeUnitMultiples(TimeUnit.WEEK, 1));
				grouping.addUnit(new TimeUnitMultiples(TimeUnit.MONTH, 1, 2, 3,
						4, 6));
				plotOptionsCandlestick.setDataGrouping(grouping);
				dataSeries.setPlotOptions(plotOptionsCandlestick);
				for (StockPrices.OhlcData data : dataList) {
					OhlcItem item = new OhlcItem();
					item.setX(new Date(data.getDate()));
					item.setLow(data.getLow());
					item.setHigh(data.getHigh());
					item.setClose(data.getClose());
					item.setOpen(data.getOpen());
					dataSeries.add(item);
				}
				configuration.setSeries(dataSeries);

				RangeSelector rangeSelector = new RangeSelector();
				rangeSelector.setSelected(4);
				configuration.setRangeSelector(rangeSelector);

				chart.drawChart(configuration);
			}
		});
	}
}
