package hu.farago;

import javax.annotation.PostConstruct;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.eventbus.EventBus;
import com.mongodb.MongoClient;

@SpringBootApplication
public class TrdrApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrdrApplication.class, args);
	}

	@Bean
	public EventBus eventBus() {
		return new EventBus();
	}

	@PostConstruct
	public void addBSONExtra() {
		CodecRegistries.fromRegistries(CodecRegistries.fromCodecs(new DateTimeCodec()),
				MongoClient.getDefaultCodecRegistry());
	}

	public class DateTimeCodec implements Codec<DateTime> {

		@Override
		public void encode(BsonWriter writer, DateTime t, EncoderContext ec) {
			writer.writeString(t.toString());
		}

		@Override
		public Class<DateTime> getEncoderClass() {
			return DateTime.class;
		}

		@Override
		public DateTime decode(BsonReader reader, DecoderContext dc) {
			String date = reader.readString();
			return DateTime.parse(date);
		}
	}
}
