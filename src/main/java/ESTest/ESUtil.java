package ESTest;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhipengwu on 16-10-9.
 */
public class ESUtil {
    private static final Logger logger = LoggerFactory.getLogger(ESUtil.class);
    private static String CLUSTER_NAME = "elasticsearch_local";
    private static String ip = "127.0.0.1";
    private static int port = 9300;

    public static Client createClient() {
        // 设置
        Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true)
                .put("cluster.name", CLUSTER_NAME).build();
        Client client = null;
        try {
            client = TransportClient.builder().settings(settings).build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), port));
        } catch (UnknownHostException e) {
            logger.error("----------createClient---Host异常 {}", e);
        }
        return client;
    }

//    public static Client client;
//    public static void applyMapping(String index, String type, String location) throws Exception {
//
//        String source = readJsonDefn(location);
//
//        if (source != null) {
//            PutMappingRequestBuilder pmrb = client.admin().indices()
//                    .preparePutMapping(index)
//                    .setType(type);
//            pmrb.setSource(source);
//            MappingListener mappingListener = new MappingListener(pmrb);
//
//            // Create type and mapping
//            Thread thread = new Thread(mappingListener);
//
//            thread.start();
//            while (!mappingListener.processComplete.get()) {
//                System.out.println("not complete yet. Waiting for 100 ms")
//                Thread.sleep(100);
//
//            }
//
//        } else {
//            System.out.println("mapping error");
//        }
//
//    }
//
//
//    public static String readJsonDefn(String url) throws Exception {
//        //implement it the way you like
//        StringBuffer bufferJSON = new StringBuffer();
//
//        FileInputStream input = new FileInputStream(new File(url));
//        DataInputStream inputStream = new DataInputStream(input);
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//
//        String line;
//
//        while ((line = br.readLine()) != null) {
//            bufferJSON.append(line);
//        }
//        br.close();
//        return bufferJSON.toString();
//    }

}
