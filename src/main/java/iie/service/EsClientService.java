package iie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EsClientService {

    @Value("${es.ip}")
    public String esIP;

    @Value("${es.port}")
    public String esPort;


    private ElasticsearchClient client = null;



    public EsClientService()
    {
       /* RestClientTransport ss =  (RestClientTransport)client._transport();
        ss.restClient().isRunning()*/
    }

    public ElasticsearchClient getClient()
    {
        if (client == null){
            //首次连接
            ConnectES();
        }else {

            RestClientTransport restClientTransport =  (RestClientTransport)client._transport();
            RestClient restClient = restClientTransport.restClient();
            try {
                if (restClient.isRunning() && client.ping().value()){
                    //客户端正常
                }else {
                   //客户端不正常
                    if (restClient.isRunning()){
                        restClient.close();
                    }
                    client = null;
                    //重连
                    ConnectES();
                }
            } catch (ElasticsearchException | IOException e) {
                e.printStackTrace();
                client = null;

            }finally {
                return client;
            }
        }

        return client;
    }

    private void ConnectES() {
        if (client != null) {
        } else {
            System.out.println("连接Es中......");
            try {
                RestClient restClient = RestClient.builder(new HttpHost(esIP, Integer.parseInt(esPort) , "http")).build();
                RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(), null);
                client = new ElasticsearchClient(transport);
            }catch (Exception e)
            {
                System.err.println("连接Es出现异常!!!!");
                e.printStackTrace();
                client = null;
            }
        }
    }




}
