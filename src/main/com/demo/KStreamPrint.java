import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

/**
 * ʹ��KStreamʵ�ֵ��ʸ���ͳ��
 * @author damon
 */
public class KStreamPrint {

    public static void main(String[] args) throws InterruptedException {
        //����ʵ����KafkaStreams���������
        Properties prop = new Properties();
        /**
         * ÿ��StreamsӦ�ó������Ҫ��һ��Ӧ��ID�����ID����Э��Ӧ��ʵ����Ҳ���������ڲ��ı��ش洢��������⡣
         * ����ͬһ��kafka��Ⱥ���ÿһ��StreamsӦ����˵��������ֱ�����Ψһ�ġ�
         */
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "mywordcount");//���������𣬵�����Ψһ
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");//kafka������IP
        //�ڶ�д����ʱ��Ӧ�ó�����Ҫ����Ϣ�������л��ͷ����л�������ṩ�����л���ͷ����л��ࡣ
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        /*
         * ����KStreamBuilder����
         * KStreamBuilder builder = new KStreamBuilder();
         * �����ϲο��ĺܶ಩���Լ��鼮�����õ����ַ���������KStreamBuiler���󣬴Ӷ��õ�KStream��
         * �������ұ�д����ʱ�ᷢ��������޷����룬�������˹ٷ��ĵ����֣����õ������°��kafka�������һЩ������ˡ�
         * �����ڴ˲ο�kafka�ٷ�ʾ���и����ķ���������StreamsBuilder���Ӷ��õ�KStream��
         * �ɴ˿ɼ������ǹٷ��ĵ�����ʾ����Ȩ����
         */

        //����StreamsBuilder����
        StreamsBuilder builder = new StreamsBuilder();
        //��Ҫ�����Topic��Ϊ����ʹ��builder��stram�����õ�һ��KStream��
        KStream<String, String> countStream = builder.stream("streamstopic-in");
        KTable<String, Long> wordcounts = countStream
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split(" ")))//��topic�еõ������е�ÿһ��ֵ�����СдȻ���Կո�ָ�
                .groupBy((key, word) -> word)//����
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));//����
        //��������־��תΪ��¼��Ȼ���������һ��Topic
        wordcounts.toStream().to("streamstopic-out", Produced.with(Serdes.String(), Serdes.Long()));
        //�������˺��������Զ���һ��KafkaStreams����
        KafkaStreams streams = new KafkaStreams(builder.build(), prop);
        //����kafkastreams����
        streams.start();
		/*//һ������£�StreamsӦ�ó����һֱ������ȥ���˴�����ģ����ԣ��������٣����Ǿ����߳�����һ��ʱ��Ȼ��ֹͣ����
		Thread.sleep(5000L);
		//ֹͣ����
		streams.close();*/

    }

}