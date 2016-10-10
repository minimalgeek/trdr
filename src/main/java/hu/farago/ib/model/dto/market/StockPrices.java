package hu.farago.ib.model.dto.market;


public class StockPrices {

    protected static class TimeData {

        private long date;

        public TimeData(long date) {
            this.date = date;
        }

        public long getDate() {
            return date;
        }
    }

    public static class PriceData extends TimeData {

        private double price;

        public PriceData(long date, double price) {
            super(date);
            this.price = price;
        }

        public double getPrice() {
            return price;
        }
    }

    public static class OhlcData extends TimeData {

        private double open;
        private double high;
        private double low;
        private double close;

        public OhlcData(long date, double open, double high, double low,
            double close) {
            super(date);
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }

        public double getOpen() {
            return open;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }

        public double getClose() {
            return close;
        }
    }


    public static class JsonData {
        private Number[][] data;

        public Number[][] getData() {
            return data;
        }

        public void setData(Number[][] data) {
            this.data = data;
        }
    }

}
