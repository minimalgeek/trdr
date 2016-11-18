package hu.farago.web.component.equity;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataLabels;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsSpline;
import com.vaadin.addon.charts.model.SeriesTooltip;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.ZoomType;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.charts.util.Util;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import hu.farago.ib.model.dto.equity.EquityOfOrder;
import hu.farago.ib.service.EquityService;

@SpringComponent
@UIScope
public class EquityChart extends VerticalLayout {

	private static final long serialVersionUID = 3328766977134526758L;

	private Chart chart;

	@Autowired
	public EquityChart(EquityService equityService) {
		chart = new Chart();
		chart.setId("chart");

		Configuration conf = chart.getConfiguration();

		conf.getChart().setZoomType(ZoomType.XY);
		conf.setTitle("Equity Curve");
		conf.setSubTitle("Daily P/L and cumulative cash flow");

		XAxis xAxis = new XAxis();
		xAxis.setType(AxisType.DATETIME);
		conf.addxAxis(xAxis);

		YAxis primary = new YAxis();
		primary.setTitle("Equity");
		Style style = new Style();
		style.setColor(SolidColor.CHOCOLATE);
		primary.getTitle().setStyle(style);
		conf.addyAxis(primary);

		YAxis snd = new YAxis();
		snd.setTitle("Profit and Loss");
		snd.setOpposite(true);
		style = new Style();
		style.setColor(SolidColor.ORANGE);
		snd.getTitle().setStyle(style);
		conf.addyAxis(snd);

		Legend legend = new Legend();
		legend.setLayout(LayoutDirection.VERTICAL);
		legend.setAlign(HorizontalAlign.LEFT);
		legend.setX(120);
		legend.setVerticalAlign(VerticalAlign.TOP);
		legend.setY(100);
		legend.setFloating(true);
		legend.setBackgroundColor(SolidColor.WHITE);
		conf.setLegend(legend);

		chart.addPointClickListener(new PointClickListener() {

			private static final long serialVersionUID = -8446131337285166359L;

			@Override
			public void onClick(PointClickEvent event) {
				Window win = new Window("Point Data");
				win.setContent(new Label(
						"Date: " + Util.toServerDate(event.getX()) + "\nValue: " + event.getY() + "\nTicker: "
								+ ((DataSeries) event.getSeries()).get(event.getPointIndex()).getName(),
						ContentMode.PREFORMATTED));
				win.setPositionX(event.getAbsoluteX());
				win.setPositionY(event.getAbsoluteY());
				getUI().addWindow(win);
			}
		});

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatter("function() { return ''+ this.series.name +': '+ this.y +'';}");
		conf.setTooltip(tooltip);

		addComponent(chart);
	}

	public void reBuildChart(List<EquityOfOrder> equities) {
		if (equities == null) {
			return;
		}
		Configuration conf = chart.getConfiguration();
		conf.setSeries(Lists.newArrayList());

		DataSeries series = new DataSeries();
		PlotOptionsColumn column = new PlotOptionsColumn();
		column.setPointWidth(20);
		column.setNegativeColor(SolidColor.RED);
		column.setDataLabels(new DataLabels(true));
		series.setPlotOptions(column);
		series.setName("Profit and Loss");
		DataSeries series2 = new DataSeries();
		series2.setPlotOptions(new PlotOptionsSpline());
		series2.setName("Equity");

		double sum = 0.0;
		for (EquityOfOrder equity : equities) {
			DateTime closeDate = equity.closeDate;
			if (closeDate != null) {
				DataSeriesItem columnItem = new DataSeriesItem(closeDate.toDate(), equity.profitAndLoss);
				columnItem.setName(equity.ticker);
				series.add(columnItem);

				sum += equity.profitAndLoss;
				DataSeriesItem lineItem = new DataSeriesItem(closeDate.toDate(), sum);
				lineItem.setName(equity.ticker);
				series2.add(lineItem);
			}
		}

		conf.addSeries(series);
		conf.addSeries(series2);

		series.setyAxis(1);
		chart.drawChart(conf);
	}
}
