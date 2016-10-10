package ESTest;

import ESTest.bean.IndexEntity;
import ESTest.bean.SuggestionItem;
import ESTest.concurrent.MultiThreadService;
import com.google.common.collect.Lists;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Index;
import io.searchbox.indices.IndicesExists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by zhipengwu on 16-10-8.
 */
public class ESTest extends MultiThreadService {
    private static final Logger logger = LoggerFactory.getLogger(ESTest.class);
    private String elasticsearchurl = "http://127.0.0.1:9200/";
    private int shards = 5;
    private int replicas = 1;
    private static boolean isBulk = true;
    private static int bulkSize = 1000;

    public ESTest() {
        super(5, 10, 5L, TimeUnit.MINUTES, 1024);
    }

    public ESTest(int corePoolSize, int maximumPoolSize, Long keepAliveTime, TimeUnit unit, int capacity) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, capacity);
    }

    private class BulkWorker implements Runnable {

        /* 索引的详细记录 */
        private List<IndexEntity> indexEntityList;

        /* es客户端 */
        private JestClient client;

        private BulkWorker(List<IndexEntity> indexEntityList, JestClient client) {
            this.indexEntityList = indexEntityList;
            this.client = client;
        }

        public void run() {
            if (indexEntityList == null || indexEntityList.isEmpty() || client == null) {
                return;
            }
            try {
                /* build */
                Bulk.Builder builder = new Bulk.Builder();
                for (IndexEntity indexEntity : indexEntityList) {
                    Index userIndex;
                    if (StringUtils.isNotEmpty(indexEntity.getId())) {
                        userIndex = new Index.Builder(indexEntity.getSource()).index(indexEntity.getIndex())
                                .type(indexEntity.getType()).id(indexEntity.getId()).build();
                    } else {
                        userIndex = new Index.Builder(indexEntity.getSource()).index(indexEntity.getIndex())
                                .type(indexEntity.getType()).build();
                    }
                    builder.addAction(userIndex);
                }
                long start = System.currentTimeMillis();
                BulkResult bulkResult = client.execute(builder.build());
                if (bulkResult.isSucceeded()) {
                    logger.info(String.format("bulk create %d index success and cost %sms !", indexEntityList.size(),
                            (System.currentTimeMillis() - start)));
                    System.out.println(String.format("bulk create %d index success and cost %sms !",
                            indexEntityList.size(), (System.currentTimeMillis() - start)));
                } else {
                    List<BulkResult.BulkResultItem> failedItems = bulkResult.getFailedItems();
                    for (BulkResult.BulkResultItem bulkResultItem : failedItems) {
                        logger.info(String.format("bulk create %s index failed and error message is %s!",
                                bulkResultItem.id, bulkResultItem.error));
                        System.out.println(String.format("bulk create %s index failed and error message is %s!",
                                bulkResultItem.id, bulkResultItem.error));
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("exception occured when create index!"), e);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String targetName = "山东vCro4";
        String index = "searchanalysis121";
        Client client = ESUtil.createClient();
        // createIndex(client, index);
        // List<IndexEntity> list=Lists.newArrayList();

        client.prepareGet();
        String userName = "";
        String age = "";
        SearchRequestBuilder searchRequestBuilder = null;
        searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(QueryBuilders.termQuery("query", targetName));
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHit[] searchHits;
        searchHits = searchResponse.getHits().getHits();
        for (int i = 0; i < searchHits.length; i++) {
            Map<String, Object> sourceMap = searchHits[i].getSource();
            userName = searchHits[i].getSource().get("query").toString();
            List<SuggestionItem> suggestionItemList = (List<SuggestionItem>)sourceMap.get("itemList");
//            age = searchHits[i].getSource().get("itemList");
            System.out.println(String.format("name:%s, age:%s", userName, age));
        }
        System.out.println("###end "+searchHits.length);
        client.close();
    }

    /**
     * 创建不分词的index
     * 
     * @param client
     */
    public static void createIndex(Client client, String indexName) {

        String index = indexName;

        Settings settings = Settings.builder().put("number_of_shards", 6).put("number_of_replicas", 2).build();

        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).setSettings(settings)
                .execute().actionGet();

        // client.admin().indices().create(new CreateIndexRequest("productindex14")).actionGet();
        XContentBuilder mapping = null;
        try {
            mapping = jsonBuilder().prettyPrint().startObject().startObject(index).startObject("properties")
                    .startObject("query").field("type", "string").field("index", "not_analyzed").field("store", "yes")
                    .endObject().endObject().endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(index).source(mapping);
        client.admin().indices().putMapping(mappingRequest).actionGet();
        // client.admin().indices().preparePutMapping("productIndex13")
        // .setType("product")
        // .setSource(mapping).execute().actionGet();
    }

    public static void importdata() {
        IndexEntity indexEntity = null;
        indexEntity = new IndexEntity();
    }

    private static void put(Client client, String index, String type, String id) {
        try {
            XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
            xContentBuilder.startObject().field("title", "王俊辉").field("description", "食品").endObject();

            // Index
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(index, type, id);
            indexRequestBuilder.setSource(xContentBuilder);
            indexRequestBuilder.setTTL(8000);

            // 执行
            IndexResponse indexResponse = indexRequestBuilder.execute().actionGet();

            logger.info("----------put {}", indexResponse.toString());
        } catch (IOException e) {
            logger.error("----------put fail {} ", e);
        }
    }

    /**
     * 创建不分词的索引,实现es查询时精确匹配(不是前缀匹配)
     *
     * @param index
     * @param indexEntityList
     */
    public void createNotAnalyzedIndex(String index, List<IndexEntity> indexEntityList) {
        JestClient jestClient = JestClientBuilder.build(elasticsearchurl);
        Client client = ESUtil.createClient();

        try {
            /** 索引是否存在 */
            IndicesExists indicesExists = new IndicesExists.Builder(index).build();
            JestResult jestResult = jestClient.execute(indicesExists);

            if (!jestResult.isSucceeded()) {
                /** 创建索引库 */
                boolean createSchemaMappingStatus = createSchemaMapping(client, index);
                if (!createSchemaMappingStatus) {
                    logger.error("elasticsearch index create failed ! ! !");
                    System.out.println("elasticsearch index create failed ! ! !");
                    return;
                }
            } else {
                logger.info("elasticsearch '" + index + "' already exist ! ! !");
                System.out.println("elasticsearch '" + index + "' already exist ! ! !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isBulk) {
            int pos = 0;
            int size = indexEntityList.size();
            while (pos < size) {
                int next = pos + bulkSize <= size ? pos + bulkSize : size;
                List<IndexEntity> subEntities = indexEntityList.subList(pos, next);
                pos = next;
                while (executor.getQueue().size() >= getCapacity()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (Exception e) {
                        logger.error("main thread is interupted ", e);
                        System.out.println("main thread is interupted " + e.getMessage());
                    }
                }
                executor.execute(new BulkWorker(subEntities, jestClient));
            }
        } else {
            for (IndexEntity indexEntity : indexEntityList) {
                while (executor.getQueue().size() >= getCapacity()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(50);
                    } catch (Exception e) {
                        logger.error("main thread is interupted", e);
                        logger.error("main thread is interupted" + e.getMessage());
                    }
                }
                executor.execute(new BulkWorker(Lists.newArrayList(indexEntity), jestClient));
            }
        }
    }

    /**
     * 创建索引结构映射,以进行精确匹配
     *
     * @param client
     */
    public boolean createSchemaMapping(Client client, String indexName) {

        String index = indexName;
        Settings settings = Settings.builder().put("number_of_shards", shards).put("number_of_replicas", replicas)
                .build();

        CreateIndexResponse createIndexResponse = client.admin().indices().prepareCreate(index).setSettings(settings)
                .execute().actionGet();
        if (!createIndexResponse.isAcknowledged()) {
            return false;
        }
        // client.admin().indices().create(new CreateIndexRequest("productindex14")).actionGet();
        XContentBuilder mapping = null;
        try {
            mapping = jsonBuilder().prettyPrint().startObject().startObject(index).startObject("properties")
                    .startObject("query").field("type", "string").field("index", "not_analyzed").field("store", "yes")
                    .endObject().startObject("description").field("type", "string").field("index", "not_analyzed")
                    .endObject().startObject("price").field("type", "long").endObject().startObject("type")
                    .field("type", "string").endObject().endObject().endObject().endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(index).source(mapping);
        PutMappingResponse putMappingResponse = client.admin().indices().putMapping(mappingRequest).actionGet();
        return true;
        // client.admin().indices().preparePutMapping("productIndex13")
        // .setType("product")
        // .setSource(mapping).execute().actionGet();
    }

}
