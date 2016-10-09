package ESTest;

import ESTest.bean.IndexEntity;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by zhipengwu on 16-10-8.
 */
public class ESTest {
    private static final Logger logger = LoggerFactory.getLogger(ESTest.class);
    public static void main(String[] args) {
        String targetName = "商品";
        String index = "testindex111";
        Client client = ESUtil.createClient();
//        createIndex(client);
        client.prepareGet();
        String userName = "";
        String age = "";

        SearchRequestBuilder searchRequestBuilder = null;
        searchRequestBuilder = client.prepareSearch(index);
        searchRequestBuilder.setQuery(QueryBuilders.matchQuery("description", targetName));
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHit[] searchHits;
        searchHits = searchResponse.getHits().getHits();

        for (int i = 0; i < searchHits.length; i++) {
            userName = searchHits[i].getSource().get("description").toString();
            age = searchHits[i].getSource().get("title").toString();
            System.out.println(String.format("name:%s, age:%s", userName, age));
        }
        System.out.println("###end");
        client.close();
    }

    /**
     * 创建不分词的index
     * @param client
     */
    public static void createIndex(Client client) {

        String index="testindex111";
        client.admin().indices().prepareCreate(index).execute().actionGet();
//        client.admin().indices().create(new CreateIndexRequest("productindex14")).actionGet();
        XContentBuilder mapping = null;
        try {
            mapping = jsonBuilder().prettyPrint()
                    .startObject()
                    .startObject(index)
                    .startObject("properties")
                    .startObject("title").field("type", "string").field("index","not_analyzed").field("store", "yes").endObject()
                    .startObject("description").field("type", "string").field("index", "not_analyzed").endObject()
                    .startObject("price").field("type", "double").endObject()
                    .startObject("onSale").field("type", "boolean").endObject()
                    .startObject("type").field("type", "integer").endObject()
                    .startObject("createDate").field("type", "date").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index).type(index).source(mapping);
        client.admin().indices().putMapping(mappingRequest).actionGet();
//        client.admin().indices().preparePutMapping("productIndex13")
//                .setType("product")
//                .setSource(mapping).execute().actionGet();
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
}
