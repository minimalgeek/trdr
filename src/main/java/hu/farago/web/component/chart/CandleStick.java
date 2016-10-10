package hu.farago.web.component.chart;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataGrouping;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.OhlcItem;
import com.vaadin.addon.charts.model.PlotOptionsCandlestick;
import com.vaadin.addon.charts.model.RangeSelector;
import com.vaadin.addon.charts.model.TimeUnit;
import com.vaadin.addon.charts.model.TimeUnitMultiples;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.Theme;
import com.vaadin.addon.charts.themes.ValoLightTheme;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class CandleStick extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private final VerticalLayout content;
	
	public CandleStick() {
        content = this;
        content.setSizeFull();
        content.setMargin(true);
    } 
 
	@Override
    public void attach() {
        super.attach();
        setup();
    } 
	
    public Component getWrappedComponent() {
        setup();
        content.getComponent(0).setSizeFull();
        return content;
    }

    protected void setup() {
        if (content.getComponentCount() == 0) {
            final Component map = getChart();
            content.addComponent(map);
            content.setExpandRatio(map, 1);
        }
    }

    protected Color[] getThemeColors() {
        Theme theme = ChartOptions.get().getTheme();
        return (theme != null) ? theme.getColors() : new ValoLightTheme()
                .getColors();
    }

    protected Theme getCurrentTheme() {
        Theme theme = ChartOptions.get().getTheme();
        return (theme != null) ? theme : new ValoLightTheme();
    }
	
    protected Component getChart() {
        final Chart chart = new Chart(ChartType.CANDLESTICK);
        chart.setHeight("450px");
        chart.setWidth("100%");
        chart.setTimeline(true);
 
        Configuration configuration = chart.getConfiguration();
        configuration.getTitle().setText("AAPL Stock Price");
 
        DataSeries dataSeries = new DataSeries();
        PlotOptionsCandlestick plotOptionsCandlestick = new PlotOptionsCandlestick();
        DataGrouping grouping = new DataGrouping();
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.WEEK, 1));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.MONTH, 1, 2, 3, 4, 6));
        plotOptionsCandlestick.setDataGrouping(grouping);
        dataSeries.setPlotOptions(plotOptionsCandlestick);
        for (StockPrices.OhlcData data : StockPrices.fetchAaplOhlcPrice()) {
            OhlcItem item = new OhlcItem();
            item.setX(data.getDate());
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
 
        return chart;
    }
}

