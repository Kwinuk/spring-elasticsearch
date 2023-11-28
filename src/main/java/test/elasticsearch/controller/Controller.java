package test.elasticsearch.controller;

import test.elasticsearch.util.ElasticsearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import test.elasticsearch.model.session.Session;
import test.elasticsearch.model.session.SessionResults;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private ElasticsearchRepository elasticsearchRepository;

    @PostMapping("/api/sessions")
    public SessionResults getSessions(@RequestBody Map<String, Object> requestBody) throws Exception {
        SessionResults results = new SessionResults();

        try {
            String fields = (String) requestBody.get("fields");
            Map<String, Object> queryMap = (Map<String, Object>) requestBody.get("query");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.wrapperQuery(new ObjectMapper().writeValueAsString(queryMap)));

            if (fields != null && !fields.isEmpty()) {
                List<String> fieldList = Arrays.asList(fields.split(","));
                searchSourceBuilder.fetchSource(fieldList.toArray(new String[0]), null);
            } else {
                searchSourceBuilder.fetchSource(new String[]{"_index", "node", "firstPacket", "lastPacket", "source.port", "destination.port", "network.packets", "source.ip", "totDataBytes", "network.bytes", "ipProtocol", "destination.ip", "id", "fileId"}, null);
            }

            String indexName = "arkime_sessions*-*";

            SearchResponse searchResponse = elasticsearchRepository.search(indexName, searchSourceBuilder);

            SearchHits searchHits = searchResponse.getHits();

            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            for (SearchHit hit : searchHits.getHits()) {
                System.out.println(hit);
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Session session = modelMapper.map(sourceAsMap, Session.class);

                results.addData(session);
            }

            return results;
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("error");
        }
    }

}
